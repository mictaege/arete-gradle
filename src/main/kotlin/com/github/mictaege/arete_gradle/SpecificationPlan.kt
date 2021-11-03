package com.github.mictaege.arete_gradle

import com.github.mictaege.arete.Spec
import org.junit.platform.engine.TestExecutionResult
import org.junit.platform.engine.TestExecutionResult.Status
import org.junit.platform.launcher.TestIdentifier
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter

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
        return steps.filter { it.cumulatedState() != ResultState.SUCCESSFUL }
            .sortedWith(Comparator<SpecificationStep> { s1, s2 ->
                s1.cumulatedState().compareTo(s2.cumulatedState())
            }.then { s1, s2 ->
                s1.displayName.compareTo(s2.displayName)
            })
    }

    fun specsOrderedByName(): List<SpecificationStep> {
        return steps.sortedWith { s1, s2 -> s1.displayName.compareTo(s2.displayName) }
    }

    fun specsOrderedByHierarchy(): List<SpecificationStep> {
        return steps.sortedWith { s1, s2 -> s1.testClassName.compareTo(s2.testClassName) }
    }

    fun specsOrderedByTags(): List<SpecificationStep> {
        return steps.filter { it.tags.isNotEmpty() }.sortedWith { s1, s2 -> s1.tags.compareTo(s2.tags) }
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
): SpecificationNode(), TestIdentifierNormalizer {
    val parentId: String = testId.parentId.orElse("")
    val uniqueId: String = testId.uniqueId
    val uniqueHash: String = normalize(testId)
    val displayName: String = testId.displayName
    val testClassName: String = testId.testClass()?.canonicalName ?: ""
    val tags: String = testId.tags.map { t -> t.name }.sorted().joinToString(" ") { n -> "#$n" }
    val resultState: ResultState
        get() = when(testResult?.status) {
            Status.SUCCESSFUL -> ResultState.SUCCESSFUL
            Status.ABORTED -> ResultState.ABORTED
            Status.FAILED -> ResultState.FAILED
            null -> ResultState.IGNORED
        }
    val errorMsg: String
        get() = testResult?.throwable?.map { t -> t.localizedMessage }?.map { m -> m.trim() }?.orElse("") ?: ""
    val hasErrorMsg: Boolean
        get() = errorMsg.isNotEmpty()
    val stackTrace: String
        get() {
            return testResult?.throwable?.map { t ->
                val stringWriter = StringWriter()
                t.printStackTrace(PrintWriter(stringWriter))
                stringWriter.toString()
            }?.map { m -> m.trim() }?.orElse("") ?: ""
        }
    val screenshot: File?
        get() {
            val tmpDir = File(File(System.getProperty("java.io.tmpdir")), "arete_screenshots")
            val screenshot = File(tmpDir, uniqueHash + ".png")
            return if (screenshot.exists()) {
                screenshot
            } else {
                null
            }
        }
    val hasScreenshot: Boolean
        get() = screenshot != null

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