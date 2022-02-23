package baaahs.app.ui.controllers

import baaahs.scene.EditingController
import baaahs.scene.MutableBrainControllerConfig
import baaahs.ui.xComponent
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.div

private val BrainControllerEditorView = xComponent<BrainControllerEditorProps>("BrainControllerEditor") { props ->
    div { +"Nothing to edit right now." }
}

external interface BrainControllerEditorProps : Props {
    var editingController: EditingController<MutableBrainControllerConfig>
}

fun RBuilder.brainControllerEditor(handler: RHandler<BrainControllerEditorProps>) =
    child(BrainControllerEditorView, handler = handler)