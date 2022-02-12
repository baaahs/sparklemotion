package baaahs.app.ui.editor

import baaahs.app.ui.CommonIcons
import baaahs.app.ui.appContext
import baaahs.englishize
import baaahs.show.ShaderChannel
import baaahs.show.mutable.EditingShader
import baaahs.show.mutable.MutableShaderChannel
import baaahs.show.mutable.MutableShaderInstance
import baaahs.ui.*
import kotlinx.html.InputType
import kotlinx.html.js.onChangeFunction
import materialui.components.divider.divider
import materialui.components.formcontrol.formControl
import materialui.components.formhelpertext.formHelperText
import materialui.components.inputlabel.inputLabel
import materialui.components.listitemicon.listItemIcon
import materialui.components.listitemtext.listItemText
import materialui.components.menuitem.menuItem
import materialui.components.select.select
import materialui.components.textfield.textField
import materialui.components.typography.enums.TypographyColor
import materialui.components.typography.typography
import materialui.icon
import org.w3c.dom.events.Event
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.b
import react.dom.br
import react.dom.div
import react.useContext

private val ShaderPropertiesEditor = xComponent<ShaderPropertiesEditorProps>("ShaderPropertiesEditor") { props ->
    val appContext = useContext(appContext)
    val shaderEditorStyles = appContext.allStyles.shaderEditor

    val shaderInstance = props.mutableShaderInstance
    val editingShader = props.editingShader

    val handleUpdate by handler(props.mutableShaderInstance, props.editableManager) { block: MutableShaderInstance.() -> Unit ->
        props.mutableShaderInstance.block()
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
                attrs.getValue = { shaderInstance.mutableShader.title }
                attrs.setValue = { value -> shaderInstance.mutableShader.title = value }
                attrs.editableManager = props.editableManager
            }
        }

        div(+shaderEditorStyles.shaderChannel) {
            formControl {
                val main = ShaderChannel.Main
                inputLabel { +"Channel" }
                select {
                    attrs.renderValue<String> { it.asTextNode() }
                    attrs.value(shaderInstance.shaderChannel.id)
                    attrs.onChangeFunction = handleSelectShaderChannel

                    menuItem {
                        attrs.value = main.id
                        listItemIcon { icon(CommonIcons.ShaderChannel) }
                        listItemText { +"${main.id.englishize()} (default)" }
                    }

                    divider {}

                    val shaderChannels = editingShader.getShaderChannelOptions(excludeMain = true)
                    shaderChannels.forEach { shaderChannel ->
                        if (shaderChannel.id != main.id) {
                            menuItem {
                                attrs.value = shaderChannel.id
                                listItemIcon { icon(CommonIcons.ShaderChannel) }
                                listItemText { +shaderChannel.id.englishize() }
                            }
                        }
                    }

                    divider {}
                    menuItem {
                        attrs.value = "__new__"
                        listItemIcon { icon(CommonIcons.Add) }
                        listItemText { +"New Channel…" }
                    }
                }
                formHelperText { +"This shader's channel." }
            }
        }

        div(+shaderEditorStyles.shaderPriority) {
            formControl {
                textField {
                    attrs.label { +"Priority" }
                    attrs.type = InputType.number
                    attrs.value = shaderInstance.priority
                    attrs.onChangeFunction = { event: Event ->
                        val priorityStr = event.target.value
                        handleUpdate { priority = priorityStr.toFloat() }
                    }
                }
                formHelperText { +"This shader's priority in the patch." }
            }
        }

        div(+shaderEditorStyles.shaderReturnType) {
            val openShader = editingShader.openShader

            if (openShader != null) {
                val outputPort = openShader.outputPort

                typography { b { +"Returns: " } }
                typography {
                    if (outputPort.contentType.isUnknown()) {
                        attrs.color = TypographyColor.error
                    }
                    +outputPort.contentType.title
                }

                br {}

                typography { b { +"Shader Type: " } }
                typography {
                    +"${openShader.shaderType.title} (${openShader.shaderDialect.title})"
                }
            }

        }
    }
}

external interface ShaderPropertiesEditorProps : Props {
    var editableManager: EditableManager<*>
    var editingShader: EditingShader
    var mutableShaderInstance: MutableShaderInstance
}

fun RBuilder.shaderPropertiesEditor(handler: RHandler<ShaderPropertiesEditorProps>) =
    child(ShaderPropertiesEditor, handler = handler)