package com.github.mictaege.arete_gradle

import freemarker.template.Template
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.StringReader
import java.io.StringWriter
import java.time.Instant

class MarkdownTemplateTest {

    @Test
    fun rendersSpecTreeAsMarkdown() {
        val template = markdownStepTemplate()

        val markdown = render(template, mapOf("step" to specModel(), "colorScheme" to AreteClassicColors()))

        assertTrue(markdown.contains("# Payment Spec"), markdown)
        assertTrue(markdown.contains("## Feature: Payment"), markdown)
        assertTrue(markdown.contains("### Scenario: Pay by card"), markdown)
        assertTrue(markdown.contains("- <span style=\"color: limegreen\">[+]</span> Given the customer pays by card"), markdown)
        assertTrue(markdown.contains("- <span style=\"color: red\">[x]</span> Then the payment fails"), markdown)
        assertTrue(markdown.contains("    - Message:"), markdown)
        assertTrue(markdown.contains("      ```"), markdown)
        assertTrue(markdown.contains("      Expected: is &lt;true&gt;"), markdown)
        assertTrue(markdown.contains("    - Stack trace:"), markdown)
        assertTrue(markdown.contains("      java.lang.AssertionError"), markdown)
        assertTrue(markdown.contains("    - Stdout:"), markdown)
        assertTrue(markdown.contains("      paying ..."), markdown)
        assertTrue(!markdown.contains("Screenshot"), markdown)
        assertTrue(markdown.contains("    - History:"), markdown)
        assertTrue(markdown.contains("        - <span style=\"color: red\">[x]</span> "), markdown)
        assertTrue(markdown.contains("              assertion failed"), markdown)
        assertTrue(markdown.contains("        - <span style=\"color: limegreen\">[+]</span> "), markdown)
        assertTrue(!markdown.contains("Then the payment succeeds"), markdown)
        assertTrue(!markdown.contains("`[x]`"), markdown)
        assertTrue(!markdown.contains("`[+]`"), markdown)
    }

    @Test
    fun rendersHiddenStepsNotAtAll() {
        val template = markdownStepTemplate()
        val hidden = step(StepType.THEN, "Then the payment succeeds", ResultState.HIDDEN)

        val markdown = render(template, mapOf("step" to hidden, "colorScheme" to AreteClassicColors()))

        assertTrue(markdown.trim().isEmpty(), markdown)
    }

    @Test
    fun rendersExampleGridAsListLikeNormalExamples() {
        val template = markdownStepTemplate()
        val grid = step(
            StepType.EXAMPLE_GRID_CONTAINER, "Example: payment methods", ResultState.FAILED,
            extra = mapOf("gridColumns" to arrayOf("method", "result")),
            steps = listOf(
                step(
                    StepType.EXAMPLE_GRID_INSTANCE, "card | ok", ResultState.SUCCESSFUL,
                    extra = mapOf("gridRowData" to listOf("card", "ok"))
                ),
                step(
                    StepType.EXAMPLE_GRID_INSTANCE, "| wire | ko |", ResultState.FAILED,
                    extra = mapOf(
                        "gridRowData" to listOf("wire", "ko"),
                        "hasErrorMsg" to true,
                        "errorMsg" to "Expected: <ko> but: was <ok>",
                        "showHistory" to true,
                        "history" to listOf(historyEntry(ResultState.FAILED, "Expected: <ko>"))
                    )
                )
            )
        )

        val markdown = render(template, mapOf("step" to grid, "colorScheme" to AreteClassicColors()))

        assertTrue(markdown.contains("# Example: payment methods"), markdown)
        assertTrue(!markdown.contains("| State |"), markdown)
        assertTrue(!markdown.contains("<a id="), markdown)
        assertTrue(markdown.contains("- <span style=\"color: limegreen\">[+]</span> card | ok"), markdown)
        assertTrue(markdown.contains("- <span style=\"color: red\">[x]</span> | wire | ko |"), markdown)
        assertTrue(markdown.contains("    - Message:"), markdown)
        assertTrue(markdown.contains("      Expected: &lt;ko&gt; but: was &lt;ok&gt;"), markdown)
        assertTrue(markdown.contains("    - History:"), markdown)
        assertTrue(markdown.contains("        - <span style=\"color: red\">[x]</span> "), markdown)
    }

