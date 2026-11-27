# Proposal

## Why

`@Ref` references currently render only as small chips on the individual spec pages
(`specs/<hash>.html`); there is no report-level page that lists all external traceability
links. Users who want an overview of which tracker items (e.g. `mytracker.org/PA-1`,
`mytracker.org/PB-3`) are referenced by which specifications have to click through spec
pages. The tags page (`tags.html`) already provides exactly this kind of grouped,
filterable listing for tags — refs need the same treatment, grouped not by stereotype but
by the shared URL prefix of the references.

## What Changes

- Add a static `references.html` page to the report (always generated, even when no refs
  exist — then it is simply empty) listing all ref links grouped by the `project` attribute
  of `@Ref`, with filter buttons and a filterable spec list, mirroring `tags.html`.
- Add a static "Refs" entry with a new bookmark icon to the `topnav` of every report page
  (including spec pages), linking to `references.html`.
- Group refs by the new `Ref#project` attribute (e.g. `project = "mytracker.org/PA-"` holds
  PA-1 and PA-2; `project = "mytracker.org/PB-"` holds PB-3). Refs with a blank `project`
  fall back to longest-common-prefix grouping of their resolved URLs (last `/` boundary);
  refs with neither `project` nor `url` form a no-link group.
- Spec-page ref chips keep linking directly to the full resolved ref URL (they do NOT link
  to `references.html`).
- New `icon-bookmark` icon template + CSS (FontAwesome regular bookmark), used for the
  topnav "Refs" entry; existing `icon-link`/`icon-external-link` are not reused for this.

## Capabilities

### New Capabilities

(none — the behavior extends existing capabilities)

### Modified Capabilities

- `refs`: adds the requirement that references are listed on a `references.html` report
  page, grouped by longest-common-prefix of the resolved URLs (last `/` as boundary), with
  filtering; clarifies that spec-page ref chips link to the full ref URL.
- `report-pages`: `references.html` becomes an always-generated static page (no longer
  "when refs exist"), and the `topnav` of all pages gains a static "Refs" entry.

## Impact

- `arete` core: new `Ref#project` attribute (default `""`), documented in arete's
  `openspec/specs/refs/spec.md`. Java/Kotlin parity decision (family rule): arete-ktx's
  `ref(value, url)` DSL SHALL gain an optional `project` parameter — to be implemented in
  arete-ktx together with the core attribute change (recorded here per the family parity rule).
- `arete-gradle`: `Refs.kt` (RefLink carries `project`; grouping by `project` with
  LCP-of-url fallback) / `SpecificationPlan.kt` (ref aggregation), new
  `templates/references.ftlh`, `templates/icon-bookmark.ftlh`,
  `templates/arete-css.ftlh` (bookmark icon CSS), `HtmlWriter.kt` (always write
  `references.html`, extract the new icon), topnav blocks in all page templates.
- `arete-gradle-test` demo specs use `project` on their `@Ref`s to validate the page
  end-to-end. Additive report change; existing pages and links stay backward compatible.
