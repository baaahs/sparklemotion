package baaahs.app.ui.controllers

import baaahs.scene.EditingController
import baaahs.scene.MutableDirectDmxControllerConfig
import baaahs.ui.xComponent
import mui.material.Container
import react.Props
import react.RBuilder
import react.RHandler

private val DirectDmxControllerEditorView =
    xComponent<DirectDmxControllerEditorProps>("DirectDmxControllerEditor") { props ->
        Container { +"Nothing to edit right now." }
    }

external interface DirectDmxControllerEditorProps : Props {
    var editingController: EditingController<MutableDirectDmxControllerConfig>
}

fun RBuilder.directDmxControllerEditor(handler: RHandler<DirectDmxControllerEditorProps>) =
    child(DirectDmxControllerEditorView, handler = handler)