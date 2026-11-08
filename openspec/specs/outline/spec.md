# Outline Specification

## Purpose

The outliner presentation of the arete reports: collapsible tree nodes, connector lines, the page
outline navigation and the interaction script. The spec structure (spec → feature/journey →
scenario → given/when/then, describe/it-should, example containers) SHALL act like an outliner:
container nodes can be collapsed/expanded, the hierarchy is visualized with connector lines, nodes
signal interactivity (pointer, hover) and are keyboard-reachable, and clicking an outline entry in
the page navigation unfolds the target before scrolling to it. What the nodes *contain* (history
strips, run entries, History page grouping) is covered by the history spec; this spec covers how the
tree is drawn and folded.

## Requirements

### Requirement: DOM structure of the tree

Every step SHALL render as one `outline-item` (`container-step` additionally for containers): the
state-coloured box (containers add `outline-header`), an `outline-body` wrapping tags/narrative/
history, and for containers an `outline-children` div with the recursive child items. The root spec
step is rendered by `dumpSteps` but NOT inside an `outline-children` container, so it draws no
connectors; its box is a collapsible `outline-header` like any container. Leaves also have an
`outline-body`, but only container bodies are toggleable. The example grid header row SHALL render
inside the first grid instance item above its box. On the History page each spec is an
`outline-item container-step` whose box is an `outline-header` with a `.spec-link-btn` (only when
`reportAvailable`).

#### Scenario: Spec root draws no connectors
- **WHEN** a spec page renders the root spec step
- **THEN** the root box is a collapsible `outline-header` but sits outside any `outline-children`,
  so no elbow/continuation line is drawn for it

### Requirement: Connector lines are pure CSS, one element per connector

All lines SHALL be drawn with two pseudo-elements per item in `--arete-color-foreground`, 3px, fully
opaque: `.outline-children > .outline-item::before` is the elbow (bordered box, `border-left` +
`border-bottom` 3px, `border-bottom-left-radius: 12px`, spanning item top to child box — vertical
line, arc and tick as ONE element so no seam can appear at any zoom/DPR) and `::after` is the
continuation (plain 3px vertical, only for non-last children; `:last-child::after { display: none }`
so the last child's line ends in the arc). Segment ends SHALL be square (no border-radius). Indentation
and vertical spacing SHALL come from item padding (leaf 22px / container 33px height, item
`padding-left: 28px` per level, elbow `top: -2px`), never from margins, so per-item segments stay
continuous.

#### Scenario: History strip tick stops before the clock icon
- **WHEN** the history strip hangs below a leaf
- **THEN** `.outline-children > .outline-item.history-item::before` has `width: 23px`, stopping 4px
  before the clock icon (do not "fix" back to flush); run entries use `width: 13px` (4px before the
  entry box), with `.history`/`.history-runs` `margin-top: 2px` each

#### Scenario: Page outline navigation snapping
- **WHEN** the page navigation tree reuses the construction on `li`
  (`.page-outline ul ul > li::before/::after`)
- **THEN** the elbow height is `calc(50% + 1px)` pixel-snapped with CSS `round(...)` (plain `calc`
  fallback kept for browsers without `round()`), and `li` spacing uses padding (4px), not margins

### Requirement: Collapsing and keyboard support

The shared `outlineScript` macro (in `history-macros.ftlh`, included by `spec.ftlh` and
`history.ftlh`) SHALL provide `setCollapsed(header, collapsed)` toggling `collapsed` on the header and
following `.outline-body` (`display: none` when collapsed) with `aria-expanded` in sync. All
`.outline-header` elements SHALL get `tabindex="0"`, `role="button"` and Enter/Space handlers; clicks
on `a`/`button` inside a header SHALL be ignored (via `closest`) so copy-link/spec-link buttons do not
toggle. A document-level click handler for `a[href^="#"]` SHALL unfold the anchor target before the
browser scrolls (`unfoldTargetOf` expands a collapsed target header; `expandAbove` removes `collapsed`
from enclosing `.outline-body`s).

#### Scenario: Page outline entry reaches a folded target
- **WHEN** a user clicks a page-outline entry pointing into a collapsed subtree
- **THEN** the target and all enclosing bodies are unfolded before scrolling to it

### Requirement: Interaction affordances

Container boxes SHALL use the hover-capable state classes (`successful`, `failed`, … — no
`-no-hover` suffix) because they are interactive, and SHALL have deliberately no chevron/arrow —
cursor and hover are the affordances. Leaves with screenshot/error keep the older fold mechanism
(`collapsible` + `.overflow-content` with `max-height` transition); container boxes never use it — a
failed container's details render inside its `.outline-body` (`.step-details`). Tree nodes SHALL NOT
use `<details>/<summary>` (interactive elements inside `summary` toggle on every click; `max-height`
computes stale heights for nested collapsibles); the history strip and run entries DO use `<details>`
because their summaries contain no interactive elements. `.spec-link-btn` SHALL be styled like
`.link-copy-btn` with the external-link icon and SHALL use the `--root-icon-*` variables because
`history.html` lives at the report root.

#### Scenario: Container failure details hidden when collapsed
- **WHEN** a failed container's body is collapsed
- **THEN** its stack trace and screenshot (rendered in `.step-details` inside the body) are hidden

### Requirement: Hidden steps render nothing

Steps hidden via `@HiddenIfDisabled` SHALL render no markup at all (`<#return/>` before any output):
they neither draw connectors nor count as children.

#### Scenario: Hidden step leaves no connector gap
- **WHEN** a step is hidden via `@HiddenIfDisabled`
- **THEN** no `outline-item` is emitted for it and sibling connector lines stay continuous

## Design and maintenance notes

- Files: `spec.ftlh` (spec tree `dumpSteps`, page outline `pageMenu`), `history.ftlh` (History page
  nodes), `history-macros.ftlh` (`historyStrip`, `historyRun`, shared `outlineScript`),
  `arete-css.ftlh` (all outline/connector/button styles). Tests: `HistoryTemplateTest` asserts the
  outline structure, `spec-link-btn` and `outlineScript`.
- Pitfall: FreeMarker renders with `RETHROW_HANDLER`; a template error aborts the whole spec page
  before the history store is saved (see history spec notes). Keep `HistoryTemplateTest` in sync when
  outline class names change.
- Connector geometry (elbow heights, tick widths, 4px gaps) was verified by rendering and measuring
  generated HTML; changing one value usually requires adjusting neighbours (arc start = height −
  radius; tick position = top + height − 3). For geometry changes render headlessly (e.g.
  `firefox --headless --screenshot`) and measure pixels; don't rely on computed offsets alone.
- `aria-expanded`/keyboard support apply only to container boxes; the old leaf fold (`collapsible`)
  has no keyboard handler (pre-existing).
- End to end: run the tests in `arete-gradle-test` (composite build picks up the local plugin) and
  inspect `build/reports/arete/test/` — spec pages and `history.html`.
