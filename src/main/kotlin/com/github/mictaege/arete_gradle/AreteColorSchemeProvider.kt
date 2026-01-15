package com.github.mictaege.arete_gradle

class AreteColorSchemeProvider {
    companion object {
        const val ARETE_COLOR_BACKGROUND = "io.github.mictaege.arete_gradle.arete_color_background"
        const val ARETE_COLOR_CARD = "io.github.mictaege.arete_gradle.arete_color_card"
        const val ARETE_COLOR_FOREGROUND = "io.github.mictaege.arete_gradle.arete_color_foreground"
        const val ARETE_COLOR_IGNORED = "io.github.mictaege.arete_gradle.arete_color_ignored"
        const val ARETE_COLOR_IGNORED_EMPH = "io.github.mictaege.arete_gradle.arete_color_ignored_emph"
        const val ARETE_COLOR_SUCCESSFUL = "io.github.mictaege.arete_gradle.arete_color_successful"
        const val ARETE_COLOR_SUCCESSFUL_EMPH = "io.github.mictaege.arete_gradle.arete_color_successful_emph"
        const val ARETE_COLOR_ABORTED = "io.github.mictaege.arete_gradle.arete_color_aborted"
        const val ARETE_COLOR_ABORTED_EMPH = "io.github.mictaege.arete_gradle.arete_color_aborted_emph"
        const val ARETE_COLOR_FAILED = "io.github.mictaege.arete_gradle.arete_color_failed"
        const val ARETE_COLOR_FAILED_EMPH = "io.github.mictaege.arete_gradle.arete_color_failed_emph"
        const val ARETE_COLOR_NEUTRAL = "io.github.mictaege.arete_gradle.arete_color_neutral"
        const val ARETE_COLOR_NEUTRAL_EMPH = "io.github.mictaege.arete_gradle.arete_color_neutral_emph"
        const val ARETE_PLANTUML_THEME = "io.github.mictaege.arete_gradle.arete_plantuml_theme"
        val colorScheme: AreteColorScheme
            get() = AreteColorSchemeProperties(
                System.getProperty(ARETE_COLOR_BACKGROUND) ?: AreteClassicColors().arete_color_background,
                System.getProperty(ARETE_COLOR_CARD) ?: AreteClassicColors().arete_color_card,
                System.getProperty(ARETE_COLOR_FOREGROUND) ?: AreteClassicColors().arete_color_foreground,
                System.getProperty(ARETE_COLOR_IGNORED) ?: AreteClassicColors().arete_color_ignored,
                System.getProperty(ARETE_COLOR_IGNORED_EMPH) ?: AreteClassicColors().arete_color_ignored_emph,
                System.getProperty(ARETE_COLOR_SUCCESSFUL) ?: AreteClassicColors().arete_color_successful,
                System.getProperty(ARETE_COLOR_SUCCESSFUL_EMPH) ?: AreteClassicColors().arete_color_successful_emph,
                System.getProperty(ARETE_COLOR_ABORTED) ?: AreteClassicColors().arete_color_aborted,
                System.getProperty(ARETE_COLOR_ABORTED_EMPH) ?: AreteClassicColors().arete_color_aborted_emph,
                System.getProperty(ARETE_COLOR_FAILED) ?: AreteClassicColors().arete_color_failed,
                System.getProperty(ARETE_COLOR_FAILED_EMPH) ?: AreteClassicColors().arete_color_failed_emph,
                System.getProperty(ARETE_COLOR_NEUTRAL) ?: AreteClassicColors().arete_color_neutral,
                System.getProperty(ARETE_COLOR_NEUTRAL_EMPH) ?: AreteClassicColors().arete_color_neutral_emph,
                System.getProperty(ARETE_PLANTUML_THEME) ?: AreteClassicColors().arete_plantuml_theme
            )
    }
}