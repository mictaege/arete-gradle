package com.github.mictaege.arete_gradle

import com.github.mictaege.arete_gradle.ResultState.*
import org.fusesource.jansi.Ansi.ansi

class ConsoleWriter: SpecificationWriter {

    override fun finishPlan(plan: SpecificationPlan) {
        //nop
    }

    override fun writeSpec(step: SpecificationStep) {
        val buffer = StringBuffer()
        collect(0, buffer, step)
        println(ansi().render(buffer.toString()).toString())
    }

    private fun collect(intend: Int, buffer: StringBuffer, step: SpecificationStep) {
        if (step.type.container) {
            buffer.append("\n")
        }
        val tab = "\t"
        val tabs = (0 until intend).fold("") { a, _ -> a + tab }
        val displayStrg: String
        if (step.resultState == FAILED && step.errorMsg.isNotEmpty()) {
            val errorQuote = step.errorMsg.split("\n")
                .map { l -> l.trim() }
                .filter { l -> l.isNotEmpty() }
                .joinToString("\n") { l -> "${tabs}${tab}@|red > ${l}|@" }
            displayStrg = "@|red ${step.resultState.sign} ${step.displayName}|@\n${errorQuote}"
        } else {
            val displayMsg = "${step.resultState.sign} ${step.displayName}"
            displayStrg = when(step.resultState) {
                SUCCESSFUL -> "@|green ${displayMsg}|@"
                ABORTED -> "@|yellow ${displayMsg}|@"
                IGNORED -> "@|white ${displayMsg}|@"
                else -> displayMsg
            }
        }
        if (step.type == StepType.SPEC) {
            buffer.append("\n")
        }
        buffer.append(tabs).append(displayStrg).append("\n")
        if (step.type == StepType.EXAMPLE_GRID_CONTAINER) {
            val seperator = "${tabs}$tab    @|white ${IntRange(1, step.gridHeader.length).map { "-" }.joinToString("")}|@\n"
            buffer.append(seperator)
            buffer.append("${tabs}$tab    @|white ${step.gridHeader}|@\n")
            buffer.append(seperator)
        }
        if (step.type == StepType.SPEC) {
            buffer.append("${tabs}@|white ________________________________________|@\n")
        }
        step.steps.forEach {
            collect(intend + 1, buffer, it)
        }
    }

}