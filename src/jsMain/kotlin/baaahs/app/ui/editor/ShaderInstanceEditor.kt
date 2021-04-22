package baaahs.app.ui.editor

import baaahs.app.ui.appContext
import baaahs.app.ui.shaderPreview
import baaahs.gl.preview.PreviewShaderBuilder
import baaahs.gl.withCache
import baaahs.show.mutable.EditingShader
import baaahs.show.mutable.MutablePatch
import baaahs.show.mutable.MutableShaderInstance
import baaahs.ui.addObserver
import baaahs.ui.on
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import kotlinx.html.js.onChangeFunction
import materialui.components.formcontrollabel.enums.FormControlLabelStyle
import materialui.components.formcontrollabel.formControlLabel
import materialui.components.switches.switch
import materialui.components.tab.enums.TabStyle
import materialui.components.tab.tab
import materialui.components.tabs.enums.TabsStyle
import materialui.components.tabs.tabs
import materialui.components.typography.typographyH6
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.events.Event
import react.*
import react.dom.div

private enum class PageTabs {
    Properties, Ports, Gadgets
}

val ShaderInstanceEditor = xComponent<ShaderInstanceEditorProps>("ShaderInstanceEditor") { props ->
    val appContext = useContext(appContext)
    val shaderEditorStyles = appContext.allStyles.shaderEditor

    val toolchain = memo { appContext.toolchain.withCache("Editor") }

    var selectedTab by state { PageTabs.Properties }
    @Suppress("UNCHECKED_CAST")
    val handleChangeTab = handler("on tab click") { _: Event, value: PageTabs ->
        selectedTab = value
    } as (Event, Any?) -> Unit

    var adjustGadgets by state { true }
    val handleChangeAdjustGadgets = handler("on adjust gadgets change") { event: Event ->
        adjustGadgets = (event.target as HTMLInputElement).checked
    }

    val editingShader = memo(props.mutableShaderInstance) {
        val newEditingShader =
            EditingShader(
                props.editableManager.currentMutableShow,
                props.mutablePatch,
                props.mutableShaderInstance,
                toolchain
            ) { shader ->
                PreviewShaderBuilder(shader, toolchain, appContext.webClient.model)
            }

        val observer = newEditingShader.addObserver {
            props.editableManager.onChange(pushToUndoStack = false)
            forceRender()
        }
        withCleanup { observer.remove() }

        newEditingShader
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
                    PageTabs.Properties -> shaderPropertiesEditor {
                        attrs.editableManager = props.editableManager
                        attrs.editingShader = editingShader
                        attrs.mutableShaderInstance = props.mutableShaderInstance
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
            attrs.toolchain = toolchain
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
