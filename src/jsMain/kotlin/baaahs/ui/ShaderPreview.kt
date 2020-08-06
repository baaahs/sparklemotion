package baaahs.ui

import baaahs.Logger
import baaahs.app.ui.appContext
import baaahs.show.mutable.MutableShaderInstance
import kotlinx.css.LinearDimension
import materialui.Warning
import materialui.components.typography.typography
import materialui.icon
import react.*
import react.dom.div

val ShaderPreview = xComponent<ShaderPreviewProps>("ShaderPreview") { props ->
    val appContext = useContext(appContext)

    val patch = try {
        val shader = props.mutableShaderInstance.mutableShader.build()
        appContext.autoWirer
            .autoWire(shader)
            .resolve()
            .openForPreview(appContext.autoWirer, shader.type)
    } catch (e: Exception) {
        Logger("ShaderPreview").warn(e) { "Can't preview shader."}
        null
    }

    if (patch == null) {
        icon(Warning)
        typography { +"Preview failed." }
    } else {
        patchPreview {
            attrs.patch = patch
            attrs.width = props.width
            attrs.height = props.height
            attrs.onSuccess = {}
            attrs.onGadgetsChange = {}
            attrs.onError = {}
        }
    }
}

external interface ShaderPreviewProps : RProps {
    var mutableShaderInstance: MutableShaderInstance
    var width: LinearDimension?
    var height: LinearDimension?
}

fun RBuilder.shaderPreview(handler: RHandler<ShaderPreviewProps>) =
    child(ShaderPreview, handler = handler)