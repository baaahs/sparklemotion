package baaahs.visualizer.movers

import baaahs.app.ui.editor.betterSelect
import baaahs.device.MovingHeadDevice
import baaahs.model.MovingHeadAdapter
import baaahs.scene.EditingController
import baaahs.ui.xComponent
import react.Props
import react.RBuilder
import react.RHandler
import react.buildElement

private val MovingHeadFixtureConfigEditorView =
    xComponent<MovingHeadFixtureConfigEditorProps>("MovingHeadFixtureConfigEditor") { props ->
        val mutableConfig = props.mutableFixtureConfig

        val handleAdapterChange by handler(
            props.editingController, mutableConfig
        ) { value: MovingHeadAdapter? ->
            mutableConfig.adapter = value
            props.editingController.onChange()
        }

        betterSelect<MovingHeadAdapter?> {
            attrs.label = "Adapter"
            attrs.values = listOf(null) + MovingHeadAdapter.all
            attrs.renderValueOption = { adapter, _ -> buildElement { +(adapter?.id ?: "Default") } }
            attrs.value = mutableConfig.adapter
            attrs.onChange = handleAdapterChange
        }
    }

external interface MovingHeadFixtureConfigEditorProps : Props {
    var editingController: EditingController<*>
    var mutableFixtureConfig: MovingHeadDevice.MutableOptions
}

fun RBuilder.movingHeadFixtureConfigEditor(handler: RHandler<MovingHeadFixtureConfigEditorProps>) =
    child(MovingHeadFixtureConfigEditorView, handler = handler)