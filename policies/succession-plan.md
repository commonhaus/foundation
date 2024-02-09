---
status: draft
---
# Continuity and Administrative Access Policy

The Commonhaus Foundation (CF) understands the impact of reliable libraries and frameworks on the broader tech community. Businesses run on applications built using these assets, and it is logical for them to do so. A retailer specializing in fish tanks shouldn’t need to understand low-level internet protocols. However, time brings change, and code owners, the nurturers of these invaluable tools, might pivot to new endeavors. In their absence, who ensures the continuity and security of these tools?

The CF aims to help projects address this vital concern.

This policy articulates the foundation's need for administrative access to project resources and the principles that guide this requirement.

## Policy Rationale

Administrative access empowers the CF to:

**Facilitate Smooth Transitions:** Provide continuity when ownership changes or after prolonged inactivity, ensuring that important libraries and frameworks can survive without expensive forks.

**CVE Remediation:** Act swiftly during security threats, especially if the primary code owners are unreachable.

**Artifact Updates:** Provide consistent access to updated project artifacts, even amidst maintainer hiatus.

The CF may intervene in certain situations, but we will exhaust every option to contact the current code owners before we do so.
For libraries under heavy use, we will encourage a "path of least disturbance" approach to provide stability for applications relying on these libraries.

## Scope of Access and Intervention

**Limited Interference:** Although the CF maintains administrative access, every option to contact the current code owners will be made before we do so.  Action will require a consensus of [X%] of the project's active contributors or CF Council members.

**Project Autonomy:** Projects need not join the CF GitHub organization. They're free to stay within their preferred GitHub (or Gitlab, BitBucket, etc.) organization and/or repository.

**Transparent Communication:** Any exercise of administrative privilege by the CF will be recorded and communicated to the project code owners.

## Commitment to Project Code Owners and the Community

The CF respects each project's chosen governance model. This policy complements, and does not supersede, those models. We recommend projects define:

- **Code Ownership:** Maintain a list of individuals or groups with the authority to review and merge contributions (`CODEOWNERS`).

- **Project governance:** Describe project decision-making processes and contact information in `GOVERNANCE.md`. The CF must have direct contact details (kept confidential) for emergencies or coordination in the case of an extended absence.

- **Contribution guidelines:** Describe project build, test, and packaging instructions in `CONTRIBUTING.md`. Include criteria for contributions, versioning practices, and preferences in the event of an ownership transition.

**Open Source**: Project code must be publically accessible on any public code-hosting platform (like GitHub or GitLab).

Further Reading:

- `CODEOWNERS`: [About code owners][owners], maintained in `CODEOWNERS` or `.github/CODEOWNERS`
- `CONTRIBUTING.md`: [Wrangling Web Contributions: How to Build a CONTRIBUTING.md][contrib]. See [CONTRIBUTING.md][].
- `GOVERNANCE.md`: See [GOVERNANCE.md][] and the [Project GOVERNANCE.md template][GOV-TPL] for examples.

[owners]: https://docs.github.com/en/repositories/managing-your-repositorys-settings-and-features/customizing-your-repository/about-code-owners
[contrib]: https://mozillascience.github.io/working-open-workshop/contributing/

## Review of Policy

This policy may undergo periodic reviews and updates to cater to the evolving needs of the CF and its projects. Project leaders and the community will be involved in and informed of any changes. Amendments or changes to this policy will follow the [amendment process][].

For questions or clarifications on this policy, please contact the [`legal` mailing list][CONTACTS.yaml].

[CONTACTS.yaml]: https://github.com/commonhaus/foundation-draft/blob/main/CONTACTS.yaml
[CONTRIBUTING.md]: https://github.com/commonhaus/foundation-draft/blob/main/CONTRIBUTING.md
[GOV-TPL]: https://github.com/commonhaus/foundation-draft/blob/main/templates/GOVERNANCE.md
[GOVERNANCE.md]: https://github.com/commonhaus/foundation-draft/blob/main/GOVERNANCE.md
[amendment process]: ../bylaws/8-amendments.md
