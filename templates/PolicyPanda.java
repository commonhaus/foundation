///usr/bin/env jbang "$0" "$@" ; exit $?
//JAVA 21+
//DEPS com.fasterxml.jackson.core:jackson-databind:2.12.3
//DEPS com.fasterxml.jackson.core:jackson-core:2.12.3
//DEPS com.fasterxml.jackson.core:jackson-annotations:2.12.3
//DEPS org.kohsuke:github-api:1.327
//DEPS info.picocli:picocli:4.7.6
//DEPS com.squareup.okhttp3:okhttp:4.12.0

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import org.kohsuke.github.GHBranchProtection;
import org.kohsuke.github.GHFileNotFoundException;
import org.kohsuke.github.GHOrganization;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.kohsuke.github.GHContent;
import org.kohsuke.github.extras.okhttp3.OkHttpConnector;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(name = "PolicyPanda 🐼", mixinStandardHelpOptions = true, version = "PolicyPanda 0.1",

description = """
    The Policy Panda tries to spot if org/repos follows the requirement
    and best practices outlined in https://github.com/commonhaus/foundation/tree/main/policies
    """)
public class PolicyPanda implements Runnable {

    static {
        System.setProperty("java.util.logging.SimpleFormatter.format",
                    "%4$-7s [%3$s] %5$s %6$s%n");

    }
    private static final java.util.logging.Logger log = java.util.logging.Logger.getLogger(PolicyPanda.class.getName());

    enum AgreementType {
     DCO, CLA
    }

    enum Kind {
        SHOULD, MUST
    }

    static List<String> filesInGithubDir = List.of("CODEOWNERS");

    record check(String name, String description, GHRepository repo, Kind kind, boolean ok, GHContent content) {

        String summary() {
            return (ok ? "✅" : kind.equals(Kind.MUST) ? "❌" : "⚠️") + " " + name + " - " + description + (content != null ? " (" + content.getHtmlUrl().replace("https://github.com/","") + ")" : "");
        }
    }

    Map<String, List<check>> checks = new HashMap<>();

    @CommandLine.Option(names = {"-t", "--type"}, description = "Specify if the project uses DCO or CLA. Valid values: DCO, CLA", defaultValue="DCO")
    private AgreementType agreementType;

    private List<String> markupFormats = List.of("", ".md", ".adoc", ".txt");

    @Parameters(index = "0", description = "The organization to check")
    private String organization;

    @CommandLine.Option(names = {"-r", "--repo-regex"}, description = "Specify a regular expression to match repository names", defaultValue=".*")
    private String repoRegex;

    @CommandLine.Option(names = {"-w", "--working-branch-patterns"}, description = "Specify a regular expression to match working branch names", defaultValue="main")
    private String workingBranchRegex;


