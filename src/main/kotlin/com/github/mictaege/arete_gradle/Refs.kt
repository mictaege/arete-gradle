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
    is Ref -> listOf(RefLink(value, resolveUrl(url, value), project))

    is RefsContainer -> value.map { RefLink(it.value, resolveUrl(it.url, it.value), it.project) }

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
    return RefLink(value, resolveUrl(meta.url, value), meta.project)
}

private fun Annotation.ownStringAttribute(name: String): String =
    runCatching {
        annotationClass.java.getMethod(name).invoke(this) as? String ?: ""
    }.getOrDefault("")

/** The {@code url} of a {@link Ref} is a pattern: a {@code {0}} placeholder is replaced by the {@code value}; without a placeholder the url is used prefix. */
private fun resolveUrl(url: String, value: String): String = when {
    url.isEmpty() -> ""
    url.contains("{0}") -> url.replace("{0}", value)
    else -> url + value
}

data class RefLink(
    val value: String,
    val url: String,
    val project: String = "",
) : Comparable<RefLink> {
    val hasUrl: Boolean
        get() = url.isNotEmpty()

    override fun compareTo(other: RefLink): Int {
        return compareValuesBy(this, other, { it.project }, { it.url }, { it.value })
    }
}

/** A group of ref links sharing the same [key] (the `project` attribute, or a url prefix fallback); an empty key groups links without project and url. */
data class RefGroup(
    val key: String,
    val links: List<RefLink>,
)

/** Longest common prefix of [a] and [b]. */
internal fun lcp(a: String, b: String): String {
    val n = minOf(a.length, b.length)
    var i = 0
    while (i < n && a[i] == b[i]) i++
    return a.substring(0, i)
}

/** The directory boundary of a url: the part up to and including the last '/'. */
internal fun urlDirectory(url: String): String {
    val idx = url.lastIndexOf('/')
    return if (idx >= 0) url.substring(0, idx + 1) else url
}

/**
 * Groups ref links by their `project` attribute: links with a non-blank project form one
 * group per project (e.g. `mytracker.org/PA-` holds PA-1 and PA-2, `mytracker.org/PB-` holds
 * PB-3). Links without a project fall back to grouping by the longest common prefix of
 * their resolved urls (last '/' of the url directory as boundary); links with neither
 * project nor url form a separate group with an empty key.
 */
fun groupRefLinks(links: List<RefLink>): List<RefGroup> {
    val distinct = links.distinct()
    val groups = mutableListOf<RefGroup>()

    distinct.filter { it.project.isNotBlank() }
        .groupBy { it.project }
        .toSortedMap()
        .forEach { (project, groupLinks) -> groups += RefGroup(project, groupLinks.sorted()) }

    val withUrl = distinct.filter { it.project.isBlank() && it.hasUrl }.sorted()
    var members = mutableListOf<RefLink>()
    var keyLength = 0
    fun closeGroup() {
        if (members.isNotEmpty()) {
            val key = if (members.size == 1) members.first().url else lcp(members.first().url, members.last().url)
            groups += RefGroup(key, members.toList())
        }
    }
    for (link in withUrl) {
        if (members.isEmpty()) {
            members.add(link)
            keyLength = urlDirectory(link.url).length
        } else {
            val l = lcp(link.url, members.last().url)
            if (l.length >= keyLength) {
                if (members.size == 1) keyLength = l.length
                members.add(link)
            } else {
                closeGroup()
                members = mutableListOf(link)
                keyLength = urlDirectory(link.url).length
            }
        }
    }
    closeGroup()

    val noUrl = distinct.filter { it.project.isBlank() && !it.hasUrl }.sorted()
    if (noUrl.isNotEmpty()) {
        groups += RefGroup("", noUrl)
    }
    return groups
}
