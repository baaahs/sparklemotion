package baaahs.ui

import mui.material.*
import org.w3c.dom.events.Event
import react.Props
import react.RBuilder
import react.RHandler
import react.buildElement
import react.dom.events.ChangeEvent
import react.dom.events.KeyboardEvent
import react.dom.events.MouseEvent
import react.dom.html.InputType
import react.dom.onChange

val PromptDialog = xComponent<PromptDialogProps>("PromptDialog") { props ->
    val prompt = props.prompt
    val value = ref(prompt.defaultValue)
    var isInvalidMessage by state { prompt.isValid(value.current!!) }

    val handleChange by changeEventHandler(prompt) { event: ChangeEvent<*> ->
        val newValue = event.target.value
        value.current = newValue
        isInvalidMessage = prompt.isValid(newValue)
    }

    val handleDialogClose by handler(props.onClose, prompt.onCancel) { event: Event, _: String ->
        props.onClose()
        prompt.onCancel()
        event.stopPropagation()
    }
    val handleCancelClick by mouseEventHandler(props.onClose, prompt.onCancel) { event: Event ->
        props.onClose()
        prompt.onCancel()
        event.stopPropagation()
    }
    val handleSubmitClick by mouseEventHandler(props.onClose, prompt.onSubmit) { event: Event ->
        props.onClose()
        prompt.onSubmit(value.current!!)
        event.stopPropagation()
    }

    val handleKeyUp by keyboardEventHandler(prompt) { event: KeyboardEvent<*> ->
        if (event.asDynamic().key == "Enter" && isInvalidMessage == null) {
            handleSubmitClick(event.unsafeCast<MouseEvent<*, *>>())
        }
    }

    Dialog {
        attrs.open = true
        attrs.onClose = handleDialogClose

        DialogTitle { +prompt.title }
        DialogContent {
            +prompt.description

            TextField {
                attrs.autoFocus = true
                attrs.margin = FormControlMargin.dense
                attrs.label = buildElement { +prompt.fieldLabel }
                attrs.type = InputType.text
                attrs.fullWidth = true
                attrs.defaultValue(value.current!!)
                isInvalidMessage?.let {
                    attrs.error = true
                    attrs.helperText = it.asTextNode()
                }
                attrs.onKeyUp = handleKeyUp
                attrs.onChange = handleChange.withFormEvent()
            }
        }

        DialogActions {
            Button {
                attrs.color = ButtonColor.primary
                attrs.onClick = handleCancelClick
                +(prompt.cancelButtonLabel ?: "Cancel")
            }
            Button {
                attrs.color = ButtonColor.primary
                attrs.disabled = isInvalidMessage != null
                attrs.onClick = handleSubmitClick
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

external interface PromptDialogProps : Props {
    var prompt: Prompt
    var onClose: () -> Unit
}

fun RBuilder.promptDialog(handler: RHandler<PromptDialogProps>) =
    child(PromptDialog, handler = handler)