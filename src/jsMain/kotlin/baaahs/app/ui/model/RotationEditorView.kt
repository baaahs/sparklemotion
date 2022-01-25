package baaahs.app.ui.model

import baaahs.app.ui.appContext
import baaahs.geom.EulerAngle
import baaahs.ui.xComponent
import react.*
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
        numberTextField("Pitch", rotation.pitchRad.asDegrees, { props.adornment?.let { child(it) } }, updatePitch)
        numberTextField("Yaw", rotation.yawRad.asDegrees, { props.adornment?.let { child(it) } }, updateYaw)
        numberTextField("Roll", rotation.rollRad.asDegrees, { props.adornment?.let { child(it) } }, updateRoll)
    }
}

private val Number.asDegrees: Double get() = (this.toDouble() / (2 * PI) * 360 * 2).roundToInt() / 2.0
private val Number.fromDegrees: Double get() = this.toDouble() / 360 * (2 * PI)

external interface RotationEditorProps : Props {
    var eulerAngle: EulerAngle
    var adornment: ReactElement?
    var onChange: (EulerAngle) -> Unit
}

fun RBuilder.rotationEditor(handler: RHandler<RotationEditorProps>) =
    child(RotationEditorView, handler = handler)