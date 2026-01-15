package com.github.mictaege.arete_gradle

import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import java.util.*

class AretePlugin: Plugin<Project> {

    companion object {
        const val BUILD_DIR_PROPERTY = "io.github.mictaege.arete_gradle.buildDir"
        const val TASK_NAME_PROPERTY = "io.github.mictaege.arete_gradle.taskName"
    }

    override fun apply(project: Project) {
        val extension = project.extensions.create("arete", AreteExtension::class.java)
        val props = Properties()
        props.load(javaClass.classLoader.getResourceAsStream("arete-gradle.properties"))
        val version = props.getProperty("version")
        project.dependencies.add("testRuntimeOnly", "io.github.mictaege:arete-gradle:${version}")
        project.tasks.withType(Test::class.java, Action { testTask ->
            testTask.doFirst(Action {
                testTask.systemProperties[BUILD_DIR_PROPERTY] = project.buildDir.absolutePath
                testTask.systemProperties[TASK_NAME_PROPERTY] = testTask.name
                testTask.systemProperties[TASK_NAME_PROPERTY] = testTask.name

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
    }

}

open class AreteExtension {
    var colorScheme: AreteColorScheme = AreteClassicColors()
}
