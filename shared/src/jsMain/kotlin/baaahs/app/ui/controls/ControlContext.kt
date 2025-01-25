package baaahs.app.ui.controls

import baaahs.show.live.OpenControl
import js.objects.jso
import react.createContext

val controlContext = createContext<ControlContext>(jso {})

external interface ControlContext {
    var parentControl: OpenControl?
}