package baaahs.app.ui

import baaahs.ShowEditorState
import baaahs.app.settings.UiSettings
import baaahs.app.ui.editor.EditableManager
import baaahs.app.ui.editor.editableManagerUi
import baaahs.app.ui.editor.layout.layoutEditorDialog
import baaahs.app.ui.settings.settingsDialog
import baaahs.client.ClientStageManager
import baaahs.client.SceneEditorClient
import baaahs.client.WebClient
import baaahs.client.document.SceneManager
import baaahs.client.document.ShowManager
import baaahs.gl.withCache
import baaahs.mapper.JsMapperUi
import baaahs.mapper.sceneEditor
import baaahs.ui.*
import baaahs.util.JsClock
import baaahs.util.UndoStack
import baaahs.window
import external.ErrorBoundary
import kotlinext.js.jsObject
import materialui.components.backdrop.backdrop
import materialui.components.circularprogress.circularProgress
import materialui.components.container.container
import materialui.components.cssbaseline.cssBaseline
import materialui.components.paper.enums.PaperStyle
import materialui.components.paper.paper
import materialui.components.typography.typographyH6
import materialui.icon
import materialui.styles.createMuiTheme
import materialui.styles.muitheme.options.palette
import materialui.styles.palette.PaletteType
import materialui.styles.palette.options.type
import materialui.styles.themeprovider.themeProvider
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.div
import react.dom.p

