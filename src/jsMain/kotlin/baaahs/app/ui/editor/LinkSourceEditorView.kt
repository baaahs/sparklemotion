package baaahs.app.ui.editor

import baaahs.app.ui.appContext
import baaahs.gl.shader.InputPort
import baaahs.show.mutable.EditingShader
import baaahs.ui.unaryMinus
import baaahs.ui.withSelectEvent
import baaahs.ui.xComponent
import js.core.jso
import materialui.icon
import mui.material.*
import react.*
import web.html.HTMLElement

private val LinkSourceEditor = xComponent<LinkSourceEditorProps>("LinkSourceEditor") { props ->
    val appContext = useContext(appContext)
    val styles = appContext.allStyles.shaderEditor

    val lastLinkOptions = ref<List<LinkOption>>()
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
    val showAdvancedCheckbox = ref<HTMLElement>()
    val anyAdvanced = linkOptions.any { it.isAdvanced }
    val handleToggleShowAdvanced by mouseEventHandler { event ->
        showAdvanced = !showAdvanced
        event.stopPropagation()
    }

    FormControl {
        Select<SelectProps<LinkOption>> {
            attrs.margin = InputBaseMargin.dense
            attrs.size = Size.small
            attrs.value = selected
            if (selected == null) {
                this@xComponent.logger.warn { "Huh? None of the LinkOptions are active for ${props.inputPort.id}?" }
            }
            attrs.renderValue = {
                buildElement { Typography { +it.title } }
            }
            attrs.onChange = handleChange.withSelectEvent()
            attrs.disabled = props.editingShader.isBuilding()

            var dividerGroup: String? = null
            for (option in displayLinkOptions) {
                if (!showAdvanced && option.isAdvanced && option != selected) continue

                if (dividerGroup != option.groupName) {
                    if (dividerGroup != null) {
                        Divider {}
                    }
                    option.groupName?.let { ListSubheader { +it } }
                    dividerGroup = option.groupName
                }

                MenuItem {
                    attrs.dense = true
                    attrs.value = option.asDynamic() // TODO: yuck
                    ListItemIcon { icon(option.icon) }
                    ListItemText { +option.title }
                }
            }

            if (anyAdvanced) {
                Divider {}

                ListItem {
                    attrs.classes = jso { this.root = -styles.showAdvancedMenuItem }

                    Checkbox {
                        ref = showAdvancedCheckbox
                        attrs.checked = showAdvanced
                        attrs.onClick = handleToggleShowAdvanced
                    }
                    attrs.onClick = handleToggleShowAdvanced

                    ListItemText {
                        attrs.onClick = handleToggleShowAdvanced
                        +"Show Advanced"
                    }
                }
            }
        }
    }
}

external interface LinkSourceEditorProps : Props {
    var editableManager: EditableManager<*>
    var editingShader: EditingShader
    var inputPort: InputPort
}

fun RBuilder.linkSourceEditor(handler: RHandler<LinkSourceEditorProps>) =
    child(LinkSourceEditor, handler = handler)
