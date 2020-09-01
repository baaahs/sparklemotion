package baaahs.app.ui.controls

import baaahs.ShowState
import baaahs.show.live.OpenShow
import baaahs.show.mutable.PatchHolderEditContext
import react.RProps

external interface SpecialControlProps : RProps {
    var show: OpenShow
    var showState: ShowState
    var onShowStateChange: (ShowState) -> Unit
    var editMode: Boolean
    var editPatchHolder: (PatchHolderEditContext) -> Unit
}