package baaahs.ui

import kotlinx.html.InputType
import kotlinx.html.js.onChangeFunction
import kotlinx.html.js.onClickFunction
import kotlinx.html.js.onKeyUpFunction
import materialui.components.button.button
import materialui.components.button.enums.ButtonColor
import materialui.components.dialog.dialog
import materialui.components.dialogactions.dialogActions
import materialui.components.dialogcontent.dialogContent
import materialui.components.dialogtitle.dialogTitle
import materialui.components.formcontrol.enums.FormControlMargin
import materialui.components.textfield.textField
import org.w3c.dom.events.Event
import react.RBuilder
import react.RHandler
import react.RProps
import react.child

val PromptDialog = xComponent<PromptDialogProps>("PromptDialog") { props ->
    val prompt = props.prompt
    val value = ref { prompt.defaultValue }
    var isInvalidMessage by state { prompt.isValid(value.current) }

    val handleChange by eventHandler(prompt) { event: Event ->
        val newValue = event.target.value
        value.current = newValue
        isInvalidMessage = prompt.isValid(newValue)
    }

    val handleDialogClose by handler(props.onClose, prompt.onCancel) { event: Event, _: String ->
        props.onClose()
        prompt.onCancel()
        event.stopPropagation()
    }
    val handleCancelClick by eventHandler(props.onClose, prompt.onCancel) { event: Event ->
        props.onClose()
        prompt.onCancel()
        event.stopPropagation()
    }
    val handleSubmitClick by eventHandler(props.onClose, prompt.onSubmit) { event: Event ->
        props.onClose()
        prompt.onSubmit(value.current)
        event.stopPropagation()
    }

    val handleKeyUp by eventHandler(prompt) { event: Event ->
        if (event.asDynamic().key == "Enter" && isInvalidMessage == null) {
            handleSubmitClick(event)
        }
    }

    dialog {
        attrs.open = true
        attrs.onClose = handleDialogClose

        dialogTitle { +prompt.title }
        dialogContent {
            +prompt.description

            textField {
                attrs.autoFocus = true
                attrs.margin = FormControlMargin.dense
                attrs.label { +prompt.fieldLabel }
                attrs.type = InputType.text
                attrs.fullWidth = true
                attrs.defaultValue(value.current)
                isInvalidMessage?.let {
                    attrs.error = true
                    attrs.helperText = it.asTextNode()
                }
                attrs.onKeyUpFunction = handleKeyUp
                attrs.onChangeFunction = handleChange
            }
        }

        dialogActions {
            button {
                attrs.color = ButtonColor.primary
                attrs.onClickFunction = handleCancelClick
                +(prompt.cancelButtonLabel ?: "Cancel")
            }
            button {
                attrs.color = ButtonColor.primary
                attrs.disabled = isInvalidMessage != null
                attrs.onClickFunction = handleSubmitClick
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
    val isValid: (String) -> String? = { null },
    val onSubmit: (value: String) -> Unit,
    val onCancel: () -> Unit = {}
)

external interface PromptDialogProps : RProps {
    var prompt: Prompt
    var onClose: () -> Unit
}

fun RBuilder.promptDialog(handler: RHandler<PromptDialogProps>) =
    child(PromptDialog, handler = handler)