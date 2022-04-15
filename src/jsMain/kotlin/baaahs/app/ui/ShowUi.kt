package baaahs.app.ui

import baaahs.client.document.EditMode
import baaahs.show.live.ControlDisplay
import baaahs.show.live.ControlProps
import baaahs.show.live.OpenShow
import baaahs.ui.nuffin
import baaahs.ui.xComponent
import external.dragDropContext
import mui.base.Portal
import react.Props
import react.RBuilder
import react.RHandler
import react.useContext

val ShowUi = xComponent<ShowUiProps>("ShowUi") { props ->
    val appContext = useContext(appContext)
    val show = props.show
    val editMode = appContext.showManager.editMode
    observe(editMode)

    // TODO: Pick layout based on device characteristics.
    val currentLayoutName = "default"
    val currentLayout = show.layouts.formats[currentLayoutName] ?: error("no such layout $currentLayoutName")

    var controlDisplay by state<ControlDisplay> { nuffin() }
    logger.info { "switch state is ${props.show.getEnabledSwitchState()}" }
    onChange("show/state", props.show, props.show.getEnabledSwitchState(), appContext.dragNDrop) {
        controlDisplay = ControlDisplay(
            props.show, appContext.showManager, appContext.dragNDrop
        )

        withCleanup {
            controlDisplay.release()
        }
    }

    val genericControlProps = memo(
        props.onShowStateChange, editMode, controlDisplay
    ) {
        ControlProps(props.onShowStateChange, editMode, controlDisplay)
    }

    dragDropContext({
        onDragEnd = appContext.dragNDrop::onDragEnd
    }) {
        showLayout {
            attrs.show = show
            attrs.onShowStateChange = props.onShowStateChange
            attrs.layout = currentLayout
            attrs.controlDisplay = controlDisplay
            attrs.controlProps = genericControlProps
            attrs.editMode = props.editMode
        }

        Portal {
            controlsPalette {
                attrs.controlDisplay = controlDisplay
                attrs.controlProps = genericControlProps
                attrs.show = props.show
                attrs.editMode = props.editMode
            }
        }
    }
}

external interface ShowUiProps : Props {
    var show: OpenShow
    var editMode: EditMode
    var onShowStateChange: () -> Unit
}

fun RBuilder.showUi(handler: RHandler<ShowUiProps>) =
    child(ShowUi, handler = handler)
