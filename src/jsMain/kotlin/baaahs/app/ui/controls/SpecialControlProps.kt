package baaahs.app.ui.controls

import baaahs.ShowState
import baaahs.show.live.OpenShow
import baaahs.show.mutable.MutablePatchHolder
import react.RProps

external interface SpecialControlProps : RProps {
    var show: OpenShow
    var showState: ShowState
    var onShowStateChange: (ShowState) -> Unit
    var editMode: Boolean
    var editPatchHolder: (MutablePatchHolder) -> Unit
}