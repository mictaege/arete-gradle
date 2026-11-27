# Design

## Context

Refs (`@Ref`/`@Refs`, incl. composed annotations) are read per step in `Refs.kt` into
`RefLink(value, resolvedUrl)` and rendered only as chips on spec pages (`spec.ftlh`).
The tags page (`tags.ftlh`) shows the model to mirror: `plan.allTestTags()` groups tags by
stereotype, `spec.allTestTags` aggregates a spec's own and its non-hidden descendants' tags,
and the page offers filter buttons plus a `data-tags` spec list with `?t=` filtering.
`HtmlWriter.finishPlan` writes the static pages and owns the topnav-relevant icon extraction.
See proposal.md (Why) for motivation and specs/ deltas for the behavior contract.

## Goals / Non-Goals

- Goals: a static `references.html` listing all ref links grouped by shared resolved-URL
  prefix, filterable like the tags page; static "Refs" topnav entry with a new bookmark icon.
- Non-goals: changing per-step rendering on spec pages (chips keep linking to the full ref
  URL); changing the `arete` core annotation API (no arete-ktx parity work); inheritance of
  refs from containers to nested steps for the per-step chips (unchanged — the listing page
  aggregates descendants like `allTestTags` does).

## Decisions

1. **Grouping = the `project` attribute of `@Ref` (new in arete core), with an LCP fallback.**
   `RefLink` carries `project` read from `@Ref`/`@Refs` and from the meta `@Ref` of composed
   annotations. `groupRefLinks` puts links with a non-blank `project` into one group per
   project (sorted by project) — e.g. `project = "mytracker.org/PA-"` holds PA-1 and PA-2,
   `"mytracker.org/PB-"` holds PB-3, and refs with the same project group even across
   different URLs. Links with a blank `project` fall back to longest-common-prefix grouping
   of their resolved URLs (last `/` of the URL directory as boundary; never below
   `scheme://host/`), so refs that do not opt into `project` keep the previous behavior —
   this avoids surprising splits like `.../issues/14` vs `.../issues/16` landing under
   `.../issues/1` when the author actually wants them together via `project`. Links with
   neither `project` nor `url` form one separate no-link group (empty key), rendered as
   plain text. LCP was tried as the sole grouping key first and rejected: it is
   char-based, so `14`/`16` share `.../issues/1` and `PA-1`/`PB-3` share `.../P` — the
   `project` attribute makes grouping explicit and predictable instead.
2. **Static page and static nav.** `references.html` is always written by
   `HtmlWriter.finishPlan` (empty when no refs exist) and every topnav links to it
   unconditionally — no conditional nav. Chosen per user decision; avoids broken links and
   keeps all topnav blocks identical in shape.
3. **Spec-page ref chips keep their external href** (full resolved ref URL, `target="_blank"`
   when a url is present); they do not link to `references.html`. Per user decision — the
   references page is reached via the topnav, the chips stay traceability links.
4. **New bookmark icon.** New `templates/icon-bookmark.ftlh` (FontAwesome classic regular
   bookmark SVG, `fill:${foreground}` like the other icon templates), added to
   `HtmlWriter.extractIcons()`, plus `--root-icon-bookmark-*` / `--page-icon-bookmark-*`
   CSS variables and `.menu-icon-bookmark` / `.spec-menu-icon-bookmark` classes (incl.
   hover/active variants) in `arete-css.ftlh`. `icon-link` / `icon-external-link` are
   deliberately not reused for the nav entry.
5. **Model aggregation mirrors tags.** `SpecificationStep.allRefLinks` (own `refLinks` +
   non-hidden descendants, sorted set) for the spec list's chips and `data-refs`;
   `SpecificationPlan.allRefLinkGroups()` (group key → sorted ref links, applying decision 1)
   and `specsOrderedByRefs()` for the page. Per-step `refLinks` stays own-only, so the
   existing spec-page rendering requirement is untouched. `RefLink` becomes `Comparable`
   (by project, then url, then value).
6. **Page structure mirrors `tags.ftlh`.** New `templates/references.ftlh`: topnav with
   active "Refs", one section per group (prefix label + one filter button per ref value),
   and a `#specList` of specs with `data-refs="v1|v2"` and state classes; filter JS identical
   to the tags page (AND across selected buttons) with `?r=<value>` instead of `?t=`.
   Chips on spec cards link to the resolved ref URL.

## Risks / Trade-offs

- [Char-based LCP fallback can split numeric suffixes oddly (`.../issues/14` vs `.../16`
  share `.../issues/1`)] → mitigated by the `project` attribute, which is the primary
  grouping key; the LCP fallback only applies to refs with a blank `project`.
- [Refs without project and url have no key] → explicit separate no-link group (spec'd).
- [Always writing `references.html` adds a page even for ref-less projects] → intended
  (static nav); page is empty, cost is one small file.
- [arete-ktx parity for the new `Ref#project` attribute] → `ref(value, url)` in arete-ktx
  has no `project` parameter yet; decision recorded in proposal.md (Impact) to add an
  optional `project` parameter there with the arete-core change.

## Migration Plan

Additive report change: no API/DSL changes, no changes to existing pages' content beyond the
new topnav entry. Consumers get `references.html` and the "Refs" nav link automatically on the
next plugin version. No rollback needed beyond reverting the plugin version.

## Open Questions

- Display label for the no-url group (e.g. "(no link)") — cosmetic, can be decided during
  implementation without changing the specs.
