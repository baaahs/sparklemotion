package baaahs.mapper

import baaahs.app.ui.appContext
import baaahs.app.ui.editor.betterSelect
import baaahs.device.FixtureType
import baaahs.dmx.DmxTransport
import baaahs.fixtures.TransportType
import baaahs.getBang
import baaahs.model.Model
import baaahs.scene.EditingController
import baaahs.scene.MutableFixtureMapping
import baaahs.scene.MutableScene
import baaahs.ui.asTextNode
import baaahs.ui.on
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import kotlinx.html.js.onClickFunction
import materialui.components.card.card
import materialui.components.expansionpanel.enums.ExpansionPanelStyle
import materialui.components.expansionpanel.expansionPanel
import materialui.components.expansionpaneldetails.expansionPanelDetails
import materialui.components.expansionpanelsummary.expansionPanelSummary
import materialui.components.paper.enums.PaperStyle
import materialui.icon
import materialui.icons.ExpandMore
import react.*
import react.dom.i

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
    ) { value: Model.Entity? ->
        props.mutableFixtureMapping.entityId = value?.name
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

    var expanded by state { props.initiallyOpen ?: false }
    val toggleExpanded by eventHandler { expanded = !expanded }

    expansionPanel(styles.expansionPanelRoot on ExpansionPanelStyle.root) {
        attrs.expanded = expanded

        expansionPanelSummary {
            attrs.expandIcon { icon(ExpandMore) }
            attrs.onClickFunction = toggleExpanded

            if (!expanded) {
                val entityName = props.mutableFixtureMapping.entityId
                if (entityName != null) +entityName else i { +"Anonymous" }

                transportConfig?.toSummaryString()?.let {
                    +" – "
                    +it
                }
            } else {
                betterSelect<Model.Entity?> {
                    attrs.label = "Model Entity"
                    attrs.values = listOf(null) + allEntities.values.sortedBy { it.title }
                    attrs.renderValueOption = { option ->
                        option?.title?.asTextNode()
                            ?: buildElement { i { +"Anonymous" } }
                    }
                    attrs.value = props.mutableFixtureMapping.entityId?.let { allEntities.getBang(it, "entity") }
                    attrs.onChange = handleEntityChange
                }
            }
        }

        expansionPanelDetails(+styles.expansionPanelDetails) {
            card(styles.configCardOuter on PaperStyle.root) {
                attrs.elevation = 4

                betterSelect<FixtureType?> {
                    attrs.label = "Fixture Type"
                    attrs.values = listOf(null) + appContext.plugins.fixtureTypes.all
                    attrs.value = deviceConfig?.fixtureType
                    attrs.renderValueOption = { o -> (o?.title ?: "Default").asTextNode() }
                    attrs.onChange = handleFixtureTypeChange
                }

                if (deviceConfig != null) {
                    card(styles.configCardInner on PaperStyle.root) {
                        with(deviceConfig.getEditorView(props.editingController, props.mutableFixtureMapping)) { render() }
                    }
                }
            }

            card(styles.configCardOuter on PaperStyle.root) {
                attrs.elevation = 4

                betterSelect<TransportType?> {
                    attrs.label = "Transport Type"
                    attrs.values = listOf(null, DmxTransport)
                    attrs.value = transportConfig?.transportType
                    attrs.renderValueOption = { o -> (o?.title ?: "Default").asTextNode() }
                    attrs.onChange = handleTransportTypeChange
                }

                if (transportConfig != null) {
                    card(styles.configCardInner on PaperStyle.root) {
                        with(transportConfig.getEditorView(props.editingController, props.mutableFixtureMapping)) { render() }
                    }
                }
            }
        }
    }

}

external interface FixtureMappingEditorProps : Props {
    var mutableScene: MutableScene
    var editingController: EditingController<*>
    var mutableFixtureMapping: MutableFixtureMapping
    var initiallyOpen: Boolean?
}

fun RBuilder.fixtureMappingEditor(handler: RHandler<FixtureMappingEditorProps>) =
    child(FixtureMappingEditorView, handler = handler)