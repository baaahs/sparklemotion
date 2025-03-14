package baaahs.app.ui.model

import baaahs.app.ui.appContext
import baaahs.app.ui.editor.textFieldEditor
import baaahs.mapper.styleIf
import baaahs.model.EntityData
import baaahs.model.Model
import baaahs.scene.EditingEntity
import baaahs.scene.MutableEntity
import baaahs.scene.MutableEntityGroup
import baaahs.scene.MutableEntityMatcher
import baaahs.scene.MutableScene
import baaahs.sim.FakeDmxUniverse
import baaahs.sim.SimulationEnv
import baaahs.ui.addObserver
import baaahs.ui.asTextNode
import baaahs.ui.components.ListAndDetail
import baaahs.ui.components.ListAndDetail.Orientation.xStacked
import baaahs.ui.components.ListAndDetail.Orientation.yStacked
import baaahs.ui.components.ListAndDetail.Orientation.zStacked
import baaahs.ui.components.NestedList
import baaahs.ui.components.Renderer
import baaahs.ui.components.collapsibleSearchBox
import baaahs.ui.components.listAndDetail
import baaahs.ui.components.nestedList
import baaahs.ui.unaryMinus
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import baaahs.util.globalLaunch
import baaahs.util.useResizeListener
import baaahs.visualizer.DomOverlayExtension
import baaahs.visualizer.EntityAdapter
import baaahs.visualizer.ModelVisualEditor
import baaahs.visualizer.TransformControlsExtension
import baaahs.visualizer.extension
import baaahs.visualizer.modelEntity
import baaahs.visualizer.sim.PixelArranger
import baaahs.visualizer.sim.SwirlyPixelArranger
import baaahs.window
import materialui.icon
import mui.material.Button
import mui.material.ButtonColor
import mui.system.useMediaQuery
import react.Props
import react.RBuilder
import react.RHandler
import react.buildElement
import react.dom.div
import react.dom.span
import react.useContext
import web.dom.Element
import web.dom.document
import web.html.HTMLDivElement
import web.html.InputType

private val EDIT_TITLE_IN_HEADER = true

