# Color Schemes Specification

## Purpose

The theming of the HTML report: the `AreteColorScheme` interface, predefined schemes, how the scheme
travels from the Gradle DSL to the test JVM, and how the CSS variables and icon SVGs are generated.

## Requirements

### Requirement: Color scheme contract

`AreteColorScheme` SHALL expose the variables `arete_color_background`, `arete_color_card`,
`arete_color_foreground`, `arete_color_ignored(_emph)`, `arete_color_successful(_emph)`,
`arete_color_aborted(_emph)`, `arete_color_failed(_emph)`, `arete_color_neutral(_emph)` and
`arete_plantuml_theme` (default `"plain"`). Predefined schemes SHALL include `AreteClassicColors`
(the default), `CatppuccinMochaColors`, `CatppuccinMacchiatoColors`, `CatppuccinFrappeColors`,
`CatppuccinLatteColors` and `DraculaColors`, all `open` so single properties can be overridden.

#### Scenario: Override one property of a predefined scheme
- **WHEN** `arete { colorScheme = object : CatppuccinFrappeColors() { override var arete_plantuml_theme = "amiga" } }`
- **THEN** the report uses Frappe colors with the PlantUML theme `amiga`

### Requirement: Scheme travels via system properties into the test JVM

In each `Test` task's `doFirst`, the plugin SHALL pass every scheme value as a system property
(`AreteColorSchemeProvider.ARETE_COLOR_*` constants); `AreteColorSchemeProvider.colorScheme` in the
test JVM SHALL rebuild the scheme from them. Templates receive it as the `colorScheme` data-model
entry.

#### Scenario: Templates read theme colors
- **WHEN** a page is rendered
- **THEN** the inline CSS defines `--arete-color-*` from the active scheme

### Requirement: Icons are scheme-aware SVG templates

Each `icon-*.ftlh` template SHALL be a FreeMarker SVG parameterized by `foreground`.
`HtmlWriter.extractIcons` SHALL render every icon in three variants — `-bg` (background color),
`-fg` (foreground color), `-neutral` (neutral color) — into `icons/`, and the CSS SHALL reference them
via `--root-icon-*` (report root pages) and `--page-icon-*` (spec pages, `./../icons/`). Menu,
tagged, card and search icon classes SHALL have hover/active variants switching fg↔bg.

#### Scenario: Icon variants exist for every icon
- **WHEN** the report is generated
- **THEN** `icons/icon-<name>-{bg,fg,neutral}.svg` exists for each icon template, e.g.
  `icon-tags-bg.svg`, `icon-tags-fg.svg`, `icon-tags-neutral.svg`
