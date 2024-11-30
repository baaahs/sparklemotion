package baaahs.app.ui.model

import baaahs.app.ui.appContext
import baaahs.mapper.styleIf
import baaahs.model.EntityData
import baaahs.model.Model
import baaahs.scene.*
import baaahs.sim.FakeDmxUniverse
import baaahs.sim.SimulationEnv
import baaahs.ui.*
import baaahs.ui.components.*
import baaahs.ui.components.ListAndDetail.Orientation.*
import baaahs.util.globalLaunch
import baaahs.util.useResizeListener
import baaahs.visualizer.*
import baaahs.visualizer.sim.PixelArranger
import baaahs.visualizer.sim.SwirlyPixelArranger
import baaahs.window
import emotion.styled.styled
import materialui.icon
import mui.icons.material.Delete
import mui.icons.material.ExpandMore
import mui.material.*
import mui.material.styles.Theme
import mui.material.styles.useTheme
import mui.system.sx
import mui.system.useMediaQuery
import react.*
import react.dom.div
import react.dom.span
import web.cssom.pct
import web.cssom.px
import web.dom.Element
import web.dom.document
import web.html.HTMLDivElement

private val ModelEditorView = xComponent<ModelEditorProps>("ModelEditor") { props ->
    val appContext = useContext(appContext)
    val styles = appContext.allStyles.modelEditor
    val theme = useTheme<Theme>()
    val isPortraitScreen = useMediaQuery("(orientation: portrait)")

    val editMode = observe(appContext.sceneManager.editMode)

    val mutableScene = props.mutableScene
    val mutableModel = mutableScene.model
    val domOverlayExtension = memo {
        DomOverlayExtension { itemVisualizer ->
            document.createElement("div").also {
                it.classList.add(+styles.domOverlayItem)
                it.innerText = itemVisualizer.title
            }
        }
    }
    val entityAdapter = memo(mutableModel.units) {
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
        mutableModel.entities.add(newMutableEntity)
        nextSelectedMutableEntity.current = newMutableEntity
        props.onEdit()
    }

    val handleDeleteEntity by handler(mutableScene, selectedMutableEntity) {
        selectedMutableEntity?.let { mutableModel.delete(it) }
        visualizer.selectedEntity = null
        props.onEdit()
    }

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

    useResizeListener(visualizerParentEl) {  _, _ ->
        visualizer.resize()
    }

    div(styleIf(isPortraitScreen, styles.editorPanesPortrait, styles.editorPanesLandscape)) {
        div(+styles.visualizerPane) {
            div(+styles.visualizer) {
                ref = visualizerParentEl

                if (editMode.isOn) {
                    modelEditorToolbar {
                        attrs.visualizer = visualizer.facade
                        attrs.modelUnit = mutableModel.units
                        attrs.onAddEntity = handleAddEntity
                    }
                }
            }
        }
        div(+styles.propertiesPane) {
            listAndDetail<EditingEntity<*>> {
                attrs.listHeader = buildElement {
                    span {
                        +"Model Entities"
                        collapsibleSearchBox {
                            attrs.searchString = entityMatcher.searchString
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
                    }
                }
                attrs.selection = selectedEditingEntity
                attrs.detailHeader = selectedMutableEntity?.title
                attrs.detailRenderer = ListAndDetail.DetailRenderer { editingEntity ->
                    FormControl {
                        attrs.margin = FormControlMargin.dense
                        attrs.sx { width = 100.pct }

                        // Additional entity-specific views:
                        editingEntity.getEditorPanels().forEachIndexed { i, editorPanel ->
                            Accordion {
                                attrs.elevation = 4
                                attrs.defaultExpanded = i == 0

                                AccordionSummary {
                                    attrs.expandIcon = ExpandMore.create()
                                    editorPanel.title?.let {
                                        Typography { +it }
                                    }
                                }
                                AccordionDetails {
                                    attrs.sx { padding = 0.px }

                                    editingEntity.getView(editorPanel).render(this)
                                }
                            }
                        }

                        Accordion {
                            attrs.elevation = 4

                            AccordionSummary {
                                attrs.expandIcon = ExpandMore.create()
                                Typography { +"Transformation" }
                            }
                            AccordionDetails {
                                attrs.sx { padding = 0.px }

                                transformationEditor {
                                    attrs.editingEntity = editingEntity
                                }
                            }
                        }

                        titleAndDescriptionEditor {
                            attrs.editingEntity = editingEntity
                        }

                        Accordion {
                            attrs.elevation = 4
                            attrs.defaultExpanded = true

                            AccordionSummary {
                                attrs.expandIcon = ExpandMore.create()
                                Typography { +"Actions" }
                            }
                            AccordionDetails {
                                attrs.sx { padding = 0.px }

                                IconButton {
                                    attrs.onClick = handleDeleteEntity.withMouseEvent()
                                    attrs.color = IconButtonColor.secondary
                                    attrs.disabled = editMode.isOff
                                    icon(Delete)
                                    +"Delete Entity"
                                }
                            }
                        }
                    }
                }
                attrs.onDeselect = handleListItemDeselect
                if (isPortraitScreen)
                    attrs.orientation = if (window.innerWidth < 450) zStacked else xStacked
                else
                    attrs.orientation = if (window.innerHeight < 450) zStacked else yStacked
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
