package com.github.mictaege.arete_gradle

import com.github.mictaege.arete.*
import org.junit.platform.engine.TestExecutionResult
import org.junit.platform.launcher.TestExecutionListener
import org.junit.platform.launcher.TestIdentifier
import org.junit.platform.launcher.TestPlan

class AreteTestListener: TestExecutionListener {

    private val specPlan = SpecificationPlan()

    override fun executionStarted(testId: TestIdentifier) {
        when {
            testId.isAnnotated(Spec::class.java)
                -> specPlan.add(SpecificationStep(testId, StepType.SPEC))
            testId.isAnnotated(Feature::class.java)
                -> specPlan.add(SpecificationStep(testId, StepType.FEATURE))
            testId.isAnnotated(Scenario::class.java)
                -> specPlan.add(SpecificationStep(testId, StepType.SCENARIO))
            testId.isAnnotated(Given::class.java)
                -> specPlan.add(SpecificationStep(testId, StepType.GIVEN))
            testId.isAnnotated(When::class.java)
                -> specPlan.add(SpecificationStep(testId, StepType.WHEN))
            testId.isAnnotated(Then::class.java)
                -> specPlan.add(SpecificationStep(testId, StepType.THEN))
            testId.isAnnotated(Describe::class.java)
                -> specPlan.add(SpecificationStep(testId, StepType.DESCRIBE))
            testId.isAnnotated(ItShould::class.java)
                -> specPlan.add(SpecificationStep(testId, StepType.IT_SHOULD))
        }
    }

    override fun executionSkipped(testId: TestIdentifier, reason: String) {
        executionStarted(testId)
    }

    override fun executionFinished(testId: TestIdentifier, testResult: TestExecutionResult) {
        specPlan.addResult(testId, testResult)
    }

    override fun testPlanExecutionFinished(testPlan: TestPlan) {
        specPlan.finishPlan()
    }

}