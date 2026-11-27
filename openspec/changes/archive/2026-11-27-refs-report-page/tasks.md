# Tasks

## 1. Model and prefix grouping

- [x] 1.1 Make `RefLink` comparable (by project, then url, then value) and carry `Ref#project`; add `SpecificationStep.allRefLinks` (own `refLinks` + non-hidden descendants) plus `SpecificationPlan.allRefLinkGroups()` / `specsOrderedByRefs()` implementing grouping by the `project` attribute with LCP-of-url fallback (last `/` boundary) for blank `project`; verify with `./gradlew test` in arete-gradle (unit tests: project grouping incl. cross-url, LCP fallback, cross-host, no-url/no-project refs)

## 2. References page and writer

- [x] 2.1 Create `templates/references.ftlh` mirroring `tags.ftlh` (group sections with per-ref filter buttons, `#specList` with `data-refs`, AND-filter JS, `?r=<value>` param, chips linking to the resolved ref url); verify template renders by running the arete-gradle-test build
- [x] 2.2 Always write `references.html` in `HtmlWriter.finishPlan` (empty when no refs); verify `build/reports/arete/test/references.html` exists after a run with no refs

## 3. Bookmark icon and topnav

- [x] 3.1 Add `templates/icon-bookmark.ftlh` (FontAwesome classic regular bookmark, `fill:${foreground}`), register `icon-bookmark` in `HtmlWriter.extractIcons()`, and add the `--root-icon-bookmark-*` / `--page-icon-bookmark-*` variables plus `.menu-icon-bookmark` / `.spec-menu-icon-bookmark` classes (incl. hover/active) in `arete-css.ftlh`; verify `icons/icon-bookmark-{bg,fg,neutral}.svg` are generated
- [x] 3.2 Add the static "Refs" topnav entry to `index.ftlh`, `display_names.ftlh`, `test_specs.ftlh`, `tags.ftlh`, `history.ftlh`, `references.ftlh` (`menu-icon-bookmark`, `./references.html`) and to `spec.ftlh` (`spec-menu-icon-bookmark`, `./../references.html`); verify every generated page contains the references link

## 4. Specs and docs

- [x] 4.1 Land the delta specs (`specs/refs/spec.md`, `specs/report-pages/spec.md` in this change) and verify `openspec validate refs-report-page` passes
- [x] 4.2 Update `AGENTS.md` file-to-spec mapping to cover `references.ftlh` and `icon-bookmark.ftlh` (refs / report-pages specs); verify mapping reads correctly

## 5. End-to-end validation

- [x] 5.1 Demo refs in arete-gradle-test specs carry `project` (e.g. `arete`, `arete-gradle`); run `./gradlew test` in arete-gradle-test and verify `build/reports/arete/test/references.html` groups refs by `project` (blank-`project` refs fall back to LCP), and that spec-page chips still link to the full ref url
- [x] 5.2 Run `./gradlew test` in arete-gradle and `openspec validate refs-report-page`; verify all green

## Workflow follow-up

- Archive this change with `/openspec-archive-change` after implementation and validation (syncs the delta specs into `openspec/specs/`).
