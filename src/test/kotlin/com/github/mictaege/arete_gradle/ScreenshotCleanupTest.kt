package com.github.mictaege.arete_gradle

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Files
import java.nio.file.Path

class ScreenshotCleanupTest {
    @TempDir
    lateinit var tempDir: Path

    @Test
    fun deletesScreenshotAndEmptyDirectoriesButKeepsEngineDirectoryAndNonEmptyDirectories() {
        val outputRoot = tempDir.resolve("build")
        val engineDirectory = outputRoot.resolve("junit-jupiter")
        val emptyDirectory = engineDirectory.resolve("spec/step")
        val screenshot = emptyDirectory.resolve("arete-screenshot.png")
        Files.createDirectories(emptyDirectory)
        Files.write(screenshot, byteArrayOf(1, 2, 3))

        ScreenshotCleanup.delete(screenshot, outputRoot)

        assertFalse(Files.exists(screenshot))
        assertFalse(Files.exists(emptyDirectory))
        assertFalse(Files.exists(engineDirectory.resolve("spec")))
        assertTrue(Files.isDirectory(engineDirectory))
        assertTrue(Files.isDirectory(outputRoot))

        val nonEmptyDirectory = engineDirectory.resolve("another-spec/step")
        val secondScreenshot = nonEmptyDirectory.resolve("arete-screenshot.png")
        val otherFile = nonEmptyDirectory.resolve("other.txt")
        Files.createDirectories(nonEmptyDirectory)
        Files.write(secondScreenshot, byteArrayOf(4, 5, 6))
        Files.writeString(otherFile, "keep")

        ScreenshotCleanup.delete(secondScreenshot, outputRoot)

        assertFalse(Files.exists(secondScreenshot))
        assertTrue(Files.isDirectory(nonEmptyDirectory))
        assertTrue(Files.exists(otherFile))
    }

    @Test
    fun deletesScreenshotOutsideOutputRootWithoutDeletingItsParent() {
        val outputRoot = tempDir.resolve("build")
        val externalDirectory = tempDir.resolve("external")
        val screenshot = externalDirectory.resolve("arete-screenshot.png")
        Files.createDirectories(externalDirectory)
        Files.write(screenshot, byteArrayOf(1, 2, 3))

        ScreenshotCleanup.delete(screenshot, outputRoot)

        assertFalse(Files.exists(screenshot))
        assertTrue(Files.isDirectory(externalDirectory))
    }
}