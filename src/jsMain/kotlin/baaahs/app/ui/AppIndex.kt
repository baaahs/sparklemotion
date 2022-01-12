package baaahs.app.ui

import baaahs.app.settings.UiSettings
import baaahs.app.ui.editor.ShowEditableManager
import baaahs.app.ui.editor.editableManagerUi
import baaahs.app.ui.editor.layout.layoutEditorDialog
import baaahs.app.ui.settings.settingsDialog
import baaahs.client.ClientStageManager
import baaahs.client.SceneEditorClient
import baaahs.client.WebClient
import baaahs.client.document.DocumentManager
import baaahs.client.document.SceneManager
import baaahs.client.document.ShowManager
import baaahs.gl.withCache
import baaahs.mapper.JsMapperUi
import baaahs.mapper.sceneEditor
import baaahs.ui.*
import baaahs.util.JsClock
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
    val sceneManager = props.sceneManager
    observe(sceneManager)
    val showManager = props.showManager
    observe(showManager)

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

    var appMode by state { AppMode.Scene }
    val handleAppModeChange by handler { newAppMode: AppMode ->
        appMode = newAppMode
    }

    val allStyles = memo(theme) { AllStyles(theme) }

    val dragNDrop by state { ReactBeautifulDragNDrop() }
    var prompt by state<Prompt?> { null }
    val editableManager by state {
        ShowEditableManager { newShow ->
            showManager.onEdit(newShow)
        }
    }

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
                    showManager.show!!, editIntent, webClient.toolchain.withCache("Edit Session")
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

    val forceAppDrawerOpen = webClient.isLoaded && !showManager.isLoaded
    val renderAppDrawerOpen = appDrawerOpen && !layoutEditorDialogOpen || forceAppDrawerOpen

    val appDrawerStateStyle = if (renderAppDrawerOpen)
        themeStyles.appDrawerOpen
    else
        themeStyles.appDrawerClosed

    val editModeStyle =
        if (editMode) Styles.editModeOn else Styles.editModeOff

    val show = showManager.show

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

                        if (!webClient.isLoaded) {
                            paper(themeStyles.noShowLoadedPaper on PaperStyle.root) {
                                circularProgress {}
                                typographyH6 { +"Loading Show…" }
                            }
                        } else {
                            ErrorBoundary {
                                attrs.FallbackComponent = ErrorDisplay

                                when (appMode) {
                                    AppMode.Show -> {
                                        if (show == null) {
                                            paper(themeStyles.noShowLoadedPaper on PaperStyle.root) {
                                                icon(materialui.icons.NotificationImportant)
                                                typographyH6 { +"No open show." }
                                                p { +"Maybe you'd like to open one? " }
                                            }
                                        } else {
                                            showUi {
                                                attrs.show = showManager.openShow!!
                                                attrs.onShowStateChange = handleShowStateChange
                                                attrs.editMode = editMode
                                            }

                                            if (layoutEditorDialogOpen) {
                                                // Layout Editor dialog
                                                layoutEditorDialog {
                                                    attrs.open = layoutEditorDialogOpen
                                                    attrs.show = show
                                                    attrs.onApply = { newMutableShow ->
                                                        showManager.onEdit(newMutableShow)
                                                    }
                                                    attrs.onClose = handleLayoutEditorDialogClose
                                                }
                                            }
                                        }
                                    }

                                    AppMode.Scene -> {
                                        if (props.sceneManager.scene == null) {
                                            paper(themeStyles.noShowLoadedPaper on PaperStyle.root) {
                                                icon(materialui.icons.NotificationImportant)
                                                typographyH6 { +"No open scene." }
                                                p { +"Maybe you'd like to open one? " }
                                            }
                                        } else {
                                            sceneEditor {
                                                attrs.sceneEditorClient = props.sceneEditorClient
                                                attrs.mapperUi = props.mapperUi
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

                editableManagerUi {
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
    Show {
        override fun getDocumentManager(appContext: AppContext): ShowManager.Facade =
            appContext.showManager
    },
    Scene {
        override fun getDocumentManager(appContext: AppContext): SceneManager.Facade =
            appContext.sceneManager
    };

    abstract fun getDocumentManager(appContext: AppContext): DocumentManager<*, *>.Facade
}

external interface AppIndexProps : Props {
    var id: String
    var webClient: WebClient.Facade
    var stageManager: ClientStageManager
    var showManager: ShowManager.Facade
    var sceneManager: SceneManager.Facade

    var sceneEditorClient: SceneEditorClient.Facade
    var mapperUi: JsMapperUi
}

fun RBuilder.appIndex(handler: RHandler<AppIndexProps>) =
    child(AppIndex, handler = handler)
