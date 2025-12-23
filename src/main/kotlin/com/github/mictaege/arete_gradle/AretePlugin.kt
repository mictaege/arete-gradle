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
        val colorScheme = CatppuccinLatteColors()
    }

    override fun apply(project: Project) {
        val props = Properties()
        props.load(javaClass.classLoader.getResourceAsStream("arete-gradle.properties"))
        val version = props.getProperty("version")
        project.dependencies.add("testRuntimeOnly", "io.github.mictaege:arete-gradle:${version}")
        project.tasks.withType(Test::class.java, Action { testTask ->
            testTask.doFirst(Action {
                testTask.systemProperties[BUILD_DIR_PROPERTY] = project.buildDir.absolutePath
                testTask.systemProperties[TASK_NAME_PROPERTY] = testTask.name
            })
        })
    }

}
