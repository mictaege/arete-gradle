package com.github.mictaege.arete_gradle

import com.github.mictaege.arete_ktx.KtxNodeRegistry
import com.github.mictaege.arete_ktx.KtxTestSource
import org.junit.platform.engine.support.descriptor.ClassSource
import org.junit.platform.engine.support.descriptor.MethodSource
import org.junit.platform.launcher.TestIdentifier
import java.lang.reflect.Method


fun TestIdentifier.testClass(): Class<*>? {
    return when (val source = this.source.orElse(null)) {
        is ClassSource -> source.javaClass
        is MethodSource -> source.javaClass
        is KtxTestSource -> source.specClass
        else -> null
    }
}

fun TestIdentifier.testMethod(): Method? {
    return when (val source = this.source.orElse(null)) {
        is MethodSource -> source.javaMethod
        else -> null
    }
}

// Kotlin DSL spec nodes carry a plain ClassSource (for IDE navigation); their arete
// annotation metadata lives on the KtxNode registered under the descriptor uniqueId.
private fun TestIdentifier.ktxSource(): KtxTestSource? {
    val source = this.source.orElse(null)
    if (source is KtxTestSource) return source
    if (source is ClassSource && com.github.mictaege.arete_ktx.Spec::class.java.isAssignableFrom(source.javaClass)) {
        val node = KtxNodeRegistry.nodeFor(this.uniqueId) ?: return null
        return KtxTestSource(node.type, node.annotations, source.javaClass)
    }
    return null
}

fun TestIdentifier.isAnnotated(annotation: Class<out Annotation>): Boolean {
    ktxSource()?.let { return it.has(annotation) }
    return when (val source = this.source.orElse(null)) {
        is ClassSource -> source.javaClass.isAnnotationPresent(annotation)
        is MethodSource -> source.javaMethod.isAnnotationPresent(annotation)
        else -> false
    }
}

fun <A: Annotation>TestIdentifier.getAnnotation(annotation: Class<A>): A? {
    ktxSource()?.let { return it.proxy(annotation) }
    return when (val source = this.source.orElse(null)) {
        is ClassSource -> source.javaClass.getAnnotation(annotation)
        is MethodSource -> source.javaMethod.getAnnotation(annotation)
        else -> null
    }
}

val TestIdentifier.sourceAnnotations: Array<Annotation>
    get() {
        ktxSource()?.let { return it.allProxies() }
        return when (val source = this.source.orElse(null)) {
            is ClassSource -> source.javaClass.annotations
            is MethodSource -> source.javaMethod.annotations
            else -> emptyArray()
        }
    }

val TestIdentifier.isTestTemplate: Boolean
    get() = this.uniqueId.contains("template-invocation")


