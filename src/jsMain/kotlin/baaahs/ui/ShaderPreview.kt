package baaahs.ui

import baaahs.Logger
import baaahs.app.ui.appContext
import baaahs.show.mutable.MutableShaderInstance
import kotlinx.css.*
import materialui.Warning
import materialui.components.typography.enums.TypographyDisplay
import materialui.components.typography.typography
import materialui.icon
import react.*
import react.dom.div
import styled.StyleSheet

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
        div(+ShaderPreviewStyles.errorBox) {
            icon(Warning)
            typography {
                attrs.display = TypographyDisplay.block
                +"Preview failed."
            }
        }
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

object ShaderPreviewStyles : StyleSheet("ui-ShaderPreview", isStatic = true) {
    val errorBox by css {
        textAlign = TextAlign.center
        backgroundColor = StuffThatShouldComeFromTheTheme.lightBackgroundColor
        padding(1.em)
    }
}

external interface ShaderPreviewProps : RProps {
    var mutableShaderInstance: MutableShaderInstance
    var width: LinearDimension?
    var height: LinearDimension?
}

fun RBuilder.shaderPreview(handler: RHandler<ShaderPreviewProps>) =
    child(ShaderPreview, handler = handler)