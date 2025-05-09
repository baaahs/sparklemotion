package baaahs.app.ui

import baaahs.app.ui.editor.EditableStyles
import baaahs.gl.Toolchain
import baaahs.gl.openShader
import baaahs.gl.preview.GadgetAdjuster
import baaahs.gl.preview.ShaderBuilder
import baaahs.show.mutable.MutablePatch
import baaahs.ui.fitText
import baaahs.ui.unaryMinus
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import baaahs.util.useResizeListener
import kotlinx.css.LinearDimension
import materialui.icon
import mui.material.*
import mui.material.styles.TypographyVariant
import mui.system.sx
import react.Props
import react.RBuilder
import react.RHandler
import react.buildElement
import react.dom.div
import web.cssom.Display
import web.cssom.MaxWidth
import web.cssom.important
import web.html.HTMLDivElement

private val ShaderCardView = xComponent<ShaderCardProps>("ShaderCard") { props ->
    val styles = EditableStyles

    val mutablePatch = props.mutablePatch
    val shader = mutablePatch.mutableShader.build()
    val openShader = props.toolchain.openShader(shader)
    val dense = props.dense == true

    val handleCardClick by mouseEventHandler(props.onSelect) { e ->
        props.onSelect()
        e.stopPropagation()
    }

    val handleDeleteClick by mouseEventHandler(props.onDelete) { e ->
        props.onDelete?.invoke()
        e.stopPropagation()
    }

    val titleDivRef = ref<HTMLDivElement>()
    useResizeListener(titleDivRef) { _, _ ->
        titleDivRef.current?.fitText()
    }

    Card {
        key = mutablePatch.id
        attrs.className = -styles.shaderCard
        attrs.sx {
            maxWidth = important("initial".unsafeCast<MaxWidth>())
        }

        CardActionArea {
            attrs.onClick = handleCardClick

            if (!dense) {
                CardHeader {
                    attrs.avatar = buildElement {
                        Avatar { icon(openShader.shaderType.icon) }
                    }
                    attrs.title = buildElement { +shader.title }
                    props.subtitle?.let {
                        attrs.subheader = buildElement { +it }
                    }
                }
            }

            shaderPreview {
                attrs.shader = shader
                attrs.width = props.cardSize ?: styles.cardWidth
                attrs.height = props.cardSize ?: styles.cardWidth
                attrs.adjustGadgets = if (props.adjustGadgets != false) GadgetAdjuster.Mode.FULL_RANGE else null
                attrs.toolchain = props.toolchain
                attrs.onShaderStateChange = props.onShaderStateChange
            }

            if (dense) {
                div(+styles.shaderCardDenseText) {
                    ref = titleDivRef
                    +shader.title
                }
            }

            if (!dense) {
                CardActions {
                    attrs.className = -styles.shaderCardActions
                    Typography {
                        attrs.className = -styles.shaderCardContent
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
}

external interface ShaderCardProps : Props {
    var mutablePatch: MutablePatch
    var subtitle: String?
    var toolchain: Toolchain
    var cardSize: LinearDimension?
    var dense: Boolean?
    var adjustGadgets: Boolean?
    var onSelect: () -> Unit
    var onDelete: (() -> Unit)?
    var onShaderStateChange: ((ShaderBuilder.State) -> Unit)?
}

fun RBuilder.shaderCard(handler: RHandler<ShaderCardProps>) =
    child(ShaderCardView, handler = handler)