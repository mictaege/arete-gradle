package com.github.mictaege.arete_gradle

import freemarker.template.Configuration
import freemarker.template.Configuration.VERSION_2_3_32
import freemarker.template.Template
import freemarker.template.TemplateExceptionHandler
import net.lingala.zip4j.ZipFile
import java.io.File
import java.io.StringWriter
import java.nio.file.Files
import java.nio.file.StandardCopyOption.REPLACE_EXISTING


object BuildDir {
    val reportDir = File(System.getProperty(AretePlugin.BUILD_DIR_PROPERTY) + "/reports")
    val areteDir = File(reportDir,"arete")
    val taskDir = File(areteDir, System.getProperty(AretePlugin.TASK_NAME_PROPERTY))
    val specsDir = File(taskDir, "specs")
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
        copyScreenshots(step)
        writeHtmlFile("/spec.ftlh", mapOf("step" to step, "colorScheme" to AretePlugin.colorScheme), File(BuildDir.specsDir, "${step.uniqueHash}.html"))
        deleteOriginalScreenshots(step)
    }

    private fun copyScreenshots(step: SpecificationStep) {
        step.screenshot?.also {
            try {
                val target = File(BuildDir.specsDir, "${step.uniqueHash}.png")
                target.mkdirs()
                Files.copy(it.toPath(), target.toPath(), REPLACE_EXISTING)
            } catch (ignore: Exception) {
            }
        }
        step.steps.forEach { copyScreenshots(it) }
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
        val zipFile = File(BuildDir.taskDir, "icons.zip")
        javaClass.getResourceAsStream("/icons.zip")?.let {
            Files.copy(it, zipFile.toPath(), REPLACE_EXISTING)
            ZipFile(zipFile).extractAll(BuildDir.taskDir.absolutePath)
            zipFile.delete()
        }
    }

}