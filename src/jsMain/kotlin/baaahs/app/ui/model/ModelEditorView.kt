package baaahs.app.ui.model

import baaahs.app.ui.appContext
import baaahs.geom.EulerAngle
import baaahs.geom.Vector3F
import baaahs.scene.OpenScene
import baaahs.sim.FakeDmxUniverse
import baaahs.sim.SimulationEnv
import baaahs.ui.*
import baaahs.util.globalLaunch
import baaahs.util.useResizeListener
import baaahs.visualizer.*
import kotlinx.html.InputType
import kotlinx.html.js.onChangeFunction
import kotlinx.html.js.onClickFunction
import kotlinx.html.js.onMouseDownFunction
import materialui.components.container.container
import materialui.components.container.enums.ContainerStyle
import materialui.components.formcontrol.enums.FormControlMargin
import materialui.components.formcontrol.formControl
import materialui.components.input.enums.InputStyle
import materialui.components.input.input
import materialui.components.list.enums.ListStyle
import materialui.components.list.list
import materialui.components.listitem.listItem
import materialui.components.listitemtext.listItemText
import materialui.components.textfield.enums.TextFieldSize
import materialui.components.textfield.textField
import org.w3c.dom.Element
import org.w3c.dom.HTMLDivElement
import react.*
import react.dom.div
import react.dom.h5
import react.dom.header
import react.dom.p
import kotlin.math.PI
import kotlin.math.roundToInt

private val ModelEditorView = xComponent<ModelEditorProps>("ModelEditor") { props ->
    val appContext = useContext(appContext)
    val styles = appContext.allStyles.modelEditor

    val visualizer = memo { Visualizer(appContext.clock) }
    val visualizerEl = ref<Element>()

    val model = props.scene.model
    val mutableScene = props.scene.edit()
    var selectedEntityVisualizer by state<EntityVisualizer?> { null }

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

            visualizer.model = model
            forceRender()
        }

        withCleanup {
            visualizer.facade.container = null
        }
    }

    useResizeListener(visualizerEl) {
        visualizer.resize()
    }

    fun RBuilder.buildList(entityVisualizer: EntityVisualizer) {
        listItem {
            attrs.button = true
            attrs.selected = entityVisualizer == selectedEntityVisualizer
            attrs.onClickFunction = {
                visualizer.facade.select(entityVisualizer)
                it.stopPropagation()
            }
            attrs.onMouseDownFunction = {
                it.stopPropagation()
            }

            listItemText { +entityVisualizer.title }

            if (entityVisualizer is EntityGroupVisualizer) {
                list(styles.entityList on ListStyle.root) {
                    entityVisualizer.children.forEach { child ->
                        buildList(child)
                    }
                }
            }
        }
    }

    fun RBuilder.numberTextField(label: String, value: Float, adornment: String, onChange: (Float) -> Unit) {
        textField {
            attrs.type = InputType.number
            attrs.size = TextFieldSize.small
            attrs.InputProps = buildElement {
                input(+styles.partialUnderline on InputStyle.underline) {
                    attrs.endAdornment { +adornment }
                }
            }.props.unsafeCast<PropsWithChildren>()
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
            attrs.size = TextFieldSize.small
            attrs.InputProps = buildElement {
                input(+styles.partialUnderline on InputStyle.underline) {
                    attrs.endAdornment { +"°" }
                }
            }.props.unsafeCast<PropsWithChildren>()
            attrs.inputLabelProps { attrs.shrink = true }
            attrs.onChangeFunction = { onChange(it.currentTarget.value.toDouble().fromDegrees) }
            attrs.value(value.asDegrees)
            attrs.label { +label }
        }
    }



    div(+styles.editorPanes) {
        div(+styles.navigatorPane) {
            header { +"Navigator" }

            div(+styles.navigatorPaneContent) {
                list(styles.entityList on ListStyle.root) {
                    visualizer.children.forEach { entityVisualizer ->
                        buildList(entityVisualizer)
                    }
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

            div(+styles.propertiesPaneContent) {
                selectedEntityVisualizer?.let { entityVisualizer ->
                    formControl {
                        attrs.margin = FormControlMargin.dense

                        p {
                            +"Title: "
                            +entityVisualizer.title
                        }

                        header { +"Transformation" }
                        container(styles.transformEditBox on ContainerStyle.root) {
                            h5 { +"Translation:" }

                            fun update(value: Float, block: (Float) -> Vector3F) {
                                entityVisualizer.transformation = entityVisualizer.transformation.withTranslation(block(value))
                                this@xComponent.forceRender()
                            }
                            val translation = entityVisualizer.transformation.translation
                            translationTextField("X", translation.x) { update(it) { translation.copy(x = it) } }
                            translationTextField("Y", translation.y) { update(it) { translation.copy(y = it) } }
                            translationTextField("Z", translation.z) { update(it) { translation.copy(z = it) } }
                        }

                        container(styles.transformEditBox on ContainerStyle.root) {
                            h5 { +"Rotation" }

                            fun update(value: Double, block: (Double) -> EulerAngle) {
                                entityVisualizer.transformation = entityVisualizer.transformation.withRotation(block(value))
                                this@xComponent.forceRender()
                            }
                            val rotation = entityVisualizer.transformation.rotation
                            rotationTextField("Pitch", rotation.pitchRad) { update(it) { rotation.copy(pitchRad = it) } }
                            rotationTextField("Yaw", rotation.yawRad) { update(it) { rotation.copy(yawRad = it) } }
                            rotationTextField("Roll", rotation.rollRad) { update(it) { rotation.copy(rollRad = it) } }
                        }

                        container(styles.transformEditBox on ContainerStyle.root) {
                            h5 { +"Scale" }

                            fun update(value: Float, block: (Float) -> Vector3F) {
                                entityVisualizer.transformation = entityVisualizer.transformation.withScale(block(value))
                                this@xComponent.forceRender()
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
    }
}

private val Double.asDegrees: Number get() = (this / (2 * PI) * 360).roundToInt()
private val Double.fromDegrees: Double get() = this / 360 * (2 * PI)


external interface ModelEditorProps : Props {
    var scene: OpenScene
}

fun RBuilder.modelEditor(handler: RHandler<ModelEditorProps>) =
    child(ModelEditorView, handler = handler)