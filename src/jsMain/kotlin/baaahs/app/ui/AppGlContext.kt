package baaahs.app.ui

import baaahs.gl.SharedGlContext
import react.createContext

val appGlContext = createContext<AppGlContext>()

external interface AppGlContext {
    var sharedGlContext: SharedGlContext?
}
