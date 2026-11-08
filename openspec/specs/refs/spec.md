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