private val ModelEditorView = xComponent<ModelEditorProps>("ModelEditor") { props ->
    val appContext = useContext(appContext)
    val styles = appContext.allStyles.modelEditor
    val isPortraitScreen = useMediaQuery("(orientation: portrait)")

    val editMode = observe(appContext.sceneManager.editMode)

    val mutableScene = props.mutableScene
    val mutableModel = mutableScene.model
    val domOverlayExtension = memo(mutableModel.units) {
        DomOverlayExtension { itemVisualizer ->
            val bounds = itemVisualizer.item.bounds
            val size = bounds.second - bounds.first
            val length = size.length()
            document.createElement("div").also {
                it.classList.add(+styles.domOverlayItem)
                it.appendChild(document.createElement("div").also {
                    it.classList.add(+styles.domOverlayItemInnerDiv)
                    it.innerText = itemVisualizer.title
                    it.style.fontSize = "1em"
                    it.style.scale = "${length * .007}"
                })
            }
        }.also {
            withCleanup { it.clear() }
        }
    }
    val entityAdapter = memo(mutableModel.units, domOverlayExtension) {
        domOverlayExtension.DomOverlayEntityAdapter(
            SimulationEnv {
                component(appContext.clock)
                component(FakeDmxUniverse())
                component<PixelArranger>(SwirlyPixelArranger(0.2f, 3f))
            },
            mutableModel.units,
            true
        ) as EntityAdapter
    }

    val lastSelectedEntity = ref<Model.Entity>(null)
    val nextSelectedMutableEntity = ref<MutableEntity>(null)

    val visualizer = memo(entityAdapter, props.onEdit) {
        ModelVisualEditor(
            mutableScene, appContext.clock, entityAdapter,
            listOf(extension { domOverlayExtension })
        ) {
            props.onEdit()
        }.also {
            if (lastSelectedEntity.current != null) {
                it.selectedEntity = lastSelectedEntity.current
            }
        }
    }
    if (visualizer.mutableScene !== mutableScene) {
        visualizer.mutableScene = mutableScene
    }
    nextSelectedMutableEntity.current?.let { newMutableEntity ->
        visualizer.refresh()
        val newEntity = visualizer.model.findEntityByLocator(newMutableEntity.locator)
        console.log("nextSelectedMutableEntity: found ", newEntity, " from ", newMutableEntity)
        visualizer.selectedEntity = newEntity
        nextSelectedMutableEntity.current = null
    }
    visualizer.refresh()

    onMount(editMode, visualizer) {
        val transformControls = visualizer.findExtension(TransformControlsExtension::class)
        editMode.addObserver {
            transformControls.enabled = editMode.isOn
        }.also { withCleanup { it.remove() } }
    }

    val nestedList = memo {
        NestedList(emptyList<MutableEntity>()) {
            (it as? MutableEntityGroup)?.children ?: emptyList()
        }
    }
    nestedList.update(mutableModel.entities)

    var entityMatcher by state { MutableEntityMatcher() }
    val handleSearchChange by handler { value: String -> entityMatcher = MutableEntityMatcher(value) }
    val handleSearchRequest by handler { value: String -> }
    val handleSearchCancel by handler { entityMatcher = MutableEntityMatcher() }

    val selectedMutableEntity = visualizer.selectedEntity?.let { mutableModel.findByLocator(it.locator) }

    globalLaunch {
        // This has to happen *after* render or React complains:
        //     Cannot update a component (`NestedListItem`) while rendering a different component
        //     (`ModelEditor`). To locate the bad setState() call inside `ModelEditor`, follow
        //     the stack trace as described in https://reactjs.org/link/setstate-in-render
        //     Error Component Stack
        nestedList.select(selectedMutableEntity)
    }
    lastSelectedEntity.current = selectedMutableEntity?.let { visualizer.model.findEntityByLocator(it.locator) }

    val handleListItemSelect by handler(visualizer) { mutableEntity: MutableEntity? ->
        visualizer.selectedEntity =
            mutableEntity?.let { visualizer.findByLocator(it.locator)?.modelEntity }
        lastSelectedEntity.current = mutableEntity?.locator?.let { visualizer.model.findEntityByLocator(it) }
        forceRender()
    }

    val handleListItemDeselect by handler(visualizer) {
        visualizer.selectedEntity = null
        lastSelectedEntity.current = null
        forceRender()
    }

    val handleAddEntity by handler(mutableScene, props.onEdit) { newEntityData: EntityData ->
        val newMutableEntity = newEntityData.edit()
        mutableScene.addEntity(newMutableEntity)
        nextSelectedMutableEntity.current = newMutableEntity
        props.onEdit()
    }

    val handleDeleteEntity by handler(mutableScene, selectedMutableEntity) {
        selectedMutableEntity?.let { mutableScene.deleteEntity(it) }
        visualizer.selectedEntity = null
        props.onEdit()
    }

    var newEntityMenuAnchor by state<Element?> { null }
    val handleNewEntityClick by mouseEventHandler { newEntityMenuAnchor = it.currentTarget as Element? }
    val handleNewEntityMenuSelect by handler(handleAddEntity) { entityType: EntityType ->
        handleAddEntity(entityType.createNew())
        newEntityMenuAnchor = null
    }
    val hideNewEntityMenu by handler { newEntityMenuAnchor = null }

    val visualizerParentEl = ref<Element>()
    onMount(visualizer) {
        val domElement = domOverlayExtension.domElement
        val parent = visualizerParentEl.current as HTMLDivElement
        parent.insertBefore(visualizer.facade.canvas, null)
        domElement.classList.add(+styles.domOverlay)
        parent.insertBefore(domElement, null)
        visualizer.resize()

        val observer = visualizer.facade.addObserver {
            forceRender()
        }

        val currentVisualizer = visualizer
        withCleanup {
            parent.removeChild(currentVisualizer.facade.canvas)
            observer.remove()
        }
    }

    val selectedEditingEntity = visualizer.editingEntity
    val handleEntityNameGetValue by handler<() -> String>(selectedEditingEntity) {
        selectedMutableEntity?.title ?: ""
    }
    val handleEntityNameSetValue by handler<(String) -> Unit>(selectedEditingEntity, props.onEdit) { value ->
        selectedMutableEntity?.title = value
    }

    useResizeListener(visualizerParentEl) {  _, _ ->
        visualizer.resize()
    }

    div(styleIf(isPortraitScreen, styles.editorPanesPortrait, styles.editorPanesLandscape)) {
        div(+styles.visualizerPane) {
            div(+styles.visualizer) {
                ref = visualizerParentEl

                modelEditorToolbar {
                    attrs.visible = editMode.isOn && selectedMutableEntity != null
                    attrs.visualizer = visualizer.facade
                    attrs.modelUnit = mutableModel.units
                    attrs.onAddEntity = handleAddEntity
                }
            }
        }
        div(+styles.propertiesPane) {
            listAndDetail<EditingEntity<*>> {
                attrs.listHeader = buildElement {
                    span {
                        +"Model Entities"
                        collapsibleSearchBox {
                            attrs.alignRight = true
                            attrs.defaultSearchString = entityMatcher.searchString
                            attrs.onSearchChange = handleSearchChange
                            attrs.onSearchRequest = handleSearchRequest
                            attrs.onSearchCancel = handleSearchCancel
                        }
                    }
                }
                attrs.listHeaderText = "Model Entities".asTextNode()
                attrs.listRenderer = ListAndDetail.ListRenderer {
                    div(+styles.navigatorPaneContent) {
                        nestedList<MutableEntity> {
                            attrs.nestedList = nestedList
                            attrs.renderer = Renderer { item -> +item.item.title }
                            attrs.onSelect = handleListItemSelect
                            attrs.searchMatcher = entityMatcher::matches
                        }

                        if (editMode.isOn) {
                            Button {
                                attrs.className = -styles.newEntityButton
                                attrs.color = ButtonColor.primary
                                attrs.onClick = handleNewEntityClick

                                attrs.startIcon = buildElement { icon(mui.icons.material.AddCircleOutline) }
                                +"New…"
                            }

                            newEntityMenu {
                                attrs.menuAnchor = newEntityMenuAnchor
                                attrs.onSelect = handleNewEntityMenuSelect
                                attrs.onClose = hideNewEntityMenu
                            }
                        }
                    }
                }
                attrs.selection = selectedEditingEntity
                if (editMode.isOn && EDIT_TITLE_IN_HEADER) {
                    attrs.detailHeader =
                        buildElement {
                            div(+styles.headerEditor) {
                                textFieldEditor {
                                    attrs.type = InputType.text
                                    attrs.getValue = handleEntityNameGetValue
                                    attrs.setValue = handleEntityNameSetValue
                                    attrs.onChange = props.onEdit.unsafeCast<(Boolean) -> Unit>()
                                    attrs.noIntermediateUpdates = true
                                }
                            }
                        }
                } else {
                    attrs.detailHeader = selectedMutableEntity?.title?.asTextNode()
                }

                attrs.detailRenderer = ListAndDetail.DetailRenderer { editingEntity ->
                    entityEditor {
                        attrs.showTitleField = !EDIT_TITLE_IN_HEADER
                        attrs.editingEntity = editingEntity
                        attrs.onDelete = handleDeleteEntity
                    }
                }
                attrs.onDeselect = handleListItemDeselect
                if (isPortraitScreen)
                    attrs.orientation = if (window.innerWidth < 600) zStacked else xStacked
                else
                    attrs.orientation = if (window.innerHeight < 600) zStacked else yStacked
            }
        }

    }
}

external interface ModelEditorProps : Props {
    var mutableScene: MutableScene
    var onEdit: () -> Unit
}

fun RBuilder.modelEditor(handler: RHandler<ModelEditorProps>) =
    child(ModelEditorView, handler = handler)
