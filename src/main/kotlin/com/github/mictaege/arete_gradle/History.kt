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
import java.time.ZonedDateTime
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
    val startTime: Instant,
    val type: StepType = StepType.STEP,
    val index: Int = 0,
    val parents: List<HistoryContainer> = emptyList(),
    val errorMsg: String = "",
    val stackTrace: String = "",
    val stdout: String = "",
    val stderr: String = "",
    val screenshot: File? = null
)

data class HistoryContainer(
    val hash: String,
    val displayName: String,
    val type: StepType,
    val index: Int
)

data class HistoryEntry(
    val runId: String,
    val timestamp: Instant,
    val startTime: Instant,
    val state: ResultState,
    val errorMsg: String,
    val stackTrace: String,
    val stdout: String,
    val stderr: String,
    val screenshotFileName: String?
) {
    val timeStampLong: String
        get() = historyTimeStamp(startTime.atZone(ZoneId.systemDefault()))
}

fun historyTimeStamp(timeStamp: ZonedDateTime): String =
    "${timeStamp.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))} " +
        timeStamp.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM))

data class HistoryOverview(val specs: List<HistoryOverviewSpec>)

data class HistoryOverviewSpec(
    val hash: String,
    val displayName: String,
    val reportAvailable: Boolean,
    val nodes: List<HistoryOverviewNode>
) {
    val state: ResultState
        get() = cumulatedHistoryState(nodes.map { it.state })

    val importance: HistoryImportance
        get() = nodes.maxOfOrNull { it.importance } ?: HistoryImportance.NONE
}

data class HistoryOverviewNode(
    val hash: String,
    val displayName: String,
    val type: StepType,
    val index: Int,
    val container: Boolean,
    val children: List<HistoryOverviewNode> = emptyList(),
    val entries: List<HistoryEntry> = emptyList()
) {
    val state: ResultState
        get() = if (container) {
            cumulatedHistoryState(children.map { it.state })
        } else {
            entries.firstOrNull()?.state ?: ResultState.SUCCESSFUL
        }

    val importance: HistoryImportance
        get() = if (container) {
            children.maxOfOrNull { it.importance } ?: HistoryImportance.NONE
        } else {
            HistoryImportance(entries.map { it.state.historyWeight() })
        }

    val typeLabel: String
        get() = when (type) {
            StepType.FEATURE -> "Feature"
            StepType.DESCRIBE -> "Description"
            StepType.SCENARIO -> "Scenario"
            else -> type.name.lowercase().replace('_', ' ').replaceFirstChar(Char::uppercase)
        }
    val containerDisplayName: String
        get() = if (
            displayName.startsWith("$typeLabel:", ignoreCase = true) ||
            (type == StepType.DESCRIBE && displayName.startsWith("Describe:", ignoreCase = true))
        ) {
            displayName
        } else {
            "$typeLabel: $displayName"
        }
}

/**
 * Weights of the runs of a leaf, newest first. Compared lexicographically, so the newest run
 * dominates; missing runs count as successful. Higher is more important.
 */
data class HistoryImportance(val weights: List<Int>) : Comparable<HistoryImportance> {
    override fun compareTo(other: HistoryImportance): Int {
        for (i in 0 until maxOf(weights.size, other.weights.size)) {
            val comparison = weights.getOrElse(i) { 0 }.compareTo(other.weights.getOrElse(i) { 0 })
            if (comparison != 0) return comparison
        }
        return 0
    }

    companion object {
        val NONE = HistoryImportance(emptyList())
    }
}

private fun ResultState.historyWeight(): Int = when (this) {
    ResultState.FAILED -> 3
    ResultState.ABORTED -> 2
    ResultState.IGNORED -> 1
    else -> 0
}

private fun cumulatedHistoryState(states: Iterable<ResultState>): ResultState = when {
    states.any { it == ResultState.FAILED } -> ResultState.FAILED
    states.any { it == ResultState.ABORTED } -> ResultState.ABORTED
    states.any { it == ResultState.IGNORED } -> ResultState.IGNORED
    else -> ResultState.SUCCESSFUL
}

