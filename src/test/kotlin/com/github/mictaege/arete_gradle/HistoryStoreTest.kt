package com.github.mictaege.arete_gradle

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Files
import java.nio.file.Path
import java.time.Instant
import java.util.concurrent.Executors

class HistoryStoreTest {
    @TempDir
    lateinit var tempDir: Path

    @Test
    fun storesFailureDetailsAndScreenshot() {
        val screenshot = tempDir.resolve("failure.png")
        Files.write(screenshot, byteArrayOf(1, 2, 3))
        val historyDirectory = tempDir.resolve("history").toFile()
        val firstTimestamp = Instant.parse("2026-10-23T10:00:00Z")
        val stepStartTime = firstTimestamp.plusSeconds(15)

        HistoryStore(config(historyDirectory, "run-1", firstTimestamp)).save(
            "spec",
            listOf(
                HistoryRecord(
                    stepHash = "step",
                    displayName = "sample step",
                    state = ResultState.FAILED,
                    startTime = stepStartTime,
                    errorMsg = "assertion failed",
                    stackTrace = "failure stack",
                    stdout = "captured stdout",
                    stderr = "captured stderr",
                    screenshot = screenshot.toFile()
                )
            )
        )

        val reportScreenshotDirectory = tempDir.resolve("report/history").toFile()
        val loaded = HistoryStore(config(historyDirectory, "run-2", firstTimestamp.plusSeconds(60)))
            .load("spec", "step", reportScreenshotDirectory)
            .single()

        assertEquals("run-1", loaded.runId)
        assertEquals(firstTimestamp, loaded.timestamp)
        assertEquals(stepStartTime, loaded.startTime)
        assertEquals(ResultState.FAILED, loaded.state)
        assertEquals("assertion failed", loaded.errorMsg)
        assertEquals("failure stack", loaded.stackTrace)
        assertEquals("captured stdout", loaded.stdout)
        assertEquals("captured stderr", loaded.stderr)
        assertEquals("history/run-1_step.png", loaded.screenshotFileName)
        val reportScreenshot = reportScreenshotDirectory.resolve("run-1_step.png")
        assertTrue(reportScreenshot.isFile)
        assertEquals(byteArrayOf(1, 2, 3).toList(), reportScreenshot.readBytes().toList())
    }

    @Test
    fun excludesCurrentRunAndPrunesToConfiguredLimit() {
        val historyDirectory = tempDir.resolve("history").toFile()
        repeat(4) { index ->
            val timestamp = Instant.parse("2026-10-23T10:00:00Z").plusSeconds(index.toLong())
            HistoryStore(
                config(historyDirectory, "run-$index", timestamp, maxAge = 3)
            ).save("spec", listOf(HistoryRecord("step", "step", ResultState.SUCCESSFUL, timestamp)))
        }

        val currentRunStore = HistoryStore(
            config(historyDirectory, "run-3", Instant.parse("2026-10-23T10:00:03Z"), maxAge = 3)
        )
        val leftoverTemporaryDirectory = historyDirectory.toPath().resolve("spec/.tmp-leftover")
        Files.createDirectories(leftoverTemporaryDirectory)
        Files.writeString(leftoverTemporaryDirectory.resolve("run.properties"), "incomplete=true")
        val loaded = currentRunStore.load("spec", "step")
        val runDirectories = Files.list(historyDirectory.toPath().resolve("spec")).use { paths ->
            paths.filter { !it.fileName.toString().startsWith(".") && Files.isRegularFile(it.resolve("run.properties")) }.toList()
        }

        assertEquals(listOf("run-2", "run-1"), loaded.map { it.runId })
        assertEquals(3, runDirectories.size)
        assertFalse(loaded.any { it.runId == "run-3" })
        assertTrue(Files.isDirectory(leftoverTemporaryDirectory))
    }

    @Test
    fun concurrentWritersKeepDistinctRunDirectories() {
        val historyDirectory = tempDir.resolve("history").toFile()
        val executor = Executors.newFixedThreadPool(4)
        try {
            val tasks = (0 until 4).map { index ->
                executor.submit {
                    val timestamp = Instant.parse("2026-10-23T10:00:00Z").plusSeconds(index.toLong())
                    HistoryStore(
                        config(historyDirectory, "parallel-$index", timestamp)
                    ).save("spec", listOf(HistoryRecord("step", "step", ResultState.SUCCESSFUL, timestamp)))
                }
            }
            tasks.forEach { it.get() }
        } finally {
            executor.shutdownNow()
        }

        val loaded = HistoryStore(config(historyDirectory, "reader", Instant.now()))
            .load("spec", "step")
        assertEquals(4, loaded.size)
    }

