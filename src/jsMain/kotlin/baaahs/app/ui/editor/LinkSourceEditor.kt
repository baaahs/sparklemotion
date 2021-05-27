package baaahs.app.ui.editor

import baaahs.app.ui.appContext
import baaahs.gl.shader.InputPort
import baaahs.show.mutable.EditingShader
import baaahs.ui.on
import baaahs.ui.xComponent
import kotlinx.html.js.onChangeFunction
import kotlinx.html.js.onClickFunction
import materialui.components.checkbox.checkbox
import materialui.components.divider.divider
import materialui.components.formcontrol.formControl
import materialui.components.listitem.enums.ListItemStyle
import materialui.components.listitem.listItem
import materialui.components.listitemicon.listItemIcon
import materialui.components.listitemtext.listItemText
import materialui.components.listsubheader.listSubheader
import materialui.components.menuitem.menuItem
import materialui.components.select.select
import materialui.components.typography.typography
import materialui.icon
import org.w3c.dom.HTMLElement
import org.w3c.dom.events.Event
import react.*

val LinkSourceEditor = xComponent<LinkSourceEditorProps>("LinkSourceEditor") { props ->
    val appContext = useContext(appContext)
    val styles = appContext.allStyles.shaderEditor

    val lastLinkOptions = ref<List<LinkOption>?>(null)
    val linkOptions = props.editingShader.linkOptionsFor(props.inputPort)
        ?.also { lastLinkOptions.current = it }
        ?: lastLinkOptions.current
        ?: emptyList()

    val handleChange by eventHandler(props.editingShader, props.editableManager, props.inputPort) { event ->
        val value = event.target.asDynamic().value as? LinkOption?
            ?: return@eventHandler

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

    var showAdvanced by state { false }
    val showAdvancedCheckbox = ref<HTMLElement?>(null)
    val anyAdvanced = linkOptions.any { it.isAdvanced }
    val handleToggleShowAdvanced by eventHandler { event: Event ->
        showAdvanced = !showAdvanced
        event.stopPropagation()
    }

    formControl {
        select {
            attrs.value = selected
            if (selected == null) {
                this@xComponent.logger.warn { "Huh? None of the LinkOptions are active for ${props.inputPort.id}?" }
            }
            attrs.renderValue<LinkOption> {
                typography { +it.title }
            }
            attrs.onChangeFunction = handleChange
            attrs.disabled = props.editingShader.isBuilding()

            var dividerGroup: String? = null
            for (option in displayLinkOptions) {
                if (!showAdvanced && option.isAdvanced && option != selected) continue

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

            if (anyAdvanced) {
                divider {}

                listItem(styles.showAdvancedMenuItem on ListItemStyle.root) {
                    checkbox {
                        ref = showAdvancedCheckbox
                        attrs.checked = showAdvanced
                        attrs.onClickFunction = handleToggleShowAdvanced
                    }
                    attrs.onClickFunction = handleToggleShowAdvanced

                    listItemText {
                        attrs.onClickFunction = handleToggleShowAdvanced
                        +"Show Advanced"
                    }
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
