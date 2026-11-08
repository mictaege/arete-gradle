# Markdown Copy Specification

## Purpose

The "copy as markdown" feature of the arete reports: every container box (spec, feature, journey,
scenario, describe, example container) can be copied to the clipboard as markdown, so it can be
pasted into a ticket, merge request or chat. Description, error messages, stack traces, stdout/stderr
and the run history go into the markdown; screenshots do not (they would only resolve where the
report files are reachable). The outline structure the buttons live in is covered by the outline
spec; history content rules by the history spec.

## Requirements

### Requirement: Copy button placement

The button (`markdown-copy-btn`, clipboard icon) SHALL be rendered in the `anchor` macro of
`spec.ftlh` so every container box (including the spec root) has one on spec pages, and on the spec
rows and container nodes of the History page (`history.ftlh`). Leaf steps SHALL NOT get a button —
their markdown is part of the enclosing container's export. The button SHALL embed the markdown in a
hidden `<textarea id="<hash>Markdown">` and copy via the shared `traceToClipboard` script; clicks on
the button SHALL NOT fold the container (the outline script ignores `button` clicks). Icon variables:
`spec-markdown-icon` on spec pages, `card-markdown-icon` (root-level) on the History page.

#### Scenario: Leaf steps have no button
- **WHEN** a spec page renders a leaf step
- **THEN** no `markdown-copy-btn` is rendered for the leaf; its markdown appears in the enclosing
  container's textarea

### Requirement: Containers become headings by depth

Containers SHALL become markdown headings by nesting depth (`#` for the spec, `##`, `###`, …, capped
at `######`), each preceded by a blank line. Leaves stay at the left margin.

#### Scenario: Nesting depth maps to heading level
- **WHEN** a scenario is nested in a feature in a spec
- **THEN** the spec renders as `#`, the feature as `##`, the scenario as `###`

### Requirement: Narratives become blockquotes from raw lines

Narratives SHALL render as blockquotes built from the raw `NarrativeSection.lines` — the author's
original markdown, not the rendered HTML — with every line split and prefixed with `> `.

#### Scenario: Multi-line narrative
- **WHEN** a narrative value spans multiple lines
- **THEN** each line is prefixed with `> ` in the exported markdown

### Requirement: Leaves become bullets with a coloured state sign

Leaves SHALL render as `- <span style="color: #…">[x]</span> <display name>`, where the sign is
`ResultState.sign` (`[x]` failed, `[*]` aborted, `[/]` ignored, `[+]` successful) wrapped in an
inline `<span style="color: …">` using the active color scheme's `-emph` accent for the state (e.g.
`arete_color_failed_emph`). The span keeps the theme colour in HTML-rendering paste targets and
prevents GFM from treating `[x]` as a task-list checkbox (the list item starts with the span);
no backticks around the sign. The same coloured sign SHALL be used for history run entries.

#### Scenario: Failed leaf sign
- **WHEN** a leaf failed
- **THEN** its bullet starts with `- <span style="color: <failed-emph>">[x]</span>`

### Requirement: Leaf details are labelled fenced sub-bullets, failures only

Details (Message, Stack trace, Stdout, Stderr) SHALL be emitted only for failed or aborted steps
(`ResultState.isFailure`; FreeMarker bean property `resultState.failure`) as sub-bullets four spaces
deeper than the leaf, fence two spaces deeper than its bullet. `Message` keeps original line
alignment; `Stack trace` flattens every line (leading whitespace/tabs removed); `Stdout`/`Stderr`
follow the `Message` pattern. Successful/ignored steps are plain bullets even when stdout/stderr was
captured.

#### Scenario: Error message keeps alignment, stack trace is flattened
- **WHEN** a failed leaf has an error message with indented continuation lines and a stack trace
- **THEN** the `Message` block keeps the original alignment, the `Stack trace` block has every line
  flattened

### Requirement: History blocks follow the strip's inclusion rule

A `- History:` label at the same level as other details (four spaces deeper than the leaf) SHALL list
each run (four spaces deeper again) as a bullet with sign and timestamp only; the block appears under
the same rule as the HTML strip (`SpecificationStep.showHistory`). A failed/aborted run carries its
own detail blocks four spaces deeper again. On the History page, the leaf's own details come from the
newest run when it failed/aborted, and ALL runs are listed under `History:` (no "current run" of its
own there).

#### Scenario: History block hidden for all-green leaf
- **WHEN** a leaf's current and all earlier runs are successful
- **THEN** no `History:` block is emitted in its markdown

### Requirement: Examples render like other leaves; screenshots are not exported

`@ExampleGrid`/`@ExampleCsv`/`@Example` instances SHALL render exactly like other leaves — a signed
bullet per example with details and history below, hidden examples omitted (no markdown table form).
Screenshots SHALL NOT be exported (no link, no image reference).

#### Scenario: Example row export
- **WHEN** an example instance failed
- **THEN** it renders as a signed bullet with its detail sub-bullets, identical in form to a
  given/when/then leaf

### Requirement: Server-side rendering and escaping

All markdown SHALL be generated server-side in FreeMarker (`markdown-macros.ftlh`) at report build
time — no JavaScript generation from the DOM. Macros: `markdownStep` (spec pages),
`markdownNode`/`markdownOverviewSpec` (History page), `markdownCopyButton` (textarea + button),
`fencedBlock` (labelled fenced block at a given indent, optional line flattening). The textarea
content SHALL be HTML-escaped by FreeMarker and decoded by the browser on copy.

#### Scenario: Angle brackets survive the clipboard
- **WHEN** an assertion message contains `<8>`
- **THEN** the copied markdown contains the literal `<8>` (textarea HTML-escaped, decoded on copy)

## Design and maintenance notes

- Tests: `MarkdownTemplateTest` renders the macros directly; the spec-page macro uses a hash (map)
  model — every key the template reads must exist (`hasErrorMsg`, `stackTrace`, …) because `<#if>` on
  a missing key throws; the real `SpecificationStep` always provides them. History-page macros use
  the real `HistoryOverview` data classes.
- FreeMarker pitfalls: no `?repeat` built-in (heading hashes via `<#list 1..depth as i>#</#list>`);
  a loop variable cannot be re-assigned with `<#local>` (the loop variable shadows the local).
- End to end: run the tests in `arete-gradle-test` and inspect the `<hash>Markdown` textareas in
  `build/reports/arete/test/`.
