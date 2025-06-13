# Policy Panda 🐼

Policy Panda is a tool that evaluates GitHub organizations and repositories against Commonhaus Foundation policies and best practices. It generates reports about policy compliance, organization settings, and asset inventories.

Policy Panda is an essential part of the Commonhaus Foundation project onboarding process. By running this tool early in the onboarding journey, projects can identify and address any missing required files or governance practices before officially joining the foundation.

## Prerequisites

- [JBang](https://www.jbang.dev/) installed
- Java 21 or newer (JBang will install this if you don't already have it)
- GitHub credentials configured (see [Authentication](#authentication))

## Authentication

Policy Panda requires GitHub credentials in one of these forms:

- **Environment Variables**:
    - `GITHUB_OAUTH`: Your GitHub OAuth token
    - `GITHUB_JWT`: GitHub JWT token (alternative)

- **Property File**:
    - Create a `~/.github` file in your home directory with either:

        ```txt
        oauth=your_token_here
        ```

        or

        ```txt
        jwt=your_token_here
        ```

For more details, see [GitHub API Authentication](https://hub4j.github.io/github-api/#Authentication).

## Basic Usage

Run Policy Panda with:

```shell
jbang policypanda@commonhaus/foundation ORGANIZATION_NAME [options]
```

Where `ORGANIZATION_NAME` is the GitHub organization you want to analyze.

### Alternative Local Usage

If you have the code locally:

```shell
cd templates/panda
jbang PolicyPanda.java ORGANIZATION_NAME [options]
```

## Reports Generated

Policy Panda can generate several types of reports:

1. **Policy Compliance Report** (default) - Checks repositories against Commonhaus Foundation policies
2. **Asset Inventory Report** (optional) - Creates an inventory of GitHub organization assets
3. **Organization Settings Report** (optional) - Documents organization and repository settings

All reports are saved to the `reports` directory by default.

## Command Options

| Option | Description | Default |
|--------|-------------|---------|
| `-r, --repo-regex` | Regular expression to filter repositories | `".*"` (all repos) |
| `-w, --working-branch-patterns` | Regular expression for working branch names | `"main"` |
| `-t, --type` | Agreement type: `DCO` or `CLA` | `DCO` |
| `-o, --output-dir` | Output directory for reports | `"reports"` |
| `-p, --project-id` | Project ID to use in YAML reports | Organization name (lowercase) |
| `-s, --skip-policy-check` | Skip policy compliance check | `false` |
| `-i, --include-archived` | Included repositories that are archived | `false` |
| `-a, --init-asset-discovery` | Generate asset discovery document | `false` |
| `-g, --init-org-settings` | Generate organization settings report | `false` |
| `-v, --verbose` | Verbosity level (off, info, fine, finer, finest, all) | `"info"` |
| `-h, --help` | Show help message | |

## Examples

### Generate a Policy Compliance Report

```shell
# Check all repositories in the organization
jbang policypanda@commonhaus/foundation hibernate

# Check specific repositories using regex pattern
jbang policypanda@commonhaus/foundation hibernate -r "hibernate-orm|hibernate-validator"
```

### Generate an Asset Inventory Report

```shell
# Generate only asset inventory
jbang policypanda@commonhaus/foundation hibernate --skip-policy-check --init-asset-discovery

# Generate both policy report and asset inventory
jbang policypanda@commonhaus/foundation hibernate --init-asset-discovery
```

### Generate an Organization Settings Report

```shell
# Generate only organization settings report
jbang policypanda@commonhaus/foundation hibernate --skip-policy-check --init-org-settings

# Generate all reports
jbang policypanda@commonhaus/foundation hibernate --init-asset-discovery --init-org-settings
```

### Customize Output

```shell
# Change output directory
jbang policypanda@commonhaus/foundation hibernate -o "my-reports"

# Use custom project ID
jbang policypanda@commonhaus/foundation hibernate -p "hibernate-project"
```

## What Policy Panda Checks

Policy Panda evaluates repositories for:

- Governance files (GOVERNANCE.md)
- Code of Conduct (CODE_OF_CONDUCT.md)
- Contributing guidelines (CONTRIBUTING.md)
- Developer Certificate of Origin (DCO) or Contributor License Agreement (CLA)
- License file (LICENSE)
- CODEOWNERS configuration
- Branch protection settings
- Security measures

## Output Files

- **Policy Report**: `reports/policy-report-{organization}.md`
- **Asset Inventory**: `reports/scm-github-{organization}.yaml`
- **Organization Settings**: `reports/scm-settings-{organization}.yaml`

## Notes

- Policy Panda respects organization-level `.github` repositories for default community health files.
- The tool caches HTTP responses to reduce API calls to GitHub.
- For best results, ensure your GitHub token has sufficient permissions to read organization and repository data.
