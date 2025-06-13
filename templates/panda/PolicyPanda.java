///usr/bin/env jbang "$0" "$@" ; exit $?
//JAVA 21+
//DEPS com.fasterxml.jackson.core:jackson-databind:2.15.0
//DEPS com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.15.0
//DEPS info.picocli:picocli:4.7.6
//DEPS org.kohsuke:github-api:1.327
//DEPS ./CommonhausPanda.java

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.kohsuke.github.GHBranchProtection;
import org.kohsuke.github.GHContent;
import org.kohsuke.github.GHOrganization;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(name = "PolicyPanda 🐼", mixinStandardHelpOptions = true, version = "PolicyPanda 0.2", description = """
        The Policy Panda tries to spot if org/repos follows the requirement
        and best practices outlined in https://github.com/commonhaus/foundation/tree/main/policies
        """)
public class PolicyPanda extends CommonhausPanda implements Runnable {
    static final java.util.logging.Logger log = CommonhausPanda.log;

    enum AgreementType {
        DCO, CLA
    }

    enum Kind {
        SHOULD, MUST, BONUS
    }

    @Parameters(index = "0", description = "The organization to check")
    private String organization;

    @Option(names = { "-r",
            "--repo-regex" }, description = "Specify a regular expression to match repository names", defaultValue = ".*")
    private String repoRegex;

    @CommandLine.Option(names = { "-w",
            "--working-branch-patterns" }, description = "Specify a regular expression to match working branch names", defaultValue = "main")
    private String workingBranchRegex;

    @Option(names = { "-t",
            "--type" }, description = "Specify if the project uses DCO or CLA. Valid values: DCO, CLA", defaultValue = "DCO")
    private AgreementType agreementType;

    @Option(names = { "-o",
            "--output-dir" }, description = "Output directory for all reports", defaultValue = "reports")
    private String outputDir;

    @Option(names = { "-p", "--project-id" }, description = "Project ID to use in the YAML")
    private String projectId;

    @Option(names = {"-i",
            "--include-archived"}, description = "Include repositories in the organization that have been archived", defaultValue = "false")
    private boolean includeArchived;

    @Option(names = { "-s",
            "--skip-policy-check" }, description = "Skip policy compliance check", defaultValue = "false")
    private boolean skipPolicyCheck;

    @Option(names = { "-a",
            "--init-asset-discovery" }, description = "Generate asset discovery document (optional/opt-in)", defaultValue = "false")
    private boolean runAssetDiscovery;

    @Option(names = { "-g",
            "--init-org-settings" }, description = "Generate organization settings report (optional/opt-in)", defaultValue = "false")
    private boolean runOrgSettings;

    private final List<Information> checks = new ArrayList<>();
    private GHRepository orgDotGithubRepo;

