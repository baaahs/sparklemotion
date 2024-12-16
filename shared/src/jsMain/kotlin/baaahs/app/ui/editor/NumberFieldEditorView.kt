package baaahs.app.ui.editor

import baaahs.app.ui.appContext
import baaahs.ui.unaryMinus
import baaahs.ui.value
import baaahs.ui.withoutEvent
import baaahs.ui.xComponent
import js.objects.jso
import mui.material.*
import mui.system.sx
import react.*
import react.dom.events.FocusEvent
import react.dom.events.KeyboardEvent
import react.dom.html.ReactHTML.button
import web.cssom.ClassName
import web.cssom.Color
import web.html.HTMLInputElement
import web.html.InputType

private val NumberFieldEditor = xComponent<NumberFieldEditorProps<Number?>>("NumberFieldEditor") { props ->
    val appContext = useContext(appContext)
    val styles = appContext.allStyles.numberInput

    val valueOnUndoStack = ref(props.getValue())
    val isNullable = props.isNullable == true
    val isInteger = props.isInteger == true
    val enteredString = ref<String>(null)
    val value = props.getValue()
    enteredString.current?.let {
        if (value != it.toDoubleOrNull())
            enteredString.current = null
    }
    console.log("RENDER!!! enteredString.current == ", enteredString.current, " value = ", value)

    val notifyOfChange by handler(props.onChange, props.editableManager) { pushToUndoStack: Boolean ->
        val onChange = props.onChange
        val editableManager = props.editableManager
        if (onChange != null) {
            onChange(pushToUndoStack)
        } else if (editableManager != null) {
            editableManager.onChange(pushToUndoStack)
        }
    }

    var isError by state { false }
    var errorValue by state<String?> { null }

    val handleChange by changeEventHandler(
        isInteger, isNullable, props.setValue, props.getValue, notifyOfChange, props.noIntermediateUpdates
    ) { event ->
        val value = event.target.value
        enteredString.current = value
        val numValue = value.trimEnd('.', ',').let {
            if (isInteger) it.toIntOrNull() else it.toDoubleOrNull()
        }

        console.log("enteredString.current := ", enteredString.current, "handleChange; numValue = ", numValue)
        if (numValue == props.getValue()) {
            forceRender()
            return@changeEventHandler
        }

        try {
            if (numValue == null) {
                if (isNullable) {
                    props.setValue(null)
                    isError = false
                    errorValue = null
                } else {
                    isError = true
                    errorValue = "May not be blank."
                }
            } else {
                props.setValue(numValue)
                isError = false
                errorValue = null
            }
        } catch (e: NumberFieldException) {
            isError = true
            errorValue = e.message
        }

        if (props.noIntermediateUpdates == true) {
            forceRender()
        } else {
            notifyOfChange(false)
        }
    }

    val handleBlur by focusEventHandler(notifyOfChange) { event: FocusEvent<*> ->
        val newValue = event.target.value
        val numValue = if (isInteger) newValue.toIntOrNull() else newValue.toDoubleOrNull()

        if (numValue != valueOnUndoStack.current) {
            valueOnUndoStack.current = numValue
            notifyOfChange(true)
        }
    }

    val stepAmount = props.stepAmount ?: 1
    val handleDecrementButton by mouseEventHandler(props.getValue, props.setValue, stepAmount, notifyOfChange) {
        try {
            props.setValue(props.getValue()?.let {
                it.asDynamic() - stepAmount
            })
            isError = false
            errorValue = null
            notifyOfChange(true)
        } catch (_: NumberFieldException) {
            // Ignore.
        }
    }
    val handleIncrementButton by mouseEventHandler(props.getValue, props.setValue, stepAmount, notifyOfChange) {
        try {
            props.setValue(props.getValue()?.let { it.asDynamic() + stepAmount })
            isError = false
            errorValue = null
            notifyOfChange(true)
        } catch (_: NumberFieldException) {
            // Ignore.
        }
    }

    val handleKeyDown by keyboardEventHandler(handleBlur, handleDecrementButton, handleIncrementButton) { event: KeyboardEvent<*> ->
        val value = event.target.value
//        enteredString.current = value
//        console.log("enteredString.current := ", enteredString.current, "handleKeyDown")
        val keyCode = event.asDynamic().keyCode
        when (keyCode) {
            13 -> handleBlur(event as FocusEvent<*>)     // Return.
            38 -> handleIncrementButton.withoutEvent()() // Up arrow.
            40 -> handleDecrementButton.withoutEvent()() // Down arrow.
            else -> return@keyboardEventHandler
        }
        event.preventDefault()
    }

    FormControl {
        props.label?.let {
            FormLabel {
                attrs.className = -styles.inputLabel
                +it
            }
        }

        InputBase {
            attrs.className = -styles.newRoot
            attrs.classes = props.classes
            attrs.type = InputType.text.asDynamic()
            attrs.inputProps = jso<HTMLInputElement> {
//                this.pattern = if (isInteger) "[0-9]*" else "[0-9]*[.,]?[0-9]*"
            }.asDynamic()
            attrs.autoFocus = props.autoFocus == true
            attrs.fullWidth = (props.fullWidth ?: true) == true
            attrs.size = Size.small
            attrs.disabled = props.disabled == true
            attrs.autoComplete = "off"
            attrs.autoCorrect = "off"
            attrs.spellCheck = false
//            attrs.label = buildElement { +props.label }
            attrs.error = isError
            attrs.placeholder = props.placeholder
            attrs.endAdornment = buildElement {
                Fragment {
                    InputAdornment {
                        attrs.sx { color = Color.currentcolor }
                        attrs.disablePointerEvents = true
                        props.adornment?.let {
                            child(it)
                        }
                    }
                    button {
                        attrs.className = ClassName("decrementButton")
                        attrs.disabled = props.disabled == true
                        attrs.tabIndex = -1
                        attrs.onClick = handleDecrementButton
                    }
                    button {
                        attrs.className = ClassName("incrementButton")
                        attrs.disabled = props.disabled == true
                        attrs.tabIndex = -1
                        attrs.onClick = handleIncrementButton
                    }
                }
            }

            console.log("DRAW enteredString.current == ", enteredString.current)
            attrs.value = enteredString.current ?: value
            // Notify EditableManager of changes as we type, but don't push them to the undo stack...
            attrs.onChange = handleChange

            // ... until we lose focus or hit return; then, push to the undo stack only if the value would change.
            attrs.onBlur = handleBlur
            attrs.onKeyDown = handleKeyDown
        }

//        props.label?.let { FormControlLabel { +it} }



        if (errorValue != null) {
            FormHelperText {
                attrs.className = -styles.helperText
                attrs.error = true
                +errorValue!!
            }
        } else {
            props.helperText?.let { helperText ->
                FormHelperText {
                    attrs.className = -styles.helperText
                    +helperText
                }
            }
        }
    }
}

class NumberFieldException(override val message: String) : Exception(message)

fun numberFieldException(message: String): Nothing =
    throw NumberFieldException(message)

external interface NumberFieldEditorProps<T : Number?> : Props {
    var classes: InputBaseClasses?
    var isInteger: Boolean? // defaults to false
    var isNullable: Boolean? // defaults to false
    var label: String?
    var helperText: String?
    var placeholder: String?
    var adornment: ReactNode?
    var autoFocus: Boolean?
    var disabled: Boolean?
    var fullWidth: Boolean?
    var stepAmount: T?
    var getValue: () -> T
    var setValue: (T) -> Unit
    var editableManager: EditableManager<*>?
    var onChange: ((pushToUndoStack: Boolean) -> Unit)?

    /** If per-key updates cause input to lose focus, turn them off. */
    var noIntermediateUpdates: Boolean?
}

fun <T : Number?> RBuilder.numberFieldEditor(handler: RHandler<NumberFieldEditorProps<T>>) =
    child(NumberFieldEditor, handler = handler)