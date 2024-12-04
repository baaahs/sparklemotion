package baaahs.app.ui.model

import baaahs.app.ui.appContext
import baaahs.app.ui.controls.Styles
import baaahs.ui.asTextNode
import baaahs.ui.unaryMinus
import baaahs.ui.xComponent
import external.react.NumberInputSlotProps
import js.objects.jso
import mui.material.InputAdornment
import mui.material.InputAdornmentPosition
import mui.material.Typography
import mui.system.sx
import react.*
import web.cssom.em
import web.events.Event

val NumberTextFieldView = xComponent<NumberTextFieldProps<Number?>>("NumberTextField") { props ->
    val isNullable = props.isNullable == true
    var error: String? by state { null }
    var errorValue by state<String?> { null }

    val cachedOnChange = props.onChange.asDynamic().cachedOnClick ?: run {
        { event: Event, value: Number? ->
            val numericValue = value
            if (!isNullable && numericValue == null) {
                error = "Must not be blank."
                errorValue = ""
            } else {
                try {
                    props.onChange(numericValue)
                    error = null
                    errorValue = null
                } catch (e: Exception) {
                    error = e.message
                }
            }
        }.also { props.onChange.asDynamic().cachedOnClick = it }
    }

    val adornment = memo(props.adornment) {
        props.adornment?.let { adornment ->
            buildElement {
                InputAdornment {
                    // Styles come from app-ui-numberinput > .MuiInputAdornment-root
                    attrs.position = InputAdornmentPosition.end
                    attrs.disableTypography = true
                    Typography {
                        attrs.sx {
                            userSelect = "none".asDynamic()
                            fontSize = .8.em
                        }
                        child(adornment)
                    }
                }
            }
        }
    }

    val slotProps = memo<NumberInputSlotProps> {
        jso {
            input = jso<dynamic> {
                this.className = -Styles.inputLabel
                this.shrink = true
            }
        }
    }

    NumberInput {
        attrs.placeholder = props.placeholder?.asDynamic()

        if (props.adornment != null) {
            attrs.endAdornment = adornment
        }

        attrs.slotProps = slotProps
        attrs.disabled = props.disabled == true
        attrs.onChange = cachedOnChange
        attrs.onInputChange = cachedOnChange
        attrs.value = props.value
        attrs.label = props.label.asTextNode()
        attrs.error = error != null
        attrs.helperText = error
//        attrs.helperText = error?.asTextNode()
    }

}

external interface NumberTextFieldProps<T: Number?> : Props {
    var label: String
    var disabled: Boolean?
    var value: T
    var isNullable: Boolean? // defaults to false
    var adornment: ReactNode?
    var placeholder: String?
    var onChange: (T) -> Unit
}

fun <T : Number?> RBuilder.numberTextField(handler: RHandler<NumberTextFieldProps<T>>) =
    child(NumberTextFieldView, handler = handler)