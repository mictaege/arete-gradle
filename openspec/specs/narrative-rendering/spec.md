# Narrative Rendering Specification

## Purpose

How `@Narrative` content is rendered in the HTML report: markdown text via commonmark, embedded
images, PlantUML diagrams rendered server-side, and file attachments — all copied into the report
directory.

## Requirements

### Requirement: Narrative text is rendered as markdown per line

`NarrativeSection` SHALL expose `header` and raw `lines` from the annotation, and `formattedLines`
SHALL render each trimmed line through commonmark (`Parser` → `HtmlRenderer`) so emphasis, lists etc.
survive. The spec page SHALL render the header (`<p class="narrative">`) and each formatted line in a
`narrative` div.

#### Scenario: Emphasis in narrative
- **WHEN** a narrative line contains `*traveler*`
- **THEN** the spec page shows it as `<em>traveler</em>`

### Requirement: Images and PlantUML diagrams

`imageResourcePath` entries SHALL become `Picture` resources resolved from the classpath (fileName =
last path segment); `plantUml` entries SHALL become `PlantUmlDiagram`s rendered with PlantUML
`SourceStringReader` to PNG (theme from the color scheme's `arete_plantuml_theme`). Both are combined
in `images`; the spec page SHALL render them in an image grid (`image-grid-css/js.ftlh`) with a modal
viewer (click opens `openModal`, `data-modal-index`).

#### Scenario: PlantUML diagram in report
- **WHEN** a `@Narrative(plantUml = "@startuml ... @enduml")` is present
- **THEN** the spec page shows the rendered PNG diagram in the image grid

### Requirement: Attachments are copied and linked

`attachmentResourcePath` entries SHALL become `Attachment` resources. `HtmlWriter.copyResources` SHALL
write all narrative resources (images + diagrams + attachments) into the spec's report directory
(`specs/`), and the page SHALL link attachments for download.

#### Scenario: PDF attachment
- **WHEN** `attachmentResourcePath = "com/example/example-invoice.pdf"`
- **THEN** the file is copied next to the spec page and linked from it
