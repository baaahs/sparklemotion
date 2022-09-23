package baaahs.app.ui.editor

import baaahs.app.ui.Colors
import baaahs.app.ui.CommonIcons
import baaahs.app.ui.appContext
import baaahs.englishize
import baaahs.show.Stream
import baaahs.show.mutable.EditingShader
import baaahs.show.mutable.MutablePatch
import baaahs.show.mutable.MutableStream
import baaahs.ui.*
import kotlinx.html.org.w3c.dom.events.Event
import materialui.icon
import mui.material.*
import mui.system.sx
import org.w3c.dom.HTMLDivElement
import react.*
import react.dom.b
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

    val handleSelectStream by eventHandler(handleUpdate) { event: Event ->
        val channelId = event.target.value
        if (channelId == "__new__") {
            appContext.prompt(Prompt(
                "Create A New Stream",
                "Enter the name of the new stream.",
                fieldLabel = "Stream Name",
                cancelButtonLabel = "Cancel",
                submitButtonLabel = "Create",
                onSubmit = { name ->
                    handleUpdate {
                        stream = MutableStream.from(name)
                    }
                }
            ))
        } else {
            handleUpdate {
                stream = MutableStream.from(channelId)
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

        div(+shaderEditorStyles.stream) {
            FormControl {
                val main = Stream.Main
                InputLabel { +"Stream" }
                Select<SelectProps<String>> {
                    attrs.size = Size.small
                    attrs.renderValue = { it.asTextNode() }
                    attrs.value = patch.stream.id
                    attrs.onChange = handleSelectStream.withSelectEvent()

                    MenuItem {
                        attrs.value = main.id
                        ListItemIcon { icon(CommonIcons.Stream) }
                        ListItemText { +"${main.id.englishize()} (default)" }
                    }

                    Divider {}

                    val streams = editingShader.getStreamOptions(excludeMain = true)
                    streams.forEach { stream ->
                        if (stream.id != main.id) {
                            MenuItem {
                                attrs.value = stream.id
                                ListItemIcon { icon(CommonIcons.Stream) }
                                ListItemText { +stream.id.englishize() }
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
                    attrs.size = Size.small
                    attrs.value = patch.priority
                    attrs.onChange = { event: FormEvent<HTMLDivElement> ->
                        val priorityStr = event.target.value
                        handleUpdate { priority = priorityStr.toFloat() }
                    }
                }
                FormHelperText { +"Higher priorities are picked first." }
            }
        }

        val openShader = editingShader.openShader
        if (openShader != null) {
            div(+shaderEditorStyles.shaderData) {
                val outputPort = openShader.outputPort

                Typography {
                    b { +"Returns: " }

                    if (outputPort.contentType.isUnknown()) {
                        attrs.sx { color = Colors.error }
                    }
                    +outputPort.contentType.title
                }

                Typography {
                    b { +"Type: " }
                    +openShader.shaderType.title
                }

                Typography {
                    b { +"Dialect: " }
                    +openShader.shaderDialect.title
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