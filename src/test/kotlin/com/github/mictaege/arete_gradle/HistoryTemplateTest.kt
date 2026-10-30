package com.github.mictaege.arete_gradle

import freemarker.template.Template
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.StringReader
import java.io.StringWriter
import java.time.Instant

class HistoryTemplateTest {
    @Test
    fun exposesResultStateFailureAsBooleanTemplateProperty() {
        val template = Template(
            "result-state-failure",
            StringReader("<#if state.failure>failure<#else>not failure</#if>"),
            Freemaker.cfg
        )

        assertEquals("failure", render(template, ResultState.FAILED))
        assertEquals("not failure", render(template, ResultState.SUCCESSFUL))
    }

    @Test
    fun rendersSharedHistoryRunIconsForSpecPage() {
        val template = Template(
            "history-run-icons",
            StringReader("""
                <#import "/history-macros.ftlh" as history>
                <@history.historyRun id="step" state="FAILED" timeStamp="now" errorMsg="" stackTrace="trace" stdout="" stderr="" screenshotFileName="step.png"/>
            """.trimIndent()),
            Freemaker.cfg
        )
        val writer = StringWriter()
        template.process(emptyMap<String, Any>(), writer)

        assertTrue(writer.toString().contains("spec-icon-camera"))
        assertTrue(writer.toString().contains("spec-icon-file"))
    }

    @Test
    fun rendersPopulatedHistoryOverview() {
        val startTime = Instant.parse("2026-10-24T10:00:00Z")
        val entry = HistoryEntry(
            runId = "run-1",
            timestamp = startTime,
            startTime = startTime,
            state = ResultState.FAILED,
            errorMsg = "assertion failed",
            stackTrace = "java.lang.AssertionError",
            stdout = "",
            stderr = "",
            screenshotFileName = "step-hash.png"
        )
        val leaf = HistoryOverviewNode(
            hash = "step-hash",
            displayName = "Then it fails",
            type = StepType.THEN,
            index = 0,
            container = false,
            entries = listOf(entry)
        )
        val scenario = HistoryOverviewNode(
            hash = "scenario-hash",
            displayName = "Example scenario",
            type = StepType.SCENARIO,
            index = 0,
            container = true,
            children = listOf(leaf)
        )
        val overview = HistoryOverview(
            listOf(
                HistoryOverviewSpec(
                    hash = "spec-hash",
                    displayName = "Spec's title",
                    reportAvailable = true,
                    nodes = listOf(scenario)
                )
            )
        )

        val html = render(overview, historyEnabled = true)

        assertTrue(html.contains("history-dot-FAILED"))
        assertTrue(html.contains("class=\"card-icon-camera\""))
        assertTrue(html.contains("class=\"card-icon-file\""))
        assertTrue(html.contains("class=\"failed step history-container outline-header\""))
        assertTrue(html.contains("class=\"outline-item container-step\""))
        assertTrue(html.contains("class=\"outline-item\""))
        assertTrue(html.contains("class=\"outline-children\""))
        assertTrue(html.contains("class=\"outline-body\""))
        assertTrue(html.contains("spec-link-btn"))
        assertTrue(html.contains("href=\"./specs/spec-hash.html\""))
        assertTrue(html.contains("querySelectorAll(\".outline-header\")"))
        assertTrue(html.contains("unfoldTargetOf"))
        assertTrue(html.contains("java.lang.AssertionError"))
        assertTrue(html.contains("function traceToClipboard"))
        assertTrue(html.contains("class=\"spec-link-btn\""), html)
        assertTrue(html.contains("class=\"markdown-copy-btn\""), html)
        assertFalse(html.contains("class=\"link-copy-btn\""), html)
        assertTrue(html.contains("outline-header\" id=\"scenario-hash\""), html)
        assertTrue(html.contains("outline-header\" id=\"spec-hash\""), html)
        assertFalse(html.contains("<a id="), html)
    }

    @Test
    fun rendersEmptyAndDisabledHistoryStates() {
        val emptyOverview = HistoryOverview(emptyList())

        assertTrue(render(emptyOverview, historyEnabled = true).contains("No failed or aborted steps"))
        assertTrue(render(emptyOverview, historyEnabled = false).contains("History is unavailable"))
    }

    private fun render(overview: HistoryOverview, historyEnabled: Boolean): String {
        val template: Template = Freemaker.cfg.getTemplate("/history.ftlh")
        val writer = StringWriter()
        template.process(
            mapOf(
                "overview" to overview,
                "historyEnabled" to historyEnabled,
                "colorScheme" to AreteClassicColors()
            ),
            writer
        )
        return writer.toString()
    }

    private fun render(template: Template, state: ResultState): String {
        val writer = StringWriter()
        template.process(mapOf("state" to state), writer)
        return writer.toString()
    }
}