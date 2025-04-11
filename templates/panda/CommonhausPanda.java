///usr/bin/env jbang "$0" "$@" ; exit $?
//JAVA 21+
//DEPS com.fasterxml.jackson.core:jackson-databind:2.15.0
//DEPS com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.15.0
//DEPS com.squareup.okhttp3:okhttp:4.12.0
//DEPS info.picocli:picocli:4.7.6
//DEPS org.kohsuke:github-api:1.327
//DEPS org.yaml:snakeyaml:1.33

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.regex.Pattern;

import org.kohsuke.github.GHContent;
import org.kohsuke.github.GHFileNotFoundException;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.kohsuke.github.extras.okhttp3.OkHttpConnector;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactoryBuilder;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import picocli.CommandLine;

public class CommonhausPanda {
    static {
        System.setProperty("java.util.logging.SimpleFormatter.format",
                "%4$-7s [%3$s] %5$s%n");
        java.util.logging.Logger rootLogger = java.util.logging.Logger.getLogger("");
        rootLogger.setLevel(Level.INFO);
        for (var handler : rootLogger.getHandlers()) {
            rootLogger.removeHandler(handler);
        }
    }
    public static final java.util.logging.Logger log = java.util.logging.Logger.getLogger("🐼");

    // Common file extensions to search for documentation files
    public static final List<String> markupFormats = List.of(".md", ".adoc", ".txt");
    public static final String markupFormatRegex = "(?:\\.(?:md|adoc|txt))?$";

    // Common directories to search for documentation files
    // https://docs.github.com/en/communities/setting-up-your-project-for-healthy-contributions/creating-a-default-community-health-file
    public static final List<String> searchDirectories = List.of("", ".github", "docs");

    // Cache directory for HTTP responses
    public final String cacheDir = System.getProperty("user.home") + "/.cache/commonhaus-panda-cache";

    // Cache maps to reduce API calls
    private final Map<String, List<GHContent>> directoryCache = new ConcurrentHashMap<>();
    private final Map<String, Pattern> patternCache = new ConcurrentHashMap<>();
    private final Map<String, GHRepository> repositoryCache = new ConcurrentHashMap<>();

