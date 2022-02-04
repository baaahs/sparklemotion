package baaahs.app.ui.model

import baaahs.app.ui.appContext
import baaahs.geom.EulerAngle
import baaahs.ui.xComponent
import react.Props
import react.RBuilder
import react.RHandler
import react.useContext
import kotlin.math.PI
import kotlin.math.roundToInt

private val RotationEditorView = xComponent<RotationEditorProps>("RotationEditor", true) { props ->
    val appContext = useContext(appContext)
    val styles = appContext.allStyles.modelEditor

    val updatePitch by handler(props.eulerAngle, props.onChange) { v: Double ->
        props.onChange(props.eulerAngle.copy(pitchRad = v.fromDegrees))
    }

    val updateYaw by handler(props.eulerAngle, props.onChange) { v: Double ->
        props.onChange(props.eulerAngle.copy(yawRad = v.fromDegrees))
    }

    val updateRoll by handler(props.eulerAngle, props.onChange) { v: Double ->
        props.onChange(props.eulerAngle.copy(rollRad = v.fromDegrees))
    }

    val rotation = props.eulerAngle
    with(styles) {
        numberTextField("Pitch", rotation.pitchRad.asDegrees, { +"°" }, updatePitch)
        numberTextField("Yaw", rotation.yawRad.asDegrees, { +"°" }, updateYaw)
        numberTextField("Roll", rotation.rollRad.asDegrees, { +"°" }, updateRoll)
    }
}

val Number.asDegrees: Double get() = (this.toDouble() / (2 * PI) * 360 * 2).roundToInt() / 2.0
val Number.fromDegrees: Double get() = this.toDouble() / 360 * (2 * PI)

external interface RotationEditorProps : Props {
    var eulerAngle: EulerAngle
    var onChange: (EulerAngle) -> Unit
}

fun RBuilder.rotationEditor(handler: RHandler<RotationEditorProps>) =
    child(RotationEditorView, handler = handler)