# Arete history — design and maintenance notes

This document describes the run history feature that was added to the arete family in release
`2026.7` (branch `feature/history`, merged into `develop`). It is meant for developers and agents
who maintain or extend the feature. For how users configure it, see the `README.md`.

## 1. Requirement

As a developer analysing acceptance-test problems, especially on a CI server such as Jenkins, I want to
see the history of every arete step: the last N runs (default 10), each with its state, the step's
start date and time, and for failures the error message, stack trace, screenshot and stdout/stderr.
The history must:

- work locally and on the build server,
- survive `gradle clean`,
- be safe when other builds run on the same machine (same project, other workspaces or other projects).

## 2. Overview of the changes

| Repository | What changed |
|---|---|
| `arete` | `ScreenshotExtension` publishes screenshots as JUnit file attachments (`ExtensionContext.publishFile`). `ScreenshotTaker` got `SCREENSHOT_NAME_PREFIX`, `getScreenshotName()` and `getMediaType()`. The old directory API (`getScreenshotsDir()`, `getScreenshotFile()`) and `normalize()` / `extends Normalizer` were removed. |
| `arete-gradle` | History store (`History.kt`), plugin settings and system properties (`AretePlugin.kt`), report integration (`HtmlWriter.kt`), screenshot reception and cleanup (`AreteTestListener.kt`, `SpecificationPlan.kt`, `ScreenshotCleanup`), history strip on spec pages, new History page, clock and weather icons, tests. |
| `arete-gradle-test` | Uses `2026.7` and sets `historyMaxAge = 10` as an example. No history-specific specs (a demo spec was removed again). |

### Files in `arete-gradle`

| File | Role |
|---|---|
| `src/main/kotlin/.../History.kt` | `HistoryConfig`, `HistoryRecord`, `HistoryContainer`, `HistoryEntry`, overview model (`HistoryOverview`, `HistoryOverviewSpec`, `HistoryOverviewNode`, `HistoryImportance`) and `HistoryStore` (save, load, overview, prune, locking). |
| `src/main/kotlin/.../AretePlugin.kt` | DSL properties, resolution of the history directory, run id, system properties for the test JVM, `cleanAreteHistory` task. |
| `src/main/kotlin/.../HtmlWriter.kt` | Loads history before rendering a spec, saves the current run after rendering, writes `history.html`, extracts icons, `ScreenshotCleanup`. |
| `src/main/kotlin/.../SpecificationPlan.kt` | `ResultState.isFailure`, shared `cumulate(...)`, `StepType.label`, screenshot map in `SpecificationPlan`, `SpecificationStep.history` / `showHistory` / `historyTimeStamp`. |
| `src/main/kotlin/.../AreteTestListener.kt` | Remembers the JUnit output root, receives screenshots via `fileEntryPublished`. |
| `src/main/resources/templates/history-macros.ftlh` | Shared macros: `stateClass`, `historyStrip`, `historyRun`, `historyErrorMsg`, `clipboardScript`, `copyLinkButton` (and the shared `outlineScript`, see [arete_outline.md](arete_outline.md)). |
| `src/main/resources/templates/spec.ftlh` | History strip below each leaf step (`historyDisplay`), History menu entry. The spec tree itself is rendered as an outline, see [arete_outline.md](arete_outline.md). |
| `src/main/resources/templates/history.ftlh` | The History overview page. |
| `src/main/resources/templates/icon-clock.ftlh`, `icon-weather.ftlh` | Font Awesome `fa-regular fa-clock` and `fa-solid fa-cloud-sun-rain` as colour-scheme-aware SVG templates. |
| `src/main/resources/templates/arete-css.ftlh` | History styles and icon classes. |
| `src/test/kotlin/.../HistoryStoreTest.kt` | Store round-trip, pruning, concurrency, overview grouping, state cumulation, ordering. |
| `src/test/kotlin/.../HistoryTemplateTest.kt` | Renders `history.ftlh` and the shared macros (populated, empty, disabled), FreeMarker boolean access. |
| `src/test/kotlin/.../ScreenshotCleanupTest.kt` | Deleting screenshot attachments and empty directories. |

## 3. Data flow of one test task

