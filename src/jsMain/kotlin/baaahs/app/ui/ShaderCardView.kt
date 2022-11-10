package baaahs.app.ui

import baaahs.app.ui.editor.EditableStyles
import baaahs.gl.Toolchain
import baaahs.gl.openShader
import baaahs.gl.preview.GadgetAdjuster
import baaahs.gl.preview.ShaderBuilder
import baaahs.show.mutable.MutablePatch
import baaahs.ui.unaryMinus
import baaahs.ui.xComponent
import js.core.jso
import kotlinx.css.LinearDimension
import materialui.icon
import mui.material.*
import mui.material.styles.TypographyVariant
import mui.system.sx
import react.Props
import react.RBuilder
import react.RHandler
import react.buildElement
import web.cssom.Display
import web.cssom.MaxWidth
import web.cssom.important

private val ShaderCardView = xComponent<ShaderCardProps>("ShaderCard") { props ->
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
        key = mutablePatch.id
        attrs.classes = jso { this.root = -styles.shaderCard }
        attrs.sx {
            maxWidth = important("initial".unsafeCast<MaxWidth>())
        }

        CardActionArea {
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
            attrs.width = props.cardSize ?: styles.cardWidth
            attrs.height = props.cardSize ?: styles.cardWidth
            attrs.adjustGadgets = if (props.adjustGadgets != false) GadgetAdjuster.Mode.FULL_RANGE else null
            attrs.toolchain = props.toolchain
            attrs.onShaderStateChange = props.onShaderStateChange
        }

            CardActions {
                attrs.classes = jso { this.root = -styles.shaderCardActions }
                Typography {
                    attrs.classes = jso { this.root = -styles.shaderCardContent }
                    attrs.variant = TypographyVariant.body2
                    attrs.sx {
                        display = Display.block
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
}

external interface ShaderCardProps : Props {
    var mutablePatch: MutablePatch
    var onSelect: () -> Unit
    var onDelete: (() -> Unit)?
    var toolchain: Toolchain
    var cardSize: LinearDimension?
    var adjustGadgets: Boolean?
    var onShaderStateChange: ((ShaderBuilder.State) -> Unit)?
}

fun RBuilder.shaderCard(handler: RHandler<ShaderCardProps>) =
    child(ShaderCardView, handler = handler)