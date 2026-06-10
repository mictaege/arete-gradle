package com.github.mictaege.arete_gradle

import com.github.mictaege.arete.StereoType
import com.github.mictaege.arete.StereoTypes
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Tags

class TestTags(step: SpecificationStep) : Comparable<TestTags> {
    val tags: List<TestTag> = if (step.isTestTemplate) {
        emptyList()
    } else {
        step.testId.sourceAnnotations
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
    }

    override fun compareTo(other: TestTags): Int {
        return tags.compareListOfTags(other.tags)
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