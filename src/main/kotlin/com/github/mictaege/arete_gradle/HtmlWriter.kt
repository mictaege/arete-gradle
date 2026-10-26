package com.github.mictaege.arete_gradle

import freemarker.template.Configuration
import freemarker.template.Configuration.VERSION_2_3_32
import freemarker.template.Template
import freemarker.template.TemplateExceptionHandler
import java.io.File
import java.io.StringWriter
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption.REPLACE_EXISTING
import java.nio.file.StandardOpenOption.CREATE
import java.nio.file.StandardOpenOption.TRUNCATE_EXISTING


object BuildDir {
    val reportDir = File(System.getProperty(AretePlugin.BUILD_DIR_PROPERTY) + "/reports")
    val areteDir = File(reportDir,"arete")
    val taskDir = File(areteDir, System.getProperty(AretePlugin.TASK_NAME_PROPERTY))
    val specsDir = File(taskDir, "specs")
    val iconsDir = File(taskDir, "icons")
}

object Freemaker {
    val cfg = Configuration(VERSION_2_3_32)
    init {
        cfg.setClassForTemplateLoading(javaClass, "/templates")
        cfg.defaultEncoding = "UTF-8"
        cfg.templateExceptionHandler = TemplateExceptionHandler.RETHROW_HANDLER
        cfg.logTemplateExceptions = false
        cfg.wrapUncheckedExceptions = true
        cfg.fallbackOnNullLoopVariable = false
    }
}

class HtmlWriter: SpecificationWriter {

    override fun writeSpec(step: SpecificationStep) {
        val historyStore = HistoryConfig.fromSystemProperties()?.let(::HistoryStore)
        historyStore?.let { loadHistory(step, step.uniqueHash, it) }
        copyResources(step)
        writeHtmlFile("/spec.ftlh", mapOf("step" to step, "colorScheme" to AreteColorSchemeProvider.colorScheme), File(BuildDir.specsDir, "${step.uniqueHash}.html"))
        historyStore?.save(step.uniqueHash, historyRecords(step), step.displayName)
        deleteOriginalScreenshots(step)
    }

    private fun loadHistory(step: SpecificationStep, specHash: String, historyStore: HistoryStore) {
        if (!step.type.container) {
            step.history = historyStore.load(specHash, step.uniqueHash, File(BuildDir.specsDir, "history"))
        }
        step.steps.forEach { loadHistory(it, specHash, historyStore) }
    }

    private fun historyRecords(spec: SpecificationStep): List<HistoryRecord> {
        val records = mutableListOf<HistoryRecord>()
        fun collect(step: SpecificationStep, parents: List<HistoryContainer>) {
            step.steps.forEachIndexed { index, child ->
                if (child.type.container) {
                    collect(
                        child,
                        parents + HistoryContainer(child.uniqueHash, child.displayName, child.type, index)
                    )
                } else {
                    records += HistoryRecord(
                        stepHash = child.uniqueHash,
                        displayName = child.displayName,
                        state = child.cumulatedState(),
                        startTime = child.timeStamp.toInstant(),
                        type = child.type,
                        index = index,
                        parents = parents,
                        errorMsg = child.errorMsg,
                        stackTrace = child.stackTrace,
                        stdout = child.stdout,
                        stderr = child.stderr,
                        screenshot = child.screenshot
                    )
                    collect(child, parents)
                }
            }
        }
        collect(spec, emptyList())
        return records
    }

    private fun copyResources(step: SpecificationStep) {
        step.screenshot?.also {
            try {
                val target = File(BuildDir.specsDir, "${step.uniqueHash}.png")
                target.parentFile.mkdirs()
                Files.copy(it.toPath(), target.toPath(), REPLACE_EXISTING)
            } catch (_: Exception) {
            }
        }
        if (step.hasNarrative) {
            step.narrative?.resources?.forEach { i ->
                val target = File(BuildDir.specsDir, i.fileName)
                target.parentFile.mkdirs()
                Files.write(
                    target.toPath(),
                    i.readResource(),
                    CREATE,
                    TRUNCATE_EXISTING
                )
            }
        }
        step.steps.forEach { copyResources(it) }
    }

    private fun deleteOriginalScreenshots(step: SpecificationStep) {
        step.screenshot?.also {
            ScreenshotCleanup.delete(it.toPath(), step.plan.screenshotOutputRoot)
            step.plan.removeScreenshot(step.uniqueId)
        }
        step.steps.forEach { deleteOriginalScreenshots(it) }
    }

    override fun finishPlan(plan: SpecificationPlan) {
        plan.flatFilter { step -> step.type == StepType.SPEC }.forEach { spec ->
            val specFile = File(BuildDir.specsDir, "${spec.uniqueHash}.html")
            if (specFile.exists()) {
                var specSource = specFile.readText()
                if (spec.hasSeeAlsoRefs) {
                    specSource = replaceSeeAlsoPlaceholder(spec, specSource)
                }
                spec.flatFilter { step -> step.hasSeeAlsoRefs }.forEach { step ->
                    specSource = replaceSeeAlsoPlaceholder(step, specSource)
                }
                specFile.writeText(specSource)
            }
        }
        writeHtmlFile("/index.ftlh", mapOf("plan" to plan, "colorScheme" to AreteColorSchemeProvider.colorScheme), File(BuildDir.taskDir, "index.html"))
        writeHtmlFile("/display_names.ftlh", mapOf("plan" to plan, "colorScheme" to AreteColorSchemeProvider.colorScheme), File(BuildDir.taskDir, "display_names.html"))
        writeHtmlFile("/test_specs.ftlh", mapOf("plan" to plan, "colorScheme" to AreteColorSchemeProvider.colorScheme), File(BuildDir.taskDir, "test_specs.html"))
        writeHtmlFile("/tags.ftlh", mapOf("plan" to plan, "colorScheme" to AreteColorSchemeProvider.colorScheme), File(BuildDir.taskDir, "tags.html"))
        val historyStore = HistoryConfig.fromSystemProperties()?.let(::HistoryStore)
        val overview = historyStore?.overview(File(BuildDir.specsDir, "history")) ?: HistoryOverview(emptyList())
        writeHtmlFile(
            "/history.ftlh",
            mapOf(
                "overview" to overview,
                "historyEnabled" to (historyStore != null),
                "colorScheme" to AreteColorSchemeProvider.colorScheme
            ),
            File(BuildDir.taskDir, "history.html")
        )
        extractIcons()
    }

    private fun replaceSeeAlsoPlaceholder(step: SpecificationStep, specSource: String): String {
        var replaced = specSource
        step.seeAlsoRefs?.refs?.forEach { ref ->
            if (ref.validTarget) {
                val placeholder = "<!--%%%%%${ref.className}%%%%%-->"
                val link = """
                            | <a href="${ref.targetUrl}" class="see-also">
                            |     <i class="see-also-icon"></i>
                            |     ${ref.displayName}
                            | </a>
                            """.trimMargin()
                replaced = replaced.replace(placeholder, link)
            }
        }
        return replaced
    }

    private fun writeHtmlFile(
        tmplFile: String,
        dataMap: Map<String, Any>,
        target: File
    ) {
        val temp: Template = Freemaker.cfg.getTemplate(tmplFile)
        val stringWriter = StringWriter()
        temp.process(dataMap, stringWriter)
        target.createAndWrite(stringWriter.toString())
    }

    private fun extractIcons() {
        File(BuildDir.taskDir, "icons").deleteRecursively()

        listOf(
            "icon-calendar",
            "icon-bars",
            "icon-bell",
            "icon-broadcast",
            "icon-buffer",
            "icon-building",
            "icon-camera",
            "icon-clipboard",
            "icon-external-link",
            "icon-file",
            "icon-gear",
            "icon-link",
            "icon-scroll",
            "icon-share",
            "icon-tag",
            "icon-users",
            "icon-flask",
            "icon-handshake",
            "icon-home",
            "icon-sitemap",
            "icon-tags",
            "icon-clock",
            "icon-weather"
        ).forEach {
            writeSvgFile("/$it.ftlh", AreteColorSchemeProvider.colorScheme.arete_color_background, File(BuildDir.iconsDir, "$it-bg.svg"))
            writeSvgFile("/$it.ftlh", AreteColorSchemeProvider.colorScheme.arete_color_foreground, File(BuildDir.iconsDir, "$it-fg.svg"))
            writeSvgFile("/$it.ftlh", AreteColorSchemeProvider.colorScheme.arete_color_neutral, File(BuildDir.iconsDir, "$it-neutral.svg"))
        }
    }

    private fun writeSvgFile(
        tmplFile: String,
        foreground: String,
        target: File
    ) {
        val temp: Template = Freemaker.cfg.getTemplate(tmplFile)
        val stringWriter = StringWriter()
        temp.process(mapOf("foreground" to foreground), stringWriter)
        target.createAndWrite(stringWriter.toString())
    }

}

internal object ScreenshotCleanup {
    fun delete(screenshot: Path, outputRoot: Path?) {
        runCatching { Files.deleteIfExists(screenshot) }

        val root = outputRoot?.toAbsolutePath()?.normalize() ?: return
        val path = screenshot.toAbsolutePath().normalize()
        if (!path.startsWith(root)) return

        var directory = path.parent
        while (directory != null && directory.startsWith(root) && directory != root && directory.parent != root) {
            try {
                Files.delete(directory)
                directory = directory.parent
            } catch (_: Exception) {
                break
            }
        }
    }
}