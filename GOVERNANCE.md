---
status: draft
---
# Commonhaus Foundation Project Governance

Welcome to the governance documentation for the Commonhaus Foundation (CF). 

This document is **Section 2** of [CF Bylaws][bylaws], outlines the structures, roles, and processes that guide our collaborative and open-source community. It's designed to ensure clarity, fairness, and inclusivity in our decision-making and community interactions.

- [2.1 CF Membership and Roles](#21-cf-membership-and-roles)
  - [2.1.1 Project Membership](#211-project-membership)
  - [2.1.2 Project Leaders and Code Owners](#212-project-leaders-and-code-owners)
  - [2.1.3 General Members](#213-general-members)
  - [2.1.4 Membership Termination and Resignation](#214-membership-termination-and-resignation)
- [2.2 CF Council](#22-cf-council)
  - [2.2.1 Composition and Membership](#221-composition-and-membership)
  - [2.2.2 Tenure and Transition](#222-tenure-and-transition)
  - [2.2.3 Apportioning Duties Among Council Members](#223-apportioning-duties-among-council-members)
- [2.3. CF Advisory Board](#23-cf-advisory-board)
- [2.4 Decision Making and Voting](#24-decision-making-and-voting)
  - [2.4.1 General Decision Making](#241-general-decision-making)
  - [2.4.2 Elections](#242-elections)
- [2.5 Record Keeping](#25-record-keeping)
- [2.6 Code of Conduct](#26-code-of-conduct)
- [2.7 Trademark Policy](#27-trademark-policy)
- [2.8 Contributing](#28-contributing)

[bylaws]: ./bylaws/README.md
[coc]: ./CODE_OF_CONDUCT.md
[coc-reports]: ./CODE_OF_CONDUCT.md#handling-reports-and-escalations
[coi-policy]: ./bylaws/conflict-of-interest.md
[Trademark Policy]: ./bylaws/trademark-policy.md
[MEMBERS.yaml]: https://github.com/commonhaus/foundation-draft/blob/main/MEMBERS.yaml
[ip-policy]: ./bylaws/ip-policy.md
[records]: ./bylaws/legal-compliance.md#record-keeping
[contrib]: ./CONTRIBUTING.md

## 2.1 CF Membership and Roles

### 2.1.1 Project Membership

We evaluate projects based on ownership, activity, usage, and originality. To be considered, projects must:

- Be novel or a substantial fork of an existing project.
- Show recent activity and/or significant usage as measured by downloads from a central package repository (Maven Central, GitHub Packages, etc.).
- Comply with the [CF IP Policy][ip-policy].

Project Leaders can submit an application for their project to join the Commonhaus Foundation.
*TBD: Link to process for submitting a project to join*

### 2.1.2 Project Leaders and Code Owners

Project Leaders and Code Owners play a crucial role in steering CF projects, shaping the foundation's future. Responsibilities include:

- Holding write permissions in project repositories (managed through the `CODEOWNERS` file).
- Full voting rights and automatic eligibility for Council positions.

Further Reading: [About code owners](https://docs.github.com/en/repositories/managing-your-repositorys-settings-and-features/customizing-your-repository/about-code-owners)

### 2.1.3 General Members

General membership is open to anyone dedicated to our mission. Active members, contributing to CF or its projects within the past year, gain voting rights after three months. Contributions include:

- Repository contributions: code, documentation, design work.
- Community management, advocacy, or forum participation.
- Active participation in elections.

*TBD: Criteria and link to application form for individual members*

### 2.1.4 Membership Termination and Resignation

- **Voluntary Resignation:** Members can resign by notifying the Council through a designated channel (e.g., email/form).
- **Revocation of Membership:** The Council may dismiss members for behavior inconsistent with CF's mission or values. This process includes a review and an opportunity for the member to respond.

## 2.2 CF Council

The CF Council (CFC) is the governing body of the CF, responsible for overseeing financial allocations, satisfying fiscal responsibilities, and setting future plans that serve the collective interests of the community.
Council members must adhere to our [Conflict of Interest Policy][coi-policy] to ensure that personal or professional interests do not unduly influence their duties within the CF.

The Council convenes regularly and holds an Annual General Meeting (AGM) to review the annual report, financial status, and set future plans.
Special general meetings may be initiated with a defined agenda if proposed by at least a third of the Council members or by a significant fraction of the general members.

Agendas and schedules for these gatherings will be released in advance.
Meetings open to the broader public are accessible to all members and interested observers.
Summaries of key decisions and discussions will be shared with the community to ensure that all members are informed and can provide feedback.

- **Concerns and Actions**: Members can voice concerns or propose actions by emailing `council@mailing-list` (TODO: or initiating a GitHub discussion, etc.).

### 2.2.1 Composition and Membership

The Council is composed of elected members who are recognized for their individual contributions and expertise in the open-source community. While members may be affiliated with various organizations, they are expected to prioritize the interests of the CF community in their decisions and actions.

Council members are required to disclose any potential conflicts of interest, especially those related to their employment. This disclosure ensures transparency and helps maintain the integrity of the Council's decisions.

The selection process for Council members aims to reflect the diversity and breadth of the open-source community. We strive for a balanced representation that brings together a wide range of perspectives and skills.

- The Council has a minimum of 3 elected members: 1 [Council Chairperson](#2231-chairperson-of-the-council) and at least 2 members-at-large.
- One-third of Council members must be direct representatives from our projects, maintaining a strong voice for project needs and perspectives. 
- **Eligibility**: Any member that has been active within the CF community for at least six months can run or be nominated.
- **Elections**: Conducted annually, with every member entitled to one vote.

### 2.2.2 Tenure and Transition

For continuity and stability, elections for Council members (including the Council Chair) are staggered.
In each election cycle, no more than two-thirds of Council seats will be up for reelection.
Upon completing their tenure, CFC members may seek re-election to extend their service, or step down.

#### 2.2.2.1 Resignation and Removal of Council Members

- Council members wishing to resign must formally notify the Chairperson or the Council, with immediate cessation of their responsibilities and rights as Council members upon resignation.
- A Council member may be removed for misconduct, failure to perform duties, or consistent non-participation.
  The removal process includes a fair review, an opportunity for the member to respond, and a decision made through a vote by the remaining Council members or a general membership vote.

In the event of a vacancy, the Council will temporarily assign the duties of the vacated position to other members. A special election will then be held to fill the vacant council seat.

### 2.2.3 Apportioning Duties Among Council Members

The Council will decide the distribution of specific roles and responsibilities, such as Secretary and Treasurer duties, among its members. This ensures effective governance and leverages the strengths and interests of each Council member.

- The **Secretary** is responsible for maintaining records, overseeing official correspondence, and managing organizational documentation.
- The **Treasurer** oversees the financial affairs of the Foundation, including budgeting and financial reporting. In cases where a Fiscal Host or Agent is used, the Treasurer will coordinate with them for accounting and financial management.

#### 2.2.3.1 Chairperson of the Council

The Chairperson orchestrates regular CFC meetings, ensuring leadership in discussions is evenly spread. They also oversee the proper handling of [Code of Conduct reports and escalations][coc-reports].

#### 2.2.3.2 Committees and Delegation

The Council may establish committees to manage specific areas of responsibility. While delegating tasks to these committees, the Council retains ultimate oversight and decision-making authority.

## 2.3. CF Advisory Board

The CF Advisory Board comprises representatives from organizations and companies that support the CF. It acts as a bridge between the CF and the broader tech industry, providing diverse insights and strategic guidance to inform the Council's decisions.

The Advisory Board does not have decision-making authority but plays a crucial role in shaping the foundation's direction through advice and industry perspectives. The structure is designed to ensure robust representation and dialogue:

- **Representation**: Each Advisory Board member organization may appoint up to two representatives. This dual representation maximizes the likelihood of attendance and ensures a breadth of perspectives, mirroring the Gnome Foundation's approach to encourage diverse viewpoints from vendors.

- **Diversity of Insight**: Where possible, organizations are encouraged to appoint representatives who bring different types of expertise or experience, such as a managerial and a technical perspective, to contribute to a well-rounded advisory process.

- **Meetings**: The Advisory Board convenes with the CFC semi-annually, providing a platform for collaborative review and feedback on the CF's direction, initiatives, and impact on the industry.

- **Communication**: Representatives are expected to actively communicate the needs and concerns of their organizations, while also conveying the goals and achievements of the CF to their respective constituencies.

- **Engagement**: The Advisory Board's engagement is pivotal in aligning the CF's strategic initiatives with industry needs, ensuring that the foundation remains relevant and responsive to the ecosystem it serves.

An up-to-date list of Advisory Board representatives will be maintained in the `advisory-board` [MEMBERS.yaml][] attribute, fostering transparency and openness.

## 2.4 Decision Making and Voting

### 2.4.1 General Decision Making

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

#### 2.4.1.1 Seeking consensus in meetings

When an agenda item has appeared to reach a consensus the moderator will ask "Does anyone object?" as a final call for dissent from the consensus.

If an agenda item cannot reach a consensus a CFC member can call for either a closing vote or a vote to table the issue to the next meeting.
The call for a vote must be seconded by a majority of the CFC or else the discussion will continue.

For all votes, a simple majority of all Voting CFC members for, or against, the issue wins.
A Voting CFC member may choose to participate in any vote through abstention.

#### 2.4.1.2 Seeking consensus on mailing lists

When seeking consensus in mailing lists, it is important to leave enough time (at least 72-hours) for everyone to read the email and respond with opinions and objections.

CFC members should consider the following when drafting consensus-seeking email:

- Use a leading indicator (`[DISCUSS]` or an emoji like 🗳️) to mark consensus-seeking email
- Use a clear subject line that summarizes the topic
- If there is a time constraint to the decision, include it in the subject.

It is customary for the initiator to post a summary once it appears that consensus has been reached, to ensure that their understanding is accurate.

### 2.4.2 Elections

For Council elections, CF adopts a preference-based voting system facilitated by [Elekto](https://elekto.dev/).
This approach allows members to rank candidates in order of preference, ensuring the elected representatives are the most broadly supported by the community, rather than simply the most popular.

- [Voting using Elekto](https://elekto.dev/docs/voting/)
- [Administering an Election using Elekto](https://elekto.dev/docs/administration/)

## 2.5 Record Keeping

The CF is committed to maintaining thorough and accessible records of its activities as documented in [Record Keeping][records].

## 2.6 Code of Conduct

All participants in the project are expected to adhere to the project's [Code of Conduct][coc]. Please ensure you are familiar with its guidelines and expectations, as it's essential for maintaining a positive and collaborative environment.

## 2.7 Trademark Policy

CF logos, icons, and domain names are protected by trademark rights. Usage of these trademarks must adhere to our [Trademark Policy][].

## 2.8 Contributing

We welcome all forms of contribution, from code improvements to documentation and design. For details on how to contribute and the process your contributions will follow, please read our [Contributing Guidelines][contrib].

