package baaahs.app.ui.editor

import baaahs.app.ui.appContext
import baaahs.app.ui.editor.help.shaderHelp
import baaahs.app.ui.shaderDiagnostics
import baaahs.app.ui.shaderPreview
import baaahs.app.ui.toolchainContext
import baaahs.gl.preview.GadgetAdjuster
import baaahs.gl.preview.PreviewShaderBuilder
import baaahs.gl.withCache
import baaahs.mapper.styleIf
import baaahs.show.mutable.EditingShader
import baaahs.show.mutable.MutablePatch
import baaahs.show.mutable.MutableShow
import baaahs.ui.*
import js.objects.jso
import materialui.icon
import mui.material.*
import mui.material.styles.Theme
import mui.material.styles.useTheme
import mui.system.sx
import mui.system.useMediaQuery
import react.*
import react.dom.div
import react.dom.onClick
import web.cssom.Auto
import web.dom.Element
import web.events.Event

private enum class PageTabs(
    val showGutter: Boolean = true
) {
    Source(showGutter = false), Patch, Ports, Gadgets, Help
}

private val PatchEditorView = xComponent<PatchEditorProps>("PatchEditor") { props ->
    val appContext = useContext(appContext)
    val shaderEditorStyles = appContext.allStyles.shaderEditor
    val theme = useTheme<Theme>()
    val isSmallScreen = useMediaQuery(theme.isSmallScreen)

    val baseToolchain = useContext(toolchainContext)
    val toolchain = memo(baseToolchain) { baseToolchain.withCache("Editor") }

    var settingsMenuAnchor by state<Element?> { null }
    val showSettingsMenu by mouseEventHandler { event -> settingsMenuAnchor = event.target as Element? }
    val hideSettingsMenu = callback { _: Event?, _: String? -> settingsMenuAnchor = null }

    var selectedTab by state { if (isSmallScreen) PageTabs.Source else PageTabs.Patch }
    val handleChangeTab by handler { _: Event, value: PageTabs -> selectedTab = value }
    val showSelectedTab = if (!isSmallScreen && selectedTab == PageTabs.Source) PageTabs.Patch else selectedTab

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
            if (!isSmallScreen) {
                div(+shaderEditorStyles.shaderEditor) {
                    shaderEditor {
                        attrs.editingShader = editingShader
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
                    attrs.onClick = showSettingsMenu

                    icon(mui.icons.material.Settings)
                }
            }

            div(+shaderEditorStyles.propsTabsAndPanels) {
                Tabs {
                    attrs.classes = muiClasses { flexContainer = -shaderEditorStyles.tabsContainer }
                    attrs.value = showSelectedTab
                    attrs.onChange = handleChangeTab.asDynamic()
                    attrs.orientation = Orientation.horizontal
                    attrs.variant = TabsVariant.scrollable
                    PageTabs.entries.forEach { tab ->
                        if (tab == PageTabs.Source && !isSmallScreen)
                            return@forEach

                        Tab {
                            attrs.className = -shaderEditorStyles.tab
                            attrs.label = buildElement { +tab.name }
                            attrs.value = tab.asDynamic()
                            attrs.sx { minWidth = Auto.auto }
                        }
                    }
                }

                div(+shaderEditorStyles.propsPanel and
                        styleIf(showSelectedTab.showGutter, shaderEditorStyles.withGutter)) {
                    when (showSelectedTab) {
                        PageTabs.Source -> shaderEditor {
                            attrs.editingShader = editingShader
                        }
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
                            attrs.editingShader = editingShader
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
