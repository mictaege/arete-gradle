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
                -> specPlan.add(SpecificationStep(specPlan, testId, StepType.SPEC))
            testId.isAnnotated(Feature::class.java)
                -> specPlan.add(SpecificationStep(specPlan, testId, StepType.FEATURE))
            testId.isAnnotated(Scenario::class.java)
                -> specPlan.add(SpecificationStep(specPlan, testId, StepType.SCENARIO))
            testId.isAnnotated(Given::class.java)
                -> specPlan.add(SpecificationStep(specPlan, testId, StepType.GIVEN))
            testId.isAnnotated(When::class.java)
                -> specPlan.add(SpecificationStep(specPlan, testId, StepType.WHEN))
            testId.isAnnotated(Then::class.java)
                -> specPlan.add(SpecificationStep(specPlan, testId, StepType.THEN))
            testId.isAnnotated(Describe::class.java)
                -> specPlan.add(SpecificationStep(specPlan, testId, StepType.DESCRIBE))
            testId.isAnnotated(ItShould::class.java)
                -> specPlan.add(SpecificationStep(specPlan, testId, StepType.IT_SHOULD))
            testId.isAnnotated(Examples::class.java) && testId.isContainer
                -> specPlan.add(SpecificationStep(specPlan, testId, StepType.EXAMPLE_CONTAINER))
            testId.isAnnotated(Examples::class.java) && !testId.isContainer
                -> specPlan.add(SpecificationStep(specPlan, testId, StepType.EXAMPLE_INSTANCE))
            testId.isAnnotated(ExampleGrid::class.java) && testId.isContainer
                -> specPlan.add(SpecificationStep(specPlan, testId, StepType.EXAMPLE_GRID_CONTAINER))
            testId.isAnnotated(ExampleGrid::class.java) && !testId.isContainer
                -> specPlan.add(SpecificationStep(specPlan, testId, StepType.EXAMPLE_GRID_INSTANCE))
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