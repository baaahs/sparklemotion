package baaahs.mapper

import baaahs.app.ui.appContext
import baaahs.app.ui.editor.betterSelect
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
import materialui.components.expansionpanel.enums.ExpansionPanelStyle
import materialui.components.expansionpanel.expansionPanel
import materialui.components.expansionpaneldetails.expansionPanelDetails
import materialui.components.expansionpanelsummary.expansionPanelSummary
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

    val transportConfig = props.mutableFixtureMapping.transportConfig

    val handleEntityChange by handler(
        props.mutableScene, props.mutableFixtureMapping, props.editingController
    ) { value: Model.Entity? ->
        props.mutableFixtureMapping.entityId = value?.name
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
            fixtureConfigPicker {
                attrs.editingController = props.editingController
                attrs.mutableFixtureConfig = props.mutableFixtureMapping.fixtureConfig
                attrs.setMutableFixtureConfig = { props.mutableFixtureMapping.fixtureConfig = it }
            }

            transportConfigPicker {
                attrs.editingController = props.editingController
                attrs.mutableTransportConfig = props.mutableFixtureMapping.transportConfig
                attrs.setMutableTransportConfig = { props.mutableFixtureMapping.transportConfig = it }
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