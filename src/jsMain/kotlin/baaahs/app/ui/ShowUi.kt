package baaahs.app.ui

import baaahs.OpenShow
import baaahs.ShowState
import baaahs.show.Show
import baaahs.ui.xComponent
import external.dragDropContext
import react.*

val ShowUi = xComponent<ShowUiProps>("ShowUi") { props ->
    val appContext = useContext(appContext)
    val show = props.show
    val showState = props.showState

    // TODO: Pick layout based on device characteristics.
    val currentLayoutName = "default"
    val currentLayout = show.layouts.map[currentLayoutName] ?: error("no such layout $currentLayoutName")

    dragDropContext({
        onDragEnd = appContext.dragNDrop::onDragEnd
    }) {
        showLayout {
            attrs.show = show
            attrs.showState = showState
            attrs.onShowStateChange = props.onShowStateChange
            attrs.layout = currentLayout
            attrs.editMode = props.editMode
            attrs.onEdit = props.onEdit
        }
    }
}

external interface ShowUiProps : RProps {
    var show: OpenShow
    var showState: ShowState
    var editMode: Boolean
    var onEdit: (Show, ShowState) -> Unit
    var onShowStateChange: (ShowState) -> Unit
}

fun RBuilder.showUi(handler: RHandler<ShowUiProps>): ReactElement =
    child(ShowUi, handler = handler)
