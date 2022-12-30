package com.github.mictaege.arete_gradle

import com.github.mictaege.arete.SeeAlso
import com.github.mictaege.arete.SeeAlsoDeclaration

class ReferenceTargets(plan: SpecificationPlan, annotations: Array<SeeAlso>) {
    constructor(plan: SpecificationPlan, annotation: SeeAlsoDeclaration) : this(plan, annotation.value)
    constructor(plan: SpecificationPlan, annotation: SeeAlso) : this(plan, arrayOf(annotation))

    val refs = annotations.map { r -> ReferenceTarget(plan, r) }
}

class ReferenceTarget(plan: SpecificationPlan, annotation: SeeAlso) {
    val type = annotation.value.java
    val className = annotation.value.java.canonicalName
    val validTarget = plan.findStepByType(type) != null
    val target = plan.findStepByType(type)
    val displayName = target?.displayName.orEmpty()
    val targetUrl = when {
            target == null -> ""
            target.type == StepType.SPEC -> "./${target.uniqueHash}.html"
            else -> {
                val spec = plan.findSpecByChild(target)
                if (spec == null) {
                    ""
                } else {
                    "./${spec.uniqueHash}.html#${target.uniqueHash}"
                }
            }
        }
}