    @Test
    fun rendersHistoryOverviewAsMarkdown() {
        val template = Template(
            "markdown-overview-spec",
            StringReader(
                """
                <#import "/markdown-macros.ftlh" as md>
                <@md.markdownOverviewSpec spec=spec/>
                """.trimIndent()
            ),
            Freemaker.cfg
        )
        val spec = HistoryOverview(
            listOf(
                HistoryOverviewSpec(
                    hash = "spec-hash",
                    displayName = "Spec's title",
                    reportAvailable = true,
                    nodes = listOf(scenarioNode())
                )
            )
        ).specs.first()

        val markdown = render(template, mapOf("spec" to spec, "colorScheme" to AreteClassicColors()))

        assertTrue(markdown.contains("# Spec&#39;s title"), markdown)
        assertTrue(markdown.contains("## Scenario: Example scenario"), markdown)
        assertTrue(markdown.contains("- <span style=\"color: red\">[x]</span> Then it fails"), markdown)
        assertTrue(markdown.contains("    - Message:"), markdown)
        assertTrue(markdown.contains("      assertion failed"), markdown)
        assertTrue(markdown.contains("    - Stack trace:"), markdown)
        assertTrue(markdown.contains("    - History:"), markdown)
        assertTrue(markdown.contains("        - <span style=\"color: red\">[x]</span> "), markdown)
        assertTrue(markdown.contains("        - <span style=\"color: limegreen\">[+]</span> "), markdown)
    }

