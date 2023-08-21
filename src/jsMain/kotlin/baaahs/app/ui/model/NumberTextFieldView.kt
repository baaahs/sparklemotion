package baaahs.app.ui.model

import baaahs.app.ui.appContext
import baaahs.app.ui.controls.Styles
import baaahs.ui.asTextNode
import baaahs.ui.unaryMinus
import baaahs.ui.value
import baaahs.ui.xComponent
import js.core.jso
import mui.material.*
import react.*
import web.events.Event
import web.html.InputType

private val NumberTextFieldView = xComponent<NumberTextFieldProps<Number?>>("NumberTextField") { props ->
    val appContext = useContext(appContext)
    val style = appContext.allStyles.modelEditor
    var error: String? by state { null }

    val cachedOnChange = props.onChange.asDynamic().cachedOnClick ?: run {
        { event: Event ->
            val numericValue = event.currentTarget.value
                .ifBlank { null }
                ?.toDouble()
            try {
                props.onChange(numericValue)
                error = null
            } catch (e: Exception) {
                error = e.message
            }
        }.also { props.onChange.asDynamic().cachedOnClick = it }
    }

    TextField<StandardTextFieldProps> {
        attrs.type = InputType.number
        attrs.margin = FormControlMargin.dense
        attrs.size = Size.small
        attrs.variant = "standard"
        attrs.placeholder = props.placeholder
        attrs.InputProps = jso {
            classes = jso { this.underline = -style.partialUnderline }
            size = Size.small
            margin = InputBaseMargin.dense
            props.adornment?.let { adornment ->
                endAdornment = buildElement {
                    InputAdornment {
                        attrs.position = InputAdornmentPosition.end
                        child(adornment)
                    }
                }
            }
        }
        attrs.InputLabelProps = jso {
            this.classes = jso { this.root = -Styles.inputLabel }
            this.shrink = true
        }
        attrs.onChange = cachedOnChange
        if (props.value != null) attrs.value = props.value
        attrs.label = props.label.asTextNode()
        attrs.error = error != null
        attrs.helperText = error?.asTextNode()
    }
}

external interface NumberTextFieldProps<T: Number?> : Props {
    var label: String
    var value: T
    var adornment: ReactNode?
    var placeholder: String?
    var onChange: (T) -> Unit
}

fun <T : Number?> RBuilder.numberTextField(handler: RHandler<NumberTextFieldProps<T>>) =
    child(NumberTextFieldView, handler = handler)