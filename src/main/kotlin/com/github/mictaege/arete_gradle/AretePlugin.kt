package com.github.mictaege.arete_gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import java.util.*

class AretePlugin: Plugin<Project>{

    companion object {
        const val BUILD_DIR_PROPERTY = "com.github.mictaege.arete_gradle.buildDir"
    }

    override fun apply(project: Project) {
        val props = Properties()
        props.load(javaClass.classLoader.getResourceAsStream("arete-gradle.properties"))
        val version = props.getProperty("version")
        project.dependencies.add("testRuntimeOnly", "com.github.mictaege:arete-gradle:${version}")
        project.tasks.withType(Test::class.java) { test ->
            test.doFirst {
                test.systemProperties[BUILD_DIR_PROPERTY] = project.buildDir.absolutePath
            }
        }
    }

}
