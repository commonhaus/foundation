---
status: draft
---
# Commonhaus Foundation Project Governance

The Commonhaus Foundation  (CF) is a collaborative space where contributors come together.
While working in groups can present challenges, this document lays out the guidelines that foster unity and progress.

[coc-reports]: CODE_OF_CONDUCT.md#handling-reports-and-escalations
[coi-policy]: governance/conflict-of-interest.md
[MEMBERS.yaml]: https://github.com/commonhaus/foundation-draft/blob/main/MEMBERS.yaml

## Table of Contents

- [Project Roles](#project-roles)
- [Commonhaus Foundation Council](#commonhaus-foundation-council)
- [Decision Making and Voting](#decision-making-and-voting)
- [Code of Conduct](#code-of-conduct)
- [Trademark Policy](#trademark-policy)
- [Contributing](#contributing)

## Project Roles

**Members:** CF Membership is open to anyone dedicated to our mission, whether through code, documentation, design, community management, or advocacy. Members are empowered to vote in Council elections, initiate proposals, and if inclined, pursue a Council seat.

- Membership application is straightforward, promoting inclusivity for all who align with our mission. Specifics of the process and member expectations will be provided.
- Active members, defined as those contributing to CF or its projects within the past year, are eligible for voting after three months of membership. Contributions include code, documentation, forum participation, issue resolution, and election involvement.

**Code Owners:** The leaders and code owners for CF projects are members. Their project roles carry the responsibility of steering project direction, which includes a stake in shaping the foundation's future. Project Leaders are automatically eligible for Council positions, ensuring our projects' voices are prominently represented.

- Access: They have write permissions to the repository.
- Reference: Managed through the repository's `CODEOWNERS` file.

Further Reading: [About code owners](https://docs.github.com/en/repositories/managing-your-repositorys-settings-and-features/customizing-your-repository/about-code-owners)

## Commonhaus Foundation Council

The Commonhaus Foundation Council (CFC) is the governing body of the CF, responsible for overseeing financial allocations, satisfying fiscal responsibilities, and setting future plans that serve the collective interests of the community.
Council members must adhere to our [Conflict of Interest Policy][coi-policy] to ensure that personal or professional interests do not unduly influence their duties within the CF.

The Council convenes regularly and holds an Annual General Meeting (AGM) to review the annual report, financial status, and set future plans.
Special general meetings may be initiated with a defined agenda if proposed by at least a third of the Council members or by a significant fraction of the general members.

Agendas and schedules for these gatherings will be released in advance.
Meetings open to the broader public are accessible to all members and interested observers.
Summaries of key decisions and discussions will be shared with the community to ensure that all members are informed and can provide feedback.

- **Concerns and Actions**: Members can voice concerns or propose actions by emailing `council@mailing-list` (TODO: or initiating a GitHub discussion, etc.).

### Composition and Membership

The Council has a minimum of 3 elected members: 1 [Council Chairperson](#chairperson-of-the-council) and at least 2 members-at-large.

At least one-third of Council members are direct representatives from our projects, maintaining a strong voice for project needs and perspectives. To avoid any single entity's undue influence, no more than one Council member may represent any given company or legal entity.

- **Eligibility**: Any member that has been active within the CF community for at least six months can run or be nominated.
- **Elections**: Conducted annually, with every member entitled to one vote.

### Tenure and Transition

For continuity and stability, elections for Council members (including the Council Chair) are staggered.
In each election cycle, no more than two-thirds of Council seats will be up for reelection.
Upon completing their tenure, CFC members may seek re-election to extend their service, or step down.

#### Chairperson of the Council

The Chairperson orchestrates regular CFC meetings, ensuring leadership in discussions is evenly spread. They also oversee the proper handling of [Code of Conduct reports and escalations][coc-reports].

## Commonhaus Foundation Advisory Board

The CF Advisory Board comprises representatives from organizations and companies that support the Commonhaus Foundation. It acts as a bridge between the CF and the broader tech industry, providing diverse insights and strategic guidance to inform the Council's decisions.

The Advisory Board does not have decision-making authority but plays a crucial role in shaping the foundation's direction through advice and industry perspectives. The structure is designed to ensure robust representation and dialogue:

- **Representation**: Each Advisory Board member organization may appoint up to two representatives. This dual representation maximizes the likelihood of attendance and ensures a breadth of perspectives, mirroring the Gnome Foundation's approach to encourage diverse viewpoints from vendors.

- **Diversity of Insight**: Where possible, organizations are encouraged to appoint representatives who bring different types of expertise or experience, such as a managerial and a technical perspective, to contribute to a well-rounded advisory process.

- **Meetings**: The Advisory Board convenes with the CF Council semi-annually, providing a platform for collaborative review and feedback on the CF's direction, initiatives, and impact on the industry.

- **Communication**: Representatives are expected to actively communicate the needs and concerns of their organizations, while also conveying the goals and achievements of the CF to their respective constituencies.

- **Engagement**: The Advisory Board's engagement is pivotal in aligning the CF's strategic initiatives with industry needs, ensuring that the foundation remains relevant and responsive to the ecosystem it serves.

An up-to-date list of Advisory Board representatives will be maintained in the `advisory-board` [MEMBERS.yaml][] attribute, fostering transparency and openness.

## Decision Making and Voting

### General Decision Making

True to Open Source roots, the CF ensures every member's voice is heard through the Lazy Consensus decision-making model.

To quote the [Consensus definition](https://community.apache.org/committers/decisionMaking.html) from the Apache Foundation:

> Consensus
>
> The word “consensus” is a bit ambiguous in English, and so can lead to some misunderstandings of intent when we use it in the context of project decisions. Consensus does not mean that everyone agrees on all details. Rather, it means that the project, as a whole, has arrived a decision, or at least a compromise, that everyone can live with.
>
> Lazy Consensus
>
> Lazy consensus is the first, and possibly the most important, consensus-building tool we have. Essentially lazy consensus means that you don’t need to get explicit approval to proceed, but you need to be prepared to listen if someone objects.

When seeking consensus, members should express objections or dissent as early as possible to ensure there is time to discuss and address objections.

Every member, regardless of their role or contribution level, has an equal voice and is entitled to one vote in all decisions.

#### Seeking consensus in meetings

When an agenda item has appeared to reach a consensus the moderator will ask "Does anyone object?" as a final call for dissent from the consensus.

If an agenda item cannot reach a consensus a CFC member can call for either a closing vote or a vote to table the issue to the next meeting.
The call for a vote must be seconded by a majority of the CFC or else the discussion will continue.

For all votes, a simple majority of all Voting CFC members for, or against, the issue wins.
A Voting CFC member may choose to participate in any vote through abstention.

#### Seeking consensus on mailing lists

When seeking consensus in mailing lists, it is important to leave enough time (at least 72-hours) for everyone to read the email and respond with opinions and objections.

CFC members should consider the following when drafting consensus-seeking email:
  - Use a leading indicator (`[DISCUSS]` or an emoji like 🗳️) to mark consensus-seeking email
  - Use a clear subject line that summarizes the topic
  - If there is a time constraint to the decision, include it in the subject.

It is customary for the initiator to post a summary once it appears that consensus has been reached, to ensure that their understanding is accurate.

### Elections

For Council elections, CF adopts a preference-based voting system facilitated by [Elekto](https://elekto.dev/).
This approach allows members to rank candidates in order of preference, ensuring the elected representatives are the most broadly supported by the community, rather than simply the most popular.

- [Voting using Elekto](https://elekto.dev/docs/voting/)
- [Administering an Election using Elekto](https://elekto.dev/docs/administration/)

## Code of Conduct

All participants in the project are expected to adhere to the project's [Code of Conduct](CODE_OF_CONDUCT.md). Please ensure you are familiar with its guidelines and expectations, as it's essential for maintaining a positive and collaborative environment.

## Trademark Policy

Commonhouse Foundation logos, icons, and domain names are protected by trademark rights. Usage of these trademarks must adhere to our [Trademark Policy](governance/trademark-policy.md).

## Contributing

We welcome all forms of contribution, from code improvements to documentation and design. For details on how to contribute and the process your contributions will follow, please read our [Contributing Guidelines](CONTRIBUTING.md).

