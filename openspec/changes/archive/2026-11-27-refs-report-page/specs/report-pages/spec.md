# Delta: report-pages

## MODIFIED Requirements

### Requirement: Report location and page set

The report root SHALL be `<buildDir>/reports/arete/<taskName>/` (from the `buildDir`/`taskName`
system properties), with pages `index.html`, `display_names.html`, `test_specs.html`, `tags.html`,
`references.html`, `history.html`, icon SVGs under `icons/`, and per-spec pages
under `specs/<uniqueHash>.html`. `references.html` SHALL always be generated (when no refs exist
it is simply empty). All pages SHALL share one `topnav` with a static link to every page —
including a "Refs" entry with the `menu-icon-bookmark` icon (`spec-menu-icon-bookmark` on spec
pages, using `./../` relative links).

#### Scenario: Report root per test task
- **WHEN** the `test` task runs
- **THEN** the report lands in `build/reports/arete/test/index.html`

#### Scenario: References page is static even without refs
- **WHEN** no step carries any `@Ref` and the `test` task runs
- **THEN** `build/reports/arete/test/references.html` still exists and every page's topnav
  links to it
