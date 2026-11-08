# Stdout/Stderr Capture Specification

## Purpose

Optional capture of a step's stdout/stderr into the report (and, for failures, into the history), to
debug unclear or flaky failures especially on CI.

## Requirements

### Requirement: DSL switches and buffer limit

The extension SHALL provide `captureStdout` and `captureStderr` (default false) and
`captureMaxBuffer` (default 1048576 bytes, must be positive). The plugin SHALL pass them to the test
JVM as the system properties `junit.platform.output.capture.stdout`, `junit.platform.output.capture.stderr`
and `junit.platform.output.capture.maxBuffer`.

#### Scenario: Enabling capture
- **WHEN** `arete { captureStdout = true; captureStderr = true }` is configured
- **THEN** the test JVM captures per-test output and publishes it as report entries

### Requirement: Captured output attaches to the step

JUnit publishes captured output as report entries with keys `stdout` / `stderr`.
`SpecificationStep.addReportEntry` SHALL append non-blank values to `stdoutEntries`/`stderrEntries`;
the `stdout`/`stderr` properties SHALL join them with newlines. The report SHALL render them for the
step (failure details on spec pages; stored in history only for FAILED/ABORTED runs, and only when
the respective capture switch was on — see the history spec).

#### Scenario: Stdout shown for a step
- **WHEN** a step printed to stdout with capture enabled
- **THEN** the spec page shows its stdout block for that step
