package com.github.mictaege.arete_gradle

import freemarker.template.Configuration
import freemarker.template.Configuration.VERSION_2_3_29
import freemarker.template.Template
import freemarker.template.TemplateExceptionHandler
import java.io.File
import java.io.StringWriter


object BuildDir {
    val reportDir = File(System.getProperty(AretePlugin.BUILD_DIR_PROPERTY) + "/reports")
    val areteDir = File(reportDir,"arete")
    val taskDir = File(areteDir, System.getProperty(AretePlugin.TASK_NAME_PROPERTY))
    val specsDir = File(taskDir, "specs")
}

object Freemaker {
    val cfg = Configuration(VERSION_2_3_29)
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
        writeHtmlFile("/spec.ftlh", mapOf("step" to step), File(BuildDir.specsDir, "${step.uniqueHash}.html"))
    }

    override fun finishPlan(plan: SpecificationPlan) {
        writeHtmlFile("/index.ftlh", mapOf("plan" to plan), File(BuildDir.taskDir, "index.html"))
        writeHtmlFile("/alphabetical.ftlh", mapOf("plan" to plan), File(BuildDir.taskDir, "alphabetical.html"))
        writeHtmlFile("/hierarchical.ftlh", mapOf("plan" to plan), File(BuildDir.taskDir, "hierarchical.html"))
        writeHtmlFile("/tagged.ftlh", mapOf("plan" to plan), File(BuildDir.taskDir, "tagged.html"))
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

}