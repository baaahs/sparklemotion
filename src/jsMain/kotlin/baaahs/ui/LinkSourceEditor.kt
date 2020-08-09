package baaahs.ui

import baaahs.glshaders.InputPort
import baaahs.show.mutable.MutablePort
import kotlinx.html.js.onChangeFunction
import materialui.components.divider.divider
import materialui.components.formcontrol.formControl
import materialui.components.inputlabel.inputLabel
import materialui.components.listsubheader.listSubheader
import materialui.components.menuitem.menuItem
import materialui.components.select.select
import react.RBuilder
import react.RHandler
import react.RProps
import react.child

val LinkSourceEditor = xComponent<LinkSourceEditorProps>("LinkSourceEditor", isPure = true) { props ->

    val handleChange =
        eventHandler("change to ${props.inputPort.id}", props.sourcePortOptions, props.onChange) { event ->
            val value = event.target.asDynamic().value as String
            props.onChange(
                when (value) {
                    "__new__" -> error("new not yet implemented") // TODO
                    "__none__" -> null
                    else -> props.sourcePortOptions[value.toInt()]
                }
            )

            this@xComponent.forceRender()
        }

    formControl {
        inputLabel { +"Source" }

        select {
            attrs.onChangeFunction = handleChange

            var dividerGroup = props.sourcePortOptions.firstOrNull()?.groupName
            props.sourcePortOptions.forEachIndexed { index, option ->
                if (dividerGroup != option.groupName) {
                    divider {}
                    listSubheader { +option.groupName }
                    dividerGroup = option.groupName
                }

                val currentSourcePort = props.currentSourcePort
                if (currentSourcePort != null && option.matches(currentSourcePort)) {
                    attrs.value(index.toString())
                }

                if (option.isAppropriateFor(props.inputPort)) {
                    menuItem {
                        attrs["value"] = index.toString()
                        +option.title
                    }
                }
            }

            if (dividerGroup != null) {
                divider {}
            }

            menuItem {
                attrs["value"] = "__new__"
                +"Create New…"
            }
        }
    }
}

external interface LinkSourceEditorProps : RProps {
    var inputPort: InputPort
    var currentSourcePort: MutablePort?
    var sourcePortOptions: List<SourcePortOption>
    var onChange: (SourcePortOption?) -> Unit
}

fun RBuilder.linkSourceEditor(handler: RHandler<LinkSourceEditorProps>) =
    child(LinkSourceEditor, handler = handler)