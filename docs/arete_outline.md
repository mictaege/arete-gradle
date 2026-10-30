# Arete outline — design and maintenance notes

This document describes the outliner presentation of the arete reports: collapsible tree nodes,
connector lines, the page outline navigation and the interaction script. It is meant for developers
and agents who maintain or extend the report templates. What the reports *contain* (history strips,
run entries, history page grouping and ordering) is documented in
[arete_history.md](arete_history.md); this document describes how the tree is *drawn and folded*.

## 1. Requirement

The structure of a spec (spec → feature/journey → scenario → given/when/then, describe/it-should,
example containers) was originally visualized by indentation only. It should act like an outliner:

- container nodes can be collapsed and expanded,
- the hierarchy is visualized with connector lines (vertical lines and rounded branches into each
  node),
- nodes signal interactivity (pointer, hover) and can be reached by keyboard,
- clicking an outline entry (e.g. in the page navigation) unfolds the target before scrolling to it.

## 2. Where the outline appears

| File | Role |
|---|---|
| `src/main/resources/templates/spec.ftlh` | The spec tree on the spec pages (`dumpSteps` macro) and the page outline navigation (`pageMenu` macro). |
| `src/main/resources/templates/history.ftlh` | Spec rows and container/leaf nodes on the History page; spec rows are collapsible nodes with a link button to the spec report. |
| `src/main/resources/templates/history-macros.ftlh` | `historyStrip` (wraps the history strip as a leaf child), `historyRun` (wraps each run entry as a child of the strip) and the shared `outlineScript`. |
| `src/main/resources/templates/arete-css.ftlh` | All outline structure, connector line and button styles. |

The history strip and its run entries are outline children like any other node; their *content*
(state dots, run boxes, folding of the strip itself) is documented in
[arete_history.md](arete_history.md) sections 8 and 9.

## 3. DOM structure

Every step renders as one `outline-item`:

```html
<div class="outline-item[ container-step]">        <!-- container steps add container-step -->
    <!-- the state-coloured box; containers add outline-header -->
    <div class="successful step outline-header">…</div>
    <!-- leaves with error/screenshot may have overflow-content here -->
    <div class="outline-body">
        <!-- history strip (as nested outline-children, see §5), tags, narrative, … -->
        <div class="outline-children">              <!-- only for container steps -->
            …further outline-items…                   <!-- recursion -->
        </div>
    </div>
</div>
```

- The root spec step is rendered by `dumpSteps` but is **not** inside an `outline-children`
  container, so it draws no connectors; its own box is a collapsible `outline-header` like any
  container.
- `.outline-body` exists for leaves too (it wraps history strip, tags and narrative); only container
  bodies are toggleable.
- The example grid header row (for `@ExampleGrid`/`@ExampleCsv`) is rendered inside the first grid
  instance item, above its box; the connector tick then points at that header row.
- On the History page, each spec is an `outline-item container-step` whose box is an
  `outline-header`; its nodes sit in the body. The name is plain text, a `.spec-link-btn` next to it
  opens the spec report (only if `reportAvailable`), see §6.

## 4. Connector lines

All lines are drawn in pure CSS with two pseudo-elements per item, in
`--arete-color-foreground`, 3px thick, fully opaque:

- `.outline-children > .outline-item::before` — the **elbow**: a bordered box
  (`box-sizing: border-box`, `border-left` + `border-bottom` 3px, `border-bottom-left-radius: 12px`)
  that spans from the item top down to the child box. It draws the vertical line, the arc and the
  tick **as one continuous element**, so the vertical-to-arc transition cannot show a seam,
  regardless of zoom or device pixel ratio.
- `.outline-children > .outline-item::after` — the **continuation**: a plain 3px vertical from item
  top to item bottom. It only exists for non-last children (the line must run past the branch point
  down to the next sibling); `:last-child::after { display: none }` lets the last child's line end
  *in* the arc.
- Elbow geometry: `top: -2px` closes the parent box's 2px bottom margin so the line starts flush at
  the parent; leaf height 22px, container height 33px, so the tick lands on the first text line of
  each box kind. The tick ends flush at the box edge (item `padding-left: 28px`).
- Segment ends are square (no border-radius): rounded caps pinched at every item boundary and at
  the arc start, producing visible gaps.
- Indentation is the item's `padding-left: 28px` per level; vertical spacing comes from item
  padding (2px per leaf, 13px per container), never from margins, so the per-item line segments
  stay continuous.

### History area overrides

The history strip hangs below a leaf and the run entries hang below the strip, each with a small,
**equal** gap between the connector end and the visible target (measured 4px, do not "fix" them
back to flush):

- `.outline-children > .outline-item.history-item::before` — `width: 23px`: the tick reaches into
  the strip's invisible card padding and stops 4px before the clock icon (the strip card has the
  same colour as the page card, so the icon is the first *visible* target).
