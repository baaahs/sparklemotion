@file:JsModule("js/app/components/Shows/ShowControls.jsx")
@file:JsNonModule

package baaahs.jsx

import baaahs.GadgetData
import react.RClass
import react.RProps

@JsName("default")
external val ShowControls : RClass<ShowControlsProps>

external interface ShowControlsProps : RProps {
    var gadgets: Array<GadgetData>
}
