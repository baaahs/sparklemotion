package baaahs.app.ui.model

import baaahs.app.ui.appContext
import baaahs.model.EntityData
import baaahs.model.Model
import baaahs.scene.EditingEntity
import baaahs.scene.MutableEntity
import baaahs.scene.MutableEntityGroup
import baaahs.scene.MutableEntityMatcher
import baaahs.scene.MutableScene
import baaahs.sim.FakeDmxUniverse
import baaahs.sim.SimulationEnv
import baaahs.ui.*
import baaahs.ui.components.DetailRenderer
import baaahs.ui.components.ListRenderer
import baaahs.ui.components.NestedList
import baaahs.ui.components.Renderer
import baaahs.ui.components.collapsibleSearchBox
import baaahs.ui.components.listAndDetail
import baaahs.ui.components.nestedList
import baaahs.util.useResizeListener
import baaahs.visualizer.*
import baaahs.visualizer.sim.PixelArranger
import baaahs.visualizer.sim.SwirlyPixelArranger
import kotlinx.css.Padding
import kotlinx.css.em
import kotlinx.css.padding
import materialui.icon
import mui.icons.material.Delete
import mui.material.*
import mui.material.styles.Theme
import mui.material.styles.useTheme
import mui.system.Breakpoint
import mui.system.useMediaQuery
import react.Props
import react.RBuilder
import react.RHandler
import react.buildElement
import react.dom.div
import react.dom.header
import react.dom.i
import react.dom.span
import react.useContext
import styled.inlineStyles
import web.dom.Element
import web.dom.document
import web.html.HTMLDivElement

private val ModelEditorView = xComponent<ModelEditorProps>("ModelEditor") { props ->
    val appContext = useContext(appContext)
    val styles = appContext.allStyles.modelEditor
    val theme = useTheme<Theme>()
    val isNarrowScreen = useMediaQuery(theme.breakpoints.down(Breakpoint.md))
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
        )
    }

    val lastSelectedEntity = ref<Model.Entity>(null)

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

    val selectedMutableEntity = visualizer.selectedEntity?.let { mutableModel.findById(it.id) }
    nestedList.select(selectedMutableEntity)
    lastSelectedEntity.current = selectedMutableEntity?.let { visualizer.model.findEntityById(it.id) }

    val handleListItemSelect by handler(visualizer) { mutableEntity: MutableEntity? ->
        visualizer.selectedEntity =
            mutableEntity?.let { visualizer.findById(it.id)?.modelEntity }
        lastSelectedEntity.current = mutableEntity?.id?.let { visualizer.model.findEntityById(it) }
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
        attrs.listRenderer = ListRenderer {
            div(+styles.navigatorPane) {
                div(+styles.navigatorPaneContent) {
                    nestedList<MutableEntity> {
                        attrs.nestedList = nestedList
                        attrs.renderer = Renderer { item -> +item.item.title }
                        attrs.onSelect = handleListItemSelect
                        attrs.searchMatcher = entityMatcher::matches
                    }
                }
            }
        }
        attrs.selection = selectedEditingEntity
        attrs.detailHeader = selectedMutableEntity?.title
        attrs.detailRenderer = DetailRenderer { editingEntity ->
            FormControl {
                attrs.margin = FormControlMargin.dense

                editingEntity.getEditorPanelViews().forEach {
                    it.render(this)
                }

                header { +"Actions" }

                if (editMode.isOn) {
                    IconButton {
                        attrs.onClick = handleDeleteEntity.withMouseEvent()
                        attrs.color = IconButtonColor.secondary
                        icon(Delete)
                        +"Delete Entity"
                    }
                }
            }
        }
        attrs.onDeselect = handleListItemDeselect
    }

    Paper {
        attrs.className = -styles.editorPanes

        div(+styles.propertiesPane) {
            div(+styles.propertiesPaneContent) {
                if (selectedEditingEntity == null) {
                    div {
                        inlineStyles { padding = Padding(1.em) }
                        i { +"Maybe click on something, why don't ya?" }
                    }
                } else {
                    selectedEditingEntity.let {
                    }
                }
            }
        }

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
    }
}

external interface ModelEditorProps : Props {
    var mutableScene: MutableScene
    var onEdit: () -> Unit
}

fun RBuilder.modelEditor(handler: RHandler<ModelEditorProps>) =
    child(ModelEditorView, handler = handler)
