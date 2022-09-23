package acex

import ReactAce.Ace.reactAce

@Suppress("unused")
private val mustLoadAceFirst = reactAce

@JsModule("ace-builds/src-min-noconflict/mode-glsl")
private external object GlslMode

@JsModule("ace-builds/src-min-noconflict/theme-github")
private external object GitHubTheme

@JsModule("ace-builds/src-min-noconflict/theme-tomorrow_night_bright")
private external object TomorrowNightBrightTheme

@JsModule("ace-builds/src-min-noconflict/ext-language_tools")
private external object ExtLanguageTools

@JsModule("ace-builds/src-min-noconflict/ext-searchbox")
private external object ExtSearchBox

object Modes {
    val glsl = Mode("glsl") { GlslMode }
}

object Themes {
    val github = Theme("github") { GitHubTheme }
    val tomorrowNightBright = Theme("tomorrow_night_bright") { TomorrowNightBrightTheme }
}

object Extensions {
    val languageTools = Ext("language_tools") { ExtLanguageTools }
    val searchBox = Ext("searchbox") { ExtSearchBox }
}

class Mode(private val idInternal: String, private val loader: () -> Unit) {
    val id: String get() { loader(); return idInternal }
}

@Suppress("unused")
class Theme(val id: String, private val obj: Any)

@Suppress("unused")
class Ext(val id: String, private val obj: Any) {
    fun install() {
        // No-op.
    }
}