All report and history code runs **inside the test JVM** (the plugin adds `arete-gradle` as
`testRuntimeOnly`, and `AreteTestListener` is registered via `META-INF/services`). The Gradle
daemon only prepares system properties.

1. **Gradle daemon (`AretePlugin`, `doFirst` of each `Test` task):** if `historyEnabled`, it computes
   the task's history directory, one `runId` and the run timestamp, and passes them together with
   `historyMaxAge` as system properties (see section 4). Because `runId` is created once per task,
   all forks (`maxParallelForks > 1`) write into the same logical run.
2. **Test JVM, during execution:** `AreteTestListener` builds the `SpecificationPlan` tree as before.
   Screenshots arrive via `fileEntryPublished` (section 7).
3. **When a spec finishes (`HtmlWriter.writeSpec`)**, in this order:
   1. `loadHistory`: for every leaf step, load the previous runs of that step (`HistoryStore.load`)
      and copy their screenshots to `specs/history/`.
   2. `copyResources`: copy the current screenshots into the report.
   3. Render `spec.ftlh`.
   4. `historyStore.save(...)`: store the current run of this spec (leaf steps only).
   5. `deleteOriginalScreenshots`: delete the JUnit attachment files.

   Step 5 must stay last, otherwise the screenshot is gone before it is copied into the report and
   the history store.
4. **When the plan finishes (`HtmlWriter.finishPlan`):** besides the existing pages, it builds
   `HistoryStore.overview(...)` over all specs in the task's store and renders `history.html`.

