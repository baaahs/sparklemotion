package baaahs.app.ui.model

import baaahs.app.ui.appContext
import baaahs.geom.Vector3F
import baaahs.ui.xComponent
import react.*

private val VectorEditorView = xComponent<VectorEditorProps>("VectorEditor", true) { props ->
    val appContext = useContext(appContext)
    val styles = appContext.allStyles.modelEditor

    val updateX by handler(props.vector3F, props.onChange) { v: Float ->
        props.onChange(props.vector3F.copy(x = v))
    }

    val updateY by handler(props.vector3F, props.onChange) { v: Float ->
        props.onChange(props.vector3F.copy(y = v))
    }

    val updateZ by handler(props.vector3F, props.onChange) { v: Float ->
        props.onChange(props.vector3F.copy(z = v))
    }

    val vector = props.vector3F
    with(styles) {
        numberTextField<Float> {
            this.attrs.label = "X"
            this.attrs.value = vector.x
            this.attrs.adornment = props.adornment
            this.attrs.onChange = updateX
        }
        numberTextField<Float> {
            this.attrs.label = "Y"
            this.attrs.value = vector.y
            this.attrs.adornment = props.adornment
            this.attrs.onChange = updateY
        }
        numberTextField<Float> {
            this.attrs.label = "Z"
            this.attrs.value = vector.z
            this.attrs.adornment = props.adornment
            this.attrs.onChange = updateZ
        }
    }
}

external interface VectorEditorProps : Props {
    var vector3F: Vector3F
    var adornment: ReactElement<*>?
    var onChange: (Vector3F) -> Unit
}

fun RBuilder.vectorEditor(handler: RHandler<VectorEditorProps>) =
    child(VectorEditorView, handler = handler)