    public static void main(String[] args) {
        int exitCode = new CommandLine(new PolicyPanda()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public void run() {

        System.out.println("## Policy Panda 🐼");
        System.out.println("The Policy Panda tries to spot if org/repos follows the requirement and recommendations by the CommonHaus Foundation.");
        System.out.println("It will look for the various files in various possbile formats (" + markupFormats + ") in the repositories, including `.github` global repo if present.");
        System.out.println("It is not perfect, it is just a panda. 🐼");
        System.out.println("If you think it is missing something, please let us know at https://github.com/commonhaus/automation/issues");

        try {

            Path cacheDirectory = Path.of(".policy-panda-cache");
            Cache cache = new Cache(cacheDirectory.toFile(), 10 * 1024 * 1024); // 10MB cache
            var x = new OkHttpConnector(new OkHttpClient.Builder().cache(cache).build());
            GitHub github = GitHubBuilder.fromEnvironment().fromPropertyFile()
                .withConnector(x)
                .build();

            log.fine("organization: " + organization);
            GHOrganization org = github.getOrganization(organization);
            log.fine("fetching repos: " + organization);

            List<GHRepository> repos = org.listRepositories().toList();

            var globalRepo = org.getRepository(".github");

            for (GHRepository repo : repos) {
                if (!repo.getName().matches(repoRegex)) {
                    log.fine(organization + "/" + repo.getName() + " skipped");
                    continue;
                }
                log.fine("Checking repository: " + repo.getName());

                var usesCodeOwners = checkFile(repo, globalRepo, "Code owners", "Code owners file", "CODEOWNERS", Kind.SHOULD);
                checkFile(repo, globalRepo, "Governance", "Governance file", "GOVERNANCE", Kind.MUST);

                switch(agreementType) {
                    case DCO -> {
                        checkFile(repo, globalRepo, "DCO", "DCO file should be present", "DCO", Kind.MUST);
                    }
                    case CLA -> {
                        checkFile(repo, globalRepo, "CLA", "CLA file should be present", "CLA", Kind.MUST);
                    }
                }

                checkFile(repo, globalRepo, "Contributing guide", "Contributing guide present", "CONTRIBUTING", Kind.MUST, (content, contentString) -> {
                    switch(agreementType) {
                        case DCO -> {
                            addCheck(repo, new check("DCO", "Contributing guide should mention DCO", repo, Kind.SHOULD, contentString.matches("(?s).*\\(DCO\\).*"), content));
                        }
                        case CLA -> {
                            addCheck(repo, new check("CLA", "Contributing guide should mention CLA", repo, Kind.SHOULD, contentString.matches("(?s).*\\(CLA\\).*"), content));
                        }
                    }
                });

                checkFile(repo, globalRepo, "Code of conduct", "Code of conduct file", "CODE_OF_CONDUCT", Kind.MUST);


                repo.getBranches().values()
                        .stream()
                        .filter(b -> b.getName().matches(workingBranchRegex))
                        .forEach(branch -> {

                            addCheck(repo, new check("[%s] Branch protection".formatted(branch.getName()), "Branch has protection", repo, Kind.SHOULD, branch.isProtected(), null));

                            if (branch.isProtected()) {
                                try {
                                    var protection = branch.getProtection();
                                    addCheck(repo, new check("[%s] Force-push disallowed".formatted(branch.getName()), "Branch disallows force-push", repo, Kind.SHOULD, !protection.getAllowForcePushes().isEnabled(), null));
                                    addCheck(repo, new check("[%s] Deletions disallowed".formatted(branch.getName()), "Branch disallows deletions", repo, Kind.SHOULD, !protection.getAllowDeletions().isEnabled(), null));
                                    addCheck(repo, new check("[%s] Admin bypass disallowed".formatted(branch.getName()), "Admin bypass disallowed", repo, Kind.SHOULD, protection.getEnforceAdmins().isEnabled(), null));
                                    var requiredReviews = branch.getProtection().getRequiredReviews();
                                    if (usesCodeOwners) {
                                        // They have are using a CODEOWNERS, so it makes sense for code-owner review to be enforced.
                                        addCheck(repo, new check("[%s] Code owner code review".formatted(branch.getName()), "Code owner review enforced", repo, Kind.SHOULD, requiredReviews != null && requiredReviews.isRequireCodeOwnerReviews(), null));
                                    } else {
                                        addCheck(repo, new check("[%s] Code review".formatted(branch.getName()), "Code review enforced", repo, Kind.SHOULD, requiredReviews != null && protection.getRequiredReviews().getRequiredReviewers() > 0, null));
                                    }
                                } catch (IOException e) {
                                    throw new UncheckedIOException("Failed to get branch protection for %s".formatted(branch.getName()), e);
                                }
                            }
                        });

                System.out.println("## " + repo.getFullName());
                checks.get(repo.getFullName()).stream().forEach(check -> {
                        System.out.println("- " + check.summary());
                });

            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean checkFile(GHRepository repo, GHRepository globalRepo, String checkName, String description, String fileName, Kind kind) {
        return checkFile(repo, globalRepo, checkName, description, fileName, kind, null);
    }

    /**
     *
     * check if file present and will add check to record if found or not. Optionally have lambada to do additional checks on the content of the file.
     *
     * @param repo the repo to check
     * @param globalRepo glboalrepo to also check in case the file is not found in the repo
     * @param checkName name of the check
     * @param description longer description
     * @param fileName name of the file to check without suffix (suffixes are tried from markupFormats)
     * @param kind SHOULD or MUST to know if should be warning or error.
     * @param contentCheck optional lambda/method to check the content of the file. The lambda should add its own checks.
     * @return true if file found, false if not found. can still have content errors
     */
    private boolean checkFile(GHRepository repo, GHRepository globalRepo, String checkName, String description, String fileName, Kind kind, BiConsumer<GHContent, String> contentCheck) {
        log.fine(checkName + " check");

        GHContent content = locateMarkupContents(repo, fileName);

        if (content == null) {
            if (filesInGithubDir.contains(fileName)) {
                content = locateMarkupContents(repo, ".github/" + fileName);
            }

            if(content == null && globalRepo != null) {
                content = locateMarkupContents(globalRepo, fileName);
            }
        }

        boolean ok = content != null;

        addCheck(repo, new check(checkName, description, repo, kind, ok, content));

        if (ok && contentCheck != null) {
            contentCheck.accept(content, readContent(content));
        }

        return ok;
    }

    private void addCheck(GHRepository repo, check check) {
        checks.computeIfAbsent(repo.getFullName(), k -> new ArrayList<>()).add(check);
        log.fine(check.summary());
    }

    /* looks for the path with markup type suffices (.adoc, .md etc.) then if not found look for it in lower case. */
    private GHContent locateMarkupContents(GHRepository repo, String path) {

        GHContent normal = locateExactMarkupContents(repo, path);

        if(normal!=null) {
            return normal;
        } else {
            // hibernate uses dco.txt rather than DCO.TXT
            return locateExactMarkupContents(repo, path.toLowerCase());
        }

    }
    private GHContent locateExactMarkupContents(GHRepository repo, String path) {
        for (String format : markupFormats) {
            GHContent content = locateContent(repo, path+format);
            if(content != null) {
                return content;
            }
        }
        return null;
    }

    /**get GHContent or null if not found */
    private GHContent locateContent(GHRepository repo, String path) {
            try {
                return repo.getFileContent(path);
            } catch (GHFileNotFoundException e) {
                return null;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
    }

    private String readContent(GHContent content) {
        try {
            return new String(content.read().readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}