package baaahs.app.ui.model

import baaahs.app.ui.appContext
import baaahs.model.EntityData
import baaahs.model.Model
import baaahs.scene.MutableEntity
import baaahs.scene.MutableEntityGroup
import baaahs.scene.MutableScene
import baaahs.sim.FakeDmxUniverse
import baaahs.sim.SimulationEnv
import baaahs.ui.*
import baaahs.ui.components.NestedList
import baaahs.ui.components.Renderer
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
import react.*
import react.dom.div
import react.dom.header
import react.dom.i
import styled.inlineStyles
import web.dom.Element
import web.dom.document
import web.html.HTMLDivElement

private val ModelEditorView = xComponent<ModelEditorProps>("ModelEditor") { props ->
    val appContext = useContext(appContext)
    val styles = appContext.allStyles.modelEditor
    val editMode = observe(appContext.sceneManager.editMode)

    val mutableModel = props.mutableScene.model
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
        ModelVisualEditor(mutableModel, appContext.clock, entityAdapter,
            listOf(extension { domOverlayExtension })
        ) {
            props.onEdit()
        }.also {
            if (lastSelectedEntity.current != null) {
                it.selectedEntity = lastSelectedEntity.current
            }
        }
    }
    if (visualizer.mutableModel !== mutableModel) {
        visualizer.mutableModel = mutableModel
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

    val selectedMutableEntity = visualizer.selectedEntity?.let { mutableModel.findById(it.id) }
    nestedList.select(selectedMutableEntity)
    lastSelectedEntity.current = selectedMutableEntity?.let { visualizer.model.findEntityById(it.id) }

    val handleListItemSelect by handler(visualizer) { mutableEntity: MutableEntity? ->
        visualizer.selectedEntity =
            mutableEntity?.let { visualizer.findById(it.id)?.modelEntity }
        lastSelectedEntity.current = mutableEntity?.id?.let { visualizer.model.findEntityById(it) }
        forceRender()
    }

    val handleAddEntity by handler(mutableModel, props.onEdit) { newEntityData: EntityData ->
        val newMutableEntity = newEntityData.edit()
        mutableModel.entities.add(newMutableEntity)
        props.onEdit()
    }

    val handleDeleteEntity by handler(mutableModel, selectedMutableEntity) {
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

    val editingEntity = visualizer.editingEntity

    useResizeListener(visualizerParentEl) {  _, _ ->
        visualizer.resize()
    }

    Paper {
        attrs.className = -styles.editorPanes
        div(+styles.navigatorPane) {
            header { +"Model Entities" }

            div(+styles.navigatorPaneContent) {
                nestedList<MutableEntity> {
                    attrs.nestedList = nestedList
                    attrs.renderer = Renderer { item -> +item.item.title }
                    attrs.onSelect = handleListItemSelect
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

        div(+styles.propertiesPane) {
            div(+styles.propertiesPaneContent) {
                if (editingEntity == null) {
                    div {
                        inlineStyles { padding = Padding(1.em) }
                        i { +"Maybe click on something, why don't ya?" }
                    }
                } else {
                    editingEntity.let {
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