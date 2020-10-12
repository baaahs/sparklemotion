package baaahs.app.ui.editor

import baaahs.ui.value
import baaahs.ui.xComponent
import kotlinx.html.js.onBlurFunction
import kotlinx.html.js.onChangeFunction
import kotlinx.html.js.onKeyDownFunction
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

    val handleChange = handler("on change", props.setValue, props.editableManager) { event: Event ->
        props.setValue(event.target.value)
        props.editableManager.onChange(false)
    }

    val handleBlur = handler("on blur", props.editableManager) { event: Event ->
        val newValue = event.target.value
        if (newValue != valueOnUndoStack.current) {
            valueOnUndoStack.current = newValue
            props.editableManager.onChange()
        }
    }

    val handleKeyDown = handler("on keydown", props.editableManager) { event: Event ->
        if (event.asDynamic().keyCode == 13) {
            handleBlur(event)
        }
    }

    formControl {
        textField {
            attrs.autoFocus = props.autoFocus == true
            attrs.fullWidth = true
            attrs.label { +props.label }
            attrs.value = props.getValue()

            // Notify EditableManager of changes as we type, but don't push them to the undo stack...
            attrs.onChangeFunction = handleChange

            // ... until we lose focus or hit return; then, push to the undo stack only if the value would change.
            attrs.onBlurFunction = handleBlur
            attrs.onKeyDownFunction = handleKeyDown
        }

        props.helperText?.let { helperText ->
            formHelperText { +helperText }
        }
    }
}

external interface TextFieldEditorProps : RProps {
    var label: String
    var helperText: String?
    var autoFocus: Boolean?
    var getValue: () -> String
    var setValue: (String) -> Unit
    var editableManager: EditableManager
}

fun RBuilder.textFieldEditor(handler: RHandler<TextFieldEditorProps>) =
    child(TextFieldEditor, handler = handler)