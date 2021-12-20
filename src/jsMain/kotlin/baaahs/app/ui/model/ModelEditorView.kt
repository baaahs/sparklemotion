package baaahs.app.ui.model

import baaahs.app.ui.appContext
import baaahs.geom.EulerAngle
import baaahs.geom.Vector3F
import baaahs.model.Model
import baaahs.scene.OpenScene
import baaahs.sim.FakeDmxUniverse
import baaahs.sim.SimulationEnv
import baaahs.ui.*
import baaahs.util.globalLaunch
import baaahs.util.useResizeListener
import baaahs.visualizer.EntityVisualizer
import baaahs.visualizer.PixelArranger
import baaahs.visualizer.SwirlyPixelArranger
import baaahs.visualizer.Visualizer
import kotlinx.html.InputType
import kotlinx.html.UL
import kotlinx.html.js.onChangeFunction
import kotlinx.html.js.onClickFunction
import materialui.components.container.container
import materialui.components.container.enums.ContainerStyle
import materialui.components.list.ListElementBuilder
import materialui.components.list.ListProps
import materialui.components.list.list
import materialui.components.listitem.listItem
import materialui.components.listitemtext.listItemText
import materialui.components.textfield.textField
import org.w3c.dom.Element
import org.w3c.dom.HTMLDivElement
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.div
import react.dom.h5
import react.dom.header
import react.dom.p
import react.useContext
import kotlin.math.PI
import kotlin.math.roundToInt

private val ModelEditorView = xComponent<ModelEditorProps>("ModelEditor") { props ->
    val appContext = useContext(appContext)
    val styles = appContext.allStyles.modelEditor

    val visualizer = memo { Visualizer(appContext.webClient.modelProvider, appContext.clock) }
    val visualizerEl = ref<Element>()

    val model = props.scene.model
    val mutableScene = props.scene.edit()
    var selectedEntityVisualizer by state<EntityVisualizer?> { null }
    val selectedEntity = selectedEntityVisualizer?.entity

    onMount {
        visualizer.facade.container = visualizerEl.current as HTMLDivElement
        visualizer.resize()
        visualizer.facade.addObserver { it ->
            selectedEntityVisualizer = it.selectedEntity
        }

        globalLaunch {
            val pixelArranger = SwirlyPixelArranger(0.2f, 3f)
            val simulationEnv = SimulationEnv {
                component(appContext.clock)
                component(FakeDmxUniverse())
                component<PixelArranger>(pixelArranger)
                component(visualizer)
            }

            model.allEntities.mapNotNull { entity ->
                entity.createFixtureSimulation(simulationEnv)?.let { simulation ->
                    val entityVisualizer = simulation.entityVisualizer
                    visualizer.addEntityVisualizer(entityVisualizer)
                    simulation.previewFixture
                }
            }
        }

        withCleanup {
            visualizer.facade.container = null
        }
    }

    useResizeListener(visualizerEl) {
        visualizer.resize()
    }

    fun ListElementBuilder<UL, ListProps>.buildList(entity: Model.Entity) {
        listItem {
            attrs.button = true
            attrs.selected = entity == selectedEntity
            attrs.onClickFunction = {
                visualizer.facade.select(entity)
            }

            listItemText { +entity.title }
        }

        if (entity is Model.EntityGroup) {
            list {
                entity.entities.forEach { subEntity ->
                    buildList(subEntity)
                }
            }
        }
    }

    fun RBuilder.numberTextField(label: String, value: Float, adornment: String, onChange: (Float) -> Unit) {
        textField {
            attrs.type = InputType.number
            attrs.inputProps { attrs.endAdornment { +adornment } }
            attrs.inputLabelProps { attrs.shrink = true }
            attrs.onChangeFunction = { onChange(it.currentTarget.value.toFloat()) }
            attrs.value(value)
            attrs.label { +label }
        }
    }

    fun RBuilder.translationTextField(label: String, value: Float, onChange: (Float) -> Unit) {
        numberTextField(label, value, model.units.display, onChange)
    }

    fun RBuilder.scaleTextField(label: String, value: Float, onChange: (Float) -> Unit) {
        numberTextField(label, value, "x", onChange)
    }

    fun RBuilder.rotationTextField(label: String, value: Double, onChange: (Double) -> Unit) {
        textField {
            attrs.type = InputType.number
            attrs.inputProps { attrs.endAdornment { +"°" } }
            attrs.inputLabelProps { attrs.shrink = true }
            attrs.onChangeFunction = { onChange(it.currentTarget.value.toDouble().fromDegrees) }
            attrs.value(value.asDegrees)
            attrs.label { +label }
        }
    }



    div(+styles.editorPanes) {
        div(+styles.navigatorPane) {
            header { +"Navigator" }

            list {
                model.entities.forEach { entity ->
                    buildList(entity)
                }
            }
        }

        div(+styles.visualizerPane) {
            div(+styles.visualizer) {
                ref = visualizerEl
            }
        }

        div(+styles.propertiesPane) {
            header { +"Properties" }

            selectedEntity?.let { entity ->
                p {
                    +"Title: "
                    +entity.title
                }

                header { +"Transformation" }
                container(styles.transformEditBox on ContainerStyle.root) {
                    h5 { +"Translation:" }

                    fun update(value: Float, block: (Float) -> Vector3F) {
                        selectedEntityVisualizer?.let {
                            it.transformation = it.transformation.withTranslation(block(value))
                            this@xComponent.forceRender()
                        }
                    }
                    val translation = selectedEntityVisualizer!!.transformation.translation
                    translationTextField("X", translation.x) { update(it) { translation.copy(x = it) } }
                    translationTextField("Y", translation.y) { update(it) { translation.copy(y = it) } }
                    translationTextField("Z", translation.z) { update(it) { translation.copy(z = it) } }
                }

                container(styles.transformEditBox on ContainerStyle.root) {
                    h5 { +"Rotation" }

                    fun update(value: Double, block: (Double) -> EulerAngle) {
                        selectedEntityVisualizer?.let {
                            it.transformation = it.transformation.withRotation(block(value))
                            this@xComponent.forceRender()
                        }
                    }
                    val rotation = selectedEntityVisualizer!!.transformation.rotation
                    rotationTextField("Pitch", rotation.pitchRad) { update(it) { rotation.copy(pitchRad = it) } }
                    rotationTextField("Yaw", rotation.yawRad) { update(it) { rotation.copy(yawRad = it) } }
                    rotationTextField("Roll", rotation.rollRad) { update(it) { rotation.copy(rollRad = it) } }
                }

                container(styles.transformEditBox on ContainerStyle.root) {
                    h5 { +"Scale" }

                    fun update(value: Float, block: (Float) -> Vector3F) {
                        selectedEntityVisualizer?.let {
                            it.transformation = it.transformation.withScale(block(value))
                            this@xComponent.forceRender()
                        }
                    }
                    val scale = selectedEntityVisualizer!!.transformation.scale
                    scaleTextField("X", scale.x) { update(it) { scale.copy(x = it) } }
                    scaleTextField("Y", scale.y) { update(it) { scale.copy(y = it) } }
                    scaleTextField("Z", scale.z) { update(it) { scale.copy(z = it) } }
                }
            }
        }
    }
}

private val Double.asDegrees: Number get() = (this / (2 * PI) * 360).roundToInt()
private val Double.fromDegrees: Double get() = this / 360 * (2 * PI)


external interface ModelEditorProps : Props {
    var scene: OpenScene
}

fun RBuilder.modelEditor(handler: RHandler<ModelEditorProps>) =
    child(ModelEditorView, handler = handler)