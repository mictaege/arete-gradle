package com.github.mictaege.arete_gradle

import com.github.mictaege.arete.Ref
import com.github.mictaege.arete.Refs as RefsContainer

class Refs(step: SpecificationStep) {
    val links: List<RefLink> = if (step.isTestTemplate) {
        emptyList()
    } else {
        step.testId.sourceAnnotations.flatMap { it.expandRefs() }
    }
}

private fun Annotation.expandRefs(): List<RefLink> = when (this) {
    is Ref -> listOf(RefLink(value, resolveUrl(url, value)))

    is RefsContainer -> value.map { RefLink(it.value, resolveUrl(it.url, it.value)) }

    else -> {
        val meta = annotationClass.java.getAnnotationsByType(Ref::class.java)
        when {
            meta.isNotEmpty() -> listOf(toComposedRefLink(meta.first()))
            else -> containerAnnotations().flatMap { it.expandRefs() }
        }
    }
}

/** Annotations held in a container attribute, e.g. the {@code value()} of {@code @GithubRefs}. */
private fun Annotation.containerAnnotations(): List<Annotation> =
    runCatching {
        val method = annotationClass.java.getMethod("value")
        if (method.returnType.isArray && Annotation::class.java.isAssignableFrom(method.returnType.componentType)) {
            (method.invoke(this) as? Array<out Annotation>)?.toList() ?: emptyList()
        } else {
            emptyList()
        }
    }.getOrDefault(emptyList())

private fun Annotation.toComposedRefLink(meta: Ref): RefLink {
    val value = ownStringAttribute("value")
    return RefLink(value, resolveUrl(meta.url, value))
}

private fun Annotation.ownStringAttribute(name: String): String =
    runCatching {
        annotationClass.java.getMethod(name).invoke(this) as? String ?: ""
    }.getOrDefault("")

/** The {@code url} of a {@link Ref} is a prefix: the {@code value} is appended to it, unless the url contains a {@code {0}} placeholder. */
private fun resolveUrl(url: String, value: String): String = when {
    url.isEmpty() -> ""
    url.contains("{0}") -> url.replace("{0}", value)
    else -> url + value
}

data class RefLink(
    val value: String,
    val url: String,
) {
    val hasUrl: Boolean
        get() = url.isNotEmpty()
}
