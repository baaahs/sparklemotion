package baaahs.mapper

import baaahs.app.ui.editor.betterSelect
import baaahs.device.MovingHeadDevice
import baaahs.device.PixelArrayDevice
import baaahs.dmx.DmxTransportConfig
import baaahs.getBang
import baaahs.scene.EditingController
import baaahs.scene.MutableFixtureMapping
import baaahs.scene.MutableScene
import baaahs.ui.asTextNode
import baaahs.ui.xComponent
import kotlinx.html.js.onClickFunction
import materialui.components.button.enums.ButtonColor
import materialui.components.container.container
import materialui.components.iconbutton.iconButton
import materialui.icon
import org.w3c.dom.events.Event
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.header

private val FixtureMappingEditorView = xComponent<FixtureMappingEditorProps>("FixtureMappingEditor") { props ->
    val allEntities = buildMap {
        props.mutableScene.model.build().open().visit { entity ->
            put(entity.name, entity)
        }
    }

    val deviceConfig = props.mutableFixtureMapping.deviceConfig
    val transportConfig = props.mutableFixtureMapping.transportConfig

    val handleEntityChange by handler(
        props.mutableScene, props.mutableFixtureMapping, props.editingController
    ) { value: String? ->
        props.mutableFixtureMapping.entityId = value
        props.editingController.onChange()
    }

    val handleMovingHeadFixtureConfigClick by handler(props.mutableFixtureMapping, props.editingController.onChange) { _: Event ->
        props.mutableFixtureMapping.deviceConfig =
            MovingHeadDevice.defaultConfig.edit()
        props.editingController.onChange()
    }
    val handlePixelArrayFixtureConfigClick by handler(props.mutableFixtureMapping, props.editingController.onChange) { _: Event ->
        props.mutableFixtureMapping.deviceConfig =
            PixelArrayDevice.defaultConfig.edit()
        props.editingController.onChange()
    }
    val handleDmxTransportConfigClick by handler(props.mutableFixtureMapping, props.editingController.onChange) { _: Event ->
        props.mutableFixtureMapping.transportConfig =
            DmxTransportConfig(1, 2, true).edit()
        props.editingController.onChange()
    }

    header { +"Fixture Mapping" }

    container {
        betterSelect<String?> {
            attrs.label = "Entity"
            attrs.values = allEntities.keys.toList()
            attrs.renderValueOption = { option ->
                (option?.let { allEntities.getBang(it, "entity").title } ?: "None")
                    .asTextNode()
            }
//            attrs.renderValueSelected = { option ->
//                (option?.let { allEntities.getBang(it, "entity").title } ?: "None")
//                    .asTextNode()
//            }
            attrs.value = props.mutableFixtureMapping.entityId
            attrs.onChange = handleEntityChange
        }

        header { +"Fixture Config" }
        if (deviceConfig != null) {
            with (deviceConfig.getEditorView(props.editingController, props.mutableFixtureMapping)) { render() }
        } else {
            iconButton {
                attrs.color = ButtonColor.secondary
                attrs.onClickFunction = handleMovingHeadFixtureConfigClick

                icon(materialui.icons.AddCircleOutline)
                +"Moving Head"
            }

            iconButton {
                attrs.color = ButtonColor.secondary
                attrs.onClickFunction = handlePixelArrayFixtureConfigClick

                icon(materialui.icons.AddCircleOutline)
                +"Pixel Array"
            }
        }

        header { +"Transport Config" }
        if (transportConfig != null) {
            with(transportConfig.getEditorView(props.editingController, props.mutableFixtureMapping)) { render() }
        } else {
            iconButton {
                attrs.color = ButtonColor.secondary
                attrs.onClickFunction = handleDmxTransportConfigClick

                icon(materialui.icons.AddCircleOutline)
                +"DMX"
            }
        }
    }
}

external interface FixtureMappingEditorProps : Props {
    var mutableScene: MutableScene
    var editingController: EditingController<*>
    var mutableFixtureMapping: MutableFixtureMapping
}

fun RBuilder.fixtureMappingEditor(handler: RHandler<FixtureMappingEditorProps>) =
    child(FixtureMappingEditorView, handler = handler)