`HistoryConfig.fromSystemProperties()` returns `null` when the directory or run id property is
missing (e.g. tests started by the IDE's own JUnit runner). History is then disabled: spec pages
show no strip and `history.html` shows an info text.

## 4. Configuration

DSL (`AreteExtension`), all optional and backward compatible:

```kotlin
arete {
    historyEnabled = true   // default true
    historyMaxAge = 10      // number of runs kept per spec (HistoryConfig.DEFAULT_MAX_AGE), not a time span
    historyDir = file("/var/lib/arete-history/my-job")  // default null
}
```

- `historyMaxAge` and `captureMaxBuffer` are validated in `project.afterEvaluate` (must be positive).
- Base directory resolution (`historyBaseDirectory`): DSL `historyDir` → `-Parete.historyDir` →
  env `ARETE_HISTORY_DIR` → `<rootDir>/.gradle/arete-history`.
- Task directory: `<base>/<rootProject.name>/<project path segments>/<task name>`, each segment
  sanitised with `[^A-Za-z0-9._-]` → `_`.
- `cleanAreteHistory` (group `arete`) deletes the history of the current project
  (`<base>/<rootProject.name>/<project path>`).

System properties passed to the test JVM (constants in `HistoryConfig`), only when history is enabled:

| Property | Meaning |
|---|---|
| `io.github.mictaege.arete_gradle.history.directory` | Task history directory (absolute). |
| `io.github.mictaege.arete_gradle.history.runId` | `yyyyMMdd'T'HHmmssSSS'Z'-<8 hex>` (UTC, sortable), see `HistoryConfig.newRunId`. |
| `io.github.mictaege.arete_gradle.history.timestamp` | Task start as ISO instant; used to sort runs. |
| `io.github.mictaege.arete_gradle.history.maxAge` | Runs kept per spec. |

There is intentionally no separate "enabled" property: no directory means disabled.

## 5. Storage layout

```
<task history dir>/
  <specHash>/                         # murmur3 hash of the spec's JUnit uniqueId (SpecificationStep.uniqueHash)
    .lock                             # file lock for this spec
    <runId>/
      run.properties                  # runId, timestamp (task start), specDisplayName
      steps.properties                # per leaf step and per parent container, see below
      <stepHash>.errormsg.txt         # only FAILED/ABORTED, only if not empty
      <stepHash>.stacktrace.txt       # only FAILED/ABORTED, only if not empty
      <stepHash>.stdout.txt           # only FAILED/ABORTED, only if captured and not empty
      <stepHash>.stderr.txt           # only FAILED/ABORTED, only if captured and not empty
      <stepHash>.png                  # only FAILED/ABORTED, if a screenshot exists
    .tmp-<runId>-*/                   # temporary, while a run is written
```

`steps.properties` keys:

- `<stepHash>.state`, `.displayName`, `.startTime` (ISO instant of the step's own start),
  `.type` (`StepType`), `.index` (position among its siblings), `.parents` (comma-separated
  container hashes from the outermost container below the spec down to the direct parent).
- `container.<hash>.displayName`, `.type`, `.index` for every parent container.

Design decisions:

- **Plain `.properties` and text files, no JSON library.** The plugin is on the consumer's test
  runtime classpath; Jackson/Gson could clash with the consumer's versions. The files are also easy to
  inspect on a CI agent.
- **Stored per spec, not per task run.** `--tests` filters, IDE runs or a spec skipped in one run don't
  push the spec's history out.
- **Only leaf steps are stored** (`!type.container`: Given/When/Then, `@ItShould`, `@Step`, single
  example rows). Containers (spec, feature, journey, scenario, describe, example containers) have no
  history of their own; their state is derived from the leaves.
- **Details only for failures.** SUCCESSFUL/IGNORED runs store only metadata, which keeps the store
  small with thousands of tests.
- **The display name is stored** although the strip doesn't show it; it is used by the History page
  and helps to recognise renamed steps or reordered examples later.
- **Start time per step**, not the task start: what developers need for log correlation. The run
  `timestamp` is only used to sort and prune runs. Since nothing was released before, there is no
  fallback for runs without `startTime`; such runs are skipped when reading.
- **Stored state** of a leaf is `cumulatedState()`, the same state that colours the report.

## 6. Concurrency and robustness

- **Unique run directory:** each run is written to `.tmp-<runId>-*` and then moved to `<runId>` with
  `ATOMIC_MOVE` (plain move as fallback). Readers never see half-written runs. If `<runId>` already
  exists for that spec, `save` does nothing.
- **Locking:** every access to a spec directory (`save`, `load`, `overview`) runs in `withLock`: a
  JVM-wide monitor per lock path (`FileChannel.lock()` is not re-entrant within one JVM) plus an
  exclusive `FileChannel.lock()` on `<specHash>/.lock`, which works across processes.
- **Pruning:** after each save, runs beyond `maxAge` (newest first by run timestamp, then run id) are
  deleted.
- **Tolerant reading:** directories starting with `.` and directories without `run.properties` are
  ignored; any exception while reading a run or entry makes that run/entry be skipped, not the report
  fail.
- **Isolation between builds:** the default directory is inside the project's own `.gradle/`, so every
  workspace (including Jenkins `ws@2`) has its own store and the data is removed with the workspace.
  For CI jobs that wipe the workspace, `historyDir` / `ARETE_HISTORY_DIR` points to persistent storage;
  the key (e.g. `JOB_NAME`) provides isolation.

## 7. Screenshots

### Old mechanism (≤ 2026.6)

`arete` wrote screenshots to `java.io.tmpdir/arete_screenshots/<hash(uniqueId)>.png`, and
`arete-gradle` rebuilt the same path by convention. This directory was shared by all builds on a
machine (overwrites, deletes, stale files from crashed runs), and the path rule was duplicated in Java
and Kotlin. An intermediate solution with a configurable `arete.screenshotsDir` system property was
rejected: nobody needs to configure it.

### Current mechanism (2026.7)

- **`arete`:** `ScreenshotExtension` (a `TestWatcher`) calls `screenshotTaker.getImageBytes()` for the
  results in `takeWhen()` and, if the bytes are not empty, publishes them with
  `context.publishFile(getScreenshotName(), getMediaType(), path -> Files.write(path, bytes))`.
  The name is `arete-screenshot.<extension>` (`SCREENSHOT_NAME_PREFIX`); the media type follows the
  extension (png/jpg/jpeg, otherwise octet-stream). Exceptions are ignored, as before.
  JUnit chooses a unique output directory per test (under `build/` for Gradle runs).
- **`arete-gradle`:** `AreteTestListener.testPlanExecutionStarted` remembers
  `testPlan.outputDirectoryCreator.rootDirectory`. `fileEntryPublished` accepts only files whose name
  starts with `arete-screenshot.` and registers them in `SpecificationPlan` by JUnit unique id.
  The map is used because `fileEntryPublished` for disabled tests can arrive before the step exists.
  `SpecificationStep.screenshot` is a lookup in that map.
- **Cleanup requirement:** on a CI server with thousands of screenshots per build, screenshots must
  survive **only** in the report and in the history. After copying, `ScreenshotCleanup.delete` removes
  the attachment file and then the empty parent directories up to, but **not including**, the
  engine-level directory directly below the output root. That directory is shared by parallel forks;
  deleting it could race with `createDirectories` in another fork. If the file is not below the output
  root (custom `OutputDirectoryCreator`), only the file is deleted.
- Without the plugin, screenshots stay as normal JUnit attachments under the JUnit output directory and
  are removed by `clean`.

### Compatibility

- The 2026.7 plugin needs `arete` 2026.7; with an older `arete`, no screenshots appear in the report.
- API break in `arete` 2026.7 (release notes): `ScreenshotTaker` no longer extends `Normalizer`;
  `normalize()`, `getScreenshotsDir()` and `getScreenshotFile()` were removed. `Normalizer` and
  `UniqueIdToHashNormalizer` themselves still exist.
- If Gradle's own test report shows JUnit file attachments (newer Gradle versions), those links will
  point to deleted files. This was not verified (validation used Gradle 8.7).

## 8. Spec page: history strip

Rendered by `historyDisplay` in `spec.ftlh` using `history.historyStrip` and `history.historyRun`.

- Shown only if `SpecificationStep.showHistory`: the step is a leaf, earlier runs exist, and at least
  one run (current or earlier) is not SUCCESSFUL. All-green histories are hidden.
- The strip is a `<details class="history">` (no JavaScript for folding), connected to the outline
  tree as a child of its leaf step, with the run entries as further children (structure and
  connector geometry: see [arete_outline.md](arete_outline.md)). It shows with normal font weight
  even inside bold container boxes.
- Folded line: a small clock icon (`tagged-icon tagged-clock`, vertically centred via flex) followed by
  coloured dots. The **first dot is the current run**, followed by up to `historyMaxAge - 1` earlier
  runs, newest first. No arrow, no highlighting of the first dot; tooltip per dot with date/time/state.
- Click behaviour (deliberately simple): any click on the line toggles the whole history; on open it
  scrolls into view (`scrollIntoView({block: 'nearest'})`). No automatic jump to or unfolding of an entry.
- Entries: coloured boxes with the same state classes as step boxes (`stateClass` → `successful`,
  `failed`, `aborted`, `ignored`, `neutral`). The box shows only the start date and time
  (`historyTimeStamp`: localized medium date + blank + medium time, no comma), no state text, no test
  name. Camera / file icons indicate a screenshot / stack trace. A failed entry shows its error message
  inside the box below the time, visible while folded.
- Entries with stack trace, stdout, stderr or screenshot can be unfolded (no arrow); they render these
  like a failed step: screenshot first, Prism highlighting, copy-to-clipboard buttons. Entries without
  details are plain boxes (`*-no-hover`).
- Entry ids (base for the copy-button textarea ids `…StackTrace`, `…Stdout`, `…Stderr`) are
  `<stepHash>HistoryCurrent` for the current run and `<stepHash>History<runId>` for stored runs on spec
  pages, and `<specHash>_<stepHash>_<runId>` on the History page, so they are unique per page.
- The per-step timestamp on failed step boxes was removed; the strip now carries the time.

## 9. History page (`history.html`)

- Menu entry "History" on all pages with the weather icon (`menu-icon-weather`, `spec-menu-icon-weather`
  on spec pages). Icons are FreeMarker SVG templates rendered three times per colour scheme
  (`-bg`, `-fg`, `-neutral`) by `HtmlWriter.extractIcons`; for both `clock` and `weather` all CSS
  variants exist (`--page-icon-*`, `--root-icon-*`, `.menu-icon-*`, `.spec-menu-icon-*` with hover and
  active rules, `.tagged-*`, `.card-tagged-*`), even where not used yet. Root-level pages use the
  `card-*` / `--root-*` variants because of the relative icon path.
- Content: `HistoryStore.overview()` reads every spec directory of the task store, all runs up to
  `maxAge`. A leaf is listed when **at least one** of its runs is FAILED or ABORTED, even if its latest
  run succeeded. All runs of a listed leaf are shown (including successful ones), newest first.
- Grouping: spec → nested containers (from `parents` metadata) → leaves; a container appears once
  with all its listed leaves below. Container labels are prefixed with `StepType.label` unless the
  display name already starts with it.
- Layout follows the other pages: `card main` (block layout; `card main-step` would create columns),
  coloured boxes like on spec pages, containers bold, and the same folded history strip per leaf
  (with `card-tagged-clock` and `card-icon-*`). The page structure — collapsible spec rows and
  container nodes, connector lines, the spec-link button that opens the spec report — is
  documented in [arete_outline.md](arete_outline.md).
- State colours: leaf = state of its newest run (as on spec pages); container/spec = `cumulate` of its
  children (FAILED > ABORTED > IGNORED > SUCCESSFUL).
- Copy-link buttons on spec and container boxes copy a Markdown link to the **spec page**
  (`specs/<specHash>.html#<hash>`); if the spec has no report page in this build, to the entry on
  `history.html`. Names are escaped with `?js_string`.
- Screenshots of stored runs are copied to `specs/history/<runId>_<stepHash>.png`; existing files with
  the same size are not copied again.

### Ordering

Most relevant problems first (`HistoryImportance`):

- Weight per run: FAILED 3, ABORTED 2, IGNORED 1, SUCCESSFUL 0.
- Leaves are compared lexicographically by their own run weights, newest first; missing runs count as
  0. So a leaf that is red now always comes before one that is green now; a persistent failure ranks
  above a fresh flake; among green-now leaves the one that failed more recently comes first.
- A container or spec ranks like its most important descendant leaf.
- Ties: stored index, then display name (specs: display name).
- A lexicographic comparison was chosen over a weighted score because a score could let many old
  failures outweigh one current failure, and it needs no tuning parameters.
- Runs are compared by position per leaf, not aligned by run timestamp, because a leaf may not have
  run in every task run (e.g. `--tests` filter).
- Spec pages keep their source order; dots inside a strip stay newest first.

## 10. Testing

- `arete`: `./gradlew test` (`ScreenshotExtensionTest`: published name, media type, bytes, `takeWhen`,
  empty bytes not published).
- `arete-gradle`: `./gradlew test` (`HistoryStoreTest`, `HistoryTemplateTest`, `ScreenshotCleanupTest`).
- End to end: run e.g. `ComplaintMediationSpec` in `arete-gradle-test` several times (with `clean` in
  between, toggling a step between pass and fail) and check
  `.gradle/arete-history/arete-gradle-test/test/<specHash>/`, `build/reports/arete/test/specs/*.html`,
  `build/reports/arete/test/history.html`, and that `build/junit-jupiter` holds no leftover screenshots.

## 11. Pitfalls and known limitations

- **FreeMarker and Kotlin `is…` properties:** a Kotlin `val isFailure` is exposed to FreeMarker as the
  bean property `failure`. Writing `state.isFailure` in a template resolves to a method and breaks
  boolean expressions. Because `Freemaker.cfg` uses `RETHROW_HANDLER`, an exception in `spec.ftlh`
  aborts `writeSpec` **before** `historyStore.save`, so no history is stored at all. Keep template tests
  (`HistoryTemplateTest`) up to date when templates change.
- **History key:** the hash of the JUnit unique id. Renaming or moving a spec class or step starts a
  new history; for `@Examples` / `@ExampleGrid` / `@ExampleCsv` the invocation index is part of the id,
  so reordering examples moves history to other rows.
- **Up-to-date or cached test tasks** don't run the listener, so no run is recorded (correct, nothing
  ran). The arete report is not a declared task output, so a `FROM-CACHE` test task produces no report.
- **Configuration cache:** `project.buildDir` and `project` access in `doFirst`/`doLast` are deprecated
  and not configuration-cache friendly. This predates the history feature and is still open.
- **Stdout/stderr** history only exists when `captureStdout` / `captureStderr` is enabled.
- **Jenkins:** published HTML needs a relaxed Content-Security-Policy for the inline JS/CSS (folding
  works without JS, but copy buttons, scrolling and Prism need it). Ephemeral agents must restore and
  save the history directory themselves (e.g. `stash`/`unstash`, caches, or a persistent `historyDir`).
