package com.github.mictaege.arete_gradle

interface AreteColorScheme {
    val arete_color_background: String
    val arete_color_card: String
    val arete_color_foreground: String
    val arete_color_ignored: String
    val arete_color_ignored_emph: String
    val arete_color_successful: String
    val arete_color_successful_emph: String
    val arete_color_aborted: String
    val arete_color_aborted_emph: String
    val arete_color_failed: String
    val arete_color_failed_emph: String
    val arete_color_neutral: String
    val arete_color_neutral_emph: String
}

class AreteClassicColors: AreteColorScheme {
    override val arete_color_background = "#282a36"
    override val arete_color_card = "#44475a"
    override val arete_color_foreground = "#f8f8f2"
    override val arete_color_ignored = "lightgray"
    override val arete_color_ignored_emph = "grey"
    override val arete_color_successful = "palegreen"
    override val arete_color_successful_emph = "limegreen"
    override val arete_color_aborted = "lightyellow"
    override val arete_color_aborted_emph = "yellow"
    override val arete_color_failed = "lightpink"
    override val arete_color_failed_emph = "red"
    override val arete_color_neutral = "lightskyblue"
    override val arete_color_neutral_emph = "deepskyblue"
}

class CatppuccinMochaColors: AreteColorScheme {
    override val arete_color_background = "#1e1e2e" //base
    override val arete_color_card = "#313244" //surface0
    override val arete_color_foreground = "#cba6f7" //mauve
    override val arete_color_ignored = "#9399b2" //overlay2
    override val arete_color_ignored_emph = "#6c7086" //overlay0
    override val arete_color_successful = "#a6e3a1" //green
    override val arete_color_successful_emph = "#94e2d5" //teal
    override val arete_color_aborted = "#f9e2af" //yellow
    override val arete_color_aborted_emph = "#fab387" //peach
    override val arete_color_failed = "#eba0ac" //maroon
    override val arete_color_failed_emph = "#f38ba8" //red
    override val arete_color_neutral = "#74c7ec" //sapphire
    override val arete_color_neutral_emph = "#89b4fa" //blue
}

class CatppuccinMacchiatoColors: AreteColorScheme {
    override val arete_color_background = "#24273a" //base
    override val arete_color_card = "#363a4f" //surface0
    override val arete_color_foreground = "#c6a0f6" //mauve
    override val arete_color_ignored = "#939ab7" //overlay2
    override val arete_color_ignored_emph = "#6e738d" //overlay0
    override val arete_color_successful = "#a6da95" //green
    override val arete_color_successful_emph = "#8bd5ca" //teal
    override val arete_color_aborted = "#eed49f" //yellow
    override val arete_color_aborted_emph = "#f5a97f" //peach
    override val arete_color_failed = "#ee99a0" //maroon
    override val arete_color_failed_emph = "#ed8796" //red
    override val arete_color_neutral = "#7dc4e4" //sapphire
    override val arete_color_neutral_emph = "#8aadf4" //blue
}

class CatppuccinFrappeColors: AreteColorScheme {
    override val arete_color_background = "#303446" //base
    override val arete_color_card = "#414559" //surface0
    override val arete_color_foreground = "#ca9ee6" //mauve
    override val arete_color_ignored = "#949cbb" //overlay2
    override val arete_color_ignored_emph = "#737994" //overlay0
    override val arete_color_successful = "#a6d189" //green
    override val arete_color_successful_emph = "#81c8be" //teal
    override val arete_color_aborted = "#e5c890" //yellow
    override val arete_color_aborted_emph = "#ef9f76" //peach
    override val arete_color_failed = "#ea999c" //maroon
    override val arete_color_failed_emph = "#e78284" //red
    override val arete_color_neutral = "#85c1dc" //sapphire
    override val arete_color_neutral_emph = "#8caaee" //blue
}

class CatppuccinLatteColors: AreteColorScheme {
    override val arete_color_background = "#eff1f5" //base
    override val arete_color_card = "#ccd0da" //surface0
    override val arete_color_foreground = "#8839ef" //mauve
    override val arete_color_ignored = "#7c7f93" //overlay2
    override val arete_color_ignored_emph = "#9ca0b0" //overlay0
    override val arete_color_successful = "#40a02b" //green
    override val arete_color_successful_emph = "#179299" //teal
    override val arete_color_aborted = "#df8e1d" //yellow
    override val arete_color_aborted_emph = "#fe640b" //peach
    override val arete_color_failed = "#e64553" //maroon
    override val arete_color_failed_emph = "#d20f39" //red
    override val arete_color_neutral = "#209fb5" //sapphire
    override val arete_color_neutral_emph = "#1e66f5" //blue
}

class DraculaColors: AreteColorScheme {
    override val arete_color_background = "#282A36"
    override val arete_color_card = "#6272A4"
    override val arete_color_foreground = "#F8F8F2"
    override val arete_color_ignored = "#BD93F9"
    override val arete_color_ignored_emph = "#44475A"
    override val arete_color_successful = "#50FA7B"
    override val arete_color_successful_emph = "#44475A"
    override val arete_color_aborted = "#F1FA8C"
    override val arete_color_aborted_emph = "#44475A"
    override val arete_color_failed = "#FF5555"
    override val arete_color_failed_emph = "#44475A"
    override val arete_color_neutral = "#8BE9FD"
    override val arete_color_neutral_emph = "#44475A"
}

class AlucardColors: AreteColorScheme {
    override val arete_color_background = "#FFFBEB"
    override val arete_color_card = "#6C664B"
    override val arete_color_foreground = "#1F1F1F"
    override val arete_color_ignored = "#644AC9"
    override val arete_color_ignored_emph = "#CFCFDE"
    override val arete_color_successful = "#14710A"
    override val arete_color_successful_emph = "#CFCFDE"
    override val arete_color_aborted = "#846E15"
    override val arete_color_aborted_emph = "#CFCFDE"
    override val arete_color_failed = "#CB3A2A"
    override val arete_color_failed_emph = "#CFCFDE"
    override val arete_color_neutral = "#036A96"
    override val arete_color_neutral_emph = "#CFCFDE"
}



