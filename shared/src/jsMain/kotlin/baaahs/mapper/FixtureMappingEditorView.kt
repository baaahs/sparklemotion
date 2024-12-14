package baaahs.mapper

import baaahs.SparkleMotion
import baaahs.app.ui.appContext
import baaahs.app.ui.editor.betterSelect
import baaahs.app.ui.model.EntityType
import baaahs.app.ui.model.entityEditor
import baaahs.app.ui.model.newEntityMenu
import baaahs.controller.ControllerId
import baaahs.fixtures.FixturePreview
import baaahs.fixtures.FixturePreviewError
import baaahs.scene.*
import baaahs.ui.buildElements
import baaahs.ui.muiClasses
import baaahs.ui.unaryMinus
import baaahs.ui.xComponent
import materialui.icon
import mui.icons.material.Delete
import mui.icons.material.ExpandMore
import mui.material.*
import mui.system.sx
import react.*
import react.dom.html.ReactHTML.div
import react.dom.i
import web.cssom.TextTransform
import web.cssom.WhiteSpace
import web.cssom.em
import web.dom.Element

private val FixtureMappingEditorView = xComponent<FixtureMappingEditorProps>("FixtureMappingEditor") { props ->
    val appContext = useContext(appContext)
    val editMode = observe(appContext.sceneManager.editMode)
    val styles = appContext.allStyles.controllerEditor

    var newEntityInProgress by state<MutableEntity?> { null }
    val handleNewEntityChange by handler(newEntityInProgress, props.editingController) {
        props.editingController.onChange()
    }
    val newEditingEntity = memo(newEntityInProgress, props.mutableScene.model.units, handleNewEntityChange) {
        newEntityInProgress?.let {
            EditingEntity(it, props.mutableScene.model.units, null, handleNewEntityChange)
        }
    }
    var newEntityMenuAnchor by state<Element?> { null }
    var newEntityMenuAnchorPosition = ref<Element>(null)
    val handleNewEntityClick by handler { newEntityMenuAnchor = newEntityMenuAnchorPosition.current }
    val handleNewEntityMenuSelect by handler(props.mutableScene) { entityType: EntityType ->
        newEntityMenuAnchor = null
        newEntityInProgress = entityType.createNew().edit().apply {
            title = props.mutableScene.model.findUniqueName(title)
        }
    }
    val handleNewEntityMenuClose by handler { newEntityMenuAnchor = null }
    val handleNewEntityDialogClose by handler { _: Any, _: String -> newEntityInProgress = null }
    val handleNewEntityCreate by mouseEventHandler(props.mutableScene, props.mutableFixtureMapping, props.editingController) {
        props.mutableScene.addEntity(newEntityInProgress!!)
        props.mutableFixtureMapping.entity = newEntityInProgress
        newEntityInProgress = null
        props.editingController.onChange()
    }
    val handleNewEntityCancel by mouseEventHandler(props.mutableScene, props.mutableFixtureMapping, props.editingController) {
        newEntityInProgress = null
    }

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
            is CreateNewMenuItem -> {
                handleNewEntityClick()
            }

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
                root = -styles.accordionSummaryRoot
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

                div {
                    attrs.ref = newEntityMenuAnchorPosition
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

    if (newEntityMenuAnchor != null) {
        newEntityMenu {
            attrs.menuAnchor = newEntityMenuAnchor
            attrs.header = "Create New…"
            attrs.onSelect = handleNewEntityMenuSelect
            attrs.onClose = handleNewEntityMenuClose
        }
    }

    if (newEditingEntity != null) {
        Dialog {
            attrs.classes = muiClasses {
                root = -styles.newEntityDialogRoot
                paper = -styles.newEntityDialogPaper
            }
            attrs.fullWidth = true
            attrs.scroll = DialogScroll.paper
            attrs.open = newEntityInProgress != null
            attrs.onClose = handleNewEntityDialogClose

            DialogTitle {
                +"Create New ${newEditingEntity.mutableEntity.entityTypeTitle}"
            }

            DialogContent {
                attrs.className = -styles.newEntityDialogContent
                entityEditor {
                    attrs.showTitleField = true
                    attrs.editingEntity = newEditingEntity
                    attrs.hideActions = true
                }
            }

            DialogActions {
                Button {
                    attrs.onClick = handleNewEntityCancel
                    +"Nope"
                }
                Button {
                    attrs.onClick = handleNewEntityCreate
                    +"Create It!"
                }
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