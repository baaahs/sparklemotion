package baaahs.app.ui.controls

import baaahs.OpenShow
import baaahs.ShowState
import baaahs.app.ui.DragNDrop
import baaahs.show.Show
import react.RProps

external interface SpecialControlProps : RProps {
    var show: OpenShow
    var showState: ShowState
    var onShowStateChange: (ShowState) -> Unit

    var editMode: Boolean
    var dragNDrop: DragNDrop
    var onEdit: (Show, ShowState) -> Unit
}