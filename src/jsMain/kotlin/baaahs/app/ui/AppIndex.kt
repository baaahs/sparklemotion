package baaahs.app.ui

import baaahs.app.settings.UiSettings
import baaahs.app.ui.editor.SceneEditableManager
import baaahs.app.ui.editor.ShowEditableManager
import baaahs.app.ui.editor.editableManagerUi
import baaahs.app.ui.editor.layout.layoutEditorDialog
import baaahs.app.ui.layout.GridLayoutContext
import baaahs.app.ui.settings.settingsDialog
import baaahs.client.ClientStageManager
import baaahs.client.SceneEditorClient
import baaahs.client.WebClient
import baaahs.client.document.DocumentManager
import baaahs.client.document.SceneManager
import baaahs.client.document.ShowManager
import baaahs.gl.withCache
import baaahs.mapper.JsMapper
import baaahs.mapper.sceneEditor
import baaahs.show.mutable.MutableShow
import baaahs.ui.*
import baaahs.util.JsClock
import baaahs.window
import csstype.Display
import csstype.ZIndex
import external.ErrorBoundary
import kotlinx.js.jso
import materialui.icon
import mui.icons.material.NotificationImportant
import mui.material.*
import mui.material.styles.ThemeProvider
import mui.system.sx
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.div
import react.dom.p

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

    val appMode = uiSettings.appMode
    val handleAppModeChange by handler(handleUiSettingsChange) { newAppMode: AppMode ->
        handleUiSettingsChange { it.copy(appMode = newAppMode) }
    }

    val allStyles = memo(theme) { AllStyles(theme) }

    val dragNDrop by state { ReactBeautifulDragNDrop() }
    var prompt by state<Prompt?> { null }
    val editableManager by state { ShowEditableManager { newShow -> showManager.onEdit(newShow) } }
    val sceneEditableManager by state { SceneEditableManager { newScene -> sceneManager.onEdit(newScene) } }
    val gridLayoutContext = memo { GridLayoutContext() }
    val keyboard = memo { KeyboardShortcutHandler() }

    val myAppContext = memo(uiSettings, allStyles) {
        jso<AppContext> {
            this.showPlayer = props.stageManager
            this.dragNDrop = dragNDrop
            this.webClient = webClient
            this.plugins = webClient.plugins
            this.uiSettings = uiSettings
            this.allStyles = allStyles
            this.prompt = { prompt = it }
            this.keyboard = keyboard
            this.clock = JsClock
            this.showManager = props.showManager
            this.sceneManager = props.sceneManager
            this.sceneProvider = webClient.sceneProvider
            this.notifier = webClient.notifier
            this.gridLayoutContext = gridLayoutContext

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

    val myAppGlContext = memo { jso<AppGlContext> { this.sharedGlContext = null } }

    val documentManager = appMode.getDocumentManager(myAppContext)

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
    val handleLayoutEditorChange by handler(showManager) { show: MutableShow, pushToUndoStack: Boolean ->
        showManager.onEdit(show, pushToUndoStack)
    }

    val handleShowStateChange = callback {
        // TODO: don't pass this around? ... and forceRender() is unnecessary.
        showManager.onShowStateChange()
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

    val forceAppDrawerOpen = webClient.serverIsOnline &&
            documentManager.everSynced && !documentManager.isLoaded
    val renderAppDrawerOpen = appDrawerOpen && !layoutEditorDialogOpen || forceAppDrawerOpen

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
                Keypress("s", metaKey = true),
                Keypress("s", ctrlKey = true) -> {
                    myAppContext.notifier.launchAndReportErrors { documentManager.onSave() }
                }
                Keypress("z", metaKey = true),
                Keypress("z", ctrlKey = true) -> {
                    documentManager.undo()
                }
                Keypress("z", metaKey = true, shiftKey = true),
                Keypress("z", ctrlKey = true, shiftKey = true) -> {
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

    appContext.Provider {
        attrs.value = myAppContext

        toolchainContext.Provider {
            attrs.value = toolchain

            appGlContext.Provider {
                attrs.value = myAppGlContext

                ThemeProvider {
                    attrs.theme = theme
                    CssBaseline {}

                    Paper {
                        attrs.classes = jso { this.root = -themeStyles.appRoot and appDrawerStateStyle and editModeStyle }

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

                        div(+themeStyles.appContent) {
                            if (!webClient.isConnected) {
                                Paper {
                                    attrs.classes = jso { root = -themeStyles.noShowLoadedPaper }
                                    CircularProgress {}
                                    icon(NotificationImportant)
                                    typographyH6 { +"Connecting…" }
                                    +"Attempting to connect to Sparkle Motion."
                                }
                            } else if (!webClient.serverIsOnline) {
                                Paper {
                                    attrs.classes = jso { root = -themeStyles.noShowLoadedPaper }
                                    CircularProgress {}
                                    icon(NotificationImportant)
                                    typographyH6 { +"Connecting…" }
                                    +"Sparkle Motion is initializing."
                                }
                            } else {
                                ErrorBoundary {
                                    attrs.FallbackComponent = ErrorDisplay

                                    when (appMode) {
                                        AppMode.Show -> {
                                            if (!webClient.showManagerIsReady) {
                                                Paper {
                                                    attrs.classes = jso { root = -themeStyles.noShowLoadedPaper }
                                                    CircularProgress {}
                                                    NotificationImportant {}
                                                    typographyH6 { +"Connecting…" }
                                                    +"Show manager is initializing."
                                                }
                                            } else if (show == null) {
                                                Paper {
                                                    attrs.classes = jso { root = -themeStyles.noShowLoadedPaper }
                                                    NotificationImportant {}
                                                    typographyH6 { +"No open show." }
                                                    p { +"Maybe you'd like to open one? " }
                                                }
                                            } else if (props.webClient.isMapping) {
                                                Backdrop {
                                                    attrs.open = true
                                                    Container {
                                                        CircularProgress {}
                                                        icon(NotificationImportant)

                                                        typographyH6 { +"Mapper Running…" }
                                                        +"Please wait."
                                                    }
                                                }
                                            } else {
                                                showUi {
                                                    attrs.show = showManager.openShow!!
                                                    attrs.onShowStateChange = handleShowStateChange
                                                    attrs.onLayoutEditorDialogToggle = handleLayoutEditorDialogToggle
                                                }

                                                if (layoutEditorDialogOpen) {
                                                    // Layout Editor dialog
                                                    layoutEditorDialog {
                                                        attrs.open = layoutEditorDialogOpen
                                                        attrs.show = show
                                                        attrs.onApply = handleLayoutEditorChange
                                                        attrs.onClose = handleLayoutEditorDialogClose
                                                    }
                                                }
                                            }

                                            if (webClient.sceneProvider.openScene == null) {
                                                Backdrop {
                                                    attrs.open = true
                                                    attrs.sx { zIndex = 100 as ZIndex; display = Display.grid }
                                                    Container {
                                                        icon(NotificationImportant)

                                                        typographyH6 { +"No scene loaded." }
                                                        +"Maybe you'd like to open one?"
                                                    }
                                                }
                                            }
                                        }

                                        AppMode.Scene -> {
                                            if (props.sceneManager.scene == null) {
                                                Paper {
                                                    attrs.classes = jso { root = -themeStyles.noShowLoadedPaper }
                                                    icon(NotificationImportant)
                                                    typographyH6 { +"No open scene." }
                                                    p { +"Maybe you'd like to open one? " }
                                                }
                                            } else {
                                                sceneEditor {
                                                    attrs.sceneEditorClient = props.sceneEditorClient
                                                    attrs.mapper = props.mapper
                                                    attrs.sceneManager = sceneManager
                                                }
                                            }
                                        }
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

                    fileDialog {
                        attrs.fileDialog = webClient.fileDialog
                    }
                }
            }
        }
    }
}

enum class AppMode {
    Show {
        override val otherOne: AppMode get() = Scene
        override fun getDocumentManager(appContext: AppContext): ShowManager.Facade =
            appContext.showManager
    },
    Scene {
        override val otherOne: AppMode get() = Show
        override fun getDocumentManager(appContext: AppContext): SceneManager.Facade =
            appContext.sceneManager
    };

    abstract val otherOne: AppMode
    abstract fun getDocumentManager(appContext: AppContext): DocumentManager<*, *>.Facade
}

external interface AppIndexProps : Props {
    var id: String
    var webClient: WebClient.Facade
    var stageManager: ClientStageManager
    var showManager: ShowManager.Facade
    var sceneManager: SceneManager.Facade

    var sceneEditorClient: SceneEditorClient.Facade
    var mapper: JsMapper
}

fun RBuilder.appIndex(handler: RHandler<AppIndexProps>) =
    child(AppIndex, handler = handler)
