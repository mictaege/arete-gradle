# AGENTS.md — arete-gradle

## Project purpose
`arete-gradle` is the Gradle reporting plugin for the [arete](https://github.com/mictaege/arete)
JUnit 6 BDD testing framework. Applied to a consumer project, it adds Gradle tasks that generate
HTML reports (parallel to the standard test report) for arete specifications/scenarios, written to
`build/reports/arete/<task>/index.html`. It also supports options such as excluding disabled tests
from the report.

Part of the arete family: it depends on the `arete` core library for the extension model, and is
itself consumed/exercised by the `arete-gradle-test` sample project.

## Technical stack
- Language: Kotlin (plugin sources under `src/main/kotlin`), targeting JVM 17.
- Build tool: Gradle (Kotlin DSL, `build.gradle.kts`) with the Gradle Kotlin plugin
  (`org.jetbrains.kotlin.jvm`) — build via `./gradlew`.
- Plugin packaging: `java-gradle-plugin` + `com.gradle.plugin-publish`, published to the Gradle
  Plugin Portal as `io.github.mictaege.arete`.
- Resources: `src/main/resources/templates` holds HTML report templates;
  `src/main/resources/META-INF/services` wires up service implementations.
- Dependencies: JUnit Jupiter API/platform-launcher (to introspect arete/JUnit test results), plus
  the Kotlin stdlib.
- Release/publishing: `org.jreleaser`, publishing to Maven Central under group `io.github.mictaege`
  (same coordinates/version scheme as `arete`).
- License: Apache License 2.0.

## Project semantics / domain notes
- This project consumes the `arete` extension's test model to build a reporting structure that
  mirrors Gradle's built-in test report layout — keep that parity in mind when changing report
  generation logic.
- Plugin metadata (tags: `testing`, `junit6`, `bdd`, `agile`) reflects its intended audience: Gradle
  users of the arete BDD framework.
- `gradle.properties` in this project stores Gradle-level configuration (e.g. Kotlin/JVM toggles) —
  check it before assuming defaults.
- Run history (history store, history strip on spec pages, History page, screenshot hand-over via
  JUnit file attachments): read [docs/arete_history.md](docs/arete_history.md) before changing
  `History.kt`, `HtmlWriter.kt`, the history templates or screenshot handling.

## Working conventions
- Run `./gradlew test` before submitting changes; also consider building and exercising the plugin
  against `arete-gradle-test`, which is the dedicated consumer/integration test project.
- Keep the plugin's public extension DSL and generated report structure backward compatible where
  possible, since external consumers apply this plugin by version.
- Match the existing Kotlin coding style already used in the plugin sources.
