package baaahs.app.ui.controls

import baaahs.show.live.OpenShow
import baaahs.show.mutable.PatchHolderEditContext
import react.RProps

external interface SpecialControlProps : RProps {
    var show: OpenShow
    var onShowStateChange: () -> Unit
    var editMode: Boolean
    var editPatchHolder: (PatchHolderEditContext) -> Unit
}