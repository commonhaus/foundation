# Commonhaus Templates

We don't want project onboarding to be a burden, but we have a few requirements to ensure transparency.

- `CODE_OF_CONDUCT.md`[^1]: Projects must define a Code of Conduct. They can use the foundation's [Code of Conduct policy][coc_policy], or define their own. See [CODE_OF_CONDUCT.md][COC] and the [Project CODE_OF_CONDUCT.md template][COC_TPL] for examples.
- `GOVERNANCE.md`[^1]: Projects must explain how decisions are made. See [Governance][GOV] and the [Project GOVERNANCE.md template][GOV_TPL] for examples.
- `CONTRIBUTING.md`[^1]: Projects must explain how to make contributions. This file must include references to [required contributor agreements](#contributor-agreements). See [CONTRIBUTING.md][CONTRIB] for an example, or [Wrangling Web Contributions: How to Build a CONTRIBUTING.md][mozilla] for other ideas.

[^1]: This file can also be defined in the `.github` repository of an organization. Other file extensions and markup syntax is fine (.adoc, .txt, .rst) provided the source is human readable.

We further recommend:

- **Code Ownership:** Maintain a list of individuals or groups with the authority to review and merge contributions (`CODEOWNERS` or `.github/CODEOWNERS`).
    - [About Code Owners (GitHub)](https://docs.github.com/en/repositories/managing-your-repositorys-settings-and-features/customizing-your-repository/about-code-owners).
    - [Code Owners (GitLab)](https://docs.gitlab.com/ee/user/project/codeowners/)

[coc_policy]: ../policies/code-of-conduct.md
[COC]: ../CODE_OF_CONDUCT.md
[COC_TPL]: ../templates/CODE_OF_CONDUCT.md
[GOV]: ../GOVERNANCE.md
[GOV_TPL]: ../templates/GOVERNANCE.md
[CONTRIB]: ../CONTRIBUTING.md
[mozilla]: https://mozillascience.github.io/working-open-workshop/contributing/

## Contributor Agreements

Explanation of the difference between DCO and CLA: <https://www.consortiuminfo.org/open-source/all-about-clas-and-dcos/>

- If your project uses a DCO, include that in your contribution guide. [For example](https://github.com/hibernate/hibernate-orm/blob/main/CONTRIBUTING.md#legal):

    ```md
    All contributions are subject to the [Developer Certificate of Origin (DCO)](http://developercertificate.org/).
    The DCO text is also included verbatim in the [dco.txt](./dco.txt) file in the root directory of this repository.
    ```

    Your project should also have a **dco.txt** (case-insensitive) file containing content from <http://developercertificate.org/>

- If your project uses a CLA, include that requirement in the contribution guide. [For example](https://github.com/jreleaser/jreleaser/blob/main/CONTRIBUTING.adoc#contributor-license-agreement):

    ```md
    Contributor License Agreement

    Contributions to My_Cool_Commonhaus_Project are protected by a CLA.
    Please read the document before making a contribution.
    You’ll be asked to digitally sign the document on your first contribution.
    Feel free to open a discussion topic if you have questions.
    ```

## Policy Panda

This repository provides [some scripts](./panda/) to help with onboarding checks.
