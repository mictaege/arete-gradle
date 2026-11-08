# Report Model Specification

## Purpose

The in-JVM model that the arete report is built from: `AreteTestListener` (a JUnit Platform
`TestExecutionListener` registered via `META-INF/services`) builds a `SpecificationPlan` tree of
`SpecificationStep`s from the test execution events, and `SpecificationWriter`s (`ConsoleWriter`,
`HtmlWriter`) render it. Everything else (pages, history, refs, …) reads this model.

## Requirements

### Requirement: Test events map to typed steps

On `executionStarted` (and `executionSkipped`, which delegates to it) the listener SHALL add a
`SpecificationStep` typed by the arete annotation found on the test id: `@Spec`→SPEC, `@Feature`→FEATURE,
`@Journey`/`@VariableJourney`→JOURNEY, `@Step`→STEP, `@Scenario`→SCENARIO, `@Given`/`@When`/`@Then`→
GIVEN/WHEN/THEN, `@Describe`→DESCRIBE, `@ItShould`→IT_SHOULD, and `@Examples`/`@ExampleGrid`/`@ExampleCsv`
→ EXAMPLE_CONTAINER / EXAMPLE_INSTANCE / EXAMPLE_GRID_CONTAINER / EXAMPLE_GRID_INSTANCE depending on
`isContainer`. Steps SHALL be attached under their `parentId` in the plan tree.

#### Scenario: VariableJourney renders as JOURNEY
- **WHEN** a `@VariableJourney` executes
- **THEN** its step in the plan has type JOURNEY (each variant invocation is a child)

### Requirement: Results, report entries and files attach to steps

`executionFinished` SHALL store the `TestExecutionResult` on the matching step (`addResult`).
`reportingEntryPublished` SHALL append non-blank `stdout`/`stderr` key-value pairs to the step's
`stdoutEntries`/`stderrEntries`. `fileEntryPublished` SHALL register files whose name starts with
`arete-screenshot.` (see `SCREENSHOT_NAME_PREFIX`) in the plan's screenshot map by uniqueId, using the
output root remembered in `testPlanExecutionStarted`.

#### Scenario: Stdout of a step is captured
- **WHEN** stdout capture is enabled and a step publishes a report entry with key `stdout`
- **THEN** the step's `stdout` property contains that text

### Requirement: ResultState and cumulation

A step's `resultState` SHALL map SUCCESSFUL→SUCCESSFUL, ABORTED→ABORTED, FAILED→FAILED, and null
(no result, e.g. disabled) → HIDDEN when `@HiddenIfDisabled` (or `@Step`) is present, else IGNORED.
`cumulate(...)` SHALL reduce child states with precedence FAILED > ABORTED > IGNORED > SUCCESSFUL;
containers display their cumulated state.

#### Scenario: Disabled scenario without HiddenIfDisabled
- **WHEN** a `@Disabled` scenario has no `@HiddenIfDisabled`
- **THEN** its resultState is IGNORED and it renders in the report as ignored

### Requirement: Hashing and identity

Each step's `uniqueHash` SHALL be the `UniqueIdToHashNormalizer` hash of its JUnit uniqueId (see the
arete `internals` spec — stability is a cross-repo contract). `testClass`/`testClassName` come from
the `ClassSource`/`MethodSource` of the test id; annotations are read via `sourceAnnotations`.

#### Scenario: Spec page file name
- **WHEN** a spec step is written
- **THEN** its page is `specs/<uniqueHash>.html`

### Requirement: Writers and lifecycle

`SpecificationWriter.allWriter` SHALL contain `ConsoleWriter` (prints an ANSI-coloured tree per
spec to the console, skipping hidden steps) and `HtmlWriter`. When a step with `@Spec` finishes, all
writers' `writeSpec` SHALL run for it; at `testPlanExecutionFinished` all writers' `finishPlan` SHALL
run.

#### Scenario: Console output mirrors the tree
- **WHEN** a spec finishes
- **THEN** the console shows the spec tree with state-coloured lines and red error quotes for failed
  steps
