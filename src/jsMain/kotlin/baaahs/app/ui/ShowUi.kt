package baaahs.app.ui

import baaahs.ShowState
import baaahs.show.live.OpenShow
import baaahs.show.mutable.PatchHolderEditContext
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
            attrs.editPatchHolder = props.editPatchHolder
        }
    }
}

external interface ShowUiProps : RProps {
    var show: OpenShow
    var showState: ShowState
    var editMode: Boolean
    var editPatchHolder: (PatchHolderEditContext) -> Unit
    var onShowStateChange: (ShowState) -> Unit
}

fun RBuilder.showUi(handler: RHandler<ShowUiProps>): ReactElement =
    child(ShowUi, handler = handler)
