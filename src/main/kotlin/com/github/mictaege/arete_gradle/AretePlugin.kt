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
        const val ARETE_COLOR_BACKGROUND = "io.github.mictaege.arete_gradle.arete_color_background"
        const val ARETE_COLOR_CARD = "io.github.mictaege.arete_gradle.arete_color_card"
        const val ARETE_COLOR_FOREGROUND = "io.github.mictaege.arete_gradle.arete_color_foreground"
        const val ARETE_COLOR_IGNORED = "io.github.mictaege.arete_gradle.arete_color_ignored"
        const val ARETE_COLOR_IGNORED_EMPH = "io.github.mictaege.arete_gradle.arete_color_ignored_emph"
        const val ARETE_COLOR_SUCCESSFUL = "io.github.mictaege.arete_gradle.arete_color_successful"
        const val ARETE_COLOR_SUCCESSFUL_EMPH = "io.github.mictaege.arete_gradle.arete_color_successful_emph"
        const val ARETE_COLOR_ABORTED = "io.github.mictaege.arete_gradle.arete_color_aborted"
        const val ARETE_COLOR_ABORTED_EMPH = "io.github.mictaege.arete_gradle.arete_color_aborted_emph"
        const val ARETE_COLOR_FAILED = "io.github.mictaege.arete_gradle.arete_color_failed"
        const val ARETE_COLOR_FAILED_EMPH = "io.github.mictaege.arete_gradle.arete_color_failed_emph"
        const val ARETE_COLOR_NEUTRAL = "io.github.mictaege.arete_gradle.arete_color_neutral"
        const val ARETE_COLOR_NEUTRAL_EMPH = "io.github.mictaege.arete_gradle.arete_color_neutral_emph"
        val colorScheme: AreteColorScheme
            get() = AreteColorSchemeProperties(
                System.getProperty(ARETE_COLOR_BACKGROUND) ?: AreteClassicColors().arete_color_background,
                System.getProperty(ARETE_COLOR_CARD) ?: AreteClassicColors().arete_color_card,
                System.getProperty(ARETE_COLOR_FOREGROUND) ?: AreteClassicColors().arete_color_foreground,
                System.getProperty(ARETE_COLOR_IGNORED) ?: AreteClassicColors().arete_color_ignored,
                System.getProperty(ARETE_COLOR_IGNORED_EMPH) ?: AreteClassicColors().arete_color_ignored_emph,
                System.getProperty(ARETE_COLOR_SUCCESSFUL) ?: AreteClassicColors().arete_color_successful,
                System.getProperty(ARETE_COLOR_SUCCESSFUL_EMPH) ?: AreteClassicColors().arete_color_successful_emph,
                System.getProperty(ARETE_COLOR_ABORTED) ?: AreteClassicColors().arete_color_aborted,
                System.getProperty(ARETE_COLOR_ABORTED_EMPH) ?: AreteClassicColors().arete_color_aborted_emph,
                System.getProperty(ARETE_COLOR_FAILED) ?: AreteClassicColors().arete_color_failed,
                System.getProperty(ARETE_COLOR_FAILED_EMPH) ?: AreteClassicColors().arete_color_failed_emph,
                System.getProperty(ARETE_COLOR_NEUTRAL) ?: AreteClassicColors().arete_color_neutral,
                System.getProperty(ARETE_COLOR_NEUTRAL_EMPH) ?: AreteClassicColors().arete_color_neutral_emph
            )
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
                testTask.systemProperties[ARETE_COLOR_BACKGROUND] = scheme.arete_color_background
                testTask.systemProperties[ARETE_COLOR_CARD] = scheme.arete_color_card
                testTask.systemProperties[ARETE_COLOR_FOREGROUND] = scheme.arete_color_foreground
                testTask.systemProperties[ARETE_COLOR_IGNORED] = scheme.arete_color_ignored
                testTask.systemProperties[ARETE_COLOR_IGNORED_EMPH] = scheme.arete_color_ignored_emph
                testTask.systemProperties[ARETE_COLOR_SUCCESSFUL] = scheme.arete_color_successful
                testTask.systemProperties[ARETE_COLOR_SUCCESSFUL_EMPH] = scheme.arete_color_successful_emph
                testTask.systemProperties[ARETE_COLOR_ABORTED] = scheme.arete_color_aborted
                testTask.systemProperties[ARETE_COLOR_ABORTED_EMPH] = scheme.arete_color_aborted_emph
                testTask.systemProperties[ARETE_COLOR_FAILED] = scheme.arete_color_failed
                testTask.systemProperties[ARETE_COLOR_FAILED_EMPH] = scheme.arete_color_failed_emph
                testTask.systemProperties[ARETE_COLOR_NEUTRAL] = scheme.arete_color_neutral
                testTask.systemProperties[ARETE_COLOR_NEUTRAL_EMPH] = scheme.arete_color_neutral_emph
            })
        })
    }

}

open class AreteExtension {
    var colorScheme: AreteColorScheme = AreteClassicColors()
}
