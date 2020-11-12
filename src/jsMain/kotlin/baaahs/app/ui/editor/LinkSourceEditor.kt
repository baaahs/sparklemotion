package baaahs.app.ui.editor

import baaahs.gl.shader.InputPort
import baaahs.show.mutable.EditingShader
import baaahs.ui.asTextNode
import baaahs.ui.xComponent
import kotlinx.html.js.onChangeFunction
import materialui.components.divider.divider
import materialui.components.formcontrol.formControl
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

val LinkSourceEditor = xComponent<LinkSourceEditorProps>("LinkSourceEditor") { props ->
    val lastLinkOptions = ref<List<LinkOption>?>(null)
    val linkOptions = props.editingShader.linkOptionsFor(props.inputPort)
        ?.also { lastLinkOptions.current = it }
        ?: lastLinkOptions.current
        ?: emptyList()

    val handleChange =
        eventHandler(
            "change to ${props.inputPort.id}",
            props.editingShader, props.editableManager, props.inputPort
        ) { event ->
            val value = event.target.asDynamic().value as LinkOption?
            val newLink = when (value) {
                NoSourcePortOption -> null
                NewSourcePortOption -> error("new not yet implemented") // TODO
                else -> value
            }
            props.editingShader.changeInputPortLink(props.inputPort, newLink)
            props.editableManager.onChange()
        }

    val currentLink = props.editingShader.getInputPortLink(props.inputPort)
    val selected: LinkOption? = linkOptions.find { it.getMutablePort() == currentLink }
    val displayLinkOptions = linkOptions.sortedWith(
        compareBy<LinkOption> { it.groupName }.thenBy { it.title }
    ) + NewSourcePortOption

    formControl {
        select {
            attrs.value = selected
            if (selected == null) {
                this@xComponent.logger.warn { "Huh? None of the LinkOptions are active for ${props.inputPort.id}?" }
            }
            attrs.renderValue<LinkOption> { it.title.asTextNode() }
            attrs.onChangeFunction = handleChange
            attrs.disabled = props.editingShader.isBuilding()

            var dividerGroup: String? = null
            displayLinkOptions.forEach { option ->
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
    var editableManager: EditableManager
    var editingShader: EditingShader
    var inputPort: InputPort
}

fun RBuilder.linkSourceEditor(handler: RHandler<LinkSourceEditorProps>) =
    child(LinkSourceEditor, handler = handler)