    @Test
    fun overviewGroupsFailureLeavesAndKeepsFailuresWhenLatestRunSucceeded() {
        val historyDirectory = tempDir.resolve("history").toFile()
        val firstTimestamp = Instant.parse("2026-10-23T10:00:00Z")
        val feature = HistoryContainer("feature", "Feature: Refunds", StepType.FEATURE, 0)
        val scenario = HistoryContainer("scenario", "Agency pays the refund", StepType.SCENARIO, 0)
        val parents = listOf(feature, scenario)
        val failedStep = HistoryRecord(
            stepHash = "failed-step",
            displayName = "refund is paid",
            state = ResultState.FAILED,
            startTime = firstTimestamp,
            type = StepType.THEN,
            index = 1,
            parents = parents,
            errorMsg = "Expected success"
        )
        val abortedStep = failedStep.copy(
            stepHash = "aborted-step",
            displayName = "receipt is issued",
            state = ResultState.ABORTED,
            index = 2
        )
        val successfulStep = failedStep.copy(
            stepHash = "successful-step",
            displayName = "confirmation is sent",
            state = ResultState.SUCCESSFUL,
            index = 3
        )

        HistoryStore(config(historyDirectory, "run-1", firstTimestamp)).save(
            "spec",
            listOf(failedStep, abortedStep, successfulStep),
            "RefundSpec"
        )
        HistoryStore(config(historyDirectory, "run-2", firstTimestamp.plusSeconds(60))).save(
            "spec",
            listOf(
                failedStep.copy(state = ResultState.SUCCESSFUL, displayName = "refund is now paid"),
                abortedStep.copy(state = ResultState.SUCCESSFUL),
                successfulStep
            ),
            "RefundSpec"
        )

        val overview = HistoryStore(config(historyDirectory, "run-3", firstTimestamp.plusSeconds(120))).overview()

        val spec = overview.specs.single()
        assertEquals("RefundSpec", spec.displayName)
        assertEquals(ResultState.SUCCESSFUL, spec.state)
        val featureNode = spec.nodes.single()
        assertEquals("feature", featureNode.hash)
        assertEquals("Feature: Refunds", featureNode.displayName)
        assertEquals("Feature: Refunds", featureNode.containerDisplayName)
        assertEquals(ResultState.SUCCESSFUL, featureNode.state)
        val scenarioNode = featureNode.children.single()
        assertEquals("Agency pays the refund", scenarioNode.displayName)
        assertEquals("Scenario: Agency pays the refund", scenarioNode.containerDisplayName)
        assertEquals(ResultState.SUCCESSFUL, scenarioNode.state)
        assertEquals(listOf("aborted-step", "failed-step"), scenarioNode.children.map { it.hash }.sorted())
        assertTrue(scenarioNode.children.none { it.hash == "successful-step" })
        assertEquals(
            listOf(ResultState.SUCCESSFUL, ResultState.FAILED),
            scenarioNode.children.single { it.hash == "failed-step" }.entries.map { it.state }
        )
        assertEquals(
            listOf(ResultState.SUCCESSFUL, ResultState.ABORTED),
            scenarioNode.children.single { it.hash == "aborted-step" }.entries.map { it.state }
        )
        assertEquals(ResultState.SUCCESSFUL, scenarioNode.children.single { it.hash == "failed-step" }.state)
        assertEquals(ResultState.SUCCESSFUL, scenarioNode.children.single { it.hash == "aborted-step" }.state)
        assertEquals("refund is now paid", scenarioNode.children.single { it.hash == "failed-step" }.displayName)

        HistoryStore(config(historyDirectory, "run-3", firstTimestamp.plusSeconds(120))).save(
            "spec",
            listOf(failedStep, abortedStep.copy(state = ResultState.SUCCESSFUL), successfulStep),
            "RefundSpec"
        )
        val failedAgain = HistoryStore(config(historyDirectory, "run-4", firstTimestamp.plusSeconds(180))).overview()
            .specs.single()
        assertEquals(ResultState.FAILED, failedAgain.state)
        assertEquals(ResultState.FAILED, failedAgain.nodes.single().state)

        val describeNode = HistoryOverviewNode("describe", "Describe: Room details", StepType.DESCRIBE, 0, true)
        assertEquals("Describe: Room details", describeNode.containerDisplayName)
    }

    @Test
    fun overviewOrdersLeavesByRecencyWeightedFailures() {
        val historyDirectory = tempDir.resolve("history").toFile()
        val scenario = HistoryContainer("scenario", "Scenario", StepType.SCENARIO, 0)
        saveHistories(
            historyDirectory, "spec", "Spec",
            listOf(
                leaf("A", 4, scenario) to "FFFS",
                leaf("B", 3, scenario) to "FSSF",
                leaf("C", 2, scenario) to "AFSS",
                leaf("D", 1, scenario) to "SFFF",
                leaf("E", 0, scenario) to "SSFS"
            )
        )

        val leaves = overview(historyDirectory).specs.single().nodes.single().children
        assertEquals(listOf("A", "B", "C", "D", "E"), leaves.map { it.hash })
    }

    @Test
    fun overviewRanksContainerByItsMostImportantLeaf() {
        val historyDirectory = tempDir.resolve("history").toFile()
        val scenarioX = HistoryContainer("X", "Scenario X", StepType.SCENARIO, 0)
        val scenarioY = HistoryContainer("Y", "Scenario Y", StepType.SCENARIO, 1)
        saveHistories(
            historyDirectory, "spec", "Spec",
            listOf(
                leaf("x1", 0, scenarioX) to "SFFF",
                leaf("x2", 1, scenarioX) to "SSFF",
                leaf("y1", 0, scenarioY) to "FSSS"
            )
        )

        val containers = overview(historyDirectory).specs.single().nodes
        assertEquals(listOf("Y", "X"), containers.map { it.hash })
    }

    @Test
    fun overviewOrdersSpecsByImportance() {
        val historyDirectory = tempDir.resolve("history").toFile()
        val scenario = HistoryContainer("scenario", "Scenario", StepType.SCENARIO, 0)
        saveHistories(historyDirectory, "alpha", "Alpha Spec", listOf(leaf("a", 0, scenario) to "SF"))
        saveHistories(historyDirectory, "zulu", "Zulu Spec", listOf(leaf("z", 0, scenario) to "FS"))

        assertEquals(listOf("Zulu Spec", "Alpha Spec"), overview(historyDirectory).specs.map { it.displayName })
    }

    @Test
    fun overviewKeepsIndexOrderForEqualHistories() {
        val historyDirectory = tempDir.resolve("history").toFile()
        val scenario = HistoryContainer("scenario", "Scenario", StepType.SCENARIO, 0)
        saveHistories(
            historyDirectory, "spec", "Spec",
            listOf(leaf("second", 1, scenario) to "FS", leaf("first", 0, scenario) to "FS")
        )

        val leaves = overview(historyDirectory).specs.single().nodes.single().children
        assertEquals(listOf("first", "second"), leaves.map { it.hash })
    }

    @Test
    fun overviewTreatsMissingRunsAsSuccessful() {
        val historyDirectory = tempDir.resolve("history").toFile()
        val scenario = HistoryContainer("scenario", "Scenario", StepType.SCENARIO, 0)
        saveHistories(
            historyDirectory, "spec", "Spec",
            listOf(
                leaf("short", 0, scenario) to "FS",
                leaf("long", 1, scenario) to "FSF",
                leaf("equal", 2, scenario) to "FSS"
            )
        )

        val leaves = overview(historyDirectory).specs.single().nodes.single().children
        assertEquals(listOf("long", "short", "equal"), leaves.map { it.hash })
        assertTrue(HistoryImportance(listOf(3, 0)).compareTo(HistoryImportance(listOf(3, 0, 0))) == 0)
    }

    private fun leaf(hash: String, index: Int, vararg parents: HistoryContainer) = HistoryRecord(
        stepHash = hash,
        displayName = hash,
        state = ResultState.SUCCESSFUL,
        startTime = Instant.parse("2026-10-23T10:00:00Z"),
        type = StepType.THEN,
        index = index,
        parents = parents.toList()
    )

    /** Histories are given newest run first, one letter per run: F, A, I or S. */
    private fun saveHistories(
        directory: java.io.File,
        specHash: String,
        specDisplayName: String,
        leaves: List<Pair<HistoryRecord, String>>
    ) {
        val runCount = leaves.maxOf { it.second.length }
        val start = Instant.parse("2026-10-23T10:00:00Z")
        (0 until runCount).forEach { run ->
            val positionFromNewest = runCount - 1 - run
            val records = leaves.filter { positionFromNewest < it.second.length }.map { (record, history) ->
                val state = when (history[positionFromNewest]) {
                    'F' -> ResultState.FAILED
                    'A' -> ResultState.ABORTED
                    'I' -> ResultState.IGNORED
                    else -> ResultState.SUCCESSFUL
                }
                record.copy(state = state)
            }
            HistoryStore(config(directory, "$specHash-run-$run", start.plusSeconds(run * 60L)))
                .save(specHash, records, specDisplayName)
        }
    }

    private fun overview(directory: java.io.File) =
        HistoryStore(config(directory, "reader", Instant.parse("2026-10-24T10:00:00Z"))).overview()

    private fun config(
        directory: java.io.File,
        runId: String,
        timestamp: Instant,
        maxAge: Int = 10
    ) = HistoryConfig(directory, runId, timestamp, maxAge)
}