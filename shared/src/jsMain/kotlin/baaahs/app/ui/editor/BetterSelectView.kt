package baaahs.app.ui.editor

import baaahs.ui.asTextNode
import baaahs.ui.unaryMinus
import baaahs.ui.xComponent
import js.objects.jso
import mui.material.FormControl
import mui.material.FormControlMargin
import mui.material.InputBaseMargin
import mui.material.InputLabel
import mui.material.InputLabelMargin
import mui.material.ListItemText
import mui.material.MenuItem
import mui.material.MenuItemProps
import mui.material.Select
import mui.material.SelectProps
import mui.material.Size
import mui.system.sx
import react.Props
import react.RBuilder
import react.RHandler
import react.ReactNode
import react.dom.events.ChangeEvent
import web.cssom.em
import web.html.HTMLInputElement
import baaahs.app.ui.controls.Styles as ControlsStyles

private val BetterSelectView = xComponent<BetterSelectProps<Any?>>("BetterSelect") { props ->
    val handleChange by handler(props.values, props.onChange) { event: ChangeEvent<HTMLInputElement>, reactNode: ReactNode ->
        val newValueIndex = event.target.value.toInt()
        val newValue = if (newValueIndex == -1) null else {
            props.values[newValueIndex]
        }
        props.onChange(newValue)
        event.stopPropagation()
    }

    val renderValueSelected = props.renderValueSelected ?: props.renderValueOption
    val renderSelected: (Int) -> ReactNode = callback(renderValueSelected, props.values) { selectedIndex ->
        val value = props.values[selectedIndex]
        renderValueSelected?.let { it(value, jso {}) }
            ?: value.toString().asTextNode()
    }

    FormControl {
        attrs.margin = FormControlMargin.dense
        attrs.fullWidth = props.fullWidth == true

        props.label?.let { label ->
            InputLabel {
                attrs.className = -ControlsStyles.inputLabel
                attrs.margin = InputLabelMargin.dense
                +label
            }
        }
        Select<SelectProps<Int>> {
            attrs.autoWidth = true
            attrs.fullWidth = true
            attrs.displayEmpty = true
            attrs.margin = InputBaseMargin.dense
            attrs.size = Size.small
            attrs.sx { marginTop = .5.em }
            attrs.disabled = props.disabled == true
            attrs.value = props.values.indexOf(props.value).let {
                if (it == -1) {
                    fun Any.render() = props.renderValueOption?.invoke(this, jso {}) ?: this.toString()
                    this@xComponent.logger.error { ("Value ${props.value?.render() ?: "null"} not found " +
                            "in [${props.values.map { it?.render() ?: "null" }.joinToString(", ")}]") }
                    null
                } else it
            }
            attrs.renderValue = renderSelected
            attrs.onChange = handleChange
            attrs.onClick = { event -> event.stopPropagation() }

            props.values.forEachIndexed { index, option ->
                MenuItem {
                    attrs.dense = true
                    attrs.value = index.toString()
                    ListItemText {
                        val renderValueOption = props.renderValueOption
                        if (renderValueOption != null) {
                            child(renderValueOption.invoke(option, this@MenuItem.attrs))
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
    var disabled: Boolean?
    var renderValueOption: ((T, MenuItemProps) -> ReactNode)?
    var renderValueSelected: ((T, MenuItemProps) -> ReactNode)?
    var value: T
    var onChange: (T) -> Unit
    var fullWidth: Boolean?
}

fun <T : Any?> RBuilder.betterSelect(handler: RHandler<BetterSelectProps<T>>) =
    child(BetterSelectView, handler = handler)