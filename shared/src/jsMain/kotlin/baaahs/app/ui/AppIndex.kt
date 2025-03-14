package baaahs.app.ui

import baaahs.app.settings.UiSettings
import baaahs.app.ui.editor.SceneEditableManager
import baaahs.app.ui.editor.ShowEditableManager
import baaahs.app.ui.editor.editableManagerUi
import baaahs.app.ui.editor.layout.layoutEditorDialog
import baaahs.app.ui.editor.shaderLibraryDialog
import baaahs.app.ui.settings.settingsDialog
import baaahs.client.ClientStageManager
import baaahs.client.SceneEditorClient
import baaahs.client.WebClient
import baaahs.client.document.SceneManager
import baaahs.client.document.ShowManager
import baaahs.gl.withCache
import baaahs.mapper.JsMapperBuilder
import baaahs.mapper.sceneEditor
import baaahs.mapper.styleIf
import baaahs.show.mutable.MutableShow
import baaahs.ui.*
import baaahs.util.SystemClock
import baaahs.window
import external.ErrorBoundary
import js.objects.jso
import materialui.icon
import mui.material.CssBaseline
import mui.material.Paper
import mui.material.styles.ThemeProvider
import mui.system.useMediaQuery
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.div

val AppIndex = xComponent<AppIndexProps>("AppIndex") { props ->
    val webClient = props.webClient
    observe(webClient)
    val sceneManager = props.sceneManager
    observe(sceneManager)
    val showManager = props.showManager
    observe(showManager)

    val uiSettings = webClient.uiSettings
    val handleUiSettingsChange by handler(webClient, webClient.uiSettings) { callback: (UiSettings) -> UiSettings ->
        val newUiSettings = callback(webClient.uiSettings)
        webClient.updateUiSettings(newUiSettings, saveToStorage = true)
    }

    val darkMode = uiSettings.darkMode
    val handleDarkModeChange = callback(handleUiSettingsChange) {
        handleUiSettingsChange { it.copy(darkMode = !it.darkMode) }
    }
    val theme = if (darkMode) Themes.Dark else Themes.Light
    val isSmallScreen = useMediaQuery(theme.isSmallScreen)

    val appMode = uiSettings.appMode
    val handleAppModeChange by handler(handleUiSettingsChange) { newAppMode: AppMode ->
        handleUiSettingsChange { it.copy(appMode = newAppMode) }
    }

    val allStyles = memo(theme) { AllStyles(theme) }

    var prompt by state<Prompt?> { null }
    val editableManager by state { ShowEditableManager { newShow -> showManager.onEdit(newShow) } }
    val sceneEditableManager by state { SceneEditableManager { newScene -> sceneManager.onEdit(newScene) } }
    val keyboard = memo { KeyboardShortcutHandler() }

    val myAppContext = memo(uiSettings, allStyles) {
        jso<AppContext> {
            this.showPlayer = props.stageManager
            this.webClient = webClient
            this.plugins = webClient.plugins
            this.uiSettings = uiSettings
            this.allStyles = allStyles
            this.prompt = { prompt = it }
            this.keyboard = keyboard
            this.clock = SystemClock
            this.showManager = props.showManager
            this.sceneManager = props.sceneManager
            this.sceneProvider = webClient.sceneProvider
            this.shaderLibraries = webClient.shaderLibraries
            this.fileDialog = webClient.fileDialog
            this.notifier = webClient.notifier
            this.featureFlags = webClient.featureFlags

            this.openEditor = { editIntent ->
                editableManager.openEditor(
                    showManager.show!!, editIntent, webClient.toolchain.withCache("Edit Session")
                )
            }

            this.openSceneEditor = { editIntent ->
                sceneEditableManager.openEditor(sceneManager.scene!!, editIntent)
            }

            this.sceneEditorClient = props.sceneEditorClient
        }
    }

    val myAppGlSharingContext = memo { jso<AppGlSharingContext> {} }
    val documentManager = appMode.getDocumentManager(myAppContext)

    onChange("global styles", allStyles) {
        allStyles.injectGlobals()
    }
    val themeStyles = allStyles.appUi

    var appDrawerOpen by state { false }
    var renderDialog by state<(RBuilder.() -> Unit)?> { null }

    val handleAppDrawerToggle =
        callback(appDrawerOpen) { appDrawerOpen = !appDrawerOpen }

    var showLayoutEditorDialog by state { false }
    val handleLayoutEditorDialogToggle =
        callback(showLayoutEditorDialog) { showLayoutEditorDialog = !showLayoutEditorDialog }
    val handleLayoutEditorDialogClose = callback { showLayoutEditorDialog = false }
    val handleLayoutEditorChange by handler(showManager) { show: MutableShow, pushToUndoStack: Boolean ->
        showManager.onEdit(show, pushToUndoStack)
    }

    var showShaderLibraryDialog by state { false }
    val handleShaderLibraryDialogToggle =
        callback(showShaderLibraryDialog) { showShaderLibraryDialog = !showShaderLibraryDialog }
//    val handleShaderLibrarySelect = callback { _: Shader? -> showShaderLibraryDialog = false }

    val handleSettings = callback {
        renderDialog = {
            settingsDialog {
                attrs.changeUiSettings = handleUiSettingsChange
                attrs.onClose = { renderDialog = null }
            }
        }
    }

    val handlePromptClose = callback { prompt = null }

    val forceAppDrawerOpen = webClient.serverIsOnline &&
            documentManager.everSynced && !documentManager.isLoaded
    val renderAppDrawerOpen = appDrawerOpen && !showLayoutEditorDialog || forceAppDrawerOpen

    val appDrawerStateStyle = if (renderAppDrawerOpen)
        themeStyles.appDrawerOpen
    else
        themeStyles.appDrawerClosed

    val editMode = observe(documentManager.editMode)
    val editModeStyle =
        if (editMode.isOn) Styles.editModeOn else Styles.editModeOff

    val show = showManager.show
    val toolchain = memo(webClient.toolchain, show) {
        webClient.toolchain.withCache("Open Show")
    }

    onMount(keyboard) {
        keyboard.listen(window)
        withCleanup { keyboard.unlisten(window) }
    }

    onMount(
        myAppContext, keyboard, documentManager, editMode,
        handleAppDrawerToggle, handleAppModeChange
    ) {
        val handler = keyboard.handle { keypress, _ ->
            var result: KeypressResult? = null

            when (keypress) {
                Keypress("Escape") -> handleAppDrawerToggle()
                Keypress("KeyS", metaKey = true),
                Keypress("KeyS", ctrlKey = true) -> {
                    myAppContext.notifier.launchAndReportErrors { documentManager.onSave() }
                }
                Keypress("KeyZ", metaKey = true),
                Keypress("KeyZ", ctrlKey = true) -> {
                    documentManager.undo()
                }
                Keypress("KeyZ", metaKey = true, shiftKey = true),
                Keypress("KeyZ", ctrlKey = true, shiftKey = true) -> {
                    documentManager.redo()
                }
                else -> result = KeypressResult.NotHandled
            }

            result ?: KeypressResult.Handled
        }

        withCleanup {
            handler.remove()
        }
    }

    val appState = AppState.getState(webClient, show, props.sceneManager.scene)

    appContext.Provider {
        attrs.value = myAppContext

        toolchainContext.Provider {
            attrs.value = toolchain

            appGlSharingContext.Provider {
                attrs.value = myAppGlSharingContext

                ThemeProvider {
                    attrs.theme = theme
                    CssBaseline {}

                    Paper {
                        attrs.className = -themeStyles.appRoot and appDrawerStateStyle and editModeStyle

                        appDrawer {
                            attrs.open = renderAppDrawerOpen
                            attrs.forcedOpen = forceAppDrawerOpen
                            attrs.onClose = handleAppDrawerToggle
                            attrs.appMode = appMode
                            attrs.onAppModeChange = handleAppModeChange
                            attrs.documentManager = documentManager
                            attrs.onLayoutEditorDialogToggle = handleLayoutEditorDialogToggle
                            attrs.darkMode = darkMode
                            attrs.onDarkModeChange = handleDarkModeChange
                            attrs.onSettings = handleSettings
                        }

                        appToolbar {
                            attrs.appMode = appMode
                            attrs.documentManager = documentManager
                            attrs.onMenuButtonClick = handleAppDrawerToggle
                            attrs.onAppModeChange = handleAppModeChange
                        }

                        div(+themeStyles.appContent and
                                styleIf(isSmallScreen, Styles.isSmallScreen, Styles.isNotSmallScreen)
                        ) {
                            ErrorBoundary {
                                attrs.FallbackComponent = ErrorDisplay

                                if (appState is AppState.FullScreenMessage) {
                                    Paper {
                                        attrs.className = -themeStyles.fullScreenMessagePaper
                                        if (appState.isInProgress)
                                            mui.material.CircularProgress {}
                                        icon(mui.icons.material.NotificationImportant)
                                        typographyH6 { +appState.title }
                                        +appState.message
                                    }
                                } else if (appState == AppState.ShowView) {
                                    showUi {
                                        attrs.show = showManager.openShow!!
                                        attrs.onLayoutEditorDialogToggle = handleLayoutEditorDialogToggle
                                        attrs.onShaderLibraryDialogToggle = handleShaderLibraryDialogToggle
                                    }

                                    if (showLayoutEditorDialog) {
                                        // Layout Editor dialog
                                        layoutEditorDialog {
                                            attrs.open = showLayoutEditorDialog
                                            attrs.show = show!!
                                            attrs.onApply = handleLayoutEditorChange
                                            attrs.onClose = handleLayoutEditorDialogClose
                                        }
                                    }
                                    if (showShaderLibraryDialog) {
                                        shaderLibraryDialog {
                                            attrs.devWarning = true
                                        }
                                    }
                                } else if (appState == AppState.SceneView) {
                                    sceneEditor {
                                        attrs.sceneEditorClient = props.sceneEditorClient
                                        attrs.sceneManager = sceneManager
                                        attrs.mapperBuilder = props.mapperBuilder
                                    }
                                }
                            }
                        }
                    }

                    renderDialog?.invoke(this)

                    if (editMode.isAvailable) {
                        editableManagerUi {
                            attrs.editableManager =
                                when (appMode) {
                                    AppMode.Show -> editableManager
                                    AppMode.Scene -> sceneEditableManager
                                }
                        }
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
}

external interface AppIndexProps : Props {
    var id: String
    var webClient: WebClient.Facade
    var stageManager: ClientStageManager
    var showManager: ShowManager.Facade
    var sceneManager: SceneManager.Facade

    var sceneEditorClient: SceneEditorClient.Facade
    var mapperBuilder: JsMapperBuilder
}

fun RBuilder.appIndex(handler: RHandler<AppIndexProps>) =
    child(AppIndex, handler = handler)
