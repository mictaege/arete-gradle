package com.github.mictaege.arete_gradle

interface SpecificationWriter {

    fun writeSpec(step: SpecificationStep)
    fun finishPlan(plan: SpecificationPlan)

    companion object {
        val allWriter = arrayListOf<SpecificationWriter>(ConsoleWriter(), HtmlWriter())
    }

}