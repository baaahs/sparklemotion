package baaahs.app.ui.model

import baaahs.app.ui.appContext
import baaahs.model.EntityData
import baaahs.model.Model
import baaahs.scene.EditingEntity
import baaahs.scene.MutableEntity
import baaahs.scene.MutableScene
import baaahs.sim.FakeDmxUniverse
import baaahs.sim.SimulationEnv
import baaahs.ui.addObserver
import baaahs.ui.on
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import baaahs.util.useResizeListener
import baaahs.visualizer.*
import materialui.components.formcontrol.enums.FormControlMargin
import materialui.components.formcontrol.formControl
import materialui.components.list.enums.ListStyle
import materialui.components.list.list
import org.w3c.dom.Element
import org.w3c.dom.HTMLDivElement
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.div
import react.dom.header
import react.useContext

private val ModelEditorView = xComponent<ModelEditorProps>("ModelEditor") { props ->
    val appContext = useContext(appContext)
    val styles = appContext.allStyles.modelEditor

    val entityAdapter = memo {
        EntityAdapter(
            SimulationEnv {
                component(appContext.clock)
                component(FakeDmxUniverse())
                component<PixelArranger>(SwirlyPixelArranger(0.2f, 3f))
            }
        )
    }

    val lastSelectedEntity = ref<Model.Entity>(null)

    val mutableModel = props.mutableScene.model
    val visualizer = memo(mutableModel, entityAdapter, props.onEdit) {
        ModelVisualEditor(mutableModel, appContext.clock, entityAdapter, true) {
            props.onEdit()
        }.also {
            if (lastSelectedEntity.current != null) {
                it.selectedEntity = lastSelectedEntity.current
            }
        }
    }
    visualizer.refresh()

    val handleAddEntity by handler(mutableModel, props.onEdit) { newEntityData: EntityData ->
        val newMutableEntity = newEntityData.edit()
        mutableModel.entities.add(newMutableEntity)
        props.onEdit()
    }

    val selectedMutableEntity = visualizer.selectedEntity?.let { mutableModel.findById(it.id) }
    lastSelectedEntity.current = selectedMutableEntity?.let { visualizer.model.findEntityById(it.id) }

    val handleListItemSelect by handler(visualizer) { mutableEntity: MutableEntity<*>? ->
        visualizer.selectedEntity =
            mutableEntity?.let { visualizer.findById(it.id)?.modelEntity }
        lastSelectedEntity.current = mutableEntity?.id?.let { visualizer.model.findEntityById(it) }
        forceRender()
    }

    val visualizerParentEl = ref<Element>()
    onMount(visualizer) {
        val parent = visualizerParentEl.current as HTMLDivElement
        parent.insertBefore(visualizer.facade.canvas, null)
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
    memo(selectedMutableEntity, mutableModel.units, entityAdapter, props.onEdit) {
        selectedMutableEntity?.let { selected ->
            EditingEntity(selected, mutableModel.units, entityAdapter) {
                props.onEdit()
            }
        }
    }

    useResizeListener(visualizerParentEl) {
        visualizer.resize()
    }

    fun <T : Model.Entity> EditingEntity<T>.renderEditorPanels(builder: RBuilder) {
        val entity = this@renderEditorPanels

        with(mutableEntity) {
            with (builder) {
                getEditorPanels().forEach { editorPanel ->
                    with (editorPanel.getView(entity)) { render() }
                }
            }
        }
    }

    div(+styles.editorPanes) {
        div(+styles.navigatorPane) {
            header { +"Navigator" }

            div(+styles.navigatorPaneContent) {
                list(styles.entityList on ListStyle.root) {
                    mutableModel.entities.forEach { mutableEntity ->
                        entityListItem {
                            attrs.mutableEntity = mutableEntity
                            attrs.selectedMutableEntity = selectedMutableEntity
                            attrs.onSelect = handleListItemSelect
                        }
                    }
                }
            }
        }

        div(+styles.visualizerPane) {
            div(+styles.visualizer) {
                ref = visualizerParentEl

                modelEditorToolbar {
                    attrs.visualizer = visualizer.facade
                    attrs.modelUnit = mutableModel.units
                    attrs.onAddEntity = handleAddEntity
                }
            }
        }

        div(+styles.propertiesPane) {
            header { +"Properties" }

            div(+styles.propertiesPaneContent) {
                editingEntity?.let {
                    formControl {
                        attrs.margin = FormControlMargin.dense
                        it.renderEditorPanels(this)
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