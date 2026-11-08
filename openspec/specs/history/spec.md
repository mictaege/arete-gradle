# History Specification

## Purpose

Per-step run history for arete reports: the last N runs (default 10) of every leaf step, each with
state, start time, and for failures the error message, stack trace, screenshot and stdout/stderr.
It must work locally and on CI servers (e.g. Jenkins), survive `gradle clean`, and be safe when
other builds run on the same machine. User-facing configuration is in `README.md`; this spec is the
behavior contract for maintainers/agents.

## Requirements

### Requirement: Runs are retained per spec outside `build/`

The system SHALL keep the newest `historyMaxAge` runs (default 10) per spec, stored outside
`build/` so `gradle clean` does not remove them, in a task directory
`<base>/<rootProject.name>/<project path segments>/<task name>` with segments sanitised
(`[^A-Za-z0-9._-]` → `_`). Base directory resolution order SHALL be: DSL `historyDir` →
`-Parete.historyDir` → env `ARETE_HISTORY_DIR` → `<rootDir>/.gradle/arete-history`.
`historyMaxAge` and `captureMaxBuffer` SHALL be validated positive in `project.afterEvaluate`.
The `cleanAreteHistory` task (group `arete`) SHALL delete the current project's history.

#### Scenario: Default storage survives clean
- **WHEN** no `historyDir` is configured and `gradle clean` runs
- **THEN** `.gradle/arete-history/...` is untouched and earlier runs still render

#### Scenario: Workspace isolation
- **WHEN** two workspaces of the same project build on one machine
- **THEN** each uses its own store below its own `.gradle/` and they do not interfere

#### Scenario: History disabled without directory
- **WHEN** tests run without the history directory/run-id system properties (e.g. IDE JUnit runner)
- **THEN** `HistoryConfig.fromSystemProperties()` returns null, spec pages show no history strip,
  and `history.html` shows an info text

### Requirement: History code runs inside the test JVM

The plugin SHALL add itself as `testRuntimeOnly`, register `AreteTestListener` via
`META-INF/services`, and pass system properties in `doFirst`: history directory, one `runId` per task
(`yyyyMMdd'T'HHmmssSSS'Z'-<8 hex>`, UTC, sortable — shared by all forks), run timestamp (ISO instant)
and `maxAge`. There SHALL be no separate "enabled" property: no directory means disabled.

#### Scenario: One runId per task across forks
- **WHEN** a test task runs with `maxParallelForks > 1`
- **THEN** all forks write into the same logical run directory

### Requirement: Storage is plain properties files, per spec, leaf steps only

