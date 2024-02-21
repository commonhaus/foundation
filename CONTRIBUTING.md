---
status: draft
---
# CONTRIBUTING to the Commonhaus Foundation Repository

Thanks for considering a contribution to the Commonhaus Foundation (CF) repository. We value every contribution, whether it's a document edit or a major revision.

## Quick Start

- **Discuss First**: For significant changes, start a GitHub discussion to establish consensus[^1]. For smaller edits, you can directly create a pull request (PR).

- **Fork and Edit**: Fork the repository to your account, make your changes there.

## Making Changes

- **Be Clear**: Ensure your contributions are direct and easy to understand.
- **Remain Consistent**: Follow the existing format and structure of the documents.
    - **markdownlint**: The build process (shown below) will lint Markdown documents using `markdownlint`. The markdownlint [VSCode extension](https://marketplace.visualstudio.com/items?itemName=DavidAnson.vscode-markdownlint) or [IntelliJ plugin](https://plugins.jetbrains.com/plugin/20851-markdownlint) can help you stick to and apply these rules while you're making changes.
    - **EditorConfig**: Similarly, the EditorConfig [VSCode extension](https://marketplace.visualstudio.com/items?itemName=EditorConfig.EditorConfig) and [IntelliJ plugin](https://plugins.jetbrains.com/plugin/7294-editorconfig) help with whitespace handling in a minimally invasive way.
- **Cross-reference Discussions and PRs**: Link back to relevant discussions or other PRs in your commit messages.
- **Building documents (optional)**: When you create a PR, a build will run that will verify internal and external links, and will generate and attach pdfs to the build.
    - **Test your changes**: To test your changes locally, , use `npm ci` to install node dependencies, and use `npm run test` or `npm run tv` (verbose) to test/lint your content. These tasks run the following:
        - **Markdown linting**: `npm run lint`
        - **YAML syntax validation**: `npm run yaml`
        - **Markdown link checks**: `npm run links` or `npm run linksv` (verbose)
    - **Build pdfs**: If you use docker or podman, run `./.github/docker-build-pdf.sh` to use a pre-configured docker image to convert markdown bylaws and policies to pdf.

## Flow of information

Markdown content is converted into pdfs and is published on the website.

- **Mailing list references**: Mailing list reference syntax ([`legal` mailing list][CONTACTS.yaml]) matters.
    - The `code` value shuld match a `mailing-list` attribute in [CONTACTS.yaml][].
    - This syntax is changed into an email address in the pdf.
    - In the case of user action, use the full phrase: "send an email to the [`legal` mailing list][CONTACTS.yaml]", so that it can be converted into a submission form reference on the website.

[CONTACTS.yaml]: ./CONTACTS.yaml

## Submitting Changes

- **Create a PR**: Submit your changes via a pull request from your fork. Check the automatic build for link or pdf generation errors.
- **Wait for Review**: A code owner will review your PR and might ask for changes or clarifications.
- **Respond to Feedback**: Address any feedback to get your PR approved and merged.

## Legal Review

Some changes may need a legal review which may take some time.

## Thank You

Every contribution matters. For any questions, feel free to reach out.

Happy contributing!

[^1]: See [decision making](./bylaws/5-decision-making.md#general-decision-making) for an overview of how we approach consensus building.
