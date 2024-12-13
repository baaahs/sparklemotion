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
import baaahs.ui.buildElements
import baaahs.ui.muiClasses
import baaahs.ui.unaryMinus
import baaahs.ui.xComponent
import materialui.icon
import mui.icons.material.Delete
import mui.icons.material.ExpandMore
import mui.material.Accordion
import mui.material.AccordionDetails
import mui.material.AccordionSummary
import mui.material.AccordionSummaryClasses
import mui.material.Box
import mui.material.Chip
import mui.material.ChipColor
import mui.material.ChipVariant
import mui.material.Divider
import mui.material.IconButton
import mui.material.IconButtonColor
import mui.material.MenuItemProps
import mui.material.Size
import mui.system.sx
import org.w3c.dom.events.Event
import react.Props
import react.RBuilder
import react.RHandler
import react.buildElement
import react.create
import react.dom.i
import react.useContext
import web.cssom.TextTransform
import web.cssom.WhiteSpace
import web.cssom.em
import web.dom.Element

private val FixtureMappingEditorView = xComponent<FixtureMappingEditorProps>("FixtureMappingEditor") { props ->
    val appContext = useContext(appContext)
    val editMode = observe(appContext.sceneManager.editMode)
    val styles = appContext.allStyles.controllerEditor

    var newEntityMenuAnchor by state<Element?> { null }
    val handleNewEntityClick by mouseEventHandler { newEntityMenuAnchor = it.currentTarget as Element? }
    val hideNewEntityMenu by handler { _: Event, _: String -> newEntityMenuAnchor = null }

    val allEntities = buildList { props.mutableScene.model.visit { add(it) } }
    val fixtureMappings = mutableMapOf<MutableEntity, MutableList<ControllerId>>().also { map ->
        props.mutableScene.fixtureMappings.forEach { (controllerId, fixtureMappings) ->
            fixtureMappings.forEach { mapping ->
                mapping.entity?.let { entity ->
                    map.getOrPut(entity) { mutableListOf() }
                        .add(controllerId)
                }
            }
        }
    }

    val alreadyMappedEntities = allEntities
        .filter { entity -> fixtureMappings.contains(entity) }
        .toSet()

    val transportConfig = props.mutableFixtureMapping.transportConfig

    val handleEntityChange by handler(
        props.mutableScene, props.mutableFixtureMapping, props.editingController
    ) { value: StyledMenuItem ->
        when (value) {
            is NoSelectionMenuItem -> {
                props.mutableFixtureMapping.entity = null
                props.editingController.onChange()
            }

            // No op for now.
            is CreateNewMenuItem -> {}

            is EntityMenuItem -> {
                props.mutableFixtureMapping.entity = value.entity

                // If the new mapped entity's fixture type doesn't match the fixture options, remove 'em.
                if (props.mutableFixtureMapping.fixtureOptions?.fixtureType != value.entity?.fixtureType) {
                    props.mutableFixtureMapping.fixtureOptions = null
                }
                props.editingController.onChange()
            }
            else -> error("Should only be called with EntityMenuItem instances.")
        }
    }

    val handleDeleteButton by mouseEventHandler(props.onDelete, props.mutableFixtureMapping) {
        props.onDelete(props.mutableFixtureMapping)
    }

    var expanded by state { props.initiallyOpen == true }
    val toggleExpanded by mouseEventHandler { expanded = !expanded
        console.error("Set expanded to ", expanded)
    }

    val selectedEntity = props.mutableFixtureMapping.entity

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
                if (selectedEntity != null) +selectedEntity.title else i { +"Anonymous" }

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

                val noneOrAnonymous = when(SparkleMotion.SUPPORT_ANONYMOUS_FIXTURE_MAPPINGS) {
                    true -> AnonymousEntityMenuItem
                    false -> NoSelectionMenuItem
                }

                val menuItems = buildList {
                    add(noneOrAnonymous)

                    val unmapped = allEntities.filter { !it.isAlreadyMapped() || it == selectedEntity }
                        .sortedBy { it.title }
                        .map { EntityMenuItem(it) }
                    val mappedToOtherControllers = allEntities.filter { it.isAlreadyMapped() && it != selectedEntity }
                        .sortedBy { it.title }
                        .map { EntityMenuItem(it, isAlreadyMapped = true) }

                    addAll(unmapped)
                    if (unmapped.isNotEmpty()) add(DividerMenuItem)
                    add(CreateNewMenuItem)
                    if (mappedToOtherControllers.isNotEmpty()) {
                        add(DividerMenuItem)
                        add(HeaderMenuItem("Already Mapped"))
                    }
                    addAll(mappedToOtherControllers)
                }
                val selectedMenuItem = if (selectedEntity == null)
                    noneOrAnonymous
                else menuItems.first { it is EntityMenuItem && it.entity == selectedEntity }

                betterSelect<StyledMenuItem> {
                    attrs.label = "Model Entity"
                    attrs.values = menuItems
                    attrs.renderValueOption = { item, menuItemProps ->
                        item.doRender(menuItemProps)
                    }
                    attrs.value = selectedMenuItem
                    attrs.disabled = editMode.isOff
                    attrs.onChange = handleEntityChange
                    attrs.fullWidth = true
                }
            }
        }

        AccordionDetails {
            attrs.className = -styles.expansionPanelDetails

            if (selectedEntity != null || SparkleMotion.SUPPORT_ANONYMOUS_FIXTURE_MAPPINGS) {
                fixtureConfigPicker {
                    attrs.editingController = props.editingController
                    attrs.fixtureType = selectedEntity?.fixtureType
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
                attrs.disabled = editMode.isOff
                attrs.onClick = handleDeleteButton
                icon(Delete)
                +"Delete"
            }
        }
    }
}

interface StyledMenuItem {
    fun doRender(menuItem: MenuItemProps) = buildElements { this.render(menuItem) }

    fun RBuilder.render(menuItem: MenuItemProps)
}
private data class EntityMenuItem(
    val entity: MutableEntity,
    val isAlreadyMapped: Boolean = false
) : StyledMenuItem {
    override fun RBuilder.render(menuItem: MenuItemProps) {
        menuItem.disabled = isAlreadyMapped
        +entity.title
    }
}

private class HeaderMenuItem(val title: String) : StyledMenuItem {
    override fun RBuilder.render(menuItem: MenuItemProps) {
        menuItem.sx { textTransform = TextTransform.uppercase }
        menuItem.disabled = true
        +title
    }
}

private object AnonymousEntityMenuItem : StyledMenuItem {
    override fun RBuilder.render(menuItem: MenuItemProps) = i { +"Anonymous" }
}

private object NoSelectionMenuItem : StyledMenuItem {
    override fun RBuilder.render(menuItem: MenuItemProps) = i { +"None" }
}

private object CreateNewMenuItem : StyledMenuItem {
    override fun RBuilder.render(menuItem: MenuItemProps) = i { +"Create New Entity…" }
}

private object DividerMenuItem : StyledMenuItem {
    override fun RBuilder.render(menuItem: MenuItemProps) = Divider {}
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