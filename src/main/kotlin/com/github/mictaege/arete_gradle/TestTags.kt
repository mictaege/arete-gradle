package com.github.mictaege.arete_gradle

import com.github.mictaege.arete.StereoType
import com.github.mictaege.arete.StereoTypes
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Tags
import org.junit.platform.launcher.TestIdentifier

class TestTags(testId: TestIdentifier) : Comparable<TestTags> {
    val tags: List<TestTag> = testId.sourceAnnotations
        .flatMap { annotation ->
            val stereoType: StereoType? = annotation.annotationClass.java.getAnnotation(StereoType::class.java)

            when (annotation) {
                is Tag -> listOf(TestTag(annotation, stereoType))

                is Tags -> annotation.value
                    .map { tag -> TestTag(tag, stereoType) }

                else -> annotation.annotationClass.java
                    .getAnnotationsByType(Tag::class.java)
                    .map { tag -> TestTag(tag, stereoType) }
            }
        }
        .sorted()

    override fun compareTo(other: TestTags): Int {
        return tags.compareLexicographically(other.tags)
    }

    private fun List<TestTag>.compareLexicographically(other: List<TestTag>): Int {
        val commonSize = minOf(size, other.size)

        for (index in 0 until commonSize) {
            val comparison = this[index].compareTo(other[index])
            if (comparison != 0) {
                return comparison
            }
        }

        return size.compareTo(other.size)
    }
}

data class TestTag(
    val tagName: String,
    val stereoType: StereoTypes,
) : Comparable<TestTag> {
    constructor(tag: Tag, stereoType: StereoType?) : this(
        tagName = tag.value,
        stereoType = stereoType?.value ?: StereoTypes.TAG,
    )

    override fun compareTo(other: TestTag): Int {
        return compareValuesBy(this, other, { it.stereoType }, { it.tagName })
    }
}