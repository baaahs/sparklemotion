package baaahs.app.ui.editor

import baaahs.app.ui.appContext
import baaahs.app.ui.editor.help.shaderHelp
import baaahs.app.ui.shaderDiagnostics
import baaahs.app.ui.shaderPreview
import baaahs.app.ui.toolchainContext
import baaahs.gl.preview.GadgetAdjuster
import baaahs.gl.preview.PreviewShaderBuilder
import baaahs.gl.withCache
import baaahs.show.mutable.EditingShader
import baaahs.show.mutable.MutablePatch
import baaahs.show.mutable.MutableShow
import baaahs.ui.addObserver
import baaahs.ui.unaryMinus
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import js.objects.jso
import materialui.icon
import mui.material.*
import mui.system.sx
import react.*
import react.dom.div
import react.dom.onClick
import web.cssom.Auto
import web.dom.Element
import web.events.Event

private enum class PageTabs {
    Patch, Ports, Gadgets, Help
}

private val PatchEditorView = xComponent<PatchEditorProps>("PatchEditor") { props ->
    val appContext = useContext(appContext)
    val shaderEditorStyles = appContext.allStyles.shaderEditor

    val baseToolchain = useContext(toolchainContext)
    val toolchain = memo(baseToolchain) { baseToolchain.withCache("Editor") }

    var settingsMenuAnchor by state<Element?> { null }
    val showSettingsMenu by mouseEventHandler { event -> settingsMenuAnchor = event.target as Element? }
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
                PreviewShaderBuilder(shader, toolchain, appContext.sceneProvider)
            }

        val observer = newEditingShader.addObserver {
            props.editableManager.onChange(pushToUndoStack = false)
            forceRender()
        }
        withCleanup { observer.remove() }

        newEditingShader
    }

    var autoAdjustGadgets by state { false }
    val handleToggleAutoAdjustGadgets by mouseEventHandler {
        autoAdjustGadgets = !autoAdjustGadgets
        settingsMenuAnchor = null
    }

    var fullRange by state { false }
    val handleToggleFullRange by mouseEventHandler {
        autoAdjustGadgets = true
        fullRange = !fullRange
        settingsMenuAnchor = null
    }

    val handleDefaultsClick by mouseEventHandler {
        autoAdjustGadgets = false
        editingShader.gadgets.forEach { gadget -> gadget.openControl.resetToDefault() }
        settingsMenuAnchor = null
    }

    var showDiagnosticsAnchor by state<Element?> { null }
    val handleShowDiagnosticsClick by mouseEventHandler {
        showDiagnosticsAnchor = settingsMenuAnchor
        settingsMenuAnchor = null
    }

    toolchainContext.Provider {
        attrs.value = toolchain

        div(+shaderEditorStyles.container) {
            div(+shaderEditorStyles.shaderEditor) {
                shaderEditor {
                    attrs.editingShader = editingShader
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
                    attrs.onClick = showSettingsMenu

                    icon(mui.icons.material.Settings)
                }
            }

            div(+shaderEditorStyles.propsTabsAndPanels) {
                Tabs {
                    attrs.classes = jso { this.flexContainer = -shaderEditorStyles.tabsContainer }
                    attrs.value = selectedTab
                    attrs.onChange = handleChangeTab.asDynamic()
                    attrs.orientation = Orientation.horizontal
                    attrs.variant = TabsVariant.scrollable
                    PageTabs.values().forEach { tab ->
                        Tab {
                            attrs.classes = jso { this.root = -shaderEditorStyles.tab }
                            attrs.label = buildElement { +tab.name }
                            attrs.value = tab.asDynamic()
                            attrs.sx { minWidth = Auto.auto }
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
        }
    }

    Menu {
        attrs.anchorEl = settingsMenuAnchor.asDynamic()
        attrs.anchorOrigin = jso {
            horizontal = "left"
            vertical = "bottom"
        }
        attrs.open = settingsMenuAnchor != null
        attrs.onClose = hideSettingsMenu

        MenuItem {
            attrs.onClick = handleToggleAutoAdjustGadgets
            Checkbox { attrs.checked = autoAdjustGadgets }
            ListItemText { +"Auto-Adjust Gadgets" }
        }

        MenuItem {
            attrs.onClick = handleToggleFullRange
            Checkbox { attrs.checked = fullRange }
            ListItemText { +"Full Range" }
        }

        Divider {}

        MenuItem {
            attrs.onClick = handleDefaultsClick
            ListItemText { +"Reset to Defaults" }
        }

        Divider {}

        MenuItem {
            attrs.onClick = handleShowDiagnosticsClick
            ListItemText { +"Show Diagnostics…" }
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