class HistoryStore(private val config: HistoryConfig) {
    fun save(specHash: String, records: List<HistoryRecord>, specDisplayName: String = specHash) {
        val specDirectory = config.directory.toPath().resolve(specHash)
        withLock(specDirectory) {
            val targetDirectory = specDirectory.resolve(config.runId)
            if (Files.exists(targetDirectory)) return@withLock

            val temporaryDirectory = Files.createTempDirectory(specDirectory, ".tmp-${config.runId}-")
            try {
                writeRun(temporaryDirectory, records, specDisplayName)
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

    fun overview(screenshotDirectory: File? = null): HistoryOverview {
        val historyDirectory = config.directory.toPath()
        if (!Files.isDirectory(historyDirectory)) return HistoryOverview(emptyList())

        val specs = try {
            Files.list(historyDirectory).use { paths ->
                paths.filter { Files.isDirectory(it) && !it.fileName.toString().startsWith(".") }
                    .toList()
            }
        } catch (_: Exception) {
            emptyList()
        }
        return HistoryOverview(
            specs.mapNotNull { specDirectory -> readOverviewSpec(specDirectory, screenshotDirectory) }
                .sortedWith(compareByDescending<HistoryOverviewSpec> { it.importance }.thenBy { it.displayName })
        )
    }

    private fun readOverviewSpec(specDirectory: Path, screenshotDirectory: File?): HistoryOverviewSpec? {
        return withLock(specDirectory) {
            val runs = runDirectories(specDirectory)
                .sortedWith(compareByDescending<Path> { runTimestamp(it) }.thenByDescending { it.fileName.toString() })
                .take(config.maxAge)
            if (runs.isEmpty()) return@withLock null

            var specDisplayName: String? = null
            val metadataByLeaf = linkedMapOf<String, StoredStepMetadata>()
            val containerMetadata = linkedMapOf<String, HistoryContainer>()
            val entriesByLeaf = linkedMapOf<String, MutableList<HistoryEntry>>()

            runs.forEach { runDirectory ->
                val runProperties = readProperties(runDirectory.resolve("run.properties")) ?: return@forEach
                if (specDisplayName == null) specDisplayName = runProperties.getProperty("specDisplayName")
                val stepsProperties = readProperties(runDirectory.resolve("steps.properties")) ?: return@forEach
                val stepHashes = stepsProperties.stringPropertyNames()
                    .filter { it.endsWith(".state") }
                    .map { it.removeSuffix(".state") }

                stepHashes.forEach { stepHash ->
                    val metadata = readStepMetadata(stepHash, stepsProperties)
                    metadataByLeaf.putIfAbsent(stepHash, metadata)
                    metadata.parents.forEach { container -> containerMetadata.putIfAbsent(container.hash, container) }

                    readEntry(runDirectory, stepHash, screenshotDirectory)
                        ?.let { entriesByLeaf.getOrPut(stepHash) { mutableListOf() }.add(it) }
                }
            }

            val failuresByLeaf = entriesByLeaf.filterValues { entries ->
                entries.any { it.state == ResultState.FAILED || it.state == ResultState.ABORTED }
            }
            if (failuresByLeaf.isEmpty()) return@withLock null

            val rootNodes = mutableListOf<MutableOverviewNode>()
            failuresByLeaf.forEach { (stepHash, entries) ->
                val metadata = metadataByLeaf[stepHash] ?: StoredStepMetadata(
                    displayName = stepHash,
                    type = StepType.STEP,
                    index = 0,
                    parents = emptyList()
                )
                var children = rootNodes
                metadata.parents.forEach { parent ->
                    val current = containerMetadata[parent.hash] ?: parent
                    val node = children.firstOrNull { it.hash == current.hash }
                        ?: MutableOverviewNode(current.hash, current.displayName, current.type, current.index, true)
                            .also(children::add)
                    children = node.children
                }
                val leaf = children.firstOrNull { it.hash == stepHash }
                    ?: MutableOverviewNode(stepHash, metadata.displayName, metadata.type, metadata.index, false)
                        .also(children::add)
                leaf.entries.addAll(entries)
            }

            HistoryOverviewSpec(
                hash = specDirectory.fileName.toString(),
                displayName = specDisplayName?.takeIf { it.isNotBlank() } ?: specDirectory.fileName.toString(),
                reportAvailable = screenshotDirectory?.parentFile
                    ?.let { File(it, "${specDirectory.fileName}.html").isFile }
                    ?: false,
                nodes = rootNodes.map(MutableOverviewNode::toOverviewNode).sortedWith(overviewNodeComparator)
            )
        }
    }

    private fun readStepMetadata(stepHash: String, properties: Properties): StoredStepMetadata {
        val displayName = properties.getProperty("$stepHash.displayName") ?: stepHash
        val type = properties.getProperty("$stepHash.type")
            ?.let { runCatching { StepType.valueOf(it) }.getOrNull() }
            ?: StepType.STEP
        val index = properties.getProperty("$stepHash.index")?.toIntOrNull() ?: 0
        val parents = properties.getProperty("$stepHash.parents")
            ?.split(',')
            ?.filter { it.isNotBlank() }
            .orEmpty()
            .map { hash ->
                val parentType = properties.getProperty("container.$hash.type")
                    ?.let { runCatching { StepType.valueOf(it) }.getOrNull() }
                    ?: StepType.SCENARIO
                HistoryContainer(
                    hash = hash,
                    displayName = properties.getProperty("container.$hash.displayName") ?: hash,
                    type = parentType,
                    index = properties.getProperty("container.$hash.index")?.toIntOrNull() ?: 0
                )
            }
        return StoredStepMetadata(displayName, type, index, parents)
    }

    private fun writeRun(directory: Path, records: List<HistoryRecord>, specDisplayName: String) {
        val runProperties = Properties().apply {
            setProperty("runId", config.runId)
            setProperty("timestamp", config.timestamp.toString())
            setProperty("specDisplayName", specDisplayName)
        }
        writeProperties(directory.resolve("run.properties"), runProperties)

        val stepsProperties = Properties()
        records.forEach { record ->
            stepsProperties.setProperty("${record.stepHash}.state", record.state.name)
            stepsProperties.setProperty("${record.stepHash}.displayName", record.displayName)
            stepsProperties.setProperty("${record.stepHash}.startTime", record.startTime.toString())
            stepsProperties.setProperty("${record.stepHash}.type", record.type.name)
            stepsProperties.setProperty("${record.stepHash}.index", record.index.toString())
            stepsProperties.setProperty("${record.stepHash}.parents", record.parents.joinToString(",") { it.hash })
            record.parents.forEach { parent ->
                stepsProperties.setProperty("container.${parent.hash}.displayName", parent.displayName)
                stepsProperties.setProperty("container.${parent.hash}.type", parent.type.name)
                stepsProperties.setProperty("container.${parent.hash}.index", parent.index.toString())
            }

            if (record.state == ResultState.FAILED || record.state == ResultState.ABORTED) {
                writeTextIfNotEmpty(directory.resolve("${record.stepHash}.errormsg.txt"), record.errorMsg)
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
            val startTime = Instant.parse(stepsProperties.getProperty("$stepHash.startTime"))
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
                startTime = startTime,
                state = state,
                errorMsg = readText(runDirectory.resolve("$stepHash.errormsg.txt")),
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
        private val overviewNodeComparator = compareByDescending<HistoryOverviewNode> { it.importance }
            .thenBy { it.index }
            .thenBy { it.displayName }
    }

    private data class StoredStepMetadata(
        val displayName: String,
        val type: StepType,
        val index: Int,
        val parents: List<HistoryContainer>
    )

    private data class MutableOverviewNode(
        val hash: String,
        val displayName: String,
        val type: StepType,
        val index: Int,
        val container: Boolean,
        val children: MutableList<MutableOverviewNode> = mutableListOf(),
        val entries: MutableList<HistoryEntry> = mutableListOf()
    ) {
        fun toOverviewNode(): HistoryOverviewNode = HistoryOverviewNode(
            hash = hash,
            displayName = displayName,
            type = type,
            index = index,
            container = container,
            children = children.map(MutableOverviewNode::toOverviewNode)
                .sortedWith(overviewNodeComparator),
            entries = entries.sortedWith(compareByDescending<HistoryEntry> { it.timestamp }.thenByDescending { it.runId })
        )
    }
}