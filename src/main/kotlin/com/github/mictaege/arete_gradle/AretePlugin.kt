package com.github.mictaege.arete_gradle

import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import java.io.File
import java.time.Instant
import java.util.*

class AretePlugin: Plugin<Project> {

    companion object {
        const val BUILD_DIR_PROPERTY = "io.github.mictaege.arete_gradle.buildDir"
        const val TASK_NAME_PROPERTY = "io.github.mictaege.arete_gradle.taskName"
        const val CAPTURE_STDOUT_PROPERTY = "junit.platform.output.capture.stdout"
        const val CAPTURE_STDERR_PROPERTY = "junit.platform.output.capture.stderr"
        const val CAPTURE_MAX_BUFFER_PROPERTY = "junit.platform.output.capture.maxBuffer"
    }

    override fun apply(project: Project) {
        val extension = project.extensions.create("arete", AreteExtension::class.java)
        val props = Properties()
        props.load(javaClass.classLoader.getResourceAsStream("arete-gradle.properties"))
        val version = props.getProperty("version")
        project.dependencies.add("testRuntimeOnly", "io.github.mictaege:arete-gradle:${version}")
        project.afterEvaluate {
            require(extension.captureMaxBuffer > 0) { "captureMaxBuffer must be positive" }
            require(extension.historyMaxAge > 0) { "historyMaxAge must be positive" }
        }
        project.tasks.withType(Test::class.java, Action { testTask ->
            testTask.doFirst(Action {
                testTask.systemProperties[BUILD_DIR_PROPERTY] = project.buildDir.absolutePath
                testTask.systemProperties[TASK_NAME_PROPERTY] = testTask.name
                testTask.systemProperties[CAPTURE_STDOUT_PROPERTY] = "${extension.captureStdout}"
                testTask.systemProperties[CAPTURE_STDERR_PROPERTY] = "${extension.captureStderr}"
                testTask.systemProperties[CAPTURE_MAX_BUFFER_PROPERTY] = "${extension.captureMaxBuffer}"

                val runTimestamp = Instant.now()
                val runId = HistoryConfig.newRunId(runTimestamp)
                if (extension.historyEnabled) {
                    testTask.systemProperties[HistoryConfig.DIRECTORY_PROPERTY] = historyTaskDirectory(project, extension, testTask.name).absolutePath
                    testTask.systemProperties[HistoryConfig.RUN_ID_PROPERTY] = runId
                    testTask.systemProperties[HistoryConfig.TIMESTAMP_PROPERTY] = runTimestamp.toString()
                    testTask.systemProperties[HistoryConfig.MAX_AGE_PROPERTY] = extension.historyMaxAge.toString()
                }

                val scheme = extension.colorScheme
                testTask.systemProperties[AreteColorSchemeProvider.ARETE_COLOR_BACKGROUND] = scheme.arete_color_background
                testTask.systemProperties[AreteColorSchemeProvider.ARETE_COLOR_CARD] = scheme.arete_color_card
                testTask.systemProperties[AreteColorSchemeProvider.ARETE_COLOR_FOREGROUND] = scheme.arete_color_foreground
                testTask.systemProperties[AreteColorSchemeProvider.ARETE_COLOR_IGNORED] = scheme.arete_color_ignored
                testTask.systemProperties[AreteColorSchemeProvider.ARETE_COLOR_IGNORED_EMPH] = scheme.arete_color_ignored_emph
                testTask.systemProperties[AreteColorSchemeProvider.ARETE_COLOR_SUCCESSFUL] = scheme.arete_color_successful
                testTask.systemProperties[AreteColorSchemeProvider.ARETE_COLOR_SUCCESSFUL_EMPH] = scheme.arete_color_successful_emph
                testTask.systemProperties[AreteColorSchemeProvider.ARETE_COLOR_ABORTED] = scheme.arete_color_aborted
                testTask.systemProperties[AreteColorSchemeProvider.ARETE_COLOR_ABORTED_EMPH] = scheme.arete_color_aborted_emph
                testTask.systemProperties[AreteColorSchemeProvider.ARETE_COLOR_FAILED] = scheme.arete_color_failed
                testTask.systemProperties[AreteColorSchemeProvider.ARETE_COLOR_FAILED_EMPH] = scheme.arete_color_failed_emph
                testTask.systemProperties[AreteColorSchemeProvider.ARETE_COLOR_NEUTRAL] = scheme.arete_color_neutral
                testTask.systemProperties[AreteColorSchemeProvider.ARETE_COLOR_NEUTRAL_EMPH] = scheme.arete_color_neutral_emph
                testTask.systemProperties[AreteColorSchemeProvider.ARETE_PLANTUML_THEME] = scheme.arete_plantuml_theme
            })
        })
        project.tasks.register("cleanAreteHistory") { task ->
            task.group = "arete"
            task.description = "Deletes the stored Arete test history for this project."
            task.doLast {
                project.delete(historyProjectDirectory(project, extension))
            }
        }
    }

}

open class AreteExtension {
    var colorScheme: AreteColorScheme = AreteClassicColors()
    var captureStdout = false
    var captureStderr = false
    var captureMaxBuffer = 1048576
    var historyEnabled = true
    var historyMaxAge = HistoryConfig.DEFAULT_MAX_AGE
    var historyDir: File? = null
}

private fun historyBaseDirectory(project: Project, extension: AreteExtension): File {
    val configuredDirectory = extension.historyDir
        ?: project.findProperty("arete.historyDir")?.toString()?.takeIf { it.isNotBlank() }?.let(project::file)
        ?: System.getenv("ARETE_HISTORY_DIR")?.takeIf { it.isNotBlank() }?.let(project::file)
        ?: File(project.rootDir, ".gradle/arete-history")
    return configuredDirectory.absoluteFile
}

private fun historyProjectDirectory(project: Project, extension: AreteExtension): File {
    val projectSegments = project.path.split(":").filter { it.isNotBlank() }
    val identity = (listOf(project.rootProject.name) + projectSegments)
        .joinToString("/") { sanitize(it) }
    return File(historyBaseDirectory(project, extension), identity)
}

private fun historyTaskDirectory(project: Project, extension: AreteExtension, taskName: String): File =
    File(historyProjectDirectory(project, extension), sanitize(taskName))

private val unsafePathCharacters = Regex("[^A-Za-z0-9._-]")

private fun sanitize(value: String): String = value.replace(unsafePathCharacters, "_")
