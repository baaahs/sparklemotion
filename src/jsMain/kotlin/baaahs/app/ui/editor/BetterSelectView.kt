package baaahs.app.ui.editor

import baaahs.ui.unaryMinus
import baaahs.ui.withSelectEvent
import baaahs.ui.xComponent
import kotlinx.js.jso
import mui.material.*
import react.*
import react.dom.html.ReactHTML
import baaahs.app.ui.controls.Styles as ControlsStyles

private val BetterSelectView = xComponent<BetterSelectProps<*>>("BetterSelect") { props ->
    val anyProps = props as BetterSelectProps<Any?>

    val handleChange by changeEventHandler(anyProps.values, anyProps.onChange) { event ->
        val newValueIndex = event.target.asDynamic()?.value as Int
        val newValue = if (newValueIndex == -1) null else {
            anyProps.values[newValueIndex]
        }
        anyProps.onChange(newValue)
        event.stopPropagation()
    }

    FormControl {
        props.label?.let { label ->
            InputLabel {
                attrs.classes = jso { this.root = -ControlsStyles.inputLabel }
                attrs.component = ReactHTML.legend
                +label
            }
        }
        Select {
            this as RElementBuilder<SelectProps<Int>> // TODO: yuck, not this.

            attrs.displayEmpty = true
            attrs.value = props.values.indexOf(props.value)
            attrs.renderValue = anyProps.renderValueSelected
            attrs.onChange = handleChange.withSelectEvent()

            props.values.forEachIndexed { index, option ->
                MenuItem {
                    attrs.dense = true
                    attrs.value = index.toString()
                    ListItemText {
                        val renderValueOption = anyProps.renderValueOption
                        if (renderValueOption != null) {
                            child(renderValueOption.invoke(option))
                        } else {
                            +option.toString()
                        }
                    }
                }
            }
        }
    }
}

external interface BetterSelectProps<T: Any?> : Props {
    var label: String?
    var values: List<T>
    var renderValueOption: ((T) -> ReactNode)?
    var renderValueSelected: ((T) -> ReactNode)?
    var value: T
    var onChange: (T) -> Unit
}

fun <T : Any?> RBuilder.betterSelect(handler: RHandler<BetterSelectProps<T>>) =
    child(BetterSelectView, handler = handler)