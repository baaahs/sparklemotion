package baaahs.mapper

import baaahs.app.ui.appContext
import baaahs.app.ui.editor.betterSelect
import baaahs.device.FixtureType
import baaahs.dmx.DmxTransport
import baaahs.fixtures.TransportType
import baaahs.getBang
import baaahs.scene.EditingController
import baaahs.scene.MutableFixtureMapping
import baaahs.scene.MutableScene
import baaahs.ui.asTextNode
import baaahs.ui.on
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import kotlinx.html.js.onClickFunction
import materialui.components.container.container
import materialui.components.divider.divider
import materialui.components.divider.enums.DividerVariant
import materialui.components.expansionpanel.enums.ExpansionPanelStyle
import materialui.components.expansionpanel.expansionPanel
import materialui.components.expansionpaneldetails.expansionPanelDetails
import materialui.components.expansionpanelsummary.expansionPanelSummary
import materialui.icon
import materialui.icons.ExpandMore
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.i
import react.useContext

private val FixtureMappingEditorView = xComponent<FixtureMappingEditorProps>("FixtureMappingEditor") { props ->
    val appContext = useContext(appContext)
    val styles = appContext.allStyles.controllerEditor

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

    val handleFixtureTypeChange by handler(
        props.mutableFixtureMapping, props.editingController.onChange
    ) { fixtureType: FixtureType? ->
        props.mutableFixtureMapping.deviceConfig = fixtureType?.emptyConfig?.edit()
        props.editingController.onChange()
    }

    val handleTransportTypeChange by handler(
        props.mutableFixtureMapping, props.editingController.onChange
    ) { transportType: TransportType? ->
        props.mutableFixtureMapping.transportConfig = transportType?.emptyConfig?.edit()
        props.editingController.onChange()
    }

    var expanded by state { false }
    val toggleExpanded by eventHandler { expanded = !expanded }

    expansionPanel(styles.expansionPanelRoot on ExpansionPanelStyle.root) {
        attrs.expanded = expanded

        expansionPanelSummary {
            attrs.expandIcon { icon(ExpandMore) }
            attrs.onClickFunction = toggleExpanded

            val entityName = props.mutableFixtureMapping.entityId
            if (entityName != null) +entityName else i { +"Anonymous" }

            if (!expanded) {
                transportConfig?.toSummaryString()?.let {
                    +" – "
                    +it
                }
            }
        }

        expansionPanelDetails(+styles.expansionPanelDetails) {
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

            divider {
                attrs.light = true
                attrs.variant = DividerVariant.middle
            }

            container {
                betterSelect<FixtureType?> {
                    attrs.label = "Fixture Type"
                    attrs.values = listOf(null) + appContext.plugins.fixtureTypes.all
                    attrs.value = deviceConfig?.fixtureType
                    attrs.renderValueOption = { o -> (o?.title ?: "Default").asTextNode() }
                    attrs.onChange = handleFixtureTypeChange
                }

                if (deviceConfig != null) {
                    with (deviceConfig.getEditorView(props.editingController, props.mutableFixtureMapping)) { render() }
                }
            }

            divider {
                attrs.light = true
                attrs.variant = DividerVariant.middle
            }

            container {
                betterSelect<TransportType?> {
                    attrs.label = "Transport Type"
                    attrs.values = listOf(null, DmxTransport)
                    attrs.value = transportConfig?.transportType
                    attrs.renderValueOption = { o -> (o?.title ?: "Default").asTextNode() }
                    attrs.onChange = handleTransportTypeChange
                }

                if (transportConfig != null) {
                    with(transportConfig.getEditorView(props.editingController, props.mutableFixtureMapping)) { render() }
                }
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