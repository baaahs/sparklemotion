package baaahs.app.ui

import baaahs.app.ui.editor.EditableStyles
import baaahs.gl.Toolchain
import baaahs.gl.openShader
import baaahs.gl.preview.GadgetAdjuster
import baaahs.show.mutable.MutablePatch
import baaahs.ui.on
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import kotlinx.html.js.onClickFunction
import materialui.components.avatar.avatar
import materialui.components.card.card
import materialui.components.cardactions.cardActions
import materialui.components.cardactions.enums.CardActionsStyle
import materialui.components.cardheader.cardHeader
import materialui.components.iconbutton.iconButton
import materialui.components.paper.enums.PaperStyle
import materialui.components.typography.enums.TypographyColor
import materialui.components.typography.enums.TypographyDisplay
import materialui.components.typography.enums.TypographyStyle
import materialui.components.typography.enums.TypographyVariant
import materialui.components.typography.typography
import materialui.icon
import react.Props
import react.RBuilder
import react.RHandler

val ShaderCard = xComponent<ShaderCardProps>("ShaderCard") { props ->
    val styles = EditableStyles

    val mutablePatch = props.mutablePatch
    val shader = mutablePatch.mutableShader.build()
    val openShader = props.toolchain.openShader(shader)

    val handleCardClick by eventHandler(props.onSelect) { e ->
        props.onSelect()
        e.stopPropagation()
    }

    val handleDeleteClick by eventHandler(props.onDelete) { e ->
        props.onDelete?.invoke()
        e.stopPropagation()
    }

    card(+styles.shaderCard on PaperStyle.root) {
        key = mutablePatch.id
        attrs.onClickFunction = handleCardClick

        cardHeader {
            attrs.avatar {
                avatar { icon(openShader.shaderType.icon) }
            }
            attrs.title { +shader.title }
//                                attrs.subheader { +"${shader.type.name} Shader" }
        }

        shaderPreview {
            attrs.shader = shader
            attrs.width = styles.cardWidth
            attrs.height = styles.cardWidth
            attrs.adjustGadgets = GadgetAdjuster.Mode.FULL_RANGE
            attrs.toolchain = props.toolchain
        }

        cardActions(styles.shaderCardActions on CardActionsStyle.root) {
            typography(styles.shaderCardContent on TypographyStyle.root) {
                attrs.display = TypographyDisplay.block
                attrs.variant = TypographyVariant.body2
                attrs.color = TypographyColor.textSecondary
                +"${openShader.shaderType.title} Shader"
            }

            if (props.onDelete != null) {
                iconButton {
                    attrs.onClickFunction = handleDeleteClick

                    icon(materialui.icons.Delete)
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