package baaahs.app.ui

import baaahs.gl.preview.ShaderBuilder
import baaahs.ui.muiClasses
import baaahs.ui.unaryMinus
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import mui.material.Divider
import mui.material.Popover
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.code
import react.dom.div
import react.dom.header
import react.dom.pre
import web.dom.Element
import web.events.Event

private val ShaderDiagnostics = xComponent<ShaderDiagnosticsProps>("ShaderDiagnostics") { props ->

    val glslErrors = props.builder.glslErrors
    val linkedPatch = props.builder.linkedProgram

    Popover {
        attrs.classes = muiClasses { paper = -ShaderPreviewStyles.errorPopup }
        attrs.open = props.anchor != null
        props.anchor?.let { anchor -> attrs.anchorEl = anchor }
        attrs.onClose = { event, _ ->
            props.onClose()
            (event as Event).stopPropagation()
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

                Divider {}

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
    var anchor: Element?
    var builder: ShaderBuilder
    var onClose: () -> Unit
}

fun RBuilder.shaderDiagnostics(handler: RHandler<ShaderDiagnosticsProps>) =
    child(ShaderDiagnostics, handler = handler)