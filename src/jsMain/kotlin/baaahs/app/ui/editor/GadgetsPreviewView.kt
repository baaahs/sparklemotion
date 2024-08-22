package baaahs.app.ui.editor

import baaahs.app.ui.controls.controlWrapper
import baaahs.show.live.ControlProps
import baaahs.show.mutable.EditingShader
import baaahs.ui.addObserver
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import kotlinx.css.*
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.div
import styled.StyleSheet

private val GadgetsPreviewView = xComponent<GadgetsPreviewProps>("GadgetsPreview") { props ->
    var mutableControls by state {
        props.editingShader.gadgets.toTypedArray()
    }

    onMount(props.editingShader) {
        val observer = props.editingShader.addObserver {
            if (it.state == EditingShader.State.Success) {
                mutableControls = props.editingShader.gadgets.toTypedArray()
            }
        }
        withCleanup { observer.remove() }
    }

    div(+GadgetsPreviewStyles.gadgetsPreview) {
        val previewControlProps = ControlProps()

        mutableControls.forEach { gadgetPreview ->
            val openControl = gadgetPreview.openControl

            controlWrapper {
                key = openControl.id

                attrs.control = openControl
                attrs.controlProps = previewControlProps
                attrs.disableEdit = true
            }
        }
    }
}

object GadgetsPreviewStyles : StyleSheet("app-ui-editor-GadgetsPreview", isStatic = true) {
    val gadgetsPreview by css {
        display = Display.flex
        flexDirection = FlexDirection.row
        gap = 1.em
    }
}

external interface GadgetsPreviewProps : Props {
    var editingShader: EditingShader
}

fun RBuilder.gadgetsPreview(handler: RHandler<GadgetsPreviewProps>) =
    child(GadgetsPreviewView, handler = handler)