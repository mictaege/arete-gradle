package com.github.mictaege.arete_gradle

import com.github.mictaege.arete.Normalizer
import com.github.mictaege.arete.UniqueIdToHashNormalizer
import org.junit.platform.launcher.TestIdentifier

interface TestIdentifierNormalizer: Normalizer<TestIdentifier, String> {
    override fun normalize(input: TestIdentifier) = UniqueIdToHashNormalizer().normalize(input.uniqueId)
}