package acex

import ReactAce.Ace.reactAce

@Suppress("unused")
private val mustLoadAceFirst = reactAce

@JsModule("ace-builds/src-noconflict/mode-glsl")
private external object GlslMode

@JsModule("ace-builds/src-noconflict/theme-github")
private external object GitHubTheme

@JsModule("ace-builds/src-noconflict/theme-tomorrow_night_bright")
private external object TomorrowNightBrightTheme

object Modes {
    val glsl = Mode("glsl") { GlslMode }
}

object Themes {
    val github = Theme("github") { GitHubTheme }
    val tomorrowNightBright = Theme("tomorrow_night_bright") { TomorrowNightBrightTheme }
}

class Mode(private val idInternal: String, private val loader: () -> Unit) {
    val id: String get() { loader(); return idInternal }
}

class Theme(val id: String, private val obj: Any)