# On-boarding checklist

This is an example of the process for Project on-boarding.

A significant part of this is an "asset inventory".
This inventory can be just as useful for the project as it is for the Foundation,
as it is a definition of "all the things".

As is stated in the agreement and terms, CF does not assume ownership or management of all the things.
Those boundaries are drawn together, and will evolve over time (by mutual agreement) as needs change.

> [!NOTE]
>
> - Steps marked 🏛️ are performed by the Foundation (CF)
> - Steps marked 👥 are perfomed by the Project team
> - Other steps are performed by whoever gets there first

Project Identifier (`PROJECTS.yaml`[^3]):

## Representative Validation

> [!TIP]
> GH Invitation is sent automatically when the PR to foundation repo containing updates to CONTACTS.yaml is merged

- Primary EGC rep in `CONTACTS.yaml`[^4]
    - [ ] 🏛️ Create issue for member on-boarding
    - [ ] 🏛️ Assign issue for member on-boarding (requires invitation accept)
    - [ ] 🏛️ After alias created: Add to egc google group
- (?) Secondary EGC rep in `CONTACTS.yaml`[^4]
    - [ ] 🏛️ Create issue for member on-boarding
    - [ ] 🏛️ Assign issue for member on-boarding (requires invitation accept)
    - [ ] 🏛️ After alias created: Add to egc google group
- Verify Generated webpage content (`PROJECTS.yaml`[^3] / `CONTACTS.yaml`[^4])
    - [ ] `PROJECTS.yaml`[^3]: Status is "on-boarding"
    - [ ] Project is in projects list
    - [ ] Project links are correct / clickable
    - [ ] Logo is displayed in light and dark modes
    - [ ] EGC rep(s) are shown on About page

## Discovery Phase

- [ ] 🏛️ Create secure collaboration spaces
    - [ ] Google Group for project
    - [ ] Private Google Drive folder for legal documents
    - [ ] Private GitHub repository for project
    - [ ] 1Password or BitWarden vault for project
        - Project-specific account: <https://github.com/1Password/for-open-source>
- [ ] 👥 Identify project signatories
- [ ] 👥 Document all source code, contribution agreements, and related bot accounts
    - Run appropriate discovery tools against named organizations and repositories, include those reports alongside inventory
- [ ] 👥 Document all domain names
- [ ] 👥 Document all trademarks (both word marks and logos)
- [ ] 👥 Describe financial agreements or processes (overview)

## Asset Transfer Process 👥 🏛️

- [ ] Review project purposes, document operational boundaries
- [ ] Fiscal Sponsorship Agreement
- Asset transfer agreements[^1][^2]
    - [ ] Trademarks
    - [ ] Domains
    - [ ] Source code repositories
    - [ ] (?) Agreements for additional assets

### Trademarks

- [ ] 🏛️ Validate trademark details in discovered assets
- [ ] 🏛️ Update registered trademark filing(s)

### Domain management

- [ ] 👥 🏛️ Transfer domain to Commonhaus NameCheap account
- [ ] 👥 Create project-specific NameCheap Account
- [ ] 🏛️ Delegate domain management to project-specific NameCheap account

### Source code management

- [ ] 👥 Install [haus-manager](https://github.com/commonhaus/automation/tree/main/haus-manager) bot for each organization
- [ ] 👥 Verify team synchronization (private project repo and team in primary organization)

### Financial setup

- [ ] 🏛️ Restricted fund / Open Collective
    - New collective -> add to Commonhaus Fiscal Host, OR
    - Link established collective with Commonhaus Fiscal Host, OR
    - Create Commonhaus Fiscal Host project

## Final Validation

- [ ] 🏛️ Verify asset transfers complete
- [ ] 🏛️ Verify all documents signed
- [ ] 🏛️ Verify installation of haus-manager bot
- [ ] 🏛️ Verify automated update of `TRADEMARKS.md`
- [ ] Update `PROJECTS.yaml`[^3] status to "active"

[^1]: Signed agreements are stored in Google Drive as PDF (docusign)
[^2]: Transferred assets may be combined into one or more agreements.
[^3]: <https://github.com/commonhaus/foundation/blob/main/PROJECTS.yaml>
[^4]: <https://github.com/commonhaus/foundation/blob/main/CONTACTS.yaml>
