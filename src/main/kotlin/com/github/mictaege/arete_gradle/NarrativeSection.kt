package com.github.mictaege.arete_gradle

import com.github.mictaege.arete.Narrative

class NarrativeSection(annotation: Narrative) {
    val header = annotation.header
    val lines = annotation.value.toList()
}