- `.history-runs.outline-children > .outline-item::before` — `width: 13px`: the tick stops 4px
  before the entry box.
- `.history` and `.history-runs` have `margin-top: 2px` each, giving the same element spacing on
  both junctions.

### Page outline navigation

The navigation tree on the spec pages reuses the same construction on `li` elements
(`.page-outline ul ul > li::before/::after`). Differences:

- Row heights vary, so the elbow height is `calc(50% + 1px)`, pixel-snapped with CSS
  `round(...)`; the plain `calc` line is kept as a fallback for browsers without `round()`
  (older than Firefox 118 / Chrome 125). Without snapping, odd row heights put the arc between
  pixels and the junction shows a nick at 100% zoom.
- `li` spacing uses padding (4px), not margins, for the same continuity reason as above.

## 5. Collapsing and navigation (`outlineScript`)

The shared `outlineScript` macro in `history-macros.ftlh` is included by `spec.ftlh` and
`history.ftlh`:

- `setCollapsed(header, collapsed)` toggles the `collapsed` class on the header and its following
  `.outline-body` (`display: none` when collapsed) and keeps `aria-expanded` in sync.
- All `.outline-header` elements get `tabindex="0"`, `role="button"` and key handlers (Enter/Space);
  clicks on `a`/`button` inside the header (copy-link, spec-link buttons) are ignored via
  `closest`, so they do not toggle.
- A document-level click handler for `a[href^="#"]` unfolds the target of in-page anchor links
  before the browser scrolls: `unfoldTargetOf` expands the target if it is a collapsed header and
  `expandAbove` removes `collapsed` from every enclosing `.outline-body`. This is what makes the
  page outline navigation reach targets inside folded subtrees.

## 6. Interaction affordances

- Container boxes use the hover-capable state classes (`successful`, `failed`, … — no
  `-no-hover` suffix) because they are interactive; the state classes' `:hover` rules do the colour
  change. There is deliberately **no chevron/arrow** on the headers; cursor and hover are the
  affordances.
- Leaves with screenshot or error keep the older fold mechanism: class `collapsible` on the box and
  the following `.overflow-content` with a `max-height` transition (unchanged pre-outline
  behaviour). Container boxes never use that mechanism — a failed container's stack trace and
  screenshot render inside its `.outline-body` (`.step-details`), so collapsing the body hides them.
- `.spec-link-btn` (History page, spec rows) is styled like `.link-copy-btn` but shows the
  external-link (see-also) icon; it uses the `--root-icon-*` variables because `history.html`
  lives at the report root where the spec pages' `./../icons/` paths would not resolve.

## 7. Design decisions

- **No `<details>/<summary>` for tree nodes.** Interactive elements inside a `summary` (copy-link
  and spec-link buttons) would toggle the details on every click, and the `max-height` animation
  computes stale heights for nested collapsibles. `display: none` toggling has neither problem.
  (The history strip and its run entries *do* use `<details>` — their summaries contain no
  interactive elements.)
- **One element per connector.** The elbow's vertical, arc and tick are one bordered box, and the
  continuation is the only separately drawn part, butted and overlapping on the pixel grid. This
  was chosen after two-element constructions showed visible seams at 100% zoom.
- **`round()` only in the navigation**, where `50%` positioning is fractional; the main tree and
  the history area use fixed integer offsets and need no snapping.
- **Equal tick-to-target gaps in the history area** (4px before the clock icon and before each run
  box). The ticks of the main tree intentionally end flush at their boxes.

## 8. Pitfalls and known limitations

- FreeMarker renders templates with `RETHROW_HANDLER`; a template error aborts the whole spec
  page *before* the history store is saved. See [arete_history.md](arete_history.md) section 11
  for the details, and keep `HistoryTemplateTest` in sync whenever outline class names change.
- The connector geometry (elbow heights, tick widths, 4px gaps) was verified by rendering and
  measuring the generated HTML (see section 9). Changing one value usually requires adjusting its
  neighbours (arc start = height − radius; tick position = top + height − 3).
- Hidden steps (`@HiddenIfDisabled`) render no markup at all (`<#return/>` before any output), so
  they neither draw connectors nor count as children.
- `aria-expanded`/keyboard support apply only to container boxes; the old leaf fold
  (`collapsible`) has no keyboard handler (pre-existing behaviour).

## 9. Testing

- `./gradlew test` in `arete-gradle`: `HistoryTemplateTest` renders `history.ftlh` and the shared
  macros and asserts the outline structure, the `spec-link-btn` and the `outlineScript`.
- End to end: run the tests in `arete-gradle-test` (composite build picks up the local plugin) and
  inspect `build/reports/arete/test/` — spec pages and `history.html`.
- For geometry changes, render the page headlessly (e.g. `firefox --headless --screenshot`) and
  measure the pixels (the styles are inline in the generated HTML, so the generated file can be
  reduced to a minimal repro page); don't rely on computed offsets alone.
