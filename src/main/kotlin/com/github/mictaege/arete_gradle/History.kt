package com.github.mictaege.arete_gradle

import java.io.File
import java.nio.channels.FileChannel
import java.nio.file.AtomicMoveNotSupportedException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption.ATOMIC_MOVE
import java.nio.file.StandardCopyOption.REPLACE_EXISTING
import java.nio.file.StandardOpenOption.CREATE
import java.nio.file.StandardOpenOption.WRITE
import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Properties
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

data class HistoryConfig(
    val directory: File,
    val runId: String,
    val timestamp: Instant,
    val maxAge: Int
) {
    companion object {
        const val DEFAULT_MAX_AGE = 10
        const val DIRECTORY_PROPERTY = "io.github.mictaege.arete_gradle.history.directory"
        const val RUN_ID_PROPERTY = "io.github.mictaege.arete_gradle.history.runId"
        const val TIMESTAMP_PROPERTY = "io.github.mictaege.arete_gradle.history.timestamp"
        const val MAX_AGE_PROPERTY = "io.github.mictaege.arete_gradle.history.maxAge"
        private val RUN_ID_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmssSSS'Z'")
            .withZone(ZoneOffset.UTC)

        fun newRunId(timestamp: Instant): String =
            "${RUN_ID_FORMAT.format(timestamp)}-${UUID.randomUUID().toString().take(8)}"

        fun fromSystemProperties(): HistoryConfig? {
            val directory = System.getProperty(DIRECTORY_PROPERTY)?.takeIf { it.isNotBlank() }?.let(::File) ?: return null
            val runId = System.getProperty(RUN_ID_PROPERTY)?.takeIf { it.isNotBlank() } ?: return null
            val timestamp = System.getProperty(TIMESTAMP_PROPERTY)?.let {
                runCatching { Instant.parse(it) }.getOrNull()
            } ?: Instant.now()
            val maxAge = System.getProperty(MAX_AGE_PROPERTY)?.toIntOrNull()?.coerceAtLeast(1) ?: DEFAULT_MAX_AGE
            return HistoryConfig(directory, runId, timestamp, maxAge)
        }
    }
}

data class HistoryRecord(
    val stepHash: String,
    val displayName: String,
    val state: ResultState,
    val stackTrace: String = "",
    val stdout: String = "",
    val stderr: String = "",
    val screenshot: File? = null
)

data class HistoryEntry(
    val runId: String,
    val timestamp: Instant,
    val state: ResultState,
    val stackTrace: String,
    val stdout: String,
    val stderr: String,
    val screenshotFileName: String?
) {
    val timeStampLong: String
        get() = timestamp.atZone(ZoneId.systemDefault())
            .format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.MEDIUM))
}

class HistoryStore(private val config: HistoryConfig) {
    fun save(specHash: String, records: List<HistoryRecord>) {
        val specDirectory = config.directory.toPath().resolve(specHash)
        withLock(specDirectory) {
            val targetDirectory = specDirectory.resolve(config.runId)
            if (Files.exists(targetDirectory)) return@withLock

            val temporaryDirectory = Files.createTempDirectory(specDirectory, ".tmp-${config.runId}-")
            try {
                writeRun(temporaryDirectory, records)
                try {
                    Files.move(temporaryDirectory, targetDirectory, ATOMIC_MOVE)
                } catch (_: AtomicMoveNotSupportedException) {
                    Files.move(temporaryDirectory, targetDirectory)
                }
                prune(specDirectory)
            } finally {
                if (Files.exists(temporaryDirectory)) temporaryDirectory.toFile().deleteRecursively()
            }
        }
    }

    fun load(specHash: String, stepHash: String, screenshotDirectory: File? = null): List<HistoryEntry> {
        val specDirectory = config.directory.toPath().resolve(specHash)
        if (!Files.isDirectory(specDirectory)) return emptyList()

        return withLock(specDirectory) {
            runDirectories(specDirectory)
                .filterNot { it.fileName.toString() == config.runId }
                .mapNotNull { runDirectory -> readEntry(runDirectory, stepHash, screenshotDirectory) }
                .sortedWith(compareByDescending<HistoryEntry> { it.timestamp }.thenByDescending { it.runId })
                .take((config.maxAge - 1).coerceAtLeast(0))
        }
    }

