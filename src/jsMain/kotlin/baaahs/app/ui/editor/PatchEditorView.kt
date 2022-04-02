package baaahs.app.ui.editor

import baaahs.app.ui.appContext
import baaahs.app.ui.shaderDiagnostics
import baaahs.app.ui.shaderPreview
import baaahs.gl.preview.GadgetAdjuster
import baaahs.gl.preview.PreviewShaderBuilder
import baaahs.gl.withCache
import baaahs.show.mutable.EditingShader
import baaahs.show.mutable.MutablePatch
import baaahs.show.mutable.MutableShow
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
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.div
import react.useContext

private enum class PageTabs {
    Patch, Ports, Gadgets, Help
}

private val PatchEditorView = xComponent<PatchEditorProps>("PatchEditor") { props ->
    val appContext = useContext(appContext)
    val shaderEditorStyles = appContext.allStyles.shaderEditor

    val toolchain = memo { appContext.toolchain.withCache("Editor") }

    var settingsMenuAnchor by state<EventTarget?> { null }
    val showSettingsMenu = callback { event: Event -> settingsMenuAnchor = event.target!! }
    val hideSettingsMenu = callback { _: Event?, _: String? -> settingsMenuAnchor = null }

    var selectedTab by state { PageTabs.Patch }
    @Suppress("UNCHECKED_CAST")
    val handleChangeTab by handler { _: Event, value: PageTabs ->
        selectedTab = value
    }

    // props.mutablePatch.id is included here so we re-memoize if we have a different instance
    // from before; this happens after clicking "Apply" when the whole mutable document is regenerated.
    val editingShader = memo(props.editableManager, props.mutablePatch, props.mutablePatch.id) {
        val newEditingShader =
            EditingShader(
                props.editableManager.currentMutableDocument as MutableShow,
                props.mutablePatch,
                toolchain
            ) { shader ->
                PreviewShaderBuilder(shader, toolchain, appContext.webClient.sceneProvider)
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
                    PageTabs.Patch -> shaderPropertiesEditor {
                        attrs.editableManager = props.editableManager
                        attrs.editingShader = editingShader
                        attrs.mutablePatch = props.mutablePatch
                    }
                    PageTabs.Ports -> linksEditor {
                        attrs.editableManager = props.editableManager
                        attrs.editingShader = editingShader
                    }
                    PageTabs.Gadgets -> gadgetsPreview {
                        attrs.editingShader = editingShader
                    }
                    PageTabs.Help -> shaderHelp {
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

external interface PatchEditorProps : Props {
    var editableManager: EditableManager<*>
    var mutablePatch: MutablePatch
}

fun RBuilder.patchEditor(handler: RHandler<PatchEditorProps>) =
    child(PatchEditorView, handler = handler)
