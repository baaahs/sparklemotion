package baaahs.mapper

import baaahs.app.ui.appContext
import baaahs.app.ui.editor.betterSelect
import baaahs.fixtures.FixturePreview
import baaahs.fixtures.FixturePreviewError
import baaahs.scene.EditingController
import baaahs.scene.MutableEntity
import baaahs.scene.MutableFixtureMapping
import baaahs.scene.MutableScene
import baaahs.ui.asTextNode
import baaahs.ui.muiClasses
import baaahs.ui.unaryMinus
import baaahs.ui.xComponent
import materialui.icon
import mui.icons.material.Delete
import mui.icons.material.ExpandMore
import mui.material.*
import mui.system.sx
import react.*
import react.dom.i
import web.cssom.WhiteSpace

private val FixtureMappingEditorView = xComponent<FixtureMappingEditorProps>("FixtureMappingEditor") { props ->
    val appContext = useContext(appContext)
    val styles = appContext.allStyles.controllerEditor

    val allEntities = buildList { props.mutableScene.model.visit { add(it) } }

    val transportConfig = props.mutableFixtureMapping.transportConfig

    val handleEntityChange by handler(
        props.mutableScene, props.mutableFixtureMapping, props.editingController
    ) { value: MutableEntity? ->
        props.mutableFixtureMapping.entity = value
        props.editingController.onChange()
    }

    val handleDeleteButton by mouseEventHandler(props.onDelete, props.mutableFixtureMapping) {
        props.onDelete(props.mutableFixtureMapping)
    }

    var expanded by state { props.initiallyOpen ?: false }
    val toggleExpanded by mouseEventHandler { expanded = !expanded }

    Accordion {
        attrs.className = -styles.accordionRoot
        attrs.expanded = expanded

        AccordionSummary {
            attrs.classes = muiClasses<AccordionSummaryClasses> { content = -styles.expansionPanelSummaryContent }
            attrs.sx {
                whiteSpace = WhiteSpace.nowrap
            }
            attrs.expandIcon = ExpandMore.create()
            attrs.onClick = toggleExpanded

            val entity = props.mutableFixtureMapping.entity
            if (entity != null) +entity.title else i { +"Anonymous" }

            if (!expanded) {
                Box {
                    attrs.className = -styles.expansionPanelSummaryChips

                    val fixturePreview = props.fixturePreview
                    if (fixturePreview is FixturePreviewError) {
                        Chip {
                            attrs.color = ChipColor.secondary
                            attrs.variant = ChipVariant.outlined
                            attrs.label = buildElement { +"Error: ${fixturePreview.e.message}" }
                        }
                    } else {
                        configPreview {
                            attrs.configPreview = fixturePreview.fixtureOptions
                        }
                        +" | "
                        configPreview {
                            attrs.configPreview = fixturePreview.transportConfig
                        }
                    }
                }
            } else {
                IconButton {
                    attrs.size = Size.small
                    attrs.color = IconButtonColor.error
                    attrs.title = "Delete Fixture Mapping"
                    attrs.onClick = handleDeleteButton
                    icon(Delete)
                    +"Delete"
                }
            }
        }

        AccordionDetails {
            attrs.className = -styles.expansionPanelDetails

            betterSelect<MutableEntity?> {
                attrs.label = "Model Entity"
                attrs.values = listOf(null) + allEntities.sortedBy { it.title }
                attrs.renderValueOption = { option ->
                    option?.title?.asTextNode()
                        ?: buildElement { i { +"Anonymous" } }
                }
                attrs.value = props.mutableFixtureMapping.entity
                attrs.onChange = handleEntityChange
                attrs.fullWidth = true
            }

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
    var onDelete: (MutableFixtureMapping) -> Unit
}

fun RBuilder.fixtureMappingEditor(handler: RHandler<FixtureMappingEditorProps>) =
    child(FixtureMappingEditorView, handler = handler)