    private fun writeRun(directory: Path, records: List<HistoryRecord>) {
        val runProperties = Properties().apply {
            setProperty("runId", config.runId)
            setProperty("timestamp", config.timestamp.toString())
        }
        writeProperties(directory.resolve("run.properties"), runProperties)

        val stepsProperties = Properties()
        records.forEach { record ->
            stepsProperties.setProperty("${record.stepHash}.state", record.state.name)
            stepsProperties.setProperty("${record.stepHash}.displayName", record.displayName)

            if (record.state == ResultState.FAILED || record.state == ResultState.ABORTED) {
                writeTextIfNotEmpty(directory.resolve("${record.stepHash}.stacktrace.txt"), record.stackTrace)
                writeTextIfNotEmpty(directory.resolve("${record.stepHash}.stdout.txt"), record.stdout)
                writeTextIfNotEmpty(directory.resolve("${record.stepHash}.stderr.txt"), record.stderr)
                record.screenshot?.takeIf { it.isFile }?.let {
                    Files.copy(it.toPath(), directory.resolve("${record.stepHash}.png"), REPLACE_EXISTING)
                }
            }
        }
        writeProperties(directory.resolve("steps.properties"), stepsProperties)
    }

    private fun readEntry(runDirectory: Path, stepHash: String, screenshotDirectory: File?): HistoryEntry? {
        return try {
            val runProperties = readProperties(runDirectory.resolve("run.properties")) ?: return null
            val stepsProperties = readProperties(runDirectory.resolve("steps.properties")) ?: return null
            val runId = runProperties.getProperty("runId") ?: runDirectory.fileName.toString()
            val timestamp = Instant.parse(runProperties.getProperty("timestamp"))
            val state = ResultState.valueOf(stepsProperties.getProperty("$stepHash.state"))
            val storedScreenshot = runDirectory.resolve("$stepHash.png").takeIf { Files.isRegularFile(it) }
            val screenshot = storedScreenshot?.let { source ->
                if (screenshotDirectory == null) {
                    source.toFile()
                } else {
                    runCatching {
                        Files.createDirectories(screenshotDirectory.toPath())
                        val target = screenshotDirectory.toPath().resolve("${runId}_${stepHash}.png")
                        Files.copy(source, target, REPLACE_EXISTING)
                        target.toFile()
                    }.getOrNull()
                }
            }
            HistoryEntry(
                runId = runId,
                timestamp = timestamp,
                state = state,
                stackTrace = readText(runDirectory.resolve("$stepHash.stacktrace.txt")),
                stdout = readText(runDirectory.resolve("$stepHash.stdout.txt")),
                stderr = readText(runDirectory.resolve("$stepHash.stderr.txt")),
                screenshotFileName = screenshot?.let { "history/${runId}_${stepHash}.png" }
            )
        } catch (_: Exception) {
            null
        }
    }

    private fun prune(specDirectory: Path) {
        runDirectories(specDirectory)
            .sortedWith(compareByDescending<Path> { runTimestamp(it) }.thenByDescending { it.fileName.toString() })
            .drop(config.maxAge)
            .forEach { it.toFile().deleteRecursively() }
    }

    private fun runDirectories(specDirectory: Path): List<Path> {
        return try {
            Files.list(specDirectory).use { paths ->
                paths.filter { Files.isDirectory(it) && !it.fileName.toString().startsWith(".") }
                    .filter { Files.isRegularFile(it.resolve("run.properties")) }
                    .toList()
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    private fun runTimestamp(directory: Path): Instant {
        return runCatching {
            Instant.parse(readProperties(directory.resolve("run.properties"))?.getProperty("timestamp"))
        }.getOrDefault(Instant.EPOCH)
    }

    private fun writeProperties(path: Path, properties: Properties) {
        Files.newOutputStream(path).use { properties.store(it, null) }
    }

    private fun readProperties(path: Path): Properties? {
        if (!Files.isRegularFile(path)) return null
        return Properties().apply { Files.newInputStream(path).use(::load) }
    }

    private fun writeTextIfNotEmpty(path: Path, value: String) {
        if (value.isNotEmpty()) Files.writeString(path, value)
    }

    private fun readText(path: Path): String =
        if (Files.isRegularFile(path)) Files.readString(path) else ""

    private fun <T> withLock(specDirectory: Path, action: () -> T): T {
        Files.createDirectories(specDirectory)
        val lockPath = specDirectory.resolve(".lock")
        val monitor = monitors.computeIfAbsent(lockPath.toAbsolutePath().normalize()) { Any() }
        return synchronized(monitor) {
            FileChannel.open(lockPath, CREATE, WRITE).use { channel ->
                channel.lock().use { action() }
            }
        }
    }

    companion object {
        private val monitors = ConcurrentHashMap<Path, Any>()
    }
}