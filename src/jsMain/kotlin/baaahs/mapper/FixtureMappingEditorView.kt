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
import baaahs.ui.unaryMinus
import baaahs.ui.xComponent
import js.objects.jso
import mui.icons.material.ExpandMore
import mui.material.*
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
    val toggleExpanded by mouseEventHandler { expanded = !expanded }

    Accordion {
        attrs.classes = jso { this.root = -styles.expansionPanelRoot }
        attrs.expanded = expanded

        AccordionSummary {
            attrs.expandIcon = ExpandMore.create()
            attrs.onClick = toggleExpanded

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
                Chip {
                    attrs.color = ChipColor.secondary
                    attrs.variant = ChipVariant.outlined
                    attrs.label = buildElement { +"Error: ${fixturePreview.e.message}" }
                }
            } else {
                fixturePreview.fixtureOptions.summary().forEach { (title, value) ->
                    Chip {
                        attrs.variant = ChipVariant.outlined
                        attrs.title = title
                        attrs.label = buildElement { +(value ?: "?") }
                    }
                }
                +" | "
                fixturePreview.transportConfig.summary().forEach { (title, value) ->
                    Chip {
                        attrs.variant = ChipVariant.outlined
                        attrs.title = title
                        attrs.label = buildElement { +(value ?: "?") }
                    }
                }
            }
        }

        AccordionDetails {
            attrs.classes = jso { this.root = -styles.expansionPanelDetails }
            fixtureConfigPicker {
                attrs.editingController = props.editingController
                attrs.mutableFixtureOptions = props.mutableFixtureMapping.fixtureOptions
                attrs.setMutableFixtureOptions = { props.mutableFixtureMapping.fixtureOptions = it!! }
                attrs.allowNullFixtureOptions = false
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