package baaahs.app.ui.controls

import baaahs.ShowState
import baaahs.show.Show
import baaahs.show.live.OpenShow
import react.RProps

external interface SpecialControlProps : RProps {
    var show: OpenShow
    var showState: ShowState
    var onShowStateChange: (ShowState) -> Unit
    var editMode: Boolean
    var onEdit: (Show, ShowState) -> Unit
}