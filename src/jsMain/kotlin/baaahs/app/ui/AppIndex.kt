package baaahs.app.ui

import baaahs.ShowEditorState
import baaahs.app.ui.editor.EditableManager
import baaahs.app.ui.editor.editableManagerUi
import baaahs.app.ui.editor.layout.layoutEditorDialog
import baaahs.app.ui.settings.UiSettings
import baaahs.app.ui.settings.settingsDialog
import baaahs.client.ClientStageManager
import baaahs.client.WebClient
import baaahs.gl.withCache
import baaahs.io.Fs
import baaahs.io.ResourcesFs
import baaahs.mapper.Storage
import baaahs.show.SampleData
import baaahs.ui.*
import baaahs.util.JsClock
import baaahs.util.UndoStack
import baaahs.window
import external.ErrorBoundary
import kotlinext.js.jsObject
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.html.js.onClickFunction
import materialui.components.backdrop.backdrop
import materialui.components.backdrop.enum.BackdropStyle
import materialui.components.circularprogress.circularProgress
import materialui.components.container.container
import materialui.components.cssbaseline.cssBaseline
import materialui.components.dialog.dialog
import materialui.components.dialogcontent.dialogContent
import materialui.components.dialogtitle.dialogTitle
import materialui.components.list.list
import materialui.components.listitem.listItem
import materialui.components.listitemtext.listItemText
import materialui.components.paper.enums.PaperStyle
import materialui.components.paper.paper
import materialui.components.typography.typographyH6
import materialui.icon
import materialui.lab.components.alert.alert
import materialui.lab.components.alert.enums.AlertSeverity
import materialui.lab.components.alerttitle.alertTitle
import materialui.styles.createMuiTheme
import materialui.styles.muitheme.options.palette
import materialui.styles.palette.PaletteType
import materialui.styles.palette.options.type
import materialui.styles.themeprovider.themeProvider
import react.*
import react.dom.div
import react.dom.p
import react.dom.pre

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

    val allStyles = memo(theme) { AllStyles(theme)}

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

    var fileDialogOpen by state { false }
    var fileDialogIsSaveAs by state { false }
    val handleFileSelected = callback { file: Fs.File ->
        fileDialogOpen = false
        if (fileDialogIsSaveAs) {
            webClient.onSaveAsShow(file.withExtension(".sparkle"))
        } else {
            webClient.onOpenShow(file)
        }
    }
    val handleFileDialogCancel = callback { fileDialogOpen = false }

    fun confirmCloseUnsaved(): Boolean {
        return true
    }

    val handleNewShow = callback {
        if (webClient.showIsModified) confirmCloseUnsaved() || return@callback
        renderDialog = {
            dialog {
                attrs.open = true
                attrs.onClose = { _, _ -> renderDialog = null }

                dialogTitle { +"New show…" }
                dialogContent {
                    list {
                        listItem {
                            attrs.button = true
                            attrs.onClickFunction = { _ ->
                                webClient.onNewShow()
                                renderDialog = null
                            }
                            listItemText {
                                attrs.primary { +"Blank" }
                            }
                        }
                    }
                    list {
                        listItem {
                            attrs.button = true
                            attrs.onClickFunction = { _ ->
                                webClient.onNewShow(SampleData.createSampleShow(withHeadlightsMode = true).getShow())
                                renderDialog = null
                            }
                            listItemText {
                                attrs.primary { +"Sample template" }
                            }
                        }
                    }
                    list {
                        listItem {
                            attrs.button = true
                            attrs.onClickFunction = { _ ->
                                GlobalScope.launch {
                                    val fs = ResourcesFs()
                                    val file = fs.resolve("Honcho.sparkle")
                                    val show = Storage(fs, webClient.plugins).loadShow(file)
                                        ?.copy(title = "New Show")
                                        ?: error("Couldn't find show")
                                    webClient.onNewShow(show)
                                    renderDialog = null
                                }
                            }
                            listItemText {
                                attrs.primary { +"Fancy template" }
                            }
                        }
                    }
                }
            }
        }
    }

    val handleOpenShow = callback {
        if (webClient.showIsModified) confirmCloseUnsaved() || return@callback
        fileDialogOpen = true
        fileDialogIsSaveAs = false
    }

    val handleSaveShow = callback {
        webClient.onSaveShow()
    }

    val handleSaveShowAs = callback {
        fileDialogOpen = true
        fileDialogIsSaveAs = true
    }

    val handleCloseShow = callback {
        if (webClient.showIsModified) confirmCloseUnsaved() || return@callback
        webClient.onCloseShow()
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

    val forceAppDrawerOpen = webClient.isLoaded && webClient.isNoOpenShow
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

                div(+Styles.root and appDrawerStateStyle and editModeStyle) {
                    appToolbar {
                        attrs.editMode = editMode
                        attrs.onEditModeChange = handleEditModeChange
                        attrs.onMenuButtonClick = handleAppDrawerToggle
                        attrs.undoStack = props.undoStack
                        attrs.onSaveShow = handleSaveShow
                        attrs.onSaveShowAs = handleSaveShowAs
                    }

                    appDrawer {
                        attrs.open = renderAppDrawerOpen
                        attrs.forcedOpen = forceAppDrawerOpen
                        attrs.onClose = handleAppDrawerToggle
                        attrs.showLoaded = show != null
                        attrs.showFile = webClient.showFile
                        attrs.editMode = editMode
                        attrs.showUnsaved = webClient.showIsModified
                        attrs.onEditModeChange = handleEditModeChange
                        attrs.onLayoutEditorDialogToggle = handleLayoutEditorDialogToggle
                        attrs.darkMode = darkMode
                        attrs.onDarkModeChange = handleDarkModeChange
                        attrs.onNewShow = handleNewShow
                        attrs.onOpenShow = handleOpenShow
                        attrs.onSaveShow = handleSaveShow
                        attrs.onSaveShowAs = handleSaveShowAs
                        attrs.onCloseShow = handleCloseShow
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

                        // TODO: this doesn't actuyally show up for some reason?
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
                                if (webClient.isLoaded && webClient.isNoOpenShow) {
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
                                            myAppContext.webClient.onShowEdit(newMutableShow)
                                        }
                                        attrs.onClose = handleLayoutEditorDialogClose
                                    }
                                }
                            }
                        }
                    }
                }

                if (fileDialogOpen) {
                    fileDialog {
                        attrs.isOpen = fileDialogOpen
                        attrs.title = if (fileDialogIsSaveAs) "Save Show As…" else "Open Show…"
                        attrs.isSaveAs = fileDialogIsSaveAs
                        attrs.fileDisplayCallback = { file, fileDisplay ->
                            if (file.isDirectory == false) {
                                fileDisplay.isSelectable = file.name.endsWith(".sparkle")
                            }
                        }
                        attrs.onSelect = handleFileSelected
                        attrs.onCancel = handleFileDialogCancel
                        attrs.defaultTarget = webClient.showFile
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

                webClient.serverNotices.let { serverNotices ->
                    if (serverNotices.isNotEmpty()) {
                        backdrop(Styles.serverNoticeBackdrop on BackdropStyle.root) {
                            attrs { open = true }

                            div {
                                serverNotices.forEach { serverNotice ->
                                    alert {
                                        attrs.severity = AlertSeverity.error
                                        attrs.onClose = { webClient.confirmServerNotice(serverNotice.id) }

                                        alertTitle {
                                            +serverNotice.title
                                        }

                                        serverNotice.message?.let {
                                            div(+Styles.serverNoticeMessage) { +it }
                                        }

                                        serverNotice.stackTrace?.let {
                                            pre(+Styles.serverNoticeStackTrace) { +it }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

external interface AppIndexProps : RProps {
    var id: String
    var webClient: WebClient.Facade
    var undoStack: UndoStack<ShowEditorState>
    var stageManager: ClientStageManager
}

fun RBuilder.appIndex(handler: RHandler<AppIndexProps>): ReactElement =
    child(AppIndex, handler = handler)