    public static void main(String[] args) {
        int exitCode = new CommandLine(new PolicyPanda()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public void run() {
        printHeader();
        try {
            // Setup output directory
            ensureDirectoryExists(outputDir);

            // Connect to GitHub
            GitHub github = setupGitHubClient();
            GHOrganization org = github.getOrganization(organization);
            if (org == null) {
                log.severe("Organization not found: " + organization);
                return;
            }
            log.info("❇️ Inspecting organization " + org.getLogin());

            // Initialize projectId if not provided
            if (projectId == null) {
                projectId = organization.toLowerCase();
            }

            // Get the organization's .github repo if it exists
            try {
                orgDotGithubRepo = github.getRepository(organization + "/.github");
                log.info("Found organization .github repository");
            } catch (IOException e) {
                log.info("No organization .github repository found");
            }

            // Fetch all repositories based on regex
            log.info("Fetching repositories matching pattern: " + repoRegex);
            Pattern repoPattern = Pattern.compile(repoRegex);
            List<GHRepository> allRepos = org.listRepositories().toList();
            List<GHRepository> filteredRepos = allRepos.stream()
                    .filter(repo -> !repo.getName().equals(".github"))
                    .filter(repo -> repoPattern.matcher(repo.getName()).matches())
                    .filter(repo -> !(!includeArchived && repo.isArchived()))
                    .toList();
            log.info("Found " + filteredRepos.size() + " repositories matching the pattern");

            // Execute selected reports

            // Policy check is the default. Skip is intentional
            if (!skipPolicyCheck) {
                runPolicyCheck(filteredRepos);
            }

            if (runAssetDiscovery) {
                runAssetDiscovery(org, filteredRepos);
            }

            if (runOrgSettings) {
                runOrgSettingsReport(org);
            }

            log.info("🎉 All reports completed for " + organization);
        } catch (IOException e) {
            log.severe("Error: " + e);
            e.printStackTrace();
        }
    }

    private void runPolicyCheck(List<GHRepository> repos) throws IOException {
        log.info("🌳 Running policy check...");

        CommunityFiles orgFiles;
        if (orgDotGithubRepo != null) {
            orgFiles = findCommunityFiles(orgDotGithubRepo);
            if (orgFiles.governance() != null) {
                addCheck(orgDotGithubRepo,
                        new Item("Organization %s file found"
                                .formatted(orgFiles.governance().getName())));
            }
            if (orgFiles.codeOfConduct() != null) {
                addCheck(orgDotGithubRepo,
                        new Item("Organization %s file found"
                                .formatted(orgFiles.codeOfConduct().getName())));
            }
            if (orgFiles.contributing() != null) {
                addCheck(orgDotGithubRepo,
                        new Item("Organization %s file found"
                                .formatted(orgFiles.contributing().getName())));
            }
        } else {
            orgFiles = new CommunityFiles(null, null, null);
        }

        // Process each repository
        for (GHRepository repo : repos) {
            processRepository(repo, orgFiles);
        }

        // Write policy report
        String outputFile = outputDir + "/policy-report-" + organization + ".md";
        try (PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {
            writer.println("# Policy Compliance Report for " + organization + "\n");
            writer.println("Generated on: " + java.time.LocalDateTime.now() + "\n");
            checks.forEach(x -> writer.println(x.markdownSummary()));
        }
        log.info("✔️ Policy report written to " + outputFile);
    }

    private void processRepository(GHRepository repo, CommunityFiles orgFiles) throws IOException {
        addCheck(repo, new Heading("🏡 " + repo.getFullName()));

        CommunityFiles repoFiles = findCommunityFiles(repo).withFallback(orgFiles);

        addCheck(repo, new FileCheck("Governance", Kind.MUST, repoFiles.governance()));
        addCheck(repo, new FileCheck("Code of Conduct", Kind.MUST, repoFiles.codeOfConduct()));
        addCheck(repo, new FileCheck("Contributing", Kind.MUST, repoFiles.contributing()));

        // Check agreement type (DCO/CLA)
        switch (agreementType) {
            case DCO -> {
                GHContent dco = findFileInRepo(repo, "dco.txt", true);
                addCheck(repo, new FileCheck("Developer Certificate of Origin", Kind.MUST, dco));

                // Check if contributing mentions DCO
                if (repoFiles.contributing() != null) {
                    String content = readContent(repoFiles.contributing());
                    boolean hasDcoMention = content.matches("(?is).*\\b(DCO|Developer Certificate of Origin)\\b.*");
                    addCheck(repo, new ContentCheck("DCO Reference", "DCO mentioned in CONTRIBUTING",
                            Kind.MUST, hasDcoMention, repoFiles.contributing()));
                }
            }
            case CLA -> {
                GHContent cla = findFileInRepo(repo, ".*(?:cla|contributor|agreement).*");
                addCheck(repo, new FileCheck("Contributor License Agreement", Kind.MUST, cla));

                // Check if contributing mentions CLA
                if (repoFiles.contributing() == null) {
                    String content = readContent(repoFiles.contributing());
                    boolean hasCLAMention = content.matches("(?is).*\\b(CLA|Contributor License Agreement)\\b.*");
                    addCheck(repo, new ContentCheck("CLA Reference", "CLA mentioned in CONTRIBUTING",
                            Kind.MUST, hasCLAMention, repoFiles.contributing()));
                }
            }
        }

        GHContent license = findFileInRepo(repo, "LICENSE");
        addCheck(repo, new FileCheck("License", Kind.SHOULD, license));

        GHContent codeowners = findFileInRepo(repo, "CODEOWNERS");
        if (codeowners != null) {
            JsonNode response = rawRequest("/repos/%s/codeowners/errors".formatted(repo.getFullName()));
            if (response != null) {
                if (response.has("errors")) {
                    JsonNode errors = response.get("errors");
                    CodeOwnerError[] codeOwnerErrors = yamlMapper.treeToValue(errors, CodeOwnerError[].class);
                    addCheck(repo, new CodeownersCheck(codeOwnerErrors, codeowners));
                } else {
                    addCheck(repo, new CodeownersCheck(null, codeowners));
                }
            }
        }

        repo.getBranches().values().stream()
                .filter(b -> b.getName().matches(workingBranchRegex))
                .forEach(branch -> {
                    addCheck(repo, new Check(branch.isProtected(), Kind.BONUS,
                            "Branch %s is protected".formatted(branch.getName()),
                            "Branch %s is not protected".formatted(branch.getName())));

                    if (branch.isProtected()) {
                        try {
                            GHBranchProtection protection = branch.getProtection();
                            addCheck(repo, new Check(!protection.getAllowForcePushes().isEnabled(), Kind.BONUS,
                                    "Branch %s does not allow force pushes".formatted(branch.getName()),
                                    "Branch %s allows force pushes".formatted(branch.getName())));
                            addCheck(repo, new Check(!protection.getAllowDeletions().isEnabled(), Kind.BONUS,
                                    "Branch %s does not allow deletions".formatted(branch.getName()),
                                    "Branch %s allows deletions".formatted(branch.getName())));
                            addCheck(repo, new Check(protection.getEnforceAdmins().isEnabled(), Kind.BONUS,
                                    "Branch %s enforces admin rules".formatted(branch.getName()),
                                    "Branch %s does not enforce admin rules".formatted(branch.getName())));
                            var requiredReviews = branch.getProtection().getRequiredReviews();
                            if (codeowners != null) {
                                addCheck(repo, new Check(requiredReviews != null && requiredReviews.isRequireCodeOwnerReviews(), Kind.BONUS,
                                        "Branch %s requires reviews from code owners".formatted(branch.getName()),
                                        "Branch %s does not require reviews from code owners".formatted(branch.getName())));
                            } else {
                                addCheck(repo, new Check(requiredReviews != null && protection.getRequiredReviews().getRequiredReviewers() > 0, Kind.BONUS,
                                        "Branch %s requires code review".formatted(branch.getName()),
                                        "Branch %s does not require code review".formatted(branch.getName())));
                            }
                        } catch (IOException e) {
                            throw new UncheckedIOException(
                                    "Failed to get branch protection for %s".formatted(branch.getName()), e);
                        }
                    }
                });
    }

    private CommunityFiles findCommunityFiles(GHRepository repo) {
        // These files cascade from the org down to the repository
        // Find and capture them in a record that we can use to validate
        // presence/contents with fallbacks
        // Standard community health files - reference:
        // https://docs.github.com/en/communities/setting-up-your-project-for-healthy-contributions/creating-a-default-community-health-file
        return new CommunityFiles(
                findFileInRepo(repo, "GOVERNANCE"),
                findFileInRepo(repo, "CODE_OF_CONDUCT"),
                findFileInRepo(repo, "CONTRIBUTING"));
    }

    private void addCheck(GHRepository repo, Information check) {
        checks.add(check);
        log.finest(check.summary().replaceAll("\n\\s+", "; "));
    }

    private void printHeader() {
        // formatter:off
        System.out.println(
                """
                        ## Policy Panda 🐼

                        The Policy Panda tries to spot if org/repos follows the requirement of the CommonHaus Foundation.
                        It will look for the various files in various possbile formats (%s) in the repositories,
                        including `.github` global repo if present.

                        It is not perfect, it is just a panda. 🐼

                        If you think it is missing something, please let us know at https://github.com/commonhaus/automation/issues
                        """
                        .formatted(String.join(", ", markupFormats)));
        // formatter:on
    }

    private void runAssetDiscovery(GHOrganization org, List<GHRepository> repos) throws IOException {
        log.info("🌳 Creating an asset inventory");
        ArrayNode owners = yamlMapper.createArrayNode();

        var admins = org.listMembersWithRole("admin");
        for (var admin : admins) {
            owners.add(yamlMapper.createObjectNode()
                    .put("login", admin.getLogin())
                    .put("name", admin.getName())
                    .put("email", admin.getEmail())
                    .put("signatory", false));
        }

        ObjectNode orgNode = yamlMapper.createObjectNode()
                .put("name", org.getLogin())
                .put("url", org.getHtmlUrl().toString());
        orgNode.set("reports", yamlMapper.createObjectNode()
                .put("policy", "reports/policy-report-" + organization + ".md")
                .put("settings", "reports/scm-settings-" + organization + ".yaml"));
        orgNode.set("owners", owners);
        orgNode.put("notes", "Owners listed above are users with an admin role.");

        ObjectNode sourceControlData = yamlMapper.createObjectNode()
                .set("github", yamlMapper.createObjectNode()
                        .set("organizations", yamlMapper.createArrayNode()
                                .add(orgNode)));

        // Check for repositories with CLA
        ArrayNode reposWithCLAs = findRepositoriesWithCLA(repos);
        if (reposWithCLAs.isEmpty()) {
            log.fine("🔍 No repositories with CLA found");
        } else {
            log.info("🔍 Found " + reposWithCLAs.size() + " repositories with CLA");
            sourceControlData.set("contributions", yamlMapper.createObjectNode()
                    .set("repositories_with_cla", reposWithCLAs));
        }

        ObjectNode assetData = yamlMapper.createObjectNode()
                .put("id", projectId)
                .set("source_control", sourceControlData);

        // Write asset data to YAML file
        String assetFile = outputDir + "/scm-github-" + organization + ".yaml";
        writeToYamlFile(assetData, assetFile);

        log.info("✔️ Organization information written to " + assetFile);
    }

    private ArrayNode findRepositoriesWithCLA(List<GHRepository> repos) {
        ArrayNode reposWithCLA = yamlMapper.createArrayNode();

        for (GHRepository repo : repos) {
            try {
                // Look for CLA files with various naming patterns
                GHContent cla = findFileInRepo(repo, ".*(?:cla|contributor|agreement).*");
                if (cla != null) {
                    ObjectNode claInfo = yamlMapper.createObjectNode()
                            .put("repository", repo.getName())
                            .put("url", repo.getHtmlUrl().toString())
                            .put("tool", "... (EasyCLA, CLA Assistant, etc.)")
                            .put("storage", "... (e.g. cla-assistant database, etc.)")
                            .put("manager", "... (who manages the CLA process?)")
                            .put("notes", "CLA detected.\nFill in the details. Any notes for future you can go here");
                    reposWithCLA.add(claInfo);
                }
            } catch (Exception e) {
                log.warning("Error checking CLA for " + repo.getName() + ": " + e.getMessage());
            }
        }
        return reposWithCLA;
    }

    private void runOrgSettingsReport(GHOrganization org) throws IOException {
        log.info("🌳 Creating a listing of organization and repository settings");

        ObjectNode orgSettings = (ObjectNode) rawRequest("/orgs/" + organization);
        if (orgSettings == null) {
            log.severe("Error fetching organization settings");
            return;
        }

        ArrayNode repositories = (ArrayNode) rawRequest("/orgs/" + organization + "/repos");
        if (repositories == null) {
            log.warning("Error fetching repository settings for organization");
            return;
        }
        repositories.forEach(repoNode -> {
            fetchBooleanValue("automated-security-fixes", repoNode);
            fetchBooleanValue("private_vulnerability_reporting", repoNode);
            fetchBooleanValue("vulnerability-alerts", repoNode);
        });
        orgSettings.set("repositories", filterFields(repositories));

        ObjectNode rootNode = yamlMapper.createObjectNode()
                .put("id", projectId)
                .set("source_control", yamlMapper.createObjectNode()
                        .set("settings", yamlMapper.createArrayNode()
                                .add(filterFields(orgSettings))));

        // Write asset data to YAML file
        String settingsFile = outputDir + "/scm-settings-" + organization + ".yaml";
        writeToYamlFile(rootNode, settingsFile);

        log.info("✔️ Organization information written to " + settingsFile);
    }

    public ArrayNode filterFields(ArrayNode nodes) {
        for (int i = 0; i < nodes.size(); i++) {
            ObjectNode node = (ObjectNode) nodes.get(i);
            filterFields(node);
        }
        return nodes;
    }

    public ObjectNode filterFields(ObjectNode node) {
        List<String> fieldNames = new ArrayList<>();
        node.fieldNames().forEachRemaining(fieldNames::add);
        for (String key : fieldNames) {
            if (key.matches("owner|.*_url")) {
                node.remove(key);
            }
        }
        return node;
    }

    public void fetchBooleanValue(String fieldName, JsonNode node) {
        String repoUrl = node.get("url").asText();
        JsonNode response = rawRequest(repoUrl + "/" + fieldName);
        if (response != null) {
            ((ObjectNode) node).set(fieldName, response);
        }
    }

    record CommunityFiles(
            GHContent governance,
            GHContent codeOfConduct,
            GHContent contributing) {

        public CommunityFiles withFallback(CommunityFiles orgFiles) {
            return new CommunityFiles(
                    governance != null ? governance : orgFiles.governance,
                    codeOfConduct != null ? codeOfConduct : orgFiles.codeOfConduct,
                    contributing != null ? contributing : orgFiles.contributing);
        }
    }

    interface Information {
        String summary();

        String markdownSummary();

        default String urlText(GHContent content) {
            return content != null
                    ? "\n    <" + content.getHtmlUrl().toString() + ">"
                    : "";
        }

        default String getMarker(Kind kind, boolean check) {
            return switch (kind) {
                case MUST -> check ? "✅" : "❌";
                case SHOULD -> check ? "✅" : "⚠️";
                case BONUS -> check ? "💚" : "💛";
            };
        }
    }

    record Heading(String note) implements Information {
        public String summary() {
            return "\n%s\n".formatted(note);
        }

        public String markdownSummary() {
            return "\n## %s\n".formatted(note);
        }
    }

    record Item(String note) implements Information {
        public String summary() {
            return markdownSummary();
        }

        public String markdownSummary() {
            return "- %s".formatted(note);
        }
    }

    record Check(boolean check, Kind kind, String ok, String error) implements Information {
        public String summary() {
            // formatter:off
            return "- %s %s".formatted(
                    getMarker(kind, check),
                    check ? ok : error);
            // formatter:on
        }

        public String markdownSummary() {
            return "- [%s] %s %s".formatted(
                    (check ? "x" : " "),
                    getMarker(kind, check),
                    check ? ok : error);
        }
    }

    record CodeownersCheck(CodeOwnerError[] errors, GHContent content)
            implements Information {
        public String summary() {
            // formatter:off
            return "- %s Codeowners - %s%s%s".formatted(
                    (ok() ? "💚" : "❌"),
                    (ok() ? "File is valid" : "File has errors"),
                    Stream.of(errors)
                            .map(CodeOwnerError::summary)
                            .collect(Collectors.joining("")),
                    urlText(content));
            // formatter:on
        }

        public String markdownSummary() {
            return "- [%s] %s Codeowners - %s%s%s".formatted(
                    (ok() ? "x" : " "),
                    (ok() ? "💚" : "❌"),
                    (ok() ? "File is valid" : "File has errors"),
                    Stream.of(errors)
                            .map(CodeOwnerError::markdownSummary)
                            .collect(Collectors.joining("")),
                    urlText(content)).replace("        \n", "\n");
        }

        boolean ok() {
            return errors == null || errors.length == 0;
        }
    }

    record ContentCheck(String name, String description, Kind kind, boolean ok, GHContent content)
            implements Information {
        public String summary() {
            // formatter:off
            return "- %s %s - %s%s".formatted(
                    getMarker(kind, ok), name, description, urlText(content));
            // formatter:on
        }

        public String markdownSummary() {
            return "- [%s] %s %s - %s%s"
                    .formatted((ok ? "x" : " "),
                            getMarker(kind, ok), name, description, urlText(content));
        }
    }

    record FileCheck(String name, Kind kind, GHContent content)
            implements Information {
        public String summary() {
            // formatter:off
            return "- %s %s - %s%s".formatted(
                    getMarker(), name, ok() ? "File exists" : "File does not exist", urlText(content));
            // formatter:on
        }

        public String markdownSummary() {
            return "- [%s] %s %s - %s%s"
                    .formatted((ok() ? "x" : " "),
                            getMarker(), name, ok() ? "File exists" : "File does not exist", urlText(content));
        }

        private String getMarker() {
            return switch (kind) {
                case MUST -> ok() ? "✅" : "❌";
                case SHOULD -> ok() ? "✅" : "⚠️";
                case BONUS -> "💚";
            };
        }

        boolean ok() {
            return content != null;
        }
    }

    record CodeOwnerError(
            int line,
            int column,
            String source,
            String kind,
            String message,
            String path,
            String suggestion) {
        public String summary() {
            return "; " + String.join(", ", text());
        }

        public String markdownSummary() {
            return "\n    - " + String.join("\n        ", text());
        }

        private List<String> text() {
            List<String> text = new ArrayList<>();
            text.add("Path: %s".formatted(path));
            text.add("[Line: %d, Column: %d] %s".formatted(line, column, kind));
            text.add("");
            text.add("  " + source.trim());
            text.add("");
            if (message != null) {
                text.addAll(List.of(("*Message*: " + message).split("\n")));
                text.add("");
            }
            if (suggestion != null && !suggestion.equals(message)) {
                text.addAll(List.of(("*Suggestion*: " + message).split("\n")));
                text.add("");
            }
            return text;
        }
    }
}
