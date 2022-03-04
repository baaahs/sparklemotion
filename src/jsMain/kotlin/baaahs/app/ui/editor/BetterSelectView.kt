package baaahs.app.ui.editor

import baaahs.ui.xComponent
import kotlinx.html.js.onChangeFunction
import materialui.components.formcontrol.formControl
import materialui.components.formlabel.formLabel
import materialui.components.listitemtext.listItemText
import materialui.components.menuitem.menuItem
import materialui.components.select.select
import react.Props
import react.RBuilder
import react.RHandler
import react.ReactElement

private val BetterSelectView = xComponent<BetterSelectProps<*>>("BetterSelect") { props ->
    val anyProps = props as BetterSelectProps<Any?>

    val handleChange by eventHandler(anyProps.values, anyProps.onChange) { event ->
        val newValueIndex = event.target.asDynamic()?.value as Int
        val newValue = if (newValueIndex == -1) null else {
            anyProps.values[newValueIndex]
        }
        anyProps.onChange(newValue)
    }

    formControl {
        props.label?.let { label ->
            formLabel {
                attrs.component = "legend"
                +label
            }
        }

        select {
            attrs.value = props.values.indexOf(props.value)
            val renderValueSelected = anyProps.renderValueSelected
            if (renderValueSelected != null) {
                attrs.renderValue(renderValueSelected)
            }
            attrs.onChangeFunction = handleChange

            props.values.forEachIndexed { index, option ->
                menuItem {
                    attrs.dense = true
                    attrs["value"] = index
                    listItemText {
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
    var renderValueOption: ((T) -> ReactElement)?
    var renderValueSelected: ((T) -> ReactElement)?
    var value: T
    var onChange: (T) -> Unit
}

fun <T : Any?> RBuilder.betterSelect(handler: RHandler<BetterSelectProps<T>>) =
    child(BetterSelectView, handler = handler)