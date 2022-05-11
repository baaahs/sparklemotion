package baaahs.app.ui

import baaahs.app.ui.editor.EditableStyles
import baaahs.gl.Toolchain
import baaahs.gl.openShader
import baaahs.gl.preview.GadgetAdjuster
import baaahs.show.mutable.MutablePatch
import baaahs.ui.unaryMinus
import baaahs.ui.xComponent
import kotlinx.js.jso
import materialui.icon
import mui.material.*
import mui.material.styles.TypographyVariant
import mui.system.sx
import react.Props
import react.RBuilder
import react.RHandler
import react.buildElement

val ShaderCard = xComponent<ShaderCardProps>("ShaderCard") { props ->
    val styles = EditableStyles

    val mutablePatch = props.mutablePatch
    val shader = mutablePatch.mutableShader.build()
    val openShader = props.toolchain.openShader(shader)

    val handleCardClick by mouseEventHandler(props.onSelect) { e ->
        props.onSelect()
        e.stopPropagation()
    }

    val handleDeleteClick by mouseEventHandler(props.onDelete) { e ->
        props.onDelete?.invoke()
        e.stopPropagation()
    }

    Card {
        attrs.classes = jso { this.root = -styles.shaderCard }

        key = mutablePatch.id
        attrs.onClick = handleCardClick

        CardHeader {
            attrs.avatar = buildElement {
                Avatar { icon(openShader.shaderType.icon) }
            }
            attrs.title = buildElement { +shader.title }
//                                attrs.subheader { +"${shader.type.name} Shader" }
        }

        shaderPreview {
            attrs.shader = shader
            attrs.width = styles.cardWidth
            attrs.height = styles.cardWidth
            attrs.adjustGadgets = GadgetAdjuster.Mode.FULL_RANGE
            attrs.toolchain = props.toolchain
        }

        CardActions {
            attrs.classes = jso { this.root = -styles.shaderCardActions }
            Typography {
                attrs.classes = jso { this.root = -styles.shaderCardContent }
                attrs.variant = TypographyVariant.body2
                attrs.sx {
                    display = csstype.Display.block
                    color = Colors.secondary
                }

                +"${openShader.shaderType.title} Shader"
            }

            if (props.onDelete != null) {
                IconButton {
                    attrs.onClick = handleDeleteClick

                    icon(mui.icons.material.Delete)
                }
            }
        }
    }
}

external interface ShaderCardProps : Props {
    var mutablePatch: MutablePatch
    var onSelect: () -> Unit
    var onDelete: (() -> Unit)?
    var toolchain: Toolchain
}

fun RBuilder.shaderCard(handler: RHandler<ShaderCardProps>) =
    child(ShaderCard, handler = handler)