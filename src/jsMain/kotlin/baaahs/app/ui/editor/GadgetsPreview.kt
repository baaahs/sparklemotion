package baaahs.app.ui.editor

import baaahs.app.ui.controls.controlWrapper
import baaahs.show.live.ControlProps
import baaahs.show.live.OpenGadgetControl
import baaahs.show.mutable.EditingShader
import baaahs.ui.addObserver
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import kotlinx.css.*
import react.RBuilder
import react.RHandler
import react.RProps
import react.child
import react.dom.div
import styled.StyleSheet

val GadgetsPreview = xComponent<GadgetsPreviewProps>("GadgetsPreview") { props ->
    var gadgets by state {
        props.editingShader.gadgets.toTypedArray()
    }

    onMount(props.editingShader) {
        val observer = props.editingShader.addObserver {
            if (it.state == EditingShader.State.Success) {
                gadgets = props.editingShader.gadgets.toTypedArray()
            }
        }
        withCleanup { observer.remove() }
    }

    div(+GadgetsPreviewStyles.gadgetsPreview) {
        gadgets.forEach { gadgetPreview ->
            controlWrapper {
                key = gadgetPreview.id

                attrs.control = OpenGadgetControl(
                    gadgetPreview.id,
                    gadgetPreview.gadget,
                    gadgetPreview.controlledDataSource
                        ?: error("no datasource?")
                )
                attrs.controlProps = ControlProps({}, false, null)
                attrs.disableEdit = true
            }
        }
    }
}

object GadgetsPreviewStyles : StyleSheet("app-ui-editor-GadgetsPreview", isStatic = true) {
    val gadgetsPreview by css {
        display = Display.flex
        flexDirection = FlexDirection.row
        gap = Gap(1.em.toString())
    }
}

external interface GadgetsPreviewProps : RProps {
    var editingShader: EditingShader
}

fun RBuilder.gadgetsPreview(handler: RHandler<GadgetsPreviewProps>) =
    child(GadgetsPreview, handler = handler)