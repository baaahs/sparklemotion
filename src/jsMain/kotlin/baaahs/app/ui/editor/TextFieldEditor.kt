package baaahs.app.ui.editor

import baaahs.ui.value
import baaahs.ui.xComponent
import kotlinx.html.js.onBlurFunction
import kotlinx.html.js.onChangeFunction
import materialui.components.formcontrol.formControl
import materialui.components.formhelpertext.formHelperText
import materialui.components.textfield.textField
import org.w3c.dom.events.Event
import react.RBuilder
import react.RHandler
import react.RProps
import react.child

val TextFieldEditor = xComponent<TextFieldEditorProps>("TextFieldEditor") { props ->
    val valueOnUndoStack = ref { props.getValue() }

    formControl {
        textField {
            attrs.autoFocus = false
            attrs.fullWidth = true
            attrs.value = props.getValue()

            // Notify EditableManager of changes as we type, but don't push them to the undo stack...
            attrs.onChangeFunction = { event: Event ->
                props.setValue(event.target.value)
                props.editableManager.onChange(false)
            }

            // ... until we lose focus; then, push to the undo stack only if the value would change.
            attrs.onBlurFunction = { event: Event ->
                val newValue = event.target.value
                if (newValue != valueOnUndoStack.current) {
                    valueOnUndoStack.current = newValue
                    props.editableManager.onChange()
                }
            }
        }
        formHelperText { +props.label }
    }
}

external interface TextFieldEditorProps : RProps {
    var label: String
    var getValue: () -> String
    var setValue: (String) -> Unit
    var editableManager: EditableManager
}

fun RBuilder.textFieldEditor(handler: RHandler<TextFieldEditorProps>) =
    child(TextFieldEditor, handler = handler)