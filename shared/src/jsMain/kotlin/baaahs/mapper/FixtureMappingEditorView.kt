package baaahs.mapper

import baaahs.SparkleMotion
import baaahs.app.ui.appContext
import baaahs.app.ui.editor.betterSelect
import baaahs.controller.ControllerId
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
import web.cssom.TextTransform
import web.cssom.WhiteSpace
import web.cssom.em

private val FixtureMappingEditorView = xComponent<FixtureMappingEditorProps>("FixtureMappingEditor") { props ->
    val appContext = useContext(appContext)
    val editMode = observe(appContext.sceneManager.editMode)
    val styles = appContext.allStyles.controllerEditor

    val allEntities = buildList { props.mutableScene.model.visit { add(it) } }
    val fixtureMappings = run {
        val map = mutableMapOf<MutableEntity, MutableList<ControllerId>>()
        props.mutableScene.fixtureMappings.forEach { (controllerId, fixtureMappings) ->
            fixtureMappings.forEach { mapping ->
                mapping.entity?.let { entity -> map.getOrPut(entity) { mutableListOf() }
                    .add(controllerId)
                }
            }
        }
        map
    }
    val alreadyMappedEntities = allEntities.filter { entity ->
        fixtureMappings.contains(entity)
    }.toSet()

    val transportConfig = props.mutableFixtureMapping.transportConfig

    val handleEntityChange by handler(
        props.mutableScene, props.mutableFixtureMapping, props.editingController
    ) { value: EntityMenuItem ->
        props.mutableFixtureMapping.entity = value.entity
        // If the new mapped entity's fixture type doesn't match the fixture options, remove 'em.
        if (props.mutableFixtureMapping.fixtureOptions?.fixtureType != value.entity?.fixtureType) {
            props.mutableFixtureMapping.fixtureOptions = null
        }
        props.editingController.onChange()
    }

    val handleDeleteButton by mouseEventHandler(props.onDelete, props.mutableFixtureMapping) {
        props.onDelete(props.mutableFixtureMapping)
    }

    var expanded by state { props.initiallyOpen == true }
    val toggleExpanded by mouseEventHandler { expanded = !expanded
        console.error("Set expanded to ", expanded)
    }

    val entity = props.mutableFixtureMapping.entity

    Accordion {
        attrs.className = -styles.accordionRoot
        attrs.expanded = expanded

        AccordionSummary {
            attrs.classes = muiClasses<AccordionSummaryClasses> {
                content = -styles.accordionSummaryContent
            }
            attrs.sx {
                whiteSpace = WhiteSpace.nowrap
            }
            attrs.expandIcon = ExpandMore.create()
            attrs.onClick = toggleExpanded

            if (!expanded) {
                if (entity != null) +entity.title else i { +"Anonymous" }

                Box {
                    attrs.className = -styles.expansionPanelSummaryChips

                    val fixturePreview = props.fixturePreview
                    if (fixturePreview is FixturePreviewError) {
                        Chip {
                            attrs.color = ChipColor.error
                            attrs.variant = ChipVariant.outlined
                            attrs.label = buildElement { +"Error: ${fixturePreview.e.message}" }
                        }
                        this@xComponent.logger.warn(fixturePreview.e) { "Fixture preview error." }
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
                fun MutableEntity.isAlreadyMapped() =
                    this in alreadyMappedEntities

                val entityList = buildList {
                    val unmapped = allEntities.filter { !it.isAlreadyMapped() || it == entity }
                        .sortedBy { it.title }
                        .map { EntityMenuItem(it, it.title, isItalic = true) }
                    val mapped = allEntities.filter { it.isAlreadyMapped() && it != entity }
                        .sortedBy { it.title }
                        .map { EntityMenuItem(
                            it,
                            it.title + " \u2713",
                            isDisabled = true,
                            isItalic = true
                        ) }

                    addAll(unmapped)
                    if (mapped.isNotEmpty()) {
                        add(EntityMenuItem(
                            label = "Already Mapped",
                            isHeader = true,
                            isDisabled = true,
                            isItalic = true
                        ))
                    }
                    addAll(mapped)
                }
                betterSelect<EntityMenuItem> {
                    val noneOrAnonymousItem = if (SparkleMotion.SUPPORT_ANONYMOUS_FIXTURE_MAPPINGS) {
                        EntityMenuItem(label = "Anonymous", isItalic = true)
                    } else {
                        EntityMenuItem(label = "None", isItalic = true)
                    }
                    attrs.label = "Model Entity"
                    attrs.values = listOf(noneOrAnonymousItem) + entityList
                    attrs.renderValueOption = { item, menuItemProps ->
                        menuItemProps.disabled = item.isDisabled
                        if (item.entity != null) {
                            item.entity.title.asTextNode()
                        } else if (item.label != null) {
                            if (item.isHeader) {
                                menuItemProps.divider = true
                                menuItemProps.disabled = true
                                menuItemProps.sx {
                                    textTransform = TextTransform.uppercase
                                }
                            }

                            buildElement {
                                if (item.isItalic) {
                                    i { +item.label }
                                } else item.label.asTextNode()
                            }
                        } else error("Huh?")
                    }
                    attrs.value = entity?.let { selected -> entityList.firstOrNull { it.entity == selected } }
                        ?: noneOrAnonymousItem
                    attrs.disabled = editMode.isOff
                    attrs.onChange = handleEntityChange
                    attrs.fullWidth = true
                }
            }
        }

        AccordionDetails {
            attrs.className = -styles.expansionPanelDetails

            if (entity != null || SparkleMotion.SUPPORT_ANONYMOUS_FIXTURE_MAPPINGS) {
                fixtureConfigPicker {
                    attrs.editingController = props.editingController
                    attrs.fixtureType = entity?.fixtureType
                    attrs.mutableFixtureOptions = props.mutableFixtureMapping.fixtureOptions
                    attrs.setMutableFixtureOptions = { props.mutableFixtureMapping.fixtureOptions = it }
                    attrs.allowNullFixtureOptions = false
                }

                transportConfigPicker {
                    attrs.editingController = props.editingController
                    attrs.mutableTransportConfig = props.mutableFixtureMapping.transportConfig
                    attrs.setMutableTransportConfig = { props.mutableFixtureMapping.transportConfig = it }
                }
            }

            IconButton {
                attrs.sx { marginTop = .5.em }
                attrs.size = Size.small
                attrs.color = IconButtonColor.error
                attrs.hidden = editMode.isOff
                attrs.onClick = handleDeleteButton
                icon(Delete)
                +"Delete"
            }
        }
    }
}

private data class EntityMenuItem(
    val entity: MutableEntity? = null,
    val label: String? = null,
    val isHeader: Boolean = false,
    val isDisabled: Boolean = false,
    val isItalic: Boolean = false
)

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