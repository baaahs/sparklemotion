package acex

import baaahs.util.Logger

open class Loadable<T: Any>(pathPrefix: String, idInternal: String, obj: Any) {
    val id by lazy {
        val path = "$pathPrefix/$idInternal"
        val loadedObj = require<T>(path)
            ?: error("Failed to load ace component $path.")

        logger.debug { "Loadable($pathPrefix/$idInternal) -> $loadedObj" }
        idInternal
    }

    companion object {
        private val logger = Logger<Loadable<Any>>()
    }
}

class Mode(idInternal: String, obj: Any) : Loadable<AceMode>("ace/mode", idInternal, obj)

class Theme(idInternal: String, obj: Any) : Loadable<AceTheme>("ace/theme", idInternal, obj)

class Ext(idInternal: String, obj: Any) : Loadable<AceExt>("ace/ext", idInternal, obj) {
    fun install() {
        // No-op.
    }
}


@JsModule("ace-builds/src-min-noconflict/mode-glsl")
private external object GlslMode

object Modes {
    val glsl = Mode("glsl", GlslMode)
}


@JsModule("ace-builds/src-min-noconflict/theme-github")
private external object GitHubTheme

@JsModule("ace-builds/src-min-noconflict/theme-tomorrow_night_bright")
private external object TomorrowNightBrightTheme

object Themes {
    val github = Theme("github", GitHubTheme)
    val tomorrowNightBright = Theme("tomorrow_night_bright", TomorrowNightBrightTheme)
}


@JsModule("ace-builds/src-min-noconflict/ext-searchbox")
private external object ExtSearchBox

object Extensions {
    val searchBox = Ext("searchbox", ExtSearchBox)
}