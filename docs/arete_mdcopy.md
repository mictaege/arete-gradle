# Arete markdown copy — design and maintenance notes

This document describes the "copy as markdown" feature of the arete reports: what every container
box exports, how the markdown is formatted, and how it is implemented. The outline structure the
buttons live in (collapsible containers, connector lines) is documented in
[arete_outline.md](arete_outline.md); what the history strips and run entries *contain* is
documented in [arete_history.md](arete_history.md).

## 1. Requirement

As a developer analysing a failed acceptance test, I want to copy the whole subtree of a container
(spec, feature, journey, scenario, describe, example container) as markdown into the clipboard, so
that I can paste it into a ticket, a merge request or a chat. Description, error messages, stack
traces, stdout/stderr and the run history go into the markdown; screenshots do not (they would only
resolve where the report files are reachable).

## 2. Where the buttons are

- Spec pages: the button is rendered in the `anchor` macro of `spec.ftlh`, so every container box
  (including the spec root) has one.
- History page: the spec rows and the container nodes have one (`history.ftlh`).
- Leaf steps never get a button; their markdown is part of the enclosing container's export.

The button (`markdown-copy-btn`, clipboard icon) embeds the markdown in a hidden
`<textarea id="<hash>Markdown">` and copies it with the shared `traceToClipboard` script; clicks on
the button do not fold the container (the outline script ignores `button` clicks). On the History
page the root-level icon variables are used (`card-markdown-icon`), on spec pages the
`spec-markdown-icon` — see [arete_outline.md](arete_outline.md) section 6 for the icon path levels.

## 3. Formatting rules

The rules were derived from a manually edited sample of a generated export: the structure of the
spec, the states, the error information and the run history were kept, and the formatting was
adjusted to read well in markdown. The general rules that came out of it:

- **Containers become headings** by nesting depth (`#` for the spec, `##`, `###`, …, capped at
  `######`), each preceded by a blank line. Leaves stay at the left margin; their nesting is
  expressed by the headings.
- **Narratives become blockquotes** built from the raw `NarrativeSection.lines` — the author's
  original markdown, not the HTML that the report pages render from it. Narrative values may be
  multi-line, so every line is split and prefixed with `> `.
- **Leaves become bullets with a colored state sign**:
  `` - <span style="color: #e78284">[x]</span> It should display the rooms popularity ``.
  The signs are arete's `ResultState.sign` — `[x]` failed, `[*]` aborted, `[/]` ignored, `[+]`
  successful — wrapped in an inline `<span style="color: …">` with the active color scheme's
  accent color for the state (the `-emph` variant, e.g. `arete_color_failed_emph`), so the
  indicator keeps its theme color when the markdown is pasted into a ticket or chat that renders
  inline HTML. The span is also what keeps `[x]` from being rendered as a task-list checkbox: GFM
  only treats `[x]` as a checkbox when it is the literal first content of the list item, and here
  the item starts with the span. No backticks around the sign — code spans inside raw HTML do not
  pick up the span color in several renderers. Renderers that strip `style` show the plain sign.
  The same colored sign is used for history run entries.
- **Leaf details are labeled sub-bullets with fenced code blocks, four spaces deeper than the
  leaf**, and the fence two spaces deeper than its bullet:

  ````
  - `[x]` It should display the rooms popularity
      - Message:
        ```
        Expected: is <8>
             but: was <9>
        ```
      - Stack trace:
        ```
        java.lang.AssertionError:
        Expected: is <8>
        but: was <9>
        at org.hamcrest.MatcherAssert.assertThat(MatcherAssert.java:20)
        ...
        ```
  ````

  `Message` keeps the original line alignment of the error message (content = indent + original
  line), while `Stack trace` flattens every line (leading whitespace and tabs removed), because a
  `printStackTrace` layout is noise. `Stdout` and `Stderr` follow the `Message` pattern.
  Details that don't exist are omitted; a successful leaf is a plain bullet.
- **History runs are bullets with sign and timestamp only** — no error text on the bullet line.
  The list sits under a `- History:` label at the same level as the other details, four spaces
  deeper than the leaf; each run is another four spaces deeper. A run with failure details carries
  its own `Message` / `Stack trace` / `Stdout` / `Stderr` blocks four spaces deeper again:

  ````
  - `[x]` It should display the rooms popularity
      - History:
          - `[x]` 30.10.2026 15:44:44
              - Message:
                ```
                Expected: is <8>
                ...
                ```
          - `[+]` 30.10.2026 12:29:35
  ````

- **`@ExampleGrid`/`@ExampleCsv`/`@Example` instances render exactly like other leaves** — a
  signed bullet per example with its details and history below, hidden examples omitted. (An
  earlier iteration rendered grids as a markdown table with a `State` column and anchor links; the
  list form was preferred because it matches normal examples and needs no extra anchors.)
- **Screenshots are not exported.** There is no link and no image reference.
- On the **History page**, the leaf's own `Message`/`Stack trace` details come from the newest run,
  and *all* runs are listed under `History:` (the History page has no "current run" of its own).

## 4. Implementation

All of it is server-side FreeMarker in `markdown-macros.ftlh`, rendered when the report page is
built (no JavaScript generation from the DOM):

- `markdownStep` walks the `SpecificationStep` tree (spec pages),
- `markdownNode` and `markdownOverviewSpec` walk the history overview model (History page),
- `markdownCopyButton` embeds the nested markdown in the hidden textarea and renders the button,
- `fencedBlock` renders a labeled fenced block at a given indent and optionally flattens its lines
  (used for stack traces).

The textarea content is HTML-escaped by FreeMarker and decoded by the browser when copied, so
angle brackets in assertion messages survive.

## 5. Testing and pitfalls

- `MarkdownTemplateTest` renders the macros directly: the spec-page macro is exercised with a hash
  (map) model — note that every key the template reads must exist (`hasErrorMsg`, `stackTrace`, …),
  because `<#if>` on a missing key throws; the real `SpecificationStep` always provides them. The
  History-page macros are exercised with the real `HistoryOverview` data classes.
- End to end: run the tests in `arete-gradle-test` and inspect the `<hash>Markdown` textareas in
  `build/reports/arete/test/`.
- FreeMarker pitfalls found on the way: there is no `?repeat` built-in (heading hashes are built
  with `<#list 1..depth as i>#</#list>`), and a loop variable cannot be re-assigned with
  `<#local>` — the loop variable shadows the local, so trimmed lines are assigned to a different
  name.