    // Reusable JSON mapper
    protected final ObjectMapper jsonMapper = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT);

    protected GitHub gh = null;
    protected String token = null;

    /**
     * A YAML mapper that is configured to be pretty-printed and to use plain
     * scalars.
     * <p>
     * Supplier creates the singleton instance when the interface is loaded.
     */
    public static final ObjectMapper yamlMapper = new Supplier<ObjectMapper>() {
        @Override
        public ObjectMapper get() {
            return new ObjectMapper(new YAMLFactoryBuilder(new YAMLFactory())
                    .disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER)
                    .enable(YAMLGenerator.Feature.INDENT_ARRAYS)
                    .enable(YAMLGenerator.Feature.INDENT_ARRAYS_WITH_INDICATOR)
                    .enable(YAMLGenerator.Feature.LITERAL_BLOCK_STYLE)
                    .enable(YAMLGenerator.Feature.MINIMIZE_QUOTES)
                    .enable(YAMLGenerator.Feature.SPLIT_LINES)
                    .build())
                    .findAndRegisterModules()
                    .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                    .setSerializationInclusion(Include.NON_EMPTY)
                    .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.NON_PRIVATE);
        }
    }.get();

    @CommandLine.Option(names = { "-v",
            "--verbose" }, description = "Verbose logging (level: off, info, fine, finer, finest, all)", defaultValue = "info")
    public void setVerbose(String level) {
        if (level == null || level.isBlank()) {
            log.setLevel(java.util.logging.Level.INFO);
            return;
        }

        Level logLevel = java.util.logging.Level.FINE;
        try {
            logLevel = java.util.logging.Level.parse(level.toUpperCase());
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid log level: " + level);
        }

        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(logLevel);

        for (var x : List.of("🐼", "org.kohsuke.github.GitHubClient")) {
            java.util.logging.Logger logger = java.util.logging.Logger.getLogger(x);
            logger.setLevel(logLevel);
            logger.setUseParentHandlers(false); // Prevent propagation to parent logger

            // Remove all existing handlers from this logger
            for (var handler : logger.getHandlers()) {
                logger.removeHandler(handler);
            }
            // Add a new ConsoleHandler
            logger.addHandler(consoleHandler);
        }
    }

    /**
     * Set up GitHub client with caching to reduce API calls
     */
    public GitHub setupGitHubClient() throws IOException {
        Path cacheDirectory = Path.of(cacheDir);
        ensureDirectoryExists(cacheDir);

        log.finest("Cache directory: " + cacheDir);
        Cache cache = new Cache(cacheDirectory.toFile(), 10 * 1024 * 1024); // 10MB cache

        log.finest("Creating GitHub API connector with caching");
        var connector = new OkHttpConnector(new OkHttpClient.Builder().cache(cache).build());

        File homeDir = new File(System.getProperty("user.home"));
        File propertyFile = new File(homeDir, ".github");

        Properties props = new Properties();
        if (!propertyFile.exists()) {
            getToken("GITHUB_OAUTH", "oauth", props);
            getToken("GITHUB_JWT", "jwt", props);
        } else {
            try (FileReader reader = new FileReader(propertyFile)) {
                props.load(reader);
            } catch (IOException e) {
                log.warning("Failed to load GitHub property file: " + propertyFile.getAbsolutePath() + ": " + e);
            }
        }

        token = props.getProperty("oauth", props.getProperty("jwt"));
        gh = GitHubBuilder.fromPropertyFile()
                .withConnector(connector)
                .build();
        log.finest("Connected successfully");
        return gh;
    }

    private void getToken(String envKey, String propName, Properties props) {
        String value = System.getenv(envKey);
        if (value != null) {
            props.put(propName, value);
        }
    }

    /**
     * Get a repository from cache or fetch it if not cached
     *
     * @param github       GitHub client
     * @param repoFullName Full repository name (e.g., "owner/repo")
     * @return GHRepository instance
     * @throws IOException If API call fails
     */
    public GHRepository getRepository(GitHub github, String repoFullName) throws IOException {
        return repositoryCache.computeIfAbsent(repoFullName, key -> {
            try {
                log.finest("Fetching repository: " + key);
                return github.getRepository(key);
            } catch (IOException e) {
                log.warning("Failed to fetch repository: " + key);
                throw new RuntimeException("Failed to fetch repository: " + key, e);
            }
        });
    }

    /**
     * Look for a file in all configured search directories
     *
     * @param repo           The repository
     * @param targetFileStem Either the base filename or a regex to match the base
     *                       file name (no extension)
     * @return GHContent for the file if found, null otherwise
     */
    public GHContent findFileInRepo(GHRepository repo, String targetFileStem) {
        return findFileInRepo(repo, targetFileStem, false);
    }

    /**
     * Look for a file in all configured search directories
     *
     * @param repo       The repository
     * @param targetFile Either the full filename or a regex to match the base file
     *                   name
     * @param exactMatch If true, the filename must match the provided
     *                   targetFileStem exactly
     * @return GHContent for the file if found, null otherwise
     */
    public GHContent findFileInRepo(GHRepository repo, String targetFile, boolean exactMatch) {
        for (String searchDir : searchDirectories) {
            GHContent content = findFileInRepoPath(repo, searchDir, targetFile, exactMatch);
            if (content != null) {
                return content;
            }
        }
        return null;
    }

    /**
     * Look for a file in a specific directory path
     *
     * @param repo       The repository
     * @param searchDir  The directory to search in
     * @param targetFile Either the full filename or a regex to match the base file
     *                   name
     * @param exactMatch If true, the filename must match the provided
     *                   targetFileStem exactly
     * @return GHContent for the file if found, null otherwise
     */
    public GHContent findFileInRepoPath(GHRepository repo, String searchDir, String targetFile, boolean exactMatch) {
        // Get cached directory contents
        List<GHContent> dirContents = getDirectoryContents(repo, searchDir);

        // Look for the file with any supported extensions
        for (GHContent content : dirContents) {
            if (matchesFileName(content.getName(), targetFile, exactMatch)) {
                log.finest("🔍 Found %s as %s in %s"
                        .formatted(targetFile, content.getName(), repo.getFullName()));
                return content;
            }
        }
        return null;
    }

    /**
     * Check if a filename matches our target filename with any supported extension
     *
     * @param actualFileName GHContents file name
     * @param targetFile     Either the full filename or a regex to match the base
     *                       file name
     * @param exactMatch     If true, the filename must match the provided
     *                       targetFileStem exactly
     */
    private boolean matchesFileName(String actualFileName, String targetFile, boolean exactMatch) {
        if (exactMatch) {
            return actualFileName.equalsIgnoreCase(targetFile);
        }
        // Get or create pattern
        Pattern pattern = patternCache.computeIfAbsent(
                targetFile,
                stem -> Pattern.compile(stem + markupFormatRegex, Pattern.CASE_INSENSITIVE));
        return pattern.matcher(actualFileName).matches();
    }

    /**
     * Get the contents of a directory with caching to reduce API calls
     *
     * @param repo The repository
     * @param path The directory path
     * @return List of directory contents or empty list if directory doesn't exist
     */
    private List<GHContent> getDirectoryContents(GHRepository repo, String path) {
        String repoKey = repo.getFullName() + "/" + path;

        // First, check if we have a cache for this repo/path
        return directoryCache.computeIfAbsent(
                repoKey, k -> {
                    // Not cached, fetch from GitHub
                    try {
                        log.finest(" 🔍 Fetching directory contents: %s".formatted(repoKey));
                        List<GHContent> contents = repo.getDirectoryContent(path);
                        return contents;
                    } catch (GHFileNotFoundException e) {
                        // Directory doesn't exist, cache empty result
                        log.finest("Directory %s not found in %s".formatted(path, repo.getFullName()));
                        return List.of();
                    } catch (IOException e) {
                        log.warning("Error fetching directory %s from %s: %s"
                                .formatted(path, repo.getFullName(), e));
                        return List.of();
                    }
                });
    }

    /**
     * Create directory if it doesn't exist
     *
     * @param dirPath Path to create
     * @return true if directory exists or was created, false otherwise
     */
    public boolean ensureDirectoryExists(String dirPath) {
        Path path = Path.of(dirPath);
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
                return true;
            } catch (IOException e) {
                log.warning("Failed to create directory: " + dirPath);
                return false;
            }
        }
        return true;
    }

    public JsonNode rawRequest(String query) {
        // Set up the connection
        HttpURLConnection connection = null;
        try {
            URI uri = URI.create(query.startsWith("https") ? query : (gh.getApiUrl() + query));
            log.fine("Making raw request to: " + uri);

            connection = gh.getConnector().connect(uri.toURL());
            connection.setRequestProperty("Accept", "application/vnd.github.v3+json");
            connection.setRequestProperty("Authorization", "Bearer " + token);

            var code = connection.getResponseCode();
            log.fine("Response " + code + " " + connection.getResponseMessage());
            // Read the response
            if (code == HttpURLConnection.HTTP_OK) {
                try (var inputStream = connection.getInputStream()) {
                    String content = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                    log.fine("Response Content: " + content);
                    return jsonMapper.readTree(content); // Parse JSON response
                }
            } else if (code == HttpURLConnection.HTTP_NOT_FOUND) {
                return null;
            } else {
                // Handle non-200 responses
                try (var errorStream = connection.getErrorStream()) {
                    if (errorStream != null) {
                        String errorContent = new String(errorStream.readAllBytes(), StandardCharsets.UTF_8);
                        log.warning("Error Response: " + errorContent);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return null;
    }

    /**
     * Read content from GHContent as string
     *
     * @param content GitHub content object
     * @return Content as string
     */
    public String readContent(GHContent content) {
        try {
            return new String(content.read().readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read content: " + content.getPath(), e);
        }
    }

    /**
     * Write object to JSON file
     *
     * @param <T>      Type of object to write
     * @param object   Object to write
     * @param filePath File path to write to
     * @throws IOException If file cannot be written
     */
    public <T> void writeToJsonFile(T object, String filePath) throws IOException {
        jsonMapper.writeValue(new java.io.File(filePath), object);
    }

    /**
     * Write object to YAML file
     *
     * @param <T>      Type of object to write
     * @param object   Object to write
     * @param filePath File path to write to
     * @throws IOException If file cannot be written
     */
    public <T> void writeToYamlFile(T object, String filePath) throws IOException {
        yamlMapper.writeValue(new java.io.File(filePath), object);
    }
}