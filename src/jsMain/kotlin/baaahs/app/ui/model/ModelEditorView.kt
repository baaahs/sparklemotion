package baaahs.app.ui.model

import baaahs.app.ui.appContext
import baaahs.geom.toEulerAngle
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
import baaahs.visualizer.PixelArranger
import baaahs.visualizer.SwirlyPixelArranger
import baaahs.visualizer.Visualizer
import kotlinx.html.js.onClickFunction
import kotlinx.html.js.onMouseDownFunction
import materialui.components.formcontrol.enums.FormControlMargin
import materialui.components.formcontrol.formControl
import materialui.components.list.enums.ListStyle
import materialui.components.list.list
import materialui.components.listitem.listItem
import materialui.components.listitemtext.listItemText
import org.w3c.dom.Element
import org.w3c.dom.HTMLDivElement
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.div
import react.dom.header
import react.useContext
import three_ext.toVector3F

private val ModelEditorView = xComponent<ModelEditorProps>("ModelEditor") { props ->
    val appContext = useContext(appContext)
    val styles = appContext.allStyles.modelEditor

    val visualizer = memo { Visualizer(appContext.clock) }
    val visualizerEl = ref<Element>()

    observe(visualizer.facade)
    val mutableModel = props.mutableScene.model

    val simulationEnv = memo {
        SimulationEnv {
            component(appContext.clock)
            component(FakeDmxUniverse())
            component<PixelArranger>(SwirlyPixelArranger(0.2f, 3f))
            component(visualizer)
        }
    }

    val createEditableEntity by handler(mutableModel, simulationEnv, visualizer, props.onEdit) { mutableEntity: MutableEntity<*> ->
        EditingEntity(mutableEntity, mutableModel.units, simulationEnv).apply {
            visualizer.add(entityVisualizer)
            entityVisualizer.addObserver { viz ->
                mutableEntity.position = viz.obj.position.toVector3F()
                mutableEntity.rotation = viz.obj.rotation.toEulerAngle()
                mutableEntity.scale = viz.obj.scale.toVector3F()
                notifyChanged()
            }

            addObserver {
                props.onEdit()
            }
        }
    }

    val editingEntities = memo(mutableModel, createEditableEntity) {
        println("(re)create editingEntities")
        visualizer.facade.clear()
        mutableModel.entities.map { mutableEntity ->
            createEditableEntity(mutableEntity)
        }
    }.toMutableList()

    val selectedEditingEntity = editingEntities.find {
        it.openEntity == visualizer.facade.selectedEntity?.entity
    }

    val handleAddEntity by handler(mutableModel, props.onEdit) { newEntityData: EntityData ->
        val newMutableEntity = newEntityData.edit()
        mutableModel.entities.add(newMutableEntity)
        editingEntities.add(createEditableEntity(newMutableEntity))
        props.onEdit()
    }

    onMount {
        visualizer.facade.container = visualizerEl.current as HTMLDivElement
        visualizer.resize()

        withCleanup {
            visualizer.facade.container = null
        }
    }

    useResizeListener(visualizerEl) {
        visualizer.resize()
    }

    fun RBuilder.buildList(editingEntity: EditingEntity<*>) {
        listItem {
            attrs.button = true
            attrs.selected = editingEntity.entityVisualizer == selectedEditingEntity?.entityVisualizer
            attrs.onClickFunction = {
                visualizer.facade.select(editingEntity.entityVisualizer)
                it.stopPropagation()
            }
            attrs.onMouseDownFunction = {
                it.stopPropagation()
            }

            listItemText { +editingEntity.mutableEntity.title }

//            if (entityWrapper is EntityGroupVisualizer) {
//                list(styles.entityList on ListStyle.root) {
//                    entityWrapper.children.forEach { child ->
//                        buildList(child)
//                    }
//                }
//            }
        }
    }



    div(+styles.editorPanes) {
        div(+styles.navigatorPane) {
            header { +"Navigator" }

            div(+styles.navigatorPaneContent) {
                list(styles.entityList on ListStyle.root) {
                    editingEntities.forEach { entityVisualizer ->
                        buildList(entityVisualizer)
                    }
                }
            }
        }

        div(+styles.visualizerPane) {
            div(+styles.visualizer) {
                ref = visualizerEl

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
                selectedEditingEntity?.let { editingEntity ->
                    formControl {
                        attrs.margin = FormControlMargin.dense

                        editingEntity.renderEditorPanels(this)
                    }
                }
            }
        }
    }
}

fun <T : Model.Entity> EditingEntity<T>.renderEditorPanels(builder: RBuilder) = with (builder) {
    mutableEntity.getEditorPanels().forEach { editorPanel ->
        with (editorPanel.getView(this@renderEditorPanels)) { render() }
    }
}

external interface ModelEditorProps : Props {
    var mutableScene: MutableScene
    var onEdit: () -> Unit
}

fun RBuilder.modelEditor(handler: RHandler<ModelEditorProps>) =
    child(ModelEditorView, handler = handler)