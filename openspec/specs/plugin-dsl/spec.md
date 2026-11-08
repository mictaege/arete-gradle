# Plugin DSL Specification

## Purpose

The `arete { ... }` extension (`AreteExtension`) and how the plugin wires configuration from the Gradle
daemon into the test JVM via system properties on every `Test` task.

## Requirements

### Requirement: Extension options

The extension SHALL expose: `colorScheme` (default `AreteClassicColors()`), `captureStdout` /
`captureStderr` (default false), `captureMaxBuffer` (default 1048576), `historyEnabled` (default
true), `historyMaxAge` (default `HistoryConfig.DEFAULT_MAX_AGE`), `historyDir` (default null).
`captureMaxBuffer` and `historyMaxAge` SHALL be validated positive in `project.afterEvaluate`.

#### Scenario: Invalid buffer size rejected
- **WHEN** `arete { captureMaxBuffer = 0 }` is configured
- **THEN** the build fails in `afterEvaluate` with "captureMaxBuffer must be positive"

### Requirement: Plugin self-wiring

Applying the plugin SHALL add `io.github.mictaege:arete-gradle:<version>` as `testRuntimeOnly` so
the listener runs inside the test JVM. For every `Test` task, in `doFirst`, the plugin SHALL set
system properties: build dir, task name, the capture flags/buffer, the history settings (when
enabled), and all color-scheme values.

#### Scenario: Test JVM sees the configuration
- **WHEN** a test task starts
- **THEN** `io.github.mictaege.arete_gradle.buildDir`, `...taskName`,
  `junit.platform.output.capture.stdout/stderr/maxBuffer` and the color/history properties are set

### Requirement: Housekeeping task

The plugin SHALL register `cleanAreteHistory` (group `arete`) which deletes the current project's
history directory (see the history spec for the directory layout).

#### Scenario: Clean history
- **WHEN** `./gradlew cleanAreteHistory` runs
- **THEN** `<historyBase>/<rootProject.name>/<project path>` is deleted
