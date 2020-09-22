package baaahs.app.ui

import baaahs.app.ui.controls.GenericControlProps
import baaahs.show.live.ControlDisplay
import baaahs.show.live.OpenShow
import baaahs.show.mutable.PatchHolderEditContext
import baaahs.ui.nuffin
import baaahs.ui.xComponent
import external.dragDropContext
import kotlinext.js.jsObject
import materialui.components.portal.portal
import react.*

val ShowUi = xComponent<ShowUiProps>("ShowUi") { props ->
    val appContext = useContext(appContext)
    val show = props.show

    // TODO: Pick layout based on device characteristics.
    val currentLayoutName = "default"
    val currentLayout = show.layouts.map[currentLayoutName] ?: error("no such layout $currentLayoutName")

    var controlDisplay by state<ControlDisplay> { nuffin() }
    logger.info { "switch state is ${props.show.getEnabledSwitchState()}" }
    onChange("show/state", props.show, props.show.getEnabledSwitchState(), props.editMode, appContext.dragNDrop) {
        controlDisplay = ControlDisplay(
            props.show, appContext.webClient, appContext.dragNDrop
        )

        withCleanup {
            controlDisplay.release()
        }
    }

    val genericControlProps = jsObject<GenericControlProps> {
        this.show = props.show
        this.onShowStateChange = props.onShowStateChange
        this.editMode = props.editMode
        this.controlDisplay = controlDisplay
        this.editPatchHolder = props.editPatchHolder
    }

    dragDropContext({
        onDragEnd = appContext.dragNDrop::onDragEnd
    }) {
        showLayout {
            attrs.show = show
            attrs.onShowStateChange = props.onShowStateChange
            attrs.layout = currentLayout
            attrs.controlDisplay = controlDisplay
            attrs.genericControlProps = genericControlProps
            attrs.editMode = props.editMode
            attrs.editPatchHolder = props.editPatchHolder
        }

        portal {
            controlsPalette {
                attrs.controlDisplay = controlDisplay
                attrs.genericControlProps = genericControlProps
                attrs.show = props.show
                attrs.editMode = props.editMode
            }
        }
    }
}

external interface ShowUiProps : RProps {
    var show: OpenShow
    var editMode: Boolean
    var editPatchHolder: (PatchHolderEditContext) -> Unit
    var onShowStateChange: () -> Unit
}

fun RBuilder.showUi(handler: RHandler<ShowUiProps>): ReactElement =
    child(ShowUi, handler = handler)
