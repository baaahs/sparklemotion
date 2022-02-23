package baaahs.app.ui.controllers

import baaahs.scene.EditingController
import baaahs.scene.MutableDirectDmxControllerConfig
import baaahs.ui.xComponent
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.div

private val DirectDmxControllerEditorView =
    xComponent<DirectDmxControllerEditorProps>("DirectDmxControllerEditor") { props ->

        div { +"Nothing to edit right now." }
    }

external interface DirectDmxControllerEditorProps : Props {
    var editingController: EditingController<MutableDirectDmxControllerConfig>
}

fun RBuilder.directDmxControllerEditor(handler: RHandler<DirectDmxControllerEditorProps>) =
    child(DirectDmxControllerEditorView, handler = handler)