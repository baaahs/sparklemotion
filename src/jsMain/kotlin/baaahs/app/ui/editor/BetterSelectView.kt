package baaahs.app.ui.editor

import baaahs.ui.asTextNode
import baaahs.ui.unaryMinus
import baaahs.ui.xComponent
import js.core.jso
import mui.material.*
import react.Props
import react.RBuilder
import react.RHandler
import react.ReactNode
import react.dom.events.ChangeEvent
import react.dom.html.ReactHTML
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
        renderValueSelected?.let { it(value) }
            ?: value.toString().asTextNode()
    }

    FormControl {
        attrs.margin = FormControlMargin.dense

        props.label?.let { label ->
            InputLabel {
                attrs.classes = jso { this.root = -ControlsStyles.inputLabel }
                attrs.margin = InputLabelMargin.dense
                attrs.component = ReactHTML.legend
                +label
            }
        }
        Select<SelectProps<Int>> {
            attrs.autoWidth = true
            attrs.fullWidth = true
            attrs.displayEmpty = true
            attrs.margin = InputBaseMargin.dense
            attrs.size = Size.small
            attrs.value = props.values.indexOf(props.value).let {
                if (it == -1) {
                    fun Any.render() = props.renderValueOption?.invoke(this) ?: this.toString()
                    this@xComponent.logger.error { ("Value ${props.value?.render() ?: "null"} not found " +
                            "in [${props.values.map { it?.render() ?: "null" }.joinToString(", ")}]") }
                    null
                } else it
            }
            attrs.renderValue = renderSelected
            attrs.onChange = handleChange

            props.values.forEachIndexed { index, option ->
                MenuItem {
                    attrs.dense = true
                    attrs.value = index.toString()
                    ListItemText {
                        val renderValueOption = props.renderValueOption
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