The system SHALL store, per spec hash directory (murmur3 of the JUnit uniqueId), one directory per
runId with `run.properties` and `steps.properties` as plain `.properties`/text files (no JSON
library — classpath clash risk on the consumer's test runtime), detail files
(`<stepHash>.errormsg.txt`, `.stacktrace.txt`, `.stdout.txt`, `.stderr.txt`, `.png`) only for
FAILED/ABORTED and only when non-empty, and only leaf steps (`!type.container`) — container state is
derived from leaves. Stored state SHALL be `cumulatedState()`, the same state that colours the report.

#### Scenario: Successful runs store only metadata
- **WHEN** a run is SUCCESSFUL or IGNORED
- **THEN** only metadata is written, no detail files

### Requirement: Concurrent and robust access

The system SHALL write each run to `.tmp-<runId>-*` then move it to `<runId>` with `ATOMIC_MOVE`
(plain move fallback; existing `<runId>` → save does nothing), guard every spec-directory access with
a JVM-wide monitor plus an exclusive `FileChannel.lock()` on `<specHash>/.lock`, prune runs beyond
`maxAge` after each save (newest first by run timestamp, then run id), and read tolerantly (ignore
dot-directories and directories without `run.properties`; a read exception skips that run/entry,
never fails the report).

#### Scenario: Parallel forks saving the same spec
- **WHEN** two forks of one task save the same spec concurrently
- **THEN** the lock serializes them and readers never see half-written runs

### Requirement: Screenshots travel as JUnit file attachments and are cleaned up

`arete`'s `ScreenshotExtension` SHALL publish screenshots via `context.publishFile` with name
`arete-screenshot.<extension>` (`SCREENSHOT_NAME_PREFIX`) and media type by extension. The plugin's
`AreteTestListener` SHALL remember the JUnit output root, accept in `fileEntryPublished` only files
named `arete-screenshot.*`, and register them in `SpecificationPlan` by JUnit uniqueId (map lookup,
because disabled tests can publish before the step exists). After copying into report and history,
`ScreenshotCleanup.delete` SHALL remove the attachment file and empty parent directories up to, but
not including, the engine-level directory below the output root (shared by parallel forks).
Compatibility: the plugin requires `arete` ≥ 2026.7 for screenshots.

#### Scenario: Write order on spec completion
- **WHEN** a spec finishes (`HtmlWriter.writeSpec`)
- **THEN** the order is loadHistory → copyResources → render spec.ftlh → historyStore.save →
  deleteOriginalScreenshots, and deletion MUST stay last so screenshots exist until copied

### Requirement: Spec pages show a history strip per leaf step

The strip SHALL show only when `SpecificationStep.showHistory` (leaf, earlier runs exist, at least
one run current or earlier not SUCCESSFUL — all-green histories are hidden). The folded line SHALL
show a clock icon then coloured dots, first dot = current run, then up to `historyMaxAge - 1` earlier
runs newest first, per-dot tooltip with date/time/state. Entries SHALL be state-classed boxes showing
only start date+time; a failed entry shows its error message inside the box even while folded;
entries with details unfold (screenshot first, Prism, copy buttons). The strip SHALL be a
`<details class="history">` (no JS for folding) and an outline child of its leaf. Entry ids SHALL be
`<stepHash>HistoryCurrent` / `<stepHash>History<runId>` on spec pages, `<specHash>_<stepHash>_<runId>`
on the History page.

#### Scenario: All-green history is hidden
- **WHEN** a leaf's current and all earlier runs are SUCCESSFUL
- **THEN** no strip is rendered for that leaf

### Requirement: The History page lists suspicious leaves across all specs

`history.html` SHALL have a "History" menu entry (weather icon) on all pages. `HistoryStore.overview()`
SHALL read every spec directory of the task store (all runs up to maxAge) and list a leaf when at
least one of its runs is FAILED or ABORTED — even if the latest run succeeded — showing all its runs
newest first. Grouping SHALL be spec → nested containers (from `parents` metadata) → leaves, with
container labels prefixed by `StepType.label` unless the display name already starts with it. Leaf
colour = newest run state; container/spec = `cumulate` of children (FAILED > ABORTED > IGNORED >
SUCCESSFUL). Ordering SHALL be by `HistoryImportance` run weights FAILED 3 / ABORTED 2 / IGNORED 1 /
SUCCESSFUL 0 compared lexicographically newest first (missing run = 0), containers/specs rank like
their most important descendant, ties by stored index then display name.

#### Scenario: Stale failure still listed
- **WHEN** a leaf failed in an earlier run but its latest run succeeded
- **THEN** the leaf is still listed on the History page with all its runs

#### Scenario: Current failure ranks first
- **WHEN** two leaves are listed, one failed in the latest run and one only in older runs
- **THEN** the currently failing leaf is ordered before the other

## Design and maintenance notes

- Files: `History.kt` (config/records/store: save, load, overview, prune, locking),
  `AretePlugin.kt` (DSL, directory resolution, run id, system properties, `cleanAreteHistory`),
  `HtmlWriter.kt` (load before render, save after render, `history.html`, icons, cleanup),
  `SpecificationPlan.kt` (`ResultState.isFailure`, `cumulate`, `StepType.label`, screenshot map,
  `history`/`showHistory`/`historyTimeStamp`), `AreteTestListener.kt` (output root, screenshot
  reception), templates `history-macros.ftlh`, `spec.ftlh`, `history.ftlh`, icons
  `icon-clock.ftlh`/`icon-weather.ftlh`, styles in `arete-css.ftlh`. Tests: `HistoryStoreTest`,
  `HistoryTemplateTest`, `ScreenshotCleanupTest`.
- `steps.properties` keys: per leaf `<stepHash>.state/.displayName/.startTime/.type/.index/.parents`
  (ISO instants; parents = comma-separated container hashes outermost-below-spec → direct parent),
  per container `container.<hash>.displayName/.type/.index`. Display name is stored for the History
  page and for recognizing renamed/reordered steps. Start time is per step (log correlation); the
  run timestamp is only for sorting/pruning. Runs without `startTime` are skipped when reading.
- History key is the hash of the JUnit uniqueId: renaming/moving a spec class or step starts a new
  history; for `@Examples`/`@ExampleGrid`/`@ExampleCsv` the invocation index is part of the id, so
  reordering examples moves history between rows.
- FreeMarker pitfall: Kotlin `val isFailure` is exposed as bean property `failure`; `state.isFailure`
  in a template breaks boolean expressions. `Freemaker.cfg` uses `RETHROW_HANDLER`, so a template
  error in `spec.ftlh` aborts `writeSpec` before `historyStore.save` — no history is stored at all.
  Keep `HistoryTemplateTest` in sync when templates change.
- Up-to-date/cached test tasks don't run the listener, so no run is recorded; a `FROM-CACHE` test
  task produces no arete report (the report is not a declared task output).
- Stdout/stderr history exists only when `captureStdout`/`captureStderr` are enabled.
- Configuration cache: `project.buildDir`/`project` access in `doFirst`/`doLast` is deprecated and
  not configuration-cache friendly (pre-existing, still open).
- Jenkins: published HTML needs a relaxed CSP for inline JS/CSS (folding works without JS, copy
  buttons/scrolling/Prism need it); ephemeral agents must persist `historyDir` themselves.
- Stored-run screenshots are copied to `specs/history/<runId>_<stepHash>.png`; existing files with
  the same size are not copied again.
- End-to-end check: run a spec in `arete-gradle-test` repeatedly (with `clean`, toggling a step
  pass/fail) and inspect `.gradle/arete-history/arete-gradle-test/test/<specHash>/`,
  `build/reports/arete/test/specs/*.html`, `build/reports/arete/test/history.html`, and that
  `build/junit-jupiter` holds no leftover screenshots.
