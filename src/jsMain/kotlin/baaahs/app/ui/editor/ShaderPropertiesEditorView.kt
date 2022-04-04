package baaahs.app.ui.editor

import baaahs.app.ui.Colors
import baaahs.app.ui.CommonIcons
import baaahs.app.ui.appContext
import baaahs.englishize
import baaahs.show.ShaderChannel
import baaahs.show.mutable.EditingShader
import baaahs.show.mutable.MutablePatch
import baaahs.show.mutable.MutableShaderChannel
import baaahs.ui.*
import materialui.icon
import mui.material.*
import mui.system.sx
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.events.Event
import react.*
import react.dom.b
import react.dom.br
import react.dom.div
import react.dom.events.FormEvent
import react.dom.html.InputType
import react.dom.onChange

private val ShaderPropertiesEditor = xComponent<ShaderPropertiesEditorProps>("ShaderPropertiesEditor") { props ->
    val appContext = useContext(appContext)
    val shaderEditorStyles = appContext.allStyles.shaderEditor

    val patch = props.mutablePatch
    val editingShader = props.editingShader

    val handleUpdate by handler(props.mutablePatch, props.editableManager) { block: MutablePatch.() -> Unit ->
        props.mutablePatch.block()
        props.editableManager.onChange()
    }

    val handleSelectShaderChannel by eventHandler(handleUpdate) { event: Event ->
        val channelId = event.target.value
        if (channelId == "__new__") {
            appContext.prompt(Prompt(
                "Create A New Channel",
                "Enter the name of the new channel.",
                fieldLabel = "Channel Name",
                cancelButtonLabel = "Cancel",
                submitButtonLabel = "Create",
                onSubmit = { name ->
                    handleUpdate {
                        shaderChannel = MutableShaderChannel.from(name)
                    }
                }
            ))
        } else {
            handleUpdate {
                shaderChannel = MutableShaderChannel.from(channelId)
            }
        }
    }

    div(+shaderEditorStyles.shaderProperties) {
        div(+shaderEditorStyles.shaderName) {
            textFieldEditor {
                attrs.label = "Shader Name"
                attrs.getValue = { patch.mutableShader.title }
                attrs.setValue = { value ->
                    props.editableManager.maybeChangeTitle(patch.mutableShader.title, value)
                    patch.mutableShader.title = value
                }
                attrs.editableManager = props.editableManager
            }
        }

        div(+shaderEditorStyles.shaderChannel) {
            FormControl {
                val main = ShaderChannel.Main
                InputLabel { +"Channel" }
                Select<SelectProps<String>> {
                    attrs.renderValue = { it.asTextNode() }
                    attrs.value = patch.shaderChannel.id
                    attrs.onChange = handleSelectShaderChannel

                    MenuItem {
                        attrs.value = main.id
                        ListItemIcon { icon(CommonIcons.ShaderChannel) }
                        ListItemText { +"${main.id.englishize()} (default)" }
                    }

                    Divider {}

                    val shaderChannels = editingShader.getShaderChannelOptions(excludeMain = true)
                    shaderChannels.forEach { shaderChannel ->
                        if (shaderChannel.id != main.id) {
                            MenuItem {
                                attrs.value = shaderChannel.id
                                ListItemIcon { icon(CommonIcons.ShaderChannel) }
                                ListItemText { +shaderChannel.id.englishize() }
                            }
                        }
                    }

                    Divider {}
                    MenuItem {
                        attrs.value = "__new__"
                        ListItemIcon { icon(CommonIcons.Add) }
                        ListItemText { +"New Channel…" }
                    }
                }
                FormHelperText { +"This shader's channel." }
            }
        }

        div(+shaderEditorStyles.shaderPriority) {
            FormControl {
                TextField {
                    attrs.label = ReactNode("Priority")
                    attrs.type = InputType.number
                    attrs.value = patch.priority
                    attrs.onChange = { event: FormEvent<HTMLDivElement> ->
                        val priorityStr = event.target.value
                        handleUpdate { priority = priorityStr.toFloat() }
                    }
                }
                FormHelperText { +"This shader's priority in the patch." }
            }
        }

        div(+shaderEditorStyles.shaderReturnType) {
            val openShader = editingShader.openShader

            if (openShader != null) {
                val outputPort = openShader.outputPort

                Typography { b { +"Returns: " } }
                Typography {
                    if (outputPort.contentType.isUnknown()) {
                        attrs.sx { color = Colors.error }
                    }
                    +outputPort.contentType.title
                }

                br {}

                Typography { b { +"Shader Type: " } }
                Typography {
                    +"${openShader.shaderType.title} (${openShader.shaderDialect.title})"
                }
            }

        }
    }
}

external interface ShaderPropertiesEditorProps : Props {
    var editableManager: EditableManager<*>
    var editingShader: EditingShader
    var mutablePatch: MutablePatch
}

fun RBuilder.shaderPropertiesEditor(handler: RHandler<ShaderPropertiesEditorProps>) =
    child(ShaderPropertiesEditor, handler = handler)