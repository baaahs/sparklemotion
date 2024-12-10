package baaahs.app.ui.model

import baaahs.app.ui.editor.numberFieldEditor
import baaahs.geom.Vector3F
import baaahs.ui.xComponent
import react.Props
import react.RBuilder
import react.RHandler
import react.ReactElement

private val VectorEditorView = xComponent<VectorEditorProps>("VectorEditor", true) { props ->
    val getX by handler(props.vector3F) { props.vector3F.x}
    val getY by handler(props.vector3F) { props.vector3F.y}
    val getZ by handler(props.vector3F) { props.vector3F.z}

    val updateX by handler(props.vector3F, props.onChange) { v: Float ->
        props.onChange(props.vector3F.copy(x = v))
    }

    val updateY by handler(props.vector3F, props.onChange) { v: Float ->
        props.onChange(props.vector3F.copy(y = v))
    }

    val updateZ by handler(props.vector3F, props.onChange) { v: Float ->
        props.onChange(props.vector3F.copy(z = v))
    }

    val handleChange by handler { _: Boolean -> }

    numberFieldEditor<Float> {
        this.attrs.label = "X"
        this.attrs.disabled = props.disabled == true
        this.attrs.adornment = props.adornment
        this.attrs.getValue = getX
        this.attrs.setValue = updateX
        this.attrs.onChange = handleChange
    }
    numberFieldEditor<Float> {
        this.attrs.label = "Y"
        this.attrs.disabled = props.disabled == true
        this.attrs.adornment = props.adornment
        this.attrs.getValue = getY
        this.attrs.setValue = updateY
        this.attrs.onChange = handleChange
    }
    numberFieldEditor<Float> {
        this.attrs.label = "Z"
        this.attrs.disabled = props.disabled == true
        this.attrs.getValue = getZ
        this.attrs.setValue = updateZ
        this.attrs.adornment = props.adornment
        this.attrs.onChange = handleChange
    }
}

external interface VectorEditorProps : Props {
    var vector3F: Vector3F
    var adornment: ReactElement<*>?
    var disabled: Boolean?
    var onChange: (Vector3F) -> Unit
}

fun RBuilder.vectorEditor(handler: RHandler<VectorEditorProps>) =
    child(VectorEditorView, handler = handler)