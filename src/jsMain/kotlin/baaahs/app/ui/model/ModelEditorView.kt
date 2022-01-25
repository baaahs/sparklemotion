package baaahs.app.ui.model

import baaahs.app.ui.appContext
import baaahs.geom.EulerAngle
import baaahs.geom.Vector3F
import baaahs.geom.toThreeEuler
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
import three.js.Object3D

private val ModelEditorView = xComponent<ModelEditorProps>("ModelEditor") { props ->
    val appContext = useContext(appContext)
    val styles = appContext.allStyles.modelEditor

    val mutableModel = props.mutableScene.model
    val modelData = mutableModel.build()
    var modelRebuilt = false
    val currentOpenModel = memo(modelData) {
        modelRebuilt = true
        modelData.open()
    }

    val simulationEnv = memo {
        SimulationEnv {
            component(appContext.clock)
            component(FakeDmxUniverse())
            component<PixelArranger>(SwirlyPixelArranger(0.2f, 3f))
        }
    }

    val visualizer = memo(simulationEnv) {
        ModelVisualizer(currentOpenModel, appContext.clock, simulationEnv, true)
    }
    val visualizerParentEl = ref<Element>()

    if (modelRebuilt) {
        visualizer.model = currentOpenModel
    }

    val handleAddEntity by handler(mutableModel, props.onEdit) { newEntityData: EntityData ->
        val newMutableEntity = newEntityData.edit()
        mutableModel.entities.add(newMutableEntity)
        props.onEdit()
    }

    var selectedEntity by state<MutableEntity<*>?> { null }
    val handleListItemSelect by handler { mutableEntity: MutableEntity<*>? ->
        selectedEntity = mutableEntity
        visualizer.selectedEntity =
            mutableEntity?.let { visualizer.findById(it.id)?.modelEntity }
    }

    onMount {
        val parent = visualizerParentEl.current as HTMLDivElement
        parent.insertBefore(visualizer.facade.canvas, null)
        visualizer.resize()

        val observer = visualizer.facade.addObserver {
            selectedEntity = visualizer.selectedEntity?.let { mutableModel.findById(it.id) }
        }

        withCleanup {
            parent.removeChild(visualizer.facade.canvas)
            observer.remove()
        }
    }

    useResizeListener(visualizerParentEl) {
        visualizer.resize()
    }

    val editingEntity = memo(selectedEntity, mutableModel.units, simulationEnv, props.onEdit) {
        selectedEntity?.let { selected ->
            EditingEntity(selected, mutableModel.units, simulationEnv) {
                props.onEdit()
            }
        }
    }

    fun <T : Model.Entity> EditingEntity<T>.renderEditorPanels(builder: RBuilder) {
        val entity = this@renderEditorPanels

        with(mutableEntity) {
            val visualization = visualizer.findById(id)
                ?: error("found no Object3D for $id ($title / ${this::class.simpleName})")
            val transformation = Transformation(this, visualization)
            entity.addObserver { transformation.onChange() }
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
                            attrs.selectedMutableEntity = selectedEntity
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

class Transformation(
    private val entity: MutableEntity<*>,
    private val visualization: Object3D,
    private var position: Vector3F = entity.position,
    private var rotation: EulerAngle = entity.rotation,
    private var scale: Vector3F = entity.scale
) {
    fun onChange() {
        if (entity.position != position) {
            position = entity.position
            visualization.position.copy(position.toVector3())
        }

        if (entity.rotation != rotation) {
            rotation = entity.rotation
            visualization.rotation = rotation.toThreeEuler()
        }

        if (entity.scale != scale) {
            scale = entity.scale
            visualization.scale.copy(scale.toVector3())
        }
    }
}

external interface ModelEditorProps : Props {
    var mutableScene: MutableScene
    var onEdit: () -> Unit
}

fun RBuilder.modelEditor(handler: RHandler<ModelEditorProps>) =
    child(ModelEditorView, handler = handler)