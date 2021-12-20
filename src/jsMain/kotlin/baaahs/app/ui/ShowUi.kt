package baaahs.app.ui

import baaahs.show.live.ControlDisplay
import baaahs.show.live.ControlProps
import baaahs.show.live.OpenShow
import baaahs.ui.nuffin
import baaahs.ui.xComponent
import external.dragDropContext
import materialui.components.portal.portal
import react.Props
import react.RBuilder
import react.RHandler
import react.useContext

val ShowUi = xComponent<ShowUiProps>("ShowUi") { props ->
    val appContext = useContext(appContext)
    val show = props.show

    // TODO: Pick layout based on device characteristics.
    val currentLayoutName = "default"
    val currentLayout = show.layouts.formats[currentLayoutName] ?: error("no such layout $currentLayoutName")

    var controlDisplay by state<ControlDisplay> { nuffin() }
    logger.info { "switch state is ${props.show.getEnabledSwitchState()}" }
    onChange("show/state", props.show, props.show.getEnabledSwitchState(), props.editMode, appContext.dragNDrop) {
        controlDisplay = ControlDisplay(
            props.show, appContext.showManager, appContext.dragNDrop
        )

        withCleanup {
            controlDisplay.release()
        }
    }

    val genericControlProps = ControlProps(
        props.onShowStateChange,
        props.editMode != false,
        controlDisplay
    )

    dragDropContext({
        onDragEnd = appContext.dragNDrop::onDragEnd
    }) {
        showLayout {
            attrs.show = show
            attrs.onShowStateChange = props.onShowStateChange
            attrs.layout = currentLayout
            attrs.controlDisplay = controlDisplay
            attrs.controlProps = genericControlProps
            attrs.editMode = props.editMode != false
        }

        portal {
            controlsPalette {
                attrs.controlDisplay = controlDisplay
                attrs.controlProps = genericControlProps
                attrs.show = props.show
                attrs.editMode = props.editMode != false
            }
        }
    }
}

external interface ShowUiProps : Props {
    var show: OpenShow
    var editMode: Boolean?
    var onShowStateChange: () -> Unit
}

fun RBuilder.showUi(handler: RHandler<ShowUiProps>) =
    child(ShowUi, handler = handler)