    @Test
    fun rendersCopyButtonWithEmbeddedMarkdown() {
        val template = Template(
            "markdown-copy-button",
            StringReader(
                """
                <#import "/markdown-macros.ftlh" as md>
                <@md.markdownCopyButton id="hashMarkdown" iconClass="card-markdown-icon"># Title</@md.markdownCopyButton>
                """.trimIndent()
            ),
            Freemaker.cfg
        )

        val html = render(template, emptyMap<String, Any>())

        assertTrue(html.contains("""<textarea type="text" id="hashMarkdown" style="display: none;"># Title</textarea>"""), html)
        assertTrue(html.contains("""class="markdown-copy-btn" """), html)
        assertTrue(html.contains("""title="Copy as markdown""""), html)
        assertTrue(html.contains("""onclick="traceToClipboard('hashMarkdown');""""), html)
        assertTrue(html.contains("""class="card-markdown-icon""""), html)
    }

    @Test
    fun rendersNarrativeAsBlockquote() {
        val template = markdownStepTemplate()
        val spec = step(
            StepType.SPEC, "Booking Spec", ResultState.SUCCESSFUL,
            extra = mapOf(
                "hasNarrative" to true,
                "narrative" to mapOf(
                    "header" to "Narrative:",
                    "lines" to listOf("As a **traveler** I want to pay", "so that I get a ticket")
                )
            )
        )

        val markdown = render(template, mapOf("step" to spec, "colorScheme" to AreteClassicColors()))

        assertTrue(markdown.contains("# Booking Spec"), markdown)
        assertTrue(markdown.contains("> Narrative:"), markdown)
        assertTrue(markdown.contains("> As a **traveler** I want to pay"), markdown)
        assertTrue(markdown.contains("> so that I get a ticket"), markdown)
    }

    private fun markdownStepTemplate(): Template = Template(
        "markdown-step",
        StringReader(
            """
            <#import "/markdown-macros.ftlh" as md>
            <@md.markdownStep step=step/>
            """.trimIndent()
        ),
        Freemaker.cfg
    )

    private fun render(template: Template, model: Map<String, Any>): String {
        val writer = StringWriter()
        template.process(model, writer)
        return writer.toString()
    }

    private fun step(
        type: StepType,
        displayName: String,
        resultState: ResultState,
        steps: List<Any> = emptyList(),
        extra: Map<String, Any> = emptyMap()
    ): Map<String, Any> =
        mapOf(
            "uniqueHash" to "step",
            "type" to type,
            "displayName" to displayName,
            "resultState" to resultState,
            "hasNarrative" to false,
            "hasErrorMsg" to false,
            "errorMsg" to "",
            "stackTrace" to "",
            "hasStdout" to false,
            "stdout" to "",
            "hasStderr" to false,
            "stderr" to "",
            "hasScreenshot" to false,
            "showHistory" to false,
            "history" to emptyList<HistoryEntry>(),
            "steps" to steps
        ) + extra

    private fun specModel(): Map<String, Any> =
        step(
            StepType.SPEC, "Payment Spec", ResultState.FAILED,
            steps = listOf(
                step(
                    StepType.FEATURE, "Feature: Payment", ResultState.FAILED,
                    steps = listOf(
                        step(
                            StepType.SCENARIO, "Scenario: Pay by card", ResultState.FAILED,
                            steps = listOf(
                                step(StepType.GIVEN, "Given the customer pays by card", ResultState.SUCCESSFUL),
                                step(
                                    StepType.THEN, "Then the payment fails", ResultState.FAILED,
                                    extra = mapOf(
                                        "hasErrorMsg" to true,
                                        "errorMsg" to "Expected: is <true> but: was <false>",
                                        "stackTrace" to "java.lang.AssertionError\n at Example.pay(Example.java:12)",
                                        "hasStdout" to true,
                                        "stdout" to "paying ...",
                                        "hasScreenshot" to true,
                                        "showHistory" to true,
                                        "history" to listOf(
                                            historyEntry(ResultState.FAILED, "assertion failed"),
                                            historyEntry(ResultState.SUCCESSFUL, "")
                                        )
                                    )
                                )
                            )
                        )
                    )
                )
            )
        )

    private fun scenarioNode(): HistoryOverviewNode {
        val leaf = HistoryOverviewNode(
            hash = "step-hash",
            displayName = "Then it fails",
            type = StepType.THEN,
            index = 0,
            container = false,
            entries = listOf(
                HistoryEntry(
                    runId = "run-2",
                    timestamp = Instant.parse("2026-10-24T10:00:00Z"),
                    startTime = Instant.parse("2026-10-24T10:00:00Z"),
                    state = ResultState.FAILED,
                    errorMsg = "assertion failed",
                    stackTrace = "java.lang.AssertionError",
                    stdout = "",
                    stderr = "",
                    screenshotFileName = ""
                ),
                HistoryEntry(
                    runId = "run-1",
                    timestamp = Instant.parse("2026-10-23T10:00:00Z"),
                    startTime = Instant.parse("2026-10-23T10:00:00Z"),
                    state = ResultState.SUCCESSFUL,
                    errorMsg = "",
                    stackTrace = "",
                    stdout = "",
                    stderr = "",
                    screenshotFileName = ""
                )
            )
        )
        return HistoryOverviewNode(
            hash = "scenario-hash",
            displayName = "Example scenario",
            type = StepType.SCENARIO,
            index = 0,
            container = true,
            children = listOf(leaf)
        )
    }

    private fun historyEntry(state: ResultState, errorMsg: String): HistoryEntry = HistoryEntry(
        runId = "run-${System.identityHashCode(state)}$errorMsg",
        timestamp = Instant.parse("2026-10-24T10:00:00Z"),
        startTime = Instant.parse("2026-10-24T10:00:00Z"),
        state = state,
        errorMsg = errorMsg,
        stackTrace = if (errorMsg.isEmpty()) "" else "java.lang.AssertionError",
        stdout = "",
        stderr = "",
        screenshotFileName = ""
    )
}
