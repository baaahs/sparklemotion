package baaahs.app.ui.controls

import baaahs.show.live.ControlDisplay
import baaahs.show.live.OpenShow
import react.RProps

external interface GenericControlProps : RProps {
    var show: OpenShow
    var onShowStateChange: () -> Unit
    var editMode: Boolean
    var controlDisplay: ControlDisplay
}