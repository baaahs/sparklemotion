package baaahs.app.ui

import baaahs.ShowEditorState
import baaahs.ShowPlayer
import baaahs.ShowState
import baaahs.client.WebClient
import baaahs.gl.patch.AutoWirer
import baaahs.io.Fs
import baaahs.show.SampleData
import baaahs.show.Show
import baaahs.show.mutable.MutablePatchHolder
import baaahs.show.mutable.MutableShow
import baaahs.ui.*
import baaahs.util.UndoStack
import kotlinext.js.jsObject
import kotlinx.css.opacity
import kotlinx.css.properties.Timing
import kotlinx.css.properties.s
import kotlinx.css.properties.transition
import kotlinx.html.js.onChangeFunction
import kotlinx.html.js.onClickFunction
import materialui.*
import materialui.components.appbar.appBar
import materialui.components.appbar.enums.AppBarPosition
import materialui.components.appbar.enums.AppBarStyle
import materialui.components.backdrop.backdrop
import materialui.components.button.enums.ButtonColor
import materialui.components.circularprogress.circularProgress
import materialui.components.container.container
import materialui.components.cssbaseline.cssBaseline
import materialui.components.dialog.dialog
import materialui.components.dialogcontent.dialogContent
import materialui.components.dialogtitle.dialogTitle
import materialui.components.formcontrollabel.formControlLabel
import materialui.components.iconbutton.enums.IconButtonEdge
import materialui.components.iconbutton.enums.IconButtonStyle
import materialui.components.iconbutton.iconButton
import materialui.components.list.list
import materialui.components.listitem.listItem
import materialui.components.listitemtext.listItemText
import materialui.components.paper.enums.PaperStyle
import materialui.components.paper.paper
import materialui.components.portal.portal
import materialui.components.switches.switch
import materialui.components.toolbar.toolbar
import materialui.components.typography.enums.TypographyStyle
import materialui.components.typography.typographyH6
import materialui.styles.createMuiTheme
import materialui.styles.muitheme.options.palette
import materialui.styles.palette.PaletteType
import materialui.styles.palette.options.type
import materialui.styles.themeprovider.themeProvider
import org.w3c.dom.*
import org.w3c.dom.events.Event
import react.*
import react.dom.b
import react.dom.div
import react.dom.i
import react.dom.p
import styled.css
import styled.injectGlobal
import styled.styledDiv
import kotlin.browser.window

