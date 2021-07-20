package baaahs.app.ui.editor

import baaahs.app.ui.appContext
import baaahs.app.ui.shaderDiagnostics
import baaahs.app.ui.shaderPreview
import baaahs.gl.preview.GadgetAdjuster
import baaahs.gl.preview.PreviewShaderBuilder
import baaahs.gl.withCache
import baaahs.show.mutable.EditingShader
import baaahs.show.mutable.MutablePatch
import baaahs.show.mutable.MutableShaderInstance
import baaahs.ui.addObserver
import baaahs.ui.on
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import kotlinx.html.js.onClickFunction
import materialui.components.checkbox.checkbox
import materialui.components.divider.divider
import materialui.components.listitemtext.listItemText
import materialui.components.menu.menu
import materialui.components.menuitem.menuItem
import materialui.components.popover.enums.PopoverOriginHorizontal
import materialui.components.popover.enums.PopoverOriginVertical
import materialui.components.popover.horizontal
import materialui.components.popover.vertical
import materialui.components.tab.enums.TabStyle
import materialui.components.tab.tab
import materialui.components.tabs.enums.TabsStyle
import materialui.components.tabs.tabs
import materialui.icon
import org.w3c.dom.events.Event
import org.w3c.dom.events.EventTarget
import react.*
import react.dom.div

private enum class PageTabs {
    Properties, Ports, Gadgets
}

val ShaderInstanceEditor = xComponent<ShaderInstanceEditorProps>("ShaderInstanceEditor") { props ->
    val appContext = useContext(appContext)
    val shaderEditorStyles = appContext.allStyles.shaderEditor

    val toolchain = memo { appContext.toolchain.withCache("Editor") }

    var settingsMenuAnchor by state<EventTarget?> { null }
    val showSettingsMenu = callback { event: Event -> settingsMenuAnchor = event.target!! }
    val hideSettingsMenu = callback { _: Event?, _: String? -> settingsMenuAnchor = null }

    var selectedTab by state { PageTabs.Properties }
    @Suppress("UNCHECKED_CAST")
    val handleChangeTab by handler { _: Event, value: PageTabs ->
        selectedTab = value
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

    var autoAdjustGadgets by state { true }
    val handleToggleAutoAdjustGadgets by eventHandler { _: Event ->
        autoAdjustGadgets = !autoAdjustGadgets
        settingsMenuAnchor = null
    }

    var fullRange by state { false }
    val handleToggleFullRange by eventHandler { _: Event ->
        autoAdjustGadgets = true
        fullRange = !fullRange
        settingsMenuAnchor = null
    }

    val handleDefaultsClick by eventHandler { _: Event ->
        autoAdjustGadgets = false
        editingShader.gadgets.forEach { gadget -> gadget.openControl.resetToDefault() }
        settingsMenuAnchor = null
    }

    var showDiagnosticsAnchor by state<EventTarget?> { null }
    val handleShowDiagnosticsClick by eventHandler { _: Event ->
        showDiagnosticsAnchor = settingsMenuAnchor
        settingsMenuAnchor = null
    }

    div(+shaderEditorStyles.propsAndPreview) {
        div(+shaderEditorStyles.propsTabsAndPanels) {
            tabs(shaderEditorStyles.tabsContainer on TabsStyle.flexContainer) {
                attrs.value = selectedTab
                attrs.onChange = handleChangeTab.asDynamic()
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
        }

        div(+shaderEditorStyles.previewContainer) {
            shaderPreview {
                attrs.shader = editingShader.shaderBuilder.shader
                attrs.previewShaderBuilder = editingShader.shaderBuilder
                attrs.width = ShaderEditorStyles.previewWidth
                attrs.height = ShaderEditorStyles.previewHeight
                attrs.adjustGadgets = if (autoAdjustGadgets) {
                    if (fullRange) GadgetAdjuster.Mode.FULL_RANGE else GadgetAdjuster.Mode.INCREMENTAL
                } else null
                attrs.toolchain = toolchain
            }

            div(+shaderEditorStyles.settingsMenuAffordance) {
                attrs.onClickFunction = showSettingsMenu

                icon(materialui.icons.Settings)
            }
        }
    }

    shaderEditor {
        attrs.editingShader = editingShader
    }

    menu {
        attrs.anchorEl(settingsMenuAnchor)
        attrs.anchorOrigin {
            horizontal(PopoverOriginHorizontal.left)
            vertical(PopoverOriginVertical.bottom)
        }
        attrs.open = settingsMenuAnchor != null
        attrs.onClose = hideSettingsMenu

        menuItem {
            attrs.onClickFunction = handleToggleAutoAdjustGadgets
            checkbox { attrs.checked = autoAdjustGadgets }
            listItemText { +"Auto-Adjust Gadgets" }
        }

        menuItem {
            attrs.onClickFunction = handleToggleFullRange
            checkbox { attrs.checked = fullRange }
            listItemText { +"Full Range" }
        }

        divider {}

        menuItem {
            attrs.onClickFunction = handleDefaultsClick
            listItemText { +"Reset to Defaults" }
        }

        divider {}

        menuItem {
            attrs.onClickFunction = handleShowDiagnosticsClick
            listItemText { +"Show Diagnostics…" }
        }
    }

    if (showDiagnosticsAnchor != null) {
        shaderDiagnostics {
            attrs.anchor = showDiagnosticsAnchor
            attrs.builder = editingShader.shaderBuilder
            attrs.onClose = { showDiagnosticsAnchor = null }
        }
    }
}

external interface ShaderInstanceEditorProps : RProps {
    var editableManager: EditableManager
    var mutablePatch: MutablePatch
    var mutableShaderInstance: MutableShaderInstance
}

fun RBuilder.shaderInstanceEditor(handler: RHandler<ShaderInstanceEditorProps>) =
    child(ShaderInstanceEditor, handler = handler)
