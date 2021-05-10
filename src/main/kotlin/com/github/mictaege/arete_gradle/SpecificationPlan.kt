package com.github.mictaege.arete_gradle

import com.github.mictaege.arete.Spec
import com.google.common.hash.Hashing
import org.junit.platform.engine.TestExecutionResult
import org.junit.platform.engine.TestExecutionResult.Status
import org.junit.platform.launcher.TestIdentifier
import java.io.PrintWriter
import java.io.StringWriter
import kotlin.text.Charsets.UTF_8

enum class StepType(val container: Boolean) {
    SPEC(true),
    FEATURE(true),
    SCENARIO(true),
    GIVEN(false),
    WHEN(false),
    THEN(false),
    DESCRIBE(true),
    IT_SHOULD(false)
}

enum class ResultState(val sign: String) {
    FAILED("[x]"), ABORTED("[*]"), IGNORED("[/]"), SUCCESSFUL("[+]")
}

abstract class SpecificationNode {
    val steps = mutableListOf<SpecificationStep>()

    abstract fun add(step: SpecificationStep): Boolean

    abstract fun addResult(testId: TestIdentifier, testResult: TestExecutionResult): Boolean

    fun findFirst(predicate: (SpecificationStep) -> Boolean): SpecificationStep? {
        var step: SpecificationStep? = null
        steps.forEach {
            if (step == null) {
                step = if (predicate.invoke(it)) {
                    it
                } else {
                    it.findFirst(predicate)
                }
            }
        }
        return step
    }

    fun flatFilter(predicate: (SpecificationStep) -> Boolean): List<SpecificationStep> {
        val flatSteps = mutableListOf<SpecificationStep>()
        steps.forEach {
            if (predicate.invoke(it)) {
                flatSteps.add(it)
            }
            flatSteps.addAll(it.flatFilter(predicate))
        }
        return flatSteps
    }


    fun anyMatch(predicate: (SpecificationStep) -> Boolean): Boolean {
        return findFirst(predicate) != null
    }

    fun cumulatedState(): ResultState {
        return when {
            anyMatch { s -> s.resultState == ResultState.FAILED } -> ResultState.FAILED
            anyMatch { s -> s.resultState == ResultState.ABORTED } -> ResultState.ABORTED
            anyMatch { s -> s.resultState == ResultState.IGNORED } -> ResultState.IGNORED
            else -> ResultState.SUCCESSFUL
        }
    }
}

class SpecificationPlan: SpecificationNode() {
    private val allWriter = SpecificationWriter.allWriter

    override fun add(step: SpecificationStep): Boolean {
        var hit = false
        steps.forEach { hit = hit || it.add(step) }
        if (!hit) {
            steps.add(step)
        }
        return hit
    }

    override fun addResult(testId: TestIdentifier, testResult: TestExecutionResult): Boolean {
        var hit = false
        steps.forEach { hit = hit || it.addResult(testId, testResult) }
        writeIfSpec(testId)
        return hit
    }

    fun finishPlan() {
        allWriter.forEach { it.finishPlan(this)}
    }

    fun specsOrderedByPrio(): List<SpecificationStep> {
        return steps.sortedWith(Comparator<SpecificationStep> { s1, s2 ->
            s1.cumulatedState().compareTo(s2.cumulatedState())
        }.then { s1, s2 ->
            s1.displayName.compareTo(s2.displayName)
        })
    }

    fun specSummaries(): PlanSummaries {
        return PlanSummaries(steps)
    }

    fun scenarioSummaries(): PlanSummaries {
        return PlanSummaries(flatFilter { it.type == StepType.SCENARIO })
    }

    fun descSummaries(): PlanSummaries {
        return PlanSummaries(flatFilter { it.type == StepType.DESCRIBE })
    }

    private fun writeIfSpec(testId: TestIdentifier) {
        if (testId.isAnnotated(Spec::class.java)) {
            findFirst { it.uniqueId == testId.uniqueId }
                ?.apply { allWriter.forEach { it.writeSpec(this) } }
        }
    }

}

data class PlanSummaries(
    val overallCount:Int,
    val failedCount:Int,
    val abortedCount:Int,
    val ignoredCount:Int,
    val successCount:Int,
) {
    constructor(steps: List<SpecificationStep>) : this(
        steps.size,
        steps.filter { it.cumulatedState() == ResultState.FAILED }.size,
        steps.filter { it.cumulatedState() == ResultState.ABORTED }.size,
        steps.filter { it.cumulatedState() == ResultState.IGNORED }.size,
        steps.filter { it.cumulatedState() == ResultState.SUCCESSFUL }.size
    )
}

class SpecificationStep(
    val testId: TestIdentifier,
    val type: StepType,
    var testResult: TestExecutionResult? = null
): SpecificationNode() {
    val parentId: String = testId.parentId.orElse("")
    val uniqueId: String = testId.uniqueId
    val uniqueHash: String = "${Hashing.murmur3_128().newHasher()
                            .putString(uniqueId, UTF_8).hash().asLong()}"
                            .replace("-", "_")
    val displayName: String = testId.displayName
    val resultState: ResultState
        get() = when(testResult?.status) {
            Status.SUCCESSFUL -> ResultState.SUCCESSFUL
            Status.ABORTED -> ResultState.ABORTED
            Status.FAILED -> ResultState.FAILED
            null -> ResultState.IGNORED
        }
    val errorMsg: String
        get() = testResult?.throwable?.map { t -> t.localizedMessage }?.orElse("") ?: ""
    val stackTrace: String
        get() {
            return testResult?.throwable?.map { t ->
                val stringWriter = StringWriter()
                t.printStackTrace(PrintWriter(stringWriter))
                stringWriter.toString()
            }?.orElse("") ?: ""
        }

    override fun add(step: SpecificationStep): Boolean {
        return if (step.parentId == uniqueId) {
            steps.add(step)
        } else {
            var hit = false
            steps.forEach { hit = hit || it.add(step) }
            hit
        }
    }

    override fun addResult(testId: TestIdentifier, testResult: TestExecutionResult): Boolean {
        return if (this.testId.uniqueId == testId.uniqueId) {
            this.testResult = testResult
            true
        } else {
            var hit = false
            steps.forEach { hit = hit || it.addResult(testId, testResult) }
            hit
        }
    }

}