package baaahs.app.ui.editor

import baaahs.gl.shader.InputPort
import baaahs.show.mutable.MutablePort
import baaahs.ui.asTextNode
import baaahs.ui.xComponent
import kotlinx.html.js.onChangeFunction
import materialui.components.divider.divider
import materialui.components.formcontrol.formControl
import materialui.components.inputlabel.inputLabel
import materialui.components.listitemicon.listItemIcon
import materialui.components.listitemtext.listItemText
import materialui.components.listsubheader.listSubheader
import materialui.components.menuitem.menuItem
import materialui.components.select.select
import materialui.icon
import react.RBuilder
import react.RHandler
import react.RProps
import react.child

val LinkSourceEditor = xComponent<LinkSourceEditorProps>("LinkSourceEditor", isPure = true) { props ->
    val handleChange =
        eventHandler("change to ${props.inputPort.id}", props.onChange) { event ->
            val value = event.target.asDynamic().value as LinkOption?
            props.onChange(
                props.inputPort,
                when (value) {
                    NoSourcePortOption -> null
                    NewSourcePortOption -> error("new not yet implemented") // TODO
                    else -> value
                }
            )
        }

    val linkOptions =
        props.linkOptions.sortedWith(
            compareBy<LinkOption> { it.groupName }.thenBy { it.title }
        ) + NewSourcePortOption
    val selected = linkOptions.find { it.matches(props.currentSourcePort) }

    formControl {
        inputLabel { +"Source" }

        select {
            attrs.value = selected
            if (selected == null) {
                this@xComponent.logger.warn { "Huh? None of the LinkOptions are active for ${props.inputPort.id}?" }
            }
            attrs.renderValue<LinkOption> { it.title.asTextNode() }
            attrs.onChangeFunction = handleChange
            attrs.disabled = props.isDisabled

            var dividerGroup: String? = null
            linkOptions.forEach { option ->
                if (dividerGroup != option.groupName) {
                    if (dividerGroup != null) {
                        divider {}
                    }
                    option.groupName?.let { listSubheader { +it } }
                    dividerGroup = option.groupName
                }

                menuItem {
                    attrs.dense = true
                    attrs["value"] = option
                    listItemIcon { icon(option.icon) }
                    listItemText { +option.title }
                }
            }
        }
    }
}

external interface LinkSourceEditorProps : RProps {
    var inputPort: InputPort
    var currentSourcePort: MutablePort?
    var linkOptions: List<LinkOption>
    var isDisabled: Boolean
    var onChange: (InputPort, LinkOption?) -> Unit
}

fun RBuilder.linkSourceEditor(handler: RHandler<LinkSourceEditorProps>) =
    child(LinkSourceEditor, handler = handler)
