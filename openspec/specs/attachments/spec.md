# Attachments Specification

## Purpose

Receive step attachments published by arete core under the `arete-attach.` namespace and narrative
attachments under the `arete-narrative-attach.` namespace, and render them in the HTML report: a
file icon on the owning step in the outline with a download chip inside the step's expanded
content, and narrative attachments as chips inside the narrative block.

## Requirements

### Requirement: Listener registers arete-attach files per step

`AreteTestListener.fileEntryPublished` SHALL accept file entries whose name starts with
`arete-attach.` (in addition to `arete-screenshot.`) and register them in `SpecificationPlan` as a
list of attachments (name + path) keyed by the test's uniqueId, so a step may carry multiple
attachments.

#### Scenario: Attachment registered on the owning step
- **WHEN** a step publishes `arete-attach.invoice.txt`
- **THEN** `SpecificationPlan.attachments(uniqueId)` contains `invoice.txt` for that step

### Requirement: Report copies attachments into specs/

`HtmlWriter.copyResources` SHALL copy each attachment into the report `specs/` directory as
`<uniqueHash>-<name>` and remove the original file afterwards.

#### Scenario: Attachment copied next to the report
- **WHEN** a step has an attachment `invoice.txt`
- **THEN** `specs/<uniqueHash>-invoice.txt` exists in the report and the original JUnit attachment
  file is removed

### Requirement: Report renders file icon and attachment chip

`spec.ftlh` SHALL show a `spec-icon-file` icon in the step box (before the camera icon when both
are present) whenever the step has attachments, make the step box collapsible, and render the
attachment as an `attachments`/`attachment` chip (file icon + name, `target="_blank"`) at the top
of the step's expanded `overflow-content`, above the screenshot.

#### Scenario: Expanded step shows chip above screenshot
- **WHEN** a successful step has both a screenshot and an attachment and is expanded
- **THEN** the report shows the file icon before the camera icon in the step box, and the
  attachment chip above the screenshot image in the expanded area

### Requirement: Listener registers arete-narrative-attach files per narrative

`AreteTestListener.fileEntryPublished` SHALL accept file entries whose name starts with
`arete-narrative-attach.` (in addition to `arete-screenshot.` and `arete-attach.`) and register
them in `SpecificationPlan` as a separate list of narrative attachments (name + path) keyed by the
test's uniqueId, distinct from step attachments, so a narrative node may carry multiple narrative
attachments.

#### Scenario: Narrative attachment registered on the owning node
- **WHEN** a `@Narrative` class publishes `arete-narrative-attach.summary.json` from `@AfterAll`
- **THEN** `SpecificationPlan.narrativeAttachments(uniqueId)` contains `summary.json` for that
  node and `SpecificationPlan.attachments(uniqueId)` does not
