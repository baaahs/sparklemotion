package baaahs.mapper

import baaahs.app.ui.appContext
import baaahs.app.ui.editor.betterSelect
import baaahs.fixtures.FixturePreview
import baaahs.fixtures.FixturePreviewError
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
import kotlinx.html.title
import materialui.components.chip.chip
import materialui.components.chip.enums.ChipColor
import materialui.components.chip.enums.ChipVariant
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

            +" | "
            val fixturePreview = props.fixturePreview
            if (fixturePreview is FixturePreviewError) {
                chip {
                    attrs.color = ChipColor.secondary
                    attrs.variant = ChipVariant.outlined
                    attrs.label { +"Error: ${fixturePreview.e.message}" }
                }
            } else {
                fixturePreview.fixtureConfig.summary().forEach { (title, value) ->
                    chip {
                        attrs.variant = ChipVariant.outlined
                        attrs.title = title
                        attrs.label { +(value ?: "?") }
                    }
                }
                +" | "
                fixturePreview.transportConfig.summary().forEach { (title, value) ->
                    chip {
                        attrs.variant = ChipVariant.outlined
                        attrs.title = title
                        attrs.label { +(value ?: "?") }
                    }
                }
            }
        }

        expansionPanelDetails(+styles.expansionPanelDetails) {
            fixtureConfigPicker {
                attrs.editingController = props.editingController
                attrs.mutableFixtureConfig = props.mutableFixtureMapping.fixtureConfig
                attrs.setMutableFixtureConfig = { props.mutableFixtureMapping.fixtureConfig = it!! }
                attrs.allowNullFixtureConfig = false
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
    var fixturePreview: FixturePreview
    var initiallyOpen: Boolean?
}

fun RBuilder.fixtureMappingEditor(handler: RHandler<FixtureMappingEditorProps>) =
    child(FixtureMappingEditorView, handler = handler)