val AppIndex = xComponent<AppIndexProps>("AppIndex") { props ->
    injectGlobal(Styles.global.toString())
    injectGlobal(baaahs.app.ui.controls.Styles.global.toString())

    val webClient = props.webClient
    observe(webClient)

    var editMode by state { false }
    val handleEditModeChange = useCallback(editMode) { _: Event -> editMode = !editMode }

    var darkMode by state { false }
    val handleDarkModeChange = useCallback(darkMode) { _: Event -> darkMode = !darkMode }

    val theme = createMuiTheme {
        palette {
            type = if (darkMode) PaletteType.dark else PaletteType.light
        }
    }

    val dragNDrop by state { DragNDrop() }
    val myAppContext by state {
        jsObject<AppContext> {
            this.showPlayer = props.showPlayer
            this.dragNDrop = dragNDrop
            this.webClient = webClient
            this.plugins = webClient.plugins
            this.autoWirer = AutoWirer(webClient.plugins)
            this.allStyles = AllStyles(theme)
        }
    }

    val themeStyles = myAppContext.allStyles.appUi
    injectGlobal(themeStyles.global.toString())

    var appDrawerOpen by state { false }
    var layoutEditorDialogOpen by state { false }
    var renderDialog by state<(RBuilder.() -> Unit)?> { null }
    var mutablePatchHolder by state<MutablePatchHolder?> { null }

    val handleAppDrawerToggle =
        useCallback(appDrawerOpen) { appDrawerOpen = !appDrawerOpen }

    val handleLayoutEditorDialogToggle =
        useCallback(layoutEditorDialogOpen) { _: Event -> layoutEditorDialogOpen = !layoutEditorDialogOpen }
    val handleLayoutEditorDialogClose = useCallback { layoutEditorDialogOpen = false }

    val undoStack = props.undoStack
    val handleUndo = handler("handleUndo", undoStack) { _: Event ->
        undoStack.undo().also { (show, showState) ->
            webClient.onShowEdit(show, showState)
        }
        Unit
    }

    val handleRedo = handler("handleRedo", undoStack) { _: Event ->
        undoStack.redo().also { (show, showState) ->
            webClient.onShowEdit(show, showState)
        }
        Unit
    }

    val handleShowEdit = useCallback { newShow: Show, newShowState: ShowState ->
        val newState = webClient.onShowEdit(newShow, newShowState)
        undoStack.changed(newState)
    }

    val handleEditPatchHolder = useCallback { forEdit: MutablePatchHolder ->
        mutablePatchHolder = forEdit
    }

    val handlePatchHolderEdit = useCallback(handleShowEdit) {
        mutablePatchHolder?.let {
            handleShowEdit(it.getShow(), it.getShowState())
        }
        mutablePatchHolder = null
    }

    val handlePatchHolderClose = useCallback {
        mutablePatchHolder = null
    }

    val handleShowStateChange = useCallback { newShowState: ShowState ->
        webClient.onShowStateChange(newShowState)
    }

    var fileDialogOpen by state { false }
    var fileDialogIsSaveAs by state { false }
    val handleFileSelected = useCallback { file: Fs.File ->
        fileDialogOpen = false
        if (fileDialogIsSaveAs) {
            webClient.onSaveAsShow(file.withExtension(".sparkle"))
        } else {
            webClient.onOpenShow(file)
        }
    }
    val handleFileDialogCancel = useCallback { fileDialogOpen = false }

    fun confirmCloseUnsaved(): Boolean {
        return true
    }

    val handleNewShow = useCallback {
        if (webClient.showIsModified) confirmCloseUnsaved() || return@useCallback
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
                                webClient.onNewShow(); renderDialog = null
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
                                webClient.onNewShow(SampleData.sampleShow); renderDialog = null
                            }
                            listItemText {
                                attrs.primary { +"Sample template" }
                            }
                        }
                    }
                }
            }
        }
    }

    val handleOpenShow = useCallback {
        if (webClient.showIsModified) confirmCloseUnsaved() || return@useCallback
        fileDialogOpen = true
        fileDialogIsSaveAs = false
    }

    val handleSaveShow = useCallback {
        webClient.onSaveShow()
    }

    val handleSaveShowAs = useCallback {
        fileDialogOpen = true
        fileDialogIsSaveAs = true
    }

    val handleCloseShow = useCallback {
        if (webClient.showIsModified) confirmCloseUnsaved() || return@useCallback
        webClient.onCloseShow()
    }

    val handleShowEditButtonClick = useCallback { _: Event ->
        webClient.show?.let { show ->
            webClient.showState?.let { showState ->
                mutablePatchHolder = MutableShow(show, showState)
            }
        }
        Unit
    }


    val renderAppDrawerOpen = appDrawerOpen || (webClient.isLoaded && webClient.show == null)

    val appDrawerStateStyle = if (renderAppDrawerOpen)
        themeStyles.appDrawerOpen
    else
        themeStyles.appDrawerClosed

    val editModeStyle =
        if (editMode) Styles.editModeOn else Styles.editModeOff

    val show = webClient.show
    val showState = webClient.showState

    onMount {
        window.onkeydown = { event ->
            when (event.target) {
                is HTMLButtonElement,
                is HTMLInputElement,
                is HTMLSelectElement,
                is HTMLOptionElement,
                is HTMLTextAreaElement -> {
                    // Ignore.
                }
                else -> {
                    when (event.key) {
                        "d" -> editMode = !editMode
                    }
                }
            }
            true
        }
        withCleanup { window.onkeydown = null }
    }

    appContext.Provider {
        attrs.value = myAppContext

        themeProvider(theme) {
            cssBaseline { }

            div(+Styles.root and appDrawerStateStyle and editModeStyle) {
                appBar(themeStyles.appToolbar on AppBarStyle.root) {
                    attrs.position = AppBarPosition.relative

                    toolbar {
                        iconButton {
                            attrs.color = ButtonColor.inherit
                            attrs.edge = IconButtonEdge.start
                            attrs.onClickFunction =
                                this@xComponent.handler("closeDrawer") { _ -> handleAppDrawerToggle() }
                            icon(Menu)
                        }

                        typographyH6(themeStyles.title on TypographyStyle.root) {
                            show?.let {
                                b { +show.title }
                                if (webClient.showIsModified) i { +" (Unsaved)" }
                            }

                            if (show != null && editMode) {
                                div(+themeStyles.editButton) {
                                    icon(Edit)
                                    attrs.onClickFunction = handleShowEditButtonClick
                                }
                            }
                        }

                        div(+themeStyles.logotype) { +"Sparkle Motion™" }

                        div(+themeStyles.appToolbarActions) {
                            styledDiv {
                                if (!editMode && !webClient.showIsModified) css { opacity = 0 }
                                css {
                                    transition("opacity", duration = .5.s, timing = Timing.linear)
                                }

                                iconButton(Styles.buttons on IconButtonStyle.root) {
                                    icon(Undo)
                                    attrs["disabled"] = !undoStack.canUndo()
                                    attrs.onClickFunction = handleUndo

                                    typographyH6 { +"Undo" }
                                }

                                iconButton(Styles.buttons on IconButtonStyle.root) {
                                    icon(Redo)
                                    attrs["disabled"] = !undoStack.canRedo()
                                    attrs.onClickFunction = handleRedo

                                    typographyH6 { +"Redo" }
                                }

                                if (webClient.showFile == null) {
                                    iconButton(Styles.buttons on IconButtonStyle.root) {
                                        icon(FileCopy)
                                        attrs.onClickFunction = handleSaveShowAs.withEvent()
                                        typographyH6 { +"Save As…" }
                                    }
                                } else {
                                    iconButton(Styles.buttons on IconButtonStyle.root) {
                                        icon(Save)
                                        attrs["disabled"] = !webClient.showIsModified
                                        attrs.onClickFunction = handleSaveShow.withEvent()
                                        typographyH6 { +"Save" }
                                    }
                                }

                                formControlLabel {
                                    attrs.control {
                                        switch {
                                            attrs.checked = editMode
                                            attrs.onChangeFunction = handleEditModeChange
                                        }
                                    }
                                    attrs.label { typographyH6 { +"Design Mode" } }
                                }
                            }

                            help {
                                attrs.divClass = themeStyles.appToolbarHelpIcon.name
                                attrs.inject(HelpText.appToolbar)
                            }
                        }
                    }
                }

                appDrawer {
                    attrs.open = renderAppDrawerOpen
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
                }

                div(+themeStyles.appContent) {
                    backdrop {
                        attrs {
                            open = !webClient.isConnected
                        }

                        container {
                            circularProgress {}
                            icon(NotificationImportant)

                            typographyH6 { +"Connecting…" }
                            +"Attempting to connect to Sparkle Motion."
                        }
                    }

                    if (show == null || showState == null) {
                        paper(themeStyles.noShowLoadedPaper on PaperStyle.root) {
                            if (webClient.isLoaded) {
                                icon(NotificationImportant)
                                typographyH6 { +"No open show." }
                                p { +"Maybe you'd like to open one? " }
                            } else {
                                circularProgress {}
                                typographyH6 { +"Loading Show…" }
                            }
                        }
                    } else {
                        showUi {
                            attrs.show = webClient.openShow!!
                            attrs.showState = showState
                            attrs.onShowStateChange = handleShowStateChange
                            attrs.editMode = editMode
                            attrs.editPatchHolder = handleEditPatchHolder
                            attrs.onEdit = handleShowEdit
                        }

                        portal {
                            // Layout Editor dialog
                            layoutEditorDialog {
                                attrs.open = layoutEditorDialogOpen
                                attrs.layouts = show.layouts
                                attrs.onApply = { newLayouts ->
                                    val mutableShow = MutableShow(show, showState).editLayouts {
                                        copyFrom(newLayouts)
                                    }
                                    handleShowEdit(mutableShow.getShow(), mutableShow.getShowState())
                                }
                                attrs.onClose = handleLayoutEditorDialogClose
                            }
                        }
                    }
                }
            }

            portal {
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
            }

            portal {
                renderDialog?.invoke(this)
            }

            mutablePatchHolder?.let { editor ->
                patchHolderEditor {
                    attrs.mutablePatchHolder = editor
                    attrs.onApply = handlePatchHolderEdit
                    // TODO: This doesn't actually revert the change, it just closes the editor.
                    attrs.onCancel = handlePatchHolderClose
                }
            }
        }
    }
}

external interface AppIndexProps : RProps {
    var id: String
    var webClient: WebClient.Facade
    var undoStack: UndoStack<ShowEditorState>
    var showPlayer: ShowPlayer
}

fun RBuilder.appIndex(handler: RHandler<AppIndexProps>): ReactElement =
    child(AppIndex, handler = handler)
