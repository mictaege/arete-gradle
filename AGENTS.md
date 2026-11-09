# AGENTS.md — arete-gradle

> New to the arete family? Read the onboarding guide in the family store first:
> https://github.com/mictaege/arete-openspec (README.md).

Project context and conventions live in OpenSpec: `openspec/config.yaml` (project context) and
`openspec/specs/<capability>/spec.md` (capability specs). Use the OpenSpec workflow for behavior
changes: propose via `/openspec-propose`, implement via `/openspec-apply-change`, validate with
`openspec validate`, finish with `/openspec-archive-change`.

## Read the relevant spec or doc before changing

- References (`@Ref` links rendered on spec pages): read `openspec/specs/refs/spec.md` before
  changing `Refs.kt`, the `refs`/`refLinks`/`hasRefLinks` properties in `SpecificationPlan.kt`, or
  the ref-links block in `spec.ftlh`.
- Run history (history store, history strip on spec pages, History page, screenshot hand-over via
  JUnit file attachments): read `openspec/specs/history/spec.md` before changing `History.kt`,
  `HtmlWriter.kt`, the history templates or screenshot handling.
- Report outline (collapsible spec/scenario tree, connector lines, page outline navigation,
  unfold-on-click): read `openspec/specs/outline/spec.md` before changing `spec.ftlh`,
  `history.ftlh`, `history-macros.ftlh` or `arete-css.ftlh`.
- Markdown export (copy-as-markdown buttons on the container boxes): read
  `openspec/specs/mdcopy/spec.md` before changing `markdown-macros.ftlh` or the markdown calls in
  `spec.ftlh` and `history.ftlh`.
- Report model (listener, plan tree, step types/states, hashing): read `openspec/specs/report-model/spec.md`
  before changing `AreteTestListener.kt`, `SpecificationPlan.kt`, `TestIdentifierNormalizer.kt` or `SpecificationWriter.kt`.
- Report pages (index/display_names/test_specs/tags, nav, search, hidden steps): read
  `openspec/specs/report-pages/spec.md` before changing `index.ftlh`, `display_names.ftlh`,
  `test_specs.ftlh`, `tags.ftlh`, `search-js.ftlh` or the topnav blocks.
- Plugin DSL and system properties: read `openspec/specs/plugin-dsl/spec.md` before changing
  `AretePlugin.kt`.
- Color schemes and icon generation: read `openspec/specs/color-schemes/spec.md` before changing
  `AreteColorScheme.kt`, `AreteColorSchemeProvider.kt`, `arete-colors-css.ftlh` or the icon handling
  in `HtmlWriter.kt`.
- Stdout/stderr capture: read `openspec/specs/stdout-stderr/spec.md` before changing the capture
  properties or the `stdout`/`stderr` handling in `SpecificationPlan.kt`.
- Narrative rendering: read `openspec/specs/narrative-rendering/spec.md` before changing
  `NarrativeSection.kt` or the narrative blocks in `spec.ftlh`.
- See-also rendering (placeholder pass): read `openspec/specs/see-also-rendering/spec.md` before
  changing `ReferenceTargets.kt`, the see-also block in `spec.ftlh` or `replaceSeeAlsoPlaceholder` in
  `HtmlWriter.kt`.

## Working conventions

- Run `./gradlew test` before submitting changes; also consider building and exercising the plugin
  against `arete-gradle-test`, which is the dedicated consumer/integration test project.
- Keep the plugin's public extension DSL and generated report structure backward compatible where
  possible, since external consumers apply this plugin by version.
- Match the existing Kotlin coding style already used in the plugin sources.
- Behavior changes to the report output or the extension DSL require an OpenSpec change proposal;
  typo-level fixes do not.
