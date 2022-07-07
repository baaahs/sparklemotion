package baaahs.app.ui.controls

import baaahs.app.ui.shaderPreview
import baaahs.show.Shader
import baaahs.ui.asTextNode
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import mui.material.Dialog
import mui.material.Tab
import mui.material.Tabs
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.div
import react.dom.header

private val PatchModView = xComponent<PatchModProps>("PatchMod") { props ->



    Dialog {
        attrs.open = true
        attrs.onClose = { _, _ -> props.onClose() }

        header { +props.title }
        div(+Styles.lightboxShaderPreviewContainer) {
            shaderPreview {
                attrs.shader = props.shader
                attrs.noSharedGlContext = true
            }
        }

        Tabs {
            Tab {
                attrs.disabled = true
                attrs.label = "Position/Scale".asTextNode()
            }
            Tab {
                attrs.disabled = true
                attrs.label = "Rotation".asTextNode()
            }
            Tab {
                attrs.disabled = true
                attrs.label = "Color".asTextNode()
            }
        }
    }
}

external interface PatchModProps : Props {
    var title: String
    var shader: Shader
    var onClose: () -> Unit
}

fun RBuilder.patchMod(handler: RHandler<PatchModProps>) =
    child(PatchModView, handler = handler)