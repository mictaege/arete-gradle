# Report Pages Specification

## Purpose

The static HTML report written to `build/reports/arete/<task>/` (parallel to Gradle's own
`build/reports/test/<task>/`): the overview page, the spec listing pages, the tags page, the
per-spec pages under `specs/`, navigation and search. History and refs have their own specs.

## Requirements

### Requirement: Report location and page set

The report root SHALL be `<buildDir>/reports/arete/<taskName>/` (from the `buildDir`/`taskName`
system properties), with pages `index.html`, `display_names.html`, `test_specs.html`, `tags.html`,
`history.html`, `references.html` (when refs exist), icon SVGs under `icons/`, and per-spec pages
under `specs/<uniqueHash>.html`. All pages SHALL share one `topnav` with links to every page
(spec pages use `./../` relative links and `spec-menu-icon-*` classes).

#### Scenario: Report root per test task
- **WHEN** the `test` task runs
- **THEN** the report lands in `build/reports/arete/test/index.html`

### Requirement: Overview page

`index.html` SHALL show summary counts (overall/failed/aborted/ignored/successful) for specs,
journeys, scenarios and descriptions, plus a searchable "Suspicious Specifications" list ordered by
`specsOrderedByPrio()` (failures first, then aborted, then by name), each entry linking to its spec
page and coloured by cumulated state. Hidden steps SHALL be excluded.

#### Scenario: Search filters the list
- **WHEN** the user types into `#searchInput`
- **THEN** `search-js.ftlh`'s `searchFunction` hides `li`s whose text does not contain the query

### Requirement: Spec listing pages

`display_names.html` SHALL list specs ordered by display name (`specsOrderedByName`);
`test_specs.html` SHALL list spec classes ordered by class name (`specsOrderedByHierarchy`), both with
search and state-coloured entries linking to `specs/<hash>.html`. The tags page SHALL list all
stereotype-grouped tags as filter buttons and the spec list with `data-tags`, filtering via
`?t=<tag>`/button clicks (all selected tags must match).

#### Scenario: Tag filter via URL
- **WHEN** the tags page is opened with `?t=smoke`
- **THEN** only specs carrying the tag `smoke` are shown

### Requirement: Spec pages

Each spec page `specs/<hash>.html` SHALL render the outline tree (see outline spec), per-step tags,
refs, see-also links, narrative, stdout/stderr and failure details, with screenshots copied next to
the page. Disabled steps SHALL be included as ignored unless `@HiddenIfDisabled` (then fully hidden).

#### Scenario: Disabled scenario visible by default
- **WHEN** a `@Disabled` scenario has no `@HiddenIfDisabled`
- **THEN** its spec page lists it as ignored
