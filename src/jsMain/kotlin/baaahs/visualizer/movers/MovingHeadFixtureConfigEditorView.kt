package baaahs.visualizer.movers

import baaahs.device.MovingHeadDevice
import baaahs.scene.EditingController
import baaahs.scene.MutableFixtureMapping
import baaahs.ui.xComponent
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.div

private val MovingHeadFixtureConfigEditorView =
    xComponent<MovingHeadFixtureConfigEditorProps>("MovingHeadFixtureConfigEditor") { props ->
        val mutableConfig = props.mutableFixtureMapping.deviceConfig as MovingHeadDevice.MutableConfig?

        div {
        }
    }

external interface MovingHeadFixtureConfigEditorProps : Props {
    var editingController: EditingController<*>
    var mutableFixtureMapping: MutableFixtureMapping
}

fun RBuilder.movingHeadFixtureConfigEditor(handler: RHandler<MovingHeadFixtureConfigEditorProps>) =
    child(MovingHeadFixtureConfigEditorView, handler = handler)