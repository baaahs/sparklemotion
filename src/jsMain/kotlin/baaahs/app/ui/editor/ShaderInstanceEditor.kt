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
import kotlinx.html.InputType
import kotlinx.html.js.onChangeFunction
import materialui.components.divider.divider
import materialui.components.formcontrol.formControl
import materialui.components.formcontrollabel.enums.FormControlLabelStyle
import materialui.components.formcontrollabel.formControlLabel
import materialui.components.formhelpertext.formHelperText
import materialui.components.inputlabel.inputLabel
import materialui.components.listitemicon.listItemIcon
import materialui.components.listitemtext.listItemText
import materialui.components.menuitem.menuItem
import materialui.components.select.select
import materialui.components.switches.switch
import materialui.components.tab.enums.TabStyle
import materialui.components.tab.tab
import materialui.components.tabs.enums.TabsStyle
import materialui.components.tabs.tabs
import materialui.components.textfield.textField
import materialui.components.typography.typographyH6
import materialui.icon
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.events.Event
import react.*
import react.dom.b
import react.dom.br
import react.dom.code
import react.dom.div

private enum class PageTabs {
    Properties, Ports, Gadgets
}

val ShaderInstanceEditor = xComponent<ShaderInstanceEditorProps>("ShaderInstanceEditor") { props ->
    val appContext = useContext(appContext)
    val shaderEditorStyles = appContext.allStyles.shaderEditor

    var selectedTab by state { PageTabs.Properties }
    @Suppress("UNCHECKED_CAST")
    val handleChangeTab = handler("on tab click") { _: Event, value: PageTabs ->
        selectedTab = value
    } as (Event, Any?) -> Unit

    var adjustGadgets by state { true }
    val handleChangeAdjustGadgets = handler("on adjust gadgets change") { event: Event ->
        adjustGadgets = (event.target as HTMLInputElement).checked
    }

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
            forceRender()
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
                            b { +"Return type: " }
                            val openShader = editingShader.openShader
                            code { +(openShader?.outputPort?.contentType?.id ?: "") }

                            val isFilter = openShader?.let {
                                editingShader.mutableShaderInstance.isFilter(
                                    it
                                )
                            } ?: false
                            if (isFilter) {
                                +" (Filter)"
                            }
                            br {}
                            if (openShader != null) {
                                +"Type: ${openShader.shaderType.title} (${openShader.shaderDialect.title})"
                            }
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

            formControlLabel(shaderEditorStyles.adjustGadgetsSwitch on FormControlLabelStyle.root) {
                attrs.control {
                    switch {
                        attrs.checked = adjustGadgets
                        attrs.onChangeFunction = handleChangeAdjustGadgets
                    }
                }
                attrs.label { typographyH6 { +"Adjust Gadgets" } }
            }
        }

        shaderPreview {
            attrs.shader = editingShader.shaderBuilder.shader
            attrs.previewShaderBuilder = editingShader.shaderBuilder
            attrs.width = ShaderEditorStyles.previewWidth
            attrs.height = ShaderEditorStyles.previewHeight
            attrs.adjustGadgets = adjustGadgets
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
