# Delta: refs

## ADDED Requirements

### Requirement: References are listed on a references report page grouped by their `project` attribute

The system SHALL render a `references.html` page in the report root that lists all ref
links of all non-hidden steps, grouped by the `project` attribute of the `@Ref` (one group
per project), with one filter button per ref and a spec list filterable by the selected
refs (AND semantics, `?r=<value>` URL parameter), mirroring the tags page.

#### Scenario: Project attribute groups refs of the same tracker project
- **WHEN** steps carry `@Ref(value = "PA-1", url = "https://mytracker.org/", project = "mytracker.org/PA-")`,
  `@Ref(value = "PA-2", url = "https://mytracker.org/", project = "mytracker.org/PA-")` and
  `@Ref(value = "PB-3", url = "https://mytracker.org/", project = "mytracker.org/PB-")`
- **THEN** `references.html` groups `PA-1` and `PA-2` under `mytracker.org/PA-`, and
  `PB-3` under `mytracker.org/PB-`

#### Scenario: Project groups refs even across different urls
- **WHEN** refs with `project = "arete"` resolve to `https://github.com/mictaege/arete/issues/5`
  and `https://github.com/mictaege/arete/pull/6`
- **THEN** both refs are in the `arete` group

### Requirement: Refs with a blank `project` fall back to longest-common-prefix grouping of their urls

Refs with a blank `project` SHALL be grouped by the longest common prefix of their
resolved URLs, where the last `/` of the URL directory is the boundary (group prefixes
never start below it); refs with neither `project` nor `url` SHALL form a separate
no-link group.

#### Scenario: Blank project falls back to longest common url prefix
- **WHEN** steps carry refs resolving to `https://mytracker.org/PA-1`,
  `https://mytracker.org/PA-2` and `https://mytracker.org/PB-3`, all without a `project`
- **THEN** `references.html` groups `PA-1` and `PA-2` under the prefix
  `https://mytracker.org/PA-`, and `PB-3` under its own prefix

#### Scenario: Fallback never crosses the last slash boundary
- **WHEN** refs without `project` resolve to `https://a.org/x/1` and `https://b.org/y/2`
- **THEN** the two refs are in different groups (their common prefix `https://` is below
  the last-`/` boundary and SHALL NOT form a group)

#### Scenario: Refs without project and url form their own group
- **WHEN** a step has `@Ref("PROJ-123")` without `url` and without `project`
- **THEN** `references.html` lists `PROJ-123` in a separate no-link group rendered as
  plain text

#### Scenario: Filter by ref via URL parameter
- **WHEN** `references.html` is opened with `?r=PA-1`
- **THEN** only specs carrying the ref `PA-1` are shown

#### Scenario: Empty page when no refs exist
- **WHEN** no step carries any `@Ref`
- **THEN** `references.html` is still generated and linked from the topnav, showing no
  groups and an empty spec list

### Requirement: Spec-page reference chips link to the full ref url

The reference links rendered on a spec page SHALL keep pointing to the resolved external
ref URL (`target="_blank"` when a url is present) and SHALL NOT link to `references.html`.

#### Scenario: Chip href is the resolved ref url
- **WHEN** a spec page renders a step with `@Ref(value = "14", url = "https://github.com/mictaege/arete-gradle/issues/{0}")`
- **THEN** the chip's `href` is `https://github.com/mictaege/arete-gradle/issues/14`,
  not `references.html`
