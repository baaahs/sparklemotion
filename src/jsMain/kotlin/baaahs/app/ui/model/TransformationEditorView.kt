package baaahs.app.ui.model

import baaahs.app.ui.appContext
import baaahs.geom.EulerAngle
import baaahs.geom.Vector3F
import baaahs.geom.toEulerAngle
import baaahs.geom.toThreeEuler
import baaahs.scene.EditingEntity
import baaahs.ui.on
import baaahs.ui.unaryPlus
import baaahs.ui.value
import baaahs.ui.xComponent
import baaahs.visualizer.toVector3
import kotlinx.html.InputType
import kotlinx.html.js.onChangeFunction
import kotlinx.html.unsafe
import materialui.components.container.container
import materialui.components.container.enums.ContainerStyle
import materialui.components.input.enums.InputStyle
import materialui.components.input.input
import materialui.components.inputadornment.enums.InputAdornmentPosition
import materialui.components.textfield.enums.TextFieldSize
import materialui.components.textfield.textField
import react.*
import react.dom.header
import react.dom.span
import three_ext.toVector3F
import kotlin.math.PI
import kotlin.math.roundToInt

private val TransformationEditorView = xComponent<TransformationEditorProps>("TransformationEditor") { props ->
    val appContext = useContext(appContext)
    val styles = appContext.allStyles.modelEditor

    observe(props.editingEntity)
    val mutableEntity = props.editingEntity.mutableEntity
    val entityVisualizer = props.editingEntity.entityVisualizer

    fun RBuilder.numberTextField(label: String, value: Float, adornment: RBuilder.() -> Unit, onChange: (Float) -> Unit) {
        textField {
            attrs.type = InputType.number
            attrs.size = TextFieldSize.small
            attrs.InputProps = buildElement {
                input(+styles.partialUnderline on InputStyle.underline) {
                    attrs.endAdornment {
                        attrs.position = InputAdornmentPosition.end
                        adornment()
                    }
                }
            }.props.unsafeCast<PropsWithChildren>()
            attrs.inputLabelProps { attrs.shrink = true }
            attrs.onChangeFunction = { onChange(it.currentTarget.value.toFloat()) }
            attrs.value(value)
            attrs.label { +label }
        }
    }

    fun RBuilder.translationTextField(label: String, value: Float, onChange: (Float) -> Unit) {
        numberTextField(label, value, { +props.editingEntity.modelUnit.display }, onChange)
    }

    fun RBuilder.scaleTextField(label: String, value: Float, onChange: (Float) -> Unit) {
        numberTextField(label, value, { span { attrs.unsafe { +"&#x2715;" } } }, onChange)
    }

    fun RBuilder.rotationTextField(label: String, value: Double, onChange: (Double) -> Unit) {
        textField {
            attrs.type = InputType.number
            attrs.size = TextFieldSize.small
            attrs.InputProps = buildElement {
                input(+styles.partialUnderline on InputStyle.underline) {
                    attrs.endAdornment {
                        attrs.position = InputAdornmentPosition.end
                        +"°"
                    }
                }
            }.props.unsafeCast<PropsWithChildren>()
            attrs.inputLabelProps { attrs.shrink = true }
            attrs.onChangeFunction = { onChange(it.currentTarget.value.toDouble().fromDegrees) }
            attrs.value(value.asDegrees)
            attrs.label { +label }
        }
    }


    header { +"Transformation" }
    container(styles.transformEditSection on ContainerStyle.root) {
        header { +"Position:" }

        fun update(value: Float, block: (Float) -> Vector3F) {
            val actual = block(value)
            entityVisualizer.obj.position.copy(actual.toVector3())
            mutableEntity.position = actual
            props.editingEntity.onChange()
        }
        val position = entityVisualizer.obj.position.toVector3F()
        translationTextField("X", position.x) { update(it) { v -> position.copy(x = v) } }
        translationTextField("Y", position.y) { update(it) { v -> position.copy(y = v) } }
        translationTextField("Z", position.z) { update(it) { v -> position.copy(z = v) } }
    }

    container(styles.transformEditSection on ContainerStyle.root) {
        header { +"Rotation:" }

        fun update(value: Double, block: (Double) -> EulerAngle) {
            val actual = block(value)
            entityVisualizer.obj.rotation.copy(actual.toThreeEuler())
            mutableEntity.rotation = actual
            props.editingEntity.onChange()
        }
        val rotation = entityVisualizer.obj.rotation.toEulerAngle()
        rotationTextField("Pitch", rotation.pitchRad) { update(it) { v -> rotation.copy(pitchRad = v) } }
        rotationTextField("Yaw", rotation.yawRad) { update(it) { v -> rotation.copy(yawRad = v) } }
        rotationTextField("Roll", rotation.rollRad) { update(it) { v -> rotation.copy(rollRad = v) } }
    }

    container(styles.transformEditSection on ContainerStyle.root) {
        header { +"Scale:" }

        fun update(value: Float, block: (Float) -> Vector3F) {
            val actual = block(value)
            entityVisualizer.obj.scale.copy(actual.toVector3())
            props.editingEntity.onChange()
        }
        val scale = entityVisualizer.obj.scale.toVector3F()
        scaleTextField("X", scale.x) { update(it) { v -> scale.copy(x = v) } }
        scaleTextField("Y", scale.y) { update(it) { v -> scale.copy(y = v) } }
        scaleTextField("Z", scale.z) { update(it) { v -> scale.copy(z = v) } }
    }
}

private val Double.asDegrees: Number get() = (this / (2 * PI) * 360).roundToInt()
private val Double.fromDegrees: Double get() = this / 360 * (2 * PI)

external interface TransformationEditorProps : Props {
    var editingEntity: EditingEntity<*>
}

fun RBuilder.transformationEditor(handler: RHandler<TransformationEditorProps>) =
    child(TransformationEditorView, handler = handler)