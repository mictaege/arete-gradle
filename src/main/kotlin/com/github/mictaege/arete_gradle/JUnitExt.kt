package com.github.mictaege.arete_gradle

import org.junit.platform.engine.support.descriptor.ClassSource
import org.junit.platform.engine.support.descriptor.MethodSource
import org.junit.platform.launcher.TestIdentifier
import java.lang.reflect.Method


fun TestIdentifier.testClass(): Class<*>? {
    return when (val source = this.source.orElse(null)) {
        is ClassSource -> source.javaClass
        is MethodSource -> source.javaClass
        else -> null
    }
}

fun TestIdentifier.testMethod(): Method? {
    return when (val source = this.source.orElse(null)) {
        is MethodSource -> source.javaMethod
        else -> null
    }
}

fun TestIdentifier.isAnnotated(annotation: Class<out Annotation>): Boolean {
    return when (val source = this.source.orElse(null)) {
        is ClassSource -> source.javaClass.isAnnotationPresent(annotation)
        is MethodSource -> source.javaMethod.isAnnotationPresent(annotation)
        else -> false
    }
}