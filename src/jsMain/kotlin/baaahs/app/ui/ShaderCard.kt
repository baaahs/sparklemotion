package baaahs.app.ui

import baaahs.app.ui.editor.EditableStyles
import baaahs.show.mutable.MutableShaderInstance
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
import materialui.icons.Icons
import react.RBuilder
import react.RHandler
import react.RProps
import react.child

val ShaderInstanceCard = xComponent<ShaderInstanceCardProps>("ShaderCard") { props ->
    val styles = EditableStyles

    val mutableShaderInstance = props.mutableShaderInstance
    val shader = mutableShaderInstance.mutableShader.build()

    val handleCardClick = eventHandler("select", props.onSelect) { e ->
        props.onSelect()
        e.stopPropagation()
    }

    val handleDeleteClick = eventHandler("delete", props.onDelete) { e ->
        props.onDelete()
        e.stopPropagation()
    }

    card(+styles.shaderCard on PaperStyle.root) {
        key = mutableShaderInstance.id
        attrs.onClickFunction = handleCardClick

        cardHeader {
            attrs.avatar {
                avatar { icon(shader.type.icon) }
            }
            attrs.title { +shader.title }
//                                attrs.subheader { +"${shader.type.name} Shader" }
        }

        shaderPreview {
            attrs.shader = shader
            attrs.width = styles.cardWidth
            attrs.height = styles.cardWidth
        }

        cardActions(styles.shaderCardActions on CardActionsStyle.root) {
            typography(styles.shaderCardContent on TypographyStyle.root) {
                attrs.display = TypographyDisplay.block
                attrs.variant = TypographyVariant.body2
                attrs.color = TypographyColor.textSecondary
                +"${shader.type.name} Shader"
            }

            iconButton {
                attrs.onClickFunction = handleDeleteClick

                icon(Icons.Delete)
            }
        }
    }
}

external interface ShaderInstanceCardProps : RProps {
    var mutableShaderInstance: MutableShaderInstance
    var onSelect: () -> Unit
    var onDelete: () -> Unit
}

fun RBuilder.shaderCard(handler: RHandler<ShaderInstanceCardProps>) =
    child(ShaderInstanceCard, handler = handler)