val AppIndex = xComponent<AppIndexProps>("AppIndex") { props ->
    val webClient = props.webClient
    observe(webClient)

    var editMode by state { false }
    val handleEditModeChange = callback(editMode) { editMode = !editMode }

    val uiSettings = webClient.uiSettings
    val handleUiSettingsChange by handler { callback: (UiSettings) -> UiSettings ->
        val newUiSettings = callback(props.webClient.uiSettings)
        webClient.updateUiSettings(newUiSettings, saveToStorage = true)
    }

    val handleDarkModeChange = callback(webClient) {
        handleUiSettingsChange { it.copy(darkMode = !it.darkMode) }
    }

    val darkMode = uiSettings.darkMode
    val theme = memo(darkMode) {
        createMuiTheme {
            palette {
                type = if (darkMode) PaletteType.dark else PaletteType.light
            }
        }
    }

    var appMode by state { AppMode.Show }
    val handleAppModeChange by handler { newAppMode: AppMode ->
        appMode = newAppMode
    }

    val allStyles = memo(theme) { AllStyles(theme) }

    val dragNDrop by state { ReactBeautifulDragNDrop() }
    var prompt by state<Prompt?> { null }
    val editableManager by state { EditableManager { newShow ->
        webClient.onShowEdit(newShow)
    } }

    val myAppContext = memo(uiSettings, allStyles) {
        jsObject<AppContext> {
            this.showPlayer = props.stageManager
            this.dragNDrop = dragNDrop
            this.webClient = webClient
            this.plugins = webClient.plugins
            this.toolchain = webClient.toolchain
            this.uiSettings = uiSettings
            this.allStyles = allStyles
            this.prompt = { prompt = it }
            this.clock = JsClock
            this.showManager = props.showManager
            this.sceneManager = props.sceneManager
            this.fileDialog = webClient.fileDialog
            this.notifier = webClient.notifier

            this.openEditor = { editIntent ->
                editableManager.openEditor(
                    webClient.show!!, editIntent, webClient.toolchain.withCache("Edit Session")
                )
            }
        }
    }

    val myAppGlContext = memo { jsObject<AppGlContext> { this.sharedGlContext = null } }

    onChange("global styles", allStyles) {
        allStyles.injectGlobals()
    }
    val themeStyles = allStyles.appUi

    var appDrawerOpen by state { false }
    var layoutEditorDialogOpen by state { false }
    var renderDialog by state<(RBuilder.() -> Unit)?> { null }

    val handleAppDrawerToggle =
        callback(appDrawerOpen) { appDrawerOpen = !appDrawerOpen }

    val handleLayoutEditorDialogToggle =
        callback(layoutEditorDialogOpen) { layoutEditorDialogOpen = !layoutEditorDialogOpen }
    val handleLayoutEditorDialogClose = callback { layoutEditorDialogOpen = false }

    val handleShowStateChange = callback {
        webClient.onShowStateChange()
        forceRender()
    }

    val handleSettings = callback {
        renderDialog = {
            settingsDialog {
                attrs.changeUiSettings = handleUiSettingsChange
                attrs.onClose = { renderDialog = null }
            }
        }
    }

    val handlePromptClose = callback { prompt = null }

    val forceAppDrawerOpen = webClient.isLoaded && webClient.show == null
    val renderAppDrawerOpen = appDrawerOpen && !layoutEditorDialogOpen || forceAppDrawerOpen

    val appDrawerStateStyle = if (renderAppDrawerOpen)
        themeStyles.appDrawerOpen
    else
        themeStyles.appDrawerClosed

    val editModeStyle =
        if (editMode) Styles.editModeOn else Styles.editModeOff

    val show = webClient.show

    onMount {
        val keyboardShortcutHandler = KeyboardShortcutHandler { event ->
            when (event.key) {
                "d" -> {
                    editMode = !editMode
                    event.stopPropagation()
                }
            }
        }
        keyboardShortcutHandler.listen(window)
        withCleanup {
            keyboardShortcutHandler.unlisten(window)
        }
    }

    appContext.Provider {
        attrs.value = myAppContext

        appGlContext.Provider {
            attrs.value = myAppGlContext

            themeProvider(theme) {
                cssBaseline { }

                div(+themeStyles.appRoot and appDrawerStateStyle and editModeStyle) {
                    appToolbar {
                        attrs.appMode = appMode
                        attrs.editMode = editMode
                        attrs.onEditModeChange = handleEditModeChange
                        attrs.onMenuButtonClick = handleAppDrawerToggle
                        attrs.undoStack = props.undoStack
                    }

                    appDrawer {
                        attrs.open = renderAppDrawerOpen
                        attrs.forcedOpen = forceAppDrawerOpen
                        attrs.onClose = handleAppDrawerToggle
                        attrs.appMode = appMode
                        attrs.onAppModeChange = handleAppModeChange
                        attrs.editMode = editMode
                        attrs.onEditModeChange = handleEditModeChange
                        attrs.onLayoutEditorDialogToggle = handleLayoutEditorDialogToggle
                        attrs.darkMode = darkMode
                        attrs.onDarkModeChange = handleDarkModeChange
                        attrs.onSettings = handleSettings
                    }

                    div(+themeStyles.appContent) {
                        backdrop {
                            attrs {
                                open = !webClient.isConnected
                            }

                            container {
                                circularProgress {}
                                icon(materialui.icons.NotificationImportant)

                                typographyH6 { +"Connecting…" }
                                +"Attempting to connect to Sparkle Motion."
                            }
                        }

                        // TODO: this doesn't actually show up for some reason?
                        if (props.webClient.isMapping) {
                            backdrop {
                                attrs {
                                    open = true
                                }

                                container {
                                    circularProgress {}
                                    icon(materialui.icons.NotificationImportant)

                                    typographyH6 { +"Mapper Running…" }
                                    +"Please wait."
                                }
                            }
                        }

                        if (show == null) {
                            paper(themeStyles.noShowLoadedPaper on PaperStyle.root) {
                                if (webClient.isLoaded) {
                                    icon(materialui.icons.NotificationImportant)
                                    typographyH6 { +"No open show." }
                                    p { +"Maybe you'd like to open one? " }
                                } else {
                                    circularProgress {}
                                    typographyH6 { +"Loading Show…" }
                                }
                            }
                        } else {
                            ErrorBoundary {
                                attrs.FallbackComponent = ErrorDisplay

                                when (appMode) {
                                    AppMode.Show -> {
                                        showUi {
                                            attrs.show = webClient.openShow!!
                                            attrs.onShowStateChange = handleShowStateChange
                                            attrs.editMode = editMode
                                        }

                                        if (layoutEditorDialogOpen) {
                                            // Layout Editor dialog
                                            layoutEditorDialog {
                                                attrs.open = layoutEditorDialogOpen
                                                attrs.show = show
                                                attrs.onApply = { newMutableShow ->
                                                    props.webClient.onShowEdit(newMutableShow)
                                                }
                                                attrs.onClose = handleLayoutEditorDialogClose
                                            }
                                        }
                                    }

                                    AppMode.Scene -> {
                                        sceneEditor {
                                            attrs.sceneEditorClient = props.sceneEditorClient
                                            attrs.mapperUi = props.mapperUi
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                renderDialog?.invoke(this)

                editableManagerUi {
                    attrs.editMode = editMode
                    attrs.editableManager = editableManager
                }

                prompt?.let {
                    promptDialog {
                        attrs.prompt = it
                        attrs.onClose = handlePromptClose
                    }
                }

                notifier {
                    attrs.notifier = webClient.notifier
                }

                fileDialog {}
            }
        }
    }
}

enum class AppMode {
    Show, Scene
}

external interface AppIndexProps : Props {
    var id: String
    var webClient: WebClient.Facade
    var undoStack: UndoStack<ShowEditorState>
    var stageManager: ClientStageManager
    var showManager: ShowManager
    var sceneManager: SceneManager

    var sceneEditorClient: SceneEditorClient.Facade
    var mapperUi: JsMapperUi
}

fun RBuilder.appIndex(handler: RHandler<AppIndexProps>) =
    child(AppIndex, handler = handler)
