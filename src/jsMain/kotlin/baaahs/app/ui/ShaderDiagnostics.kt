package baaahs.app.ui

import baaahs.gl.preview.ShaderBuilder
import baaahs.ui.on
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import materialui.components.divider.divider
import materialui.components.popover.enums.PopoverStyle
import materialui.components.popover.popover
import org.w3c.dom.events.EventTarget
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.code
import react.dom.div
import react.dom.header
import react.dom.pre

val ShaderDiagnostics = xComponent<ShaderDiagnosticsProps>("ShaderDiagnostics") { props ->

    val glslErrors = props.builder.glslErrors
    val linkedPatch = props.builder.linkedProgram

    popover(ShaderPreviewStyles.errorPopup on PopoverStyle.paper) {
        attrs.open = props.anchor != null
        attrs.anchorEl(props.anchor)
        attrs.onClose = { event, _ ->
            props.onClose()
            event.stopPropagation()
        }

        header { +"Errors:" }

        div {
            if (props.anchor != null) {
                pre(+ShaderPreviewStyles.errorMessage) {
                    if (glslErrors.isNotEmpty()) {
                        +glslErrors.joinToString("\n")
                    } else {
                        +"No errors."
                    }
                }

                divider {}

                pre(+ShaderPreviewStyles.errorSourceCode) {
                    (linkedPatch?.toFullGlsl("x") ?: "No source!?")
                        .split("\n")
                        .forEach { code { +it }; +"\n" }
                }
            }
        }
    }
}

external interface ShaderDiagnosticsProps : Props {
    var anchor: EventTarget?
    var builder: ShaderBuilder
    var onClose: () -> Unit
}

fun RBuilder.shaderDiagnostics(handler: RHandler<ShaderDiagnosticsProps>) =
    child(ShaderDiagnostics, handler = handler)