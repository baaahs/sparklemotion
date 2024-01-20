package baaahs.app.ui

import baaahs.gl.SharedGlContext
import js.objects.jso
import react.createContext

val appGlContext = createContext<AppGlContext>(jso {})

external interface AppGlContext {
    var sharedGlContext: SharedGlContext?
}
