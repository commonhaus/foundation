---
status: draft
weight: 5
---
# Decision Making, Voting, and Elections

This section describes the decision-making process, voting, and elections for the CF.

- [General Decision Making](#general-decision-making)
    - [Expectations for Consensus Decision Making](#expectations-for-consensus-decision-making)
- [Proposal Development and Review Process](#proposal-development-and-review-process)
    - [Sense Vote](#sense-vote)
    - [Moving forward](#moving-forward)
    - [Handling Unresolved Issues](#handling-unresolved-issues)
- [Elections](#elections)

## General Decision Making

In general, the CF adopts the Lazy Consensus model, as defined by the [Apache Foundation](https://community.apache.org/committers/decisionMaking.html), combined with [Martha's Rules](https://digitalcommons.unl.edu/cgi/viewcontent.cgi?article=1825&context=sociologyfacpub) adapted for our context.

### Expectations for Consensus Decision Making

Consensus is about finding a workable compromise:

- Dissent should be voiced early for constructive discussion.
- Every member, irrespective of role, is entitled to an equal voice and vote.

## Proposal Development and Review Process

Proposals typically begin as GitHub Discussions for collaborative refinement.

> [!NOTE]
> [Architecture Decision Definition of Done][ADD] or [Y-statements][y-statements] can be useful for framing proposals or summarizing outcomes, though they are not mandatory.

[ADD]: https://www.ozimmer.ch/practices/2020/05/22/ADDefinitionOfDone.html
[y-statements]: https://medium.com/olzzio/y-statements-10eb07b5a177

### Sense Vote

- **👍 (:+1:):** Indicates strong support or agreement with the proposal.
- **👀 (:eyes:):** Suggests acceptance or willingness to go along with the proposal, even if it's not the preferred choice.
- **👎 (:-1:):** Signifies discomfort or disagreement with the proposal, requiring further discussion.

Members are expected to *react* to express their level of comfort or agreement with a proposal,
ensuring a comprehensive understanding of the community's stance.

Discussion of the proposal should occur in comments on the PR or GitHub Discussion. If conversations happen elsewhere (in chat, for example), a summary of the conversation should be added as a comment if the discussion helped clarify the proposal or resolve a concern.

If significant changes are needed, the current Discussion/PR should be closed as outdated, and replaced with a revised proposal.

### Moving forward

In the absence of discomfort or disagreement, the proposal can move forward.

For significant matters, like those requiring a CF Council [Supermajority Vote][cfc-sv], a formal voting process should be followed.

[cfc-sv]: ./3-cf-council.md#matters-requiring-supermajority-vote

### Handling Unresolved Issues

In cases where consensus is not reached:

- Arrange a meeting with all relevant parties to discuss the proposal.
- Use a structured, time-boxed format to address objections.
- Conclude with a vote to decide whether to move forward despite unresolved objections.

Record the meeting's outcome in the original Discussion/PR to maintain transparency and traceability.

## Elections

CF uses an electronic, asychronous, preference-based voting system through [Elekto](https://elekto.dev/docs/).

- [Voting Process](https://elekto.dev/docs/voting/)
- [Election Administration](https://elekto.dev/docs/administration/)
