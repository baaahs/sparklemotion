package baaahs.ui

import kotlinx.html.InputType
import kotlinx.html.js.onChangeFunction
import kotlinx.html.js.onClickFunction
import materialui.components.button.button
import materialui.components.button.enums.ButtonColor
import materialui.components.dialog.dialog
import materialui.components.dialogactions.dialogActions
import materialui.components.dialogcontent.dialogContent
import materialui.components.dialogtitle.dialogTitle
import materialui.components.formcontrol.enums.FormControlMargin
import materialui.components.textfield.textField
import org.w3c.dom.HTMLInputElement
import react.RBuilder
import react.RHandler
import react.RProps
import react.child

val PromptDialog = xComponent<PromptDialogProps>("PromptDialog") { props ->
    val prompt = props.prompt
    var value = prompt.defaultValue ?: ""

    dialog {
        attrs.open = true
        attrs.onClose = { event, reason ->
            props.onClose()
            prompt.onCancel()
            event.stopPropagation()
        }

        dialogTitle { +prompt.title }

        dialogContent {
//            dialogContentText {
            +prompt.description
//            }
        }

        textField {
            attrs.autoFocus = true
            attrs.margin = FormControlMargin.dense
            attrs.label { +prompt.fieldLabel }
            attrs.type = InputType.text
            attrs.fullWidth = true
            attrs.onChangeFunction = { event ->
                value = (event.target as HTMLInputElement).value
            }
        }

        dialogActions {
            button {
                attrs.onClickFunction = { event ->
                    props.onClose()
                    prompt.onCancel()
                    event.stopPropagation()
                }
                attrs.color = ButtonColor.primary
                +(prompt.cancelButtonLabel ?: "Cancel")
            }
            button {
                attrs.onClickFunction = { event ->
                    props.onClose()
                    prompt.onSubmit(value)
                    event.stopPropagation()
                }
                attrs.color = ButtonColor.primary
                +(prompt.submitButtonLabel ?: "Submit")
            }
        }
    }
}

class Prompt(
    val title: String,
    val description: String,
    val defaultValue: String = "",
    val fieldLabel: String,
    val cancelButtonLabel: String?,
    val submitButtonLabel: String?,
    val onSubmit: (value: String) -> Unit,
    val onCancel: () -> Unit = {},
    val isValid: (String) -> Boolean = { true }
) {
}

external interface PromptDialogProps : RProps {
    var prompt: Prompt
    var onClose: () -> Unit
}

fun RBuilder.promptDialog(handler: RHandler<PromptDialogProps>) =
    child(PromptDialog, handler = handler)