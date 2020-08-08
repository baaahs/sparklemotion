package baaahs.ui.preview

import baaahs.ui.EditingShader
import baaahs.ui.addObserver
import baaahs.ui.showControls
import baaahs.ui.xComponent
import react.RBuilder
import react.RHandler
import react.RProps
import react.child

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

    showControls {
        attrs.gadgets = gadgets
    }
}

external interface GadgetsPreviewProps : RProps {
    var editingShader: EditingShader
}

fun RBuilder.gadgetsPreview(handler: RHandler<GadgetsPreviewProps>) =
    child(GadgetsPreview, handler = handler)