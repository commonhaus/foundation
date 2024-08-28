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

- Early dissent is encouraged to facilitate productive dialog.
- Every member, regardless of their role, has an equal opportunity to contribute.

## Proposal Development and Review Process

Proposals typically begin as GitHub Discussions for collaborative refinement.

> [!NOTE]
> [Architecture Decision Definition of Done][ADD] or [Y-statements][y-statements] can be useful for framing proposals or summarizing outcomes, though they are not mandatory.

[ADD]: https://www.ozimmer.ch/practices/2020/05/22/ADDefinitionOfDone.html
[y-statements]: https://medium.com/olzzio/y-statements-10eb07b5a177

### Sense Vote

Members indicate their level of comfort or agreement with a proposal using *reactions*.

Sense votes typically use the following reactions, though alternatives may be suggested for specific proposals:

- **👍 (:+1:):** Indicates strong support or agreement with the proposal.
- **👀 (:eyes:):** Suggests acceptance or willingness to go along with the proposal, even if it's not the preferred choice.
- **👎 (:-1:):** Signifies discomfort or disagreement with the proposal, requiring further discussion.

Discussion of the proposal should occur in comments on the PR or GitHub Discussion. If conversations happen elsewhere (in chat, for example), a summary of the conversation should be added as a comment if the discussion helped clarify the proposal or resolve a concern.

If significant revisions are necessary, the current proposal should be closed as outdated and replaced with a new proposal.
The new proposal has revised content to maintain focus on the most current version and simplify navigation for members.

In all instances, efforts should be made to [address concerns and objections](#handling-unresolved-issues) constructively.

### Moving forward

A proposal moves forward if it receives sufficient support and meets applicable participation thresholds defined in the [CFC/EGC voting rules][cfc-sv].

[cfc-sv]: ./4-cf-council.md#voting

### Handling Unresolved Issues

If consensus cannot be reached:

- A member of the CFC will arrange a meeting with all relevant parties to discuss the proposal.
- The meeting will use a structured, time-boxed format to allow the proposal and objections to be presented and discussed.
- The meeting will conclude with a vote, subject to [voting rules][cfc-sv], that will decide whether to proceed despite unresolved objections.
- The outcome will be documented in the discussion thread and the proposal will be marked as resolved.

## Elections

CF uses an electronic, asychronous, preference-based voting system through [Elekto](https://elekto.dev/docs/).

- [Voting Process](https://elekto.dev/docs/voting/)
- [Election Administration](https://elekto.dev/docs/administration/)
