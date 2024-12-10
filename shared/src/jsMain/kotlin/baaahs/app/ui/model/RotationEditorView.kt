package baaahs.app.ui.model

import baaahs.app.ui.editor.numberFieldEditor
import baaahs.geom.EulerAngle
import baaahs.ui.xComponent
import kotlinx.css.em
import kotlinx.css.fontSize
import react.Props
import react.RBuilder
import react.RHandler
import react.buildElement
import styled.css
import styled.styledSpan
import kotlin.math.PI
import kotlin.math.roundToInt

private val RotationEditorView = xComponent<RotationEditorProps>("RotationEditor", true) { props ->
    val updatePitch by handler(props.eulerAngle, props.onChange) { v: Double ->
        props.onChange(props.eulerAngle.copy(pitchRad = v.fromDegrees))
    }

    val updateYaw by handler(props.eulerAngle, props.onChange) { v: Double ->
        props.onChange(props.eulerAngle.copy(yawRad = v.fromDegrees))
    }

    val updateRoll by handler(props.eulerAngle, props.onChange) { v: Double ->
        props.onChange(props.eulerAngle.copy(rollRad = v.fromDegrees))
    }

    val degreeAdornment = memo {
        buildElement {
            styledSpan { css { fontSize = 1.5.em }; +Typography.degree.toString() }
        }
    }

    val rotation = props.eulerAngle
    numberFieldEditor<Double> {
        this.attrs.label = "Pitch"
        this.attrs.disabled = props.disabled == true
        this.attrs.adornment = degreeAdornment
        this.attrs.getValue = { rotation.pitchRad.asDegrees }
        this.attrs.setValue = updatePitch
    }
    numberFieldEditor<Double> {
        this.attrs.label = "Yaw"
        this.attrs.disabled = props.disabled == true
        this.attrs.adornment = degreeAdornment
        this.attrs.getValue = { rotation.yawRad.asDegrees }
        this.attrs.setValue = updateYaw
    }
    numberFieldEditor<Double> {
        this.attrs.label = "Roll"
        this.attrs.disabled = props.disabled == true
        this.attrs.adornment = degreeAdornment
        this.attrs.getValue = { rotation.rollRad.asDegrees }
        this.attrs.setValue = updateRoll
    }
}

val Number.asDegrees: Double get() = (this.toDouble() / (2 * PI) * 360 * 2).roundToInt() / 2.0
val Number.fromDegrees: Double get() = this.toDouble() / 360 * (2 * PI)

external interface RotationEditorProps : Props {
    var eulerAngle: EulerAngle
    var disabled: Boolean?
    var onChange: (EulerAngle) -> Unit
}

fun RBuilder.rotationEditor(handler: RHandler<RotationEditorProps>) =
    child(RotationEditorView, handler = handler)