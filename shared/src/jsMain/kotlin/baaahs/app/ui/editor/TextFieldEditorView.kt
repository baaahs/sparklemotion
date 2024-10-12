package baaahs.app.ui.editor

import baaahs.ui.value
import baaahs.ui.xComponent
import mui.material.FormControl
import mui.material.FormHelperText
import mui.material.Size
import mui.material.TextField
import react.Props
import react.RBuilder
import react.RHandler
import react.buildElement
import react.dom.events.FocusEvent
import react.dom.events.FormEvent
import react.dom.events.KeyboardEvent
import react.dom.onChange
import web.html.InputType

private val TextFieldEditor = xComponent<TextFieldEditorProps>("TextFieldEditor") { props ->
    val valueOnUndoStack = ref(props.getValue())

    val notifyOfChange by handler(props.onChange, props.editableManager) { pushToUndoStack: Boolean ->
        val onChange = props.onChange
        val editableManager = props.editableManager
        if (onChange != null) {
            onChange(pushToUndoStack)
        } else if (editableManager != null) {
            editableManager.onChange(pushToUndoStack)
        } else error("TextFieldEditor needs either onChange or editableManager.")
    }

    val handleChange by formEventHandler(props.setValue, notifyOfChange) { event: FormEvent<*> ->
        props.setValue(event.target.value)
        notifyOfChange(false)
    }

    val handleBlur by focusEventHandler(notifyOfChange) { event: FocusEvent<*> ->
        val newValue = event.target.value
        if (newValue != valueOnUndoStack.current) {
            valueOnUndoStack.current = newValue
            notifyOfChange(true)
        }
    }

    val handleKeyDown by keyboardEventHandler(handleBlur) { event: KeyboardEvent<*> ->
        if (event.asDynamic().keyCode == 13) {
            handleBlur(event as FocusEvent<*>)
        }
    }

    FormControl {
        TextField {
            attrs.type = props.type
            attrs.autoFocus = props.autoFocus == true
            attrs.fullWidth = (props.fullWidth ?: true) == true
            attrs.size = Size.small
            attrs.disabled = props.disabled == true
            attrs.label = buildElement { +props.label }
            attrs.value = props.getValue()

            // Notify EditableManager of changes as we type, but don't push them to the undo stack...
            attrs.onChange = handleChange

            // ... until we lose focus or hit return; then, push to the undo stack only if the value would change.
            attrs.onBlur = handleBlur
            attrs.onKeyDown = handleKeyDown
        }

        props.helperText?.let { helperText ->
            FormHelperText { +helperText }
        }
    }
}

external interface TextFieldEditorProps : Props {
    var type: InputType
    var label: String
    var helperText: String?
    var autoFocus: Boolean?
    var disabled: Boolean?
    var fullWidth: Boolean?
    var getValue: () -> String
    var setValue: (String) -> Unit
    var editableManager: EditableManager<*>?
    var onChange: ((pushToUndoStack: Boolean) -> Unit)?
}

fun RBuilder.textFieldEditor(handler: RHandler<TextFieldEditorProps>) =
    child(TextFieldEditor, handler = handler)