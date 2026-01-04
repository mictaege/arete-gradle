package com.github.mictaege.arete_gradle

import freemarker.template.Configuration
import freemarker.template.Configuration.VERSION_2_3_32
import freemarker.template.Template
import freemarker.template.TemplateExceptionHandler
import java.io.File
import java.io.StringWriter
import java.nio.file.Files
import java.nio.file.OpenOption
import java.nio.file.StandardCopyOption.REPLACE_EXISTING
import java.nio.file.StandardOpenOption
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
        copyResources(step)
        writeHtmlFile("/spec.ftlh", mapOf("step" to step, "colorScheme" to AretePlugin.colorScheme), File(BuildDir.specsDir, "${step.uniqueHash}.html"))
        deleteOriginalScreenshots(step)
    }

    private fun copyResources(step: SpecificationStep) {
        step.screenshot?.also {
            try {
                val target = File(BuildDir.specsDir, "${step.uniqueHash}.png")
                target.parentFile.mkdirs()
                Files.copy(it.toPath(), target.toPath(), REPLACE_EXISTING)
            } catch (ignore: Exception) {
            }
        }
        if (step.hasNarrative) {
            step.narrative?.images?.forEach { i ->
                val target = File(BuildDir.specsDir, i.imageFileName)
                target.parentFile.mkdirs()
                Files.write(
                    target.toPath(),
                    i.readImage(),
                    CREATE,
                    TRUNCATE_EXISTING
                )
            }
        }
        step.steps.forEach { copyResources(it) }
    }

    private fun deleteOriginalScreenshots(step: SpecificationStep) {
        step.screenshot?.also {
            try {
                it.delete()
            } catch (ignore: Exception) {
            }
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
        writeHtmlFile("/index.ftlh", mapOf("plan" to plan, "colorScheme" to AretePlugin.colorScheme), File(BuildDir.taskDir, "index.html"))
        writeHtmlFile("/display_names.ftlh", mapOf("plan" to plan, "colorScheme" to AretePlugin.colorScheme), File(BuildDir.taskDir, "display_names.html"))
        writeHtmlFile("/test_specs.ftlh", mapOf("plan" to plan, "colorScheme" to AretePlugin.colorScheme), File(BuildDir.taskDir, "test_specs.html"))
        writeHtmlFile("/tags.ftlh", mapOf("plan" to plan, "colorScheme" to AretePlugin.colorScheme), File(BuildDir.taskDir, "tags.html"))
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
            "icon-bars",
            "icon-camera",
            "icon-clipboard",
            "icon-external-link",
            "icon-file",
            "icon-link",
            "icon-share",
            "menu-icon-flask",
            "menu-icon-handshake",
            "menu-icon-home",
            "menu-icon-tags"
        ).forEach {
            writeSvgFile("/$it.ftlh", AretePlugin.colorScheme.arete_color_background, File(BuildDir.iconsDir, "$it-bg.svg"))
            writeSvgFile("/$it.ftlh", AretePlugin.colorScheme.arete_color_foreground, File(BuildDir.iconsDir, "$it-fg.svg"))
            writeSvgFile("/$it.ftlh", AretePlugin.colorScheme.arete_color_neutral, File(BuildDir.iconsDir, "$it-color.svg"))
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