# Refs Specification

## Purpose

References (`@Ref`) link specifications and their nested parts (features, descriptions, scenarios,
journeys) to external systems — e.g. issues, requirements or user stories in a tracking system —
for traceability. The core annotation lives in the `arete` library
(`com.github.mictaege.arete.Ref` / `Refs`); this plugin reads them from the test annotations and
renders them as links on the generated specification pages.

## Requirements

### Requirement: Elements can carry repeatable `@Ref` annotations

The system SHALL allow `@Ref` on any spec, feature, description, scenario or journey, repeatable
via the `@Refs` container. Each `@Ref` has a `value` (the reference id, e.g. `"PROJ-123"` or an
issue number) and an optional `url`.

#### Scenario: Single direct `@Ref`
- **WHEN** a spec is annotated `@Ref("PROJ-123")`
- **THEN** the report renders the reference `PROJ-123` on that element's page

#### Scenario: Repeated `@Ref` annotations
- **WHEN** an element is annotated `@Ref("A-1") @Ref("A-2")` (compiled into the `@Refs` container)
- **THEN** the report renders both references `A-1` and `A-2`

### Requirement: The `url` is a pattern resolved with the `value`

The system SHALL resolve the link URL from `url` and `value` as follows: a `{0}` placeholder in
`url` is replaced by the `value`; without a placeholder the `url` is used as a prefix and the
`value` is appended; without a `url` the reference is rendered as plain text (no link).

#### Scenario: Url pattern with placeholder
- **WHEN** `@Ref(value = "14", url = "https://github.com/mictaege/arete-gradle/issues/{0}")` is used
- **THEN** the link target is `https://github.com/mictaege/arete-gradle/issues/14`

#### Scenario: Url prefix without placeholder
- **WHEN** `@Ref(value = "16", url = "https://github.com/mictaege/arete-gradle/issues/")` is used
- **THEN** the link target is `https://github.com/mictaege/arete-gradle/issues/16`

#### Scenario: No url
- **WHEN** `@Ref("PROJ-123")` has no `url`
- **THEN** the reference renders as text without an `href`

### Requirement: Composed annotations can be meta-annotated with `@Ref`

The system SHALL support domain-specific annotations meta-annotated with `@Ref(url = ...)` that
carry their own `value()` and stay repeatable via their own container (e.g. `@GithubRef` /
`@GithubRefs` in arete-gradle-test). The composed annotation's `value` SHALL be used as the
reference value and substituted into the meta `@Ref`'s url pattern.

#### Scenario: Composed annotation
- **WHEN** `@GithubRef("14")` is used, where `@GithubRef` is meta-annotated with
  `@Ref(url = "https://github.com/mictaege/arete-gradle/issues/{0}")`
- **THEN** the report links to `https://github.com/mictaege/arete-gradle/issues/14` with text `14`

#### Scenario: Repeated composed annotation
- **WHEN** `@GithubRef("14") @GithubRef("15")` is used (compiled into `@GithubRefs`)
- **THEN** the report renders links to `.../issues/14` and `.../issues/15`

### Requirement: References render on spec pages below the see-also links

The system SHALL render the references declared on a step in `spec.ftlh` directly below the
`step.hasSeeAlsoRefs` block, as real HTML (no placeholder pass): an `<a class="see-also">` with a
`see-also-icon`, `target="_blank"` when a url is present, and the `value` as link text.

#### Scenario: Link with resolved url
- **WHEN** a spec page is generated for a step with `@GithubRef("14")`
- **THEN** the page contains
  `<a href="https://github.com/mictaege/arete-gradle/issues/14" target="_blank" class="see-also">`
  with `<i class="see-also-icon"></i>` and text `14`

#### Scenario: Reference without url
- **WHEN** a step has `@Ref("PROJ-123")` without url
- **THEN** the anchor is rendered without `href`, showing `PROJ-123`

### Requirement: Reference reading rules in the plugin model

The system SHALL read references from `TestIdentifier.sourceAnnotations` (excluding test
templates), expanding `@Ref`, the `@Refs` container, annotations meta-annotated with `@Ref`, and
container annotations whose `value()` is an array of annotations. Each step SHALL render only the
references declared directly on it; inheritance from containers to nested steps is currently NOT
implemented.

#### Scenario: Test templates are excluded
- **WHEN** a test identifier is a template invocation
- **THEN** it contributes no references

#### Scenario: Own references only per step
- **WHEN** a spec is annotated `@Ref("SPEC-1")` and its scenario has no own `@Ref`
- **THEN** the scenario row renders no reference link (the spec row renders `SPEC-1`)

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
