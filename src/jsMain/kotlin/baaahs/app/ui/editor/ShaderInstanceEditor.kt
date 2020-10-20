package baaahs.app.ui.editor

import baaahs.app.ui.CommonIcons
import baaahs.app.ui.appContext
import baaahs.app.ui.shaderPreview
import baaahs.englishize
import baaahs.gl.preview.PreviewShaderBuilder
import baaahs.show.ShaderChannel
import baaahs.show.mutable.EditingShader
import baaahs.show.mutable.MutablePatch
import baaahs.show.mutable.MutableShaderChannel
import baaahs.show.mutable.MutableShaderInstance
import baaahs.ui.*
import baaahs.ui.preview.gadgetsPreview
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
import materialui.components.tab.enums.TabStyle
import materialui.components.tab.tab
import materialui.components.tabs.enums.TabsStyle
import materialui.components.tabs.tabs
import materialui.components.textfield.textField
import materialui.icon
import materialui.useTheme
import org.w3c.dom.events.Event
import react.*
import react.dom.div

private enum class PageTabs {
    Properties, Ports, Gadgets
}

val ShaderInstanceEditor = xComponent<ShaderInstanceEditorProps>("ShaderInstanceEditor") { props ->
    val appContext = useContext(appContext)
    val theme = useTheme()
    val shaderEditorStyles = memo(theme) { ShaderEditorStyles(theme) }

    var selectedTab by state { PageTabs.Properties }
    @Suppress("UNCHECKED_CAST")
    val handleChangeTab = handler("on tab click") { event: Event, value: PageTabs ->
        selectedTab = value
    } as (Event, Any?) -> Unit

    val handleUpdate =
        handler("handleShaderUpdate", props.mutableShaderInstance) { block: MutableShaderInstance.() -> Unit ->
            props.mutableShaderInstance.block()
            props.editableManager.onChange()
        }

    val editingShader = memo(props.mutableShaderInstance) {
        val newEditingShader =
            EditingShader(
                props.editableManager.currentMutableShow,
                props.mutablePatch,
                props.mutableShaderInstance,
                appContext.autoWirer
            ) { shader ->
                PreviewShaderBuilder(shader, appContext.autoWirer, appContext.webClient.model)
            }

        val observer = newEditingShader.addObserver {
            props.editableManager.onChange(pushToUndoStack = false)
        }
        withCleanup { observer.remove() }

        newEditingShader
    }

    val shaderInstance = props.mutableShaderInstance

    val handleSelectShaderChannel = handler("select shader channel", handleUpdate) { event: Event ->
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

    div(+shaderEditorStyles.propsAndPreview) {
        div(+shaderEditorStyles.propsTabsAndPanels) {
            tabs(shaderEditorStyles.tabsContainer on TabsStyle.flexContainer) {
                attrs.value = selectedTab
                attrs.onChange = handleChangeTab
                attrs["orientation"] = "vertical"
                PageTabs.values().forEach { tab ->
                    tab(shaderEditorStyles.tab on TabStyle.root) {
                        attrs.label { +tab.name }
                        attrs.value = tab.asDynamic()
                    }
                }
            }

            div(+shaderEditorStyles.propsPanel) {
                when (selectedTab) {
                    PageTabs.Properties -> div(+shaderEditorStyles.shaderProperties) {
                        textFieldEditor {
                            attrs.label = "Shader Name"
                            attrs.getValue = { shaderInstance.mutableShader.title }
                            attrs.setValue = { value -> shaderInstance.mutableShader.title = value }
                            attrs.editableManager = props.editableManager
                        }

                        formControl {
                            val main = ShaderChannel.Main
                            inputLabel { +"Channel" }
                            select {
                                attrs.renderValue<String> { it.asTextNode() }
                                attrs.value(shaderInstance.shaderChannel.id)
                                attrs.onChangeFunction = handleSelectShaderChannel

                                menuItem {
                                    attrs["value"] = main.id
                                    listItemIcon { icon(CommonIcons.ShaderChannel) }
                                    listItemText { +"${main.id.englishize()} (default)" }
                                }

                                val shaderChannels =
                                    (editingShader.getShaderInstanceOptions()?.shaderChannels ?: emptyList())
                                        .filter { it.id != main.id }

                                divider {}
                                shaderChannels.forEach { shaderChannel ->
                                    menuItem {
                                        attrs["value"] = shaderChannel.id
                                        listItemIcon { icon(CommonIcons.ShaderChannel) }
                                        listItemText { +shaderChannel.id.englishize() }
                                    }
                                }

                                divider {}
                                menuItem {
                                    attrs["value"] = "__new__"
                                    listItemIcon { icon(CommonIcons.Add) }
                                    listItemText { +"New Channel…" }
                                }
                            }
                            formHelperText { +"This shader's channel." }
                        }

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

                    PageTabs.Ports -> linksEditor {
                        attrs.editableManager = props.editableManager
                        attrs.editingShader = editingShader
                    }

                    PageTabs.Gadgets -> gadgetsPreview {
                        attrs.editingShader = editingShader
                    }
                }
            }
        }

        shaderPreview {
            attrs.shader = editingShader.shaderBuilder.shader
            attrs.previewShaderBuilder = editingShader.shaderBuilder
            attrs.width = ShaderEditorStyles.previewWidth
            attrs.height = ShaderEditorStyles.previewHeight
        }
    }

    shaderEditor {
        attrs.editingShader = editingShader
    }
}

external interface ShaderInstanceEditorProps : RProps {
    var editableManager: EditableManager
    var mutablePatch: MutablePatch
    var mutableShaderInstance: MutableShaderInstance
}

fun RBuilder.shaderInstanceEditor(handler: RHandler<ShaderInstanceEditorProps>) =
    child(ShaderInstanceEditor, handler = handler)
