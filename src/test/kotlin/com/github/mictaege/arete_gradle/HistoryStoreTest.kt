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

        HistoryStore(config(historyDirectory, "run-1", firstTimestamp)).save(
            "spec",
            listOf(
                HistoryRecord(
                    stepHash = "step",
                    displayName = "sample step",
                    state = ResultState.FAILED,
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
        assertEquals(ResultState.FAILED, loaded.state)
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
            HistoryStore(
                config(historyDirectory, "run-$index", Instant.parse("2026-10-23T10:00:00Z").plusSeconds(index.toLong()), maxAge = 3)
            ).save("spec", listOf(HistoryRecord("step", "step", ResultState.SUCCESSFUL)))
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
                    HistoryStore(
                        config(
                            historyDirectory,
                            "parallel-$index",
                            Instant.parse("2026-10-23T10:00:00Z").plusSeconds(index.toLong())
                        )
                    ).save("spec", listOf(HistoryRecord("step", "step", ResultState.SUCCESSFUL)))
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

    private fun config(
        directory: java.io.File,
        runId: String,
        timestamp: Instant,
        maxAge: Int = 10
    ) = HistoryConfig(directory, runId, timestamp, maxAge)
}