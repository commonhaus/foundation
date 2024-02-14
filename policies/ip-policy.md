---
status: draft
---
# Intellectual Property Policy

The Commonhaus Foundation (CF) supports individual projects (each, a “Project” and together, the “Projects”).

All new inbound code contributions to individual Projects are made pursuant to the license applicable to each such Project (with respect to each Project, the “Project Code License”).

## License Selection and Usage

> [!TIP]
> CF supports a wide range of open-source licenses to ensure projects can choose what's best for them. We’ll check these licenses, including for any software they rely on, to make sure everything works together without legal issues.

CF projects can use any [open-source license approved by the Open Source Initiative](https://opensource.org/licenses/) (OSI).

The CF will review Project Code Licenses and the licenses of its dependencies to ensure that all components of the project are compatible under the chosen license and do not introduce legal conflicts or restrictions that could affect the Project's or Foundation's operation.

The CF is most familiar with the following four licenses, which can simplify the review process:

- [The Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0)
- [The MIT License][MIT]
- [The 2-Clause BSD License](https://opensource.org/license/bsd-2-clause/)
- [The 3-Clause BSD License](https://opensource.org/license/bsd-3-clause/)

### Documentation and Media Files

> [!TIP]
> Projects can pick appropriate licenses, including non-commercial Creative Commons licenses, for their non-code content like documentation and images.

A CF Project can define a "Project Documentation License" to cover all documentation, image, and audiovisual files (e.g., .txt., .rtf, .doc, .pdf, .jpg, .tif, .mp3, .wav, and some .html files) (including without limitation code that is intended as sample code if included in a documentation file).
The Project Documentation License can be any OSI-approved license or a non-commercial Creative Commons license (like [CC BY 4.0](http://creativecommons.org/licenses/by/4.0/)).

## Contributions

> [!TIP]
> All code contributions must align with the project’s license and come with a promise (DCO) from contributors that they have the right to contribute under these terms.

All new code contributions to any Project shall be made under the Project Code License accompanied by a Developers Certificate of Origin (DCO, available at <http://developercertificate.org/>), which will bind the individual contributor and, if applicable, their employer to the Project Code License.

The technical governing body of each Project may provide for additional requirements with respect to contributions. For example, a Project may require that new code contributions to a Project be accompanied by a signed Contributor License Agreement (CLA).

### Developers Certificate of Origin (DCO) or Contributor License Agreement (CLA)?

The DCO and CLA differ in their approach to intellectual property rights.

- A DCO is a lightweight method for contributors to certify they have the rights to submit their work, typically requiring a signed statement within the commit message.
- A CLA is a more formal agreement where contributors grant the project rights to use their contributions, often including a detailed legal framework around contributions.

CLAs can provide projects with more legal protection, while DCOs offer a simpler, less legally invasive way for developers to contribute.

## Intellectual Property Rights

> [!TIP]
> Joining CF doesn’t grant us any rights to your IP beyond what’s needed for project contributions. Your IP remains yours.

General membership in the Commonhaus Foundation does not imply any license to a member's intellectual property.
Members grant no license to their intellectual property to the Commonhaus Foundation, except under the following conditions:
(a) their commitment to abide by this Policy, the applicable Project Code License(s), and the applicable Project Documentation License(s) for their contributions to any Project; and
(b) any applicable contributor license agreement.

## Implementing the IP Policy

Project leaders should ensure compliance with this policy and provide clear guidance to contributors.

- Use the [standard website footer][] for member project websites to reference this and other CF Policies,
- Ensure that contribution guidelines are updated and accessible, and
- (optionally) Maintain accurate copyright statements.

## Collaborations and External Projects

> [!TIP]
> Always check and follow the license rules of any external open-source projects we interact with.
> If an upstream project uses a non-OSI license, the [CF Council (CFC)][cfc] can approve exceptions if needed.

Engaging with external open-source projects requires awareness and respect for their licensing terms to ensure our contributions are legally compatible. This ensures our projects can seamlessly integrate or collaborate with these external projects without infringing on their or our license terms.

When collaborating with external open-source projects ("Upstream Projects") conform to all license requirements of the Upstream Projects, including dependencies, leveraged by the Project. If an alternative inbound or outbound license is required for compliance with the license for an Upstream Project or is otherwise required to achieve the Commonhaus Foundation’s, or an individual Project’s, objectives, the CFC may approve the use of an alternative license for inbound or outbound contributions on an exception basis.

### Copyright Statements

Using copyright statments in source code is a practice with mixed adoption.

If your project wants to add copyrights to source code, we recommend one of the following:

- Copyright &lt;year of file creation> The XYZ Authors.
- Copyright &lt;year of file creation> Contributors to the XYZ project.
- Copyright &lt;year of file creation> The original author or authors.

Updating the year after the file is created is unnecessary.

> [!NOTE]
> Do not alter existing copyright lines unless you have the right to do so.

## Obtaining an Exemption

If this IP Policy doesn't cater to your project's needs, request special permission from the CFC.

To request an exemption, please begin by contacting the CFC through the [`council` mailing list][CONTACTS.yaml]. Further instructions will be provided based on the specifics of your request.

## Review of Policy

This policy will be reviewed periodically to ensure it remains relevant and effective. Amendments or changes to this policy will follow the [amendment process][].

For questions or clarifications on this policy, please contact the [`legal` mailing list][CONTACTS.yaml].

[CONTACTS.yaml]: https://github.com/commonhaus/foundation-draft/blob/main/CONTACTS.yaml
[MIT]: https://opensource.org/license/mit/ "The MIT License"
[amendment process]: ../bylaws/8-amendments.md
[cfc]: ../bylaws/3-cf-council.md "CF Council"
[standard website footer]: https://github.com/commonhaus/foundation-draft/blob/main/templates/website-footer.md "CF website footers"
