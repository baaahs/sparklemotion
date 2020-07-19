package baaahs.app.ui

import baaahs.ShowResources
import baaahs.ShowState
import baaahs.ShowWithState
import baaahs.client.WebClient
import baaahs.glshaders.AutoWirer
import baaahs.show.Show
import baaahs.show.ShowEditor
import baaahs.ui.*
import baaahs.util.UndoStack
import baaahs.withState
import kotlinext.js.jsObject
import kotlinx.css.opacity
import kotlinx.css.properties.Timing
import kotlinx.css.properties.s
import kotlinx.css.properties.transition
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
import materialui.components.drawer.drawer
import materialui.components.drawer.enums.DrawerAnchor
import materialui.components.drawer.enums.DrawerVariant
import materialui.components.iconbutton.enums.IconButtonEdge
import materialui.components.iconbutton.enums.IconButtonStyle
import materialui.components.iconbutton.iconButton
import materialui.components.paper.paper
import materialui.components.portal.portal
import materialui.components.toolbar.toolbar
import materialui.components.typography.enums.TypographyStyle
import materialui.components.typography.typographyH6
import materialui.styles.createMuiTheme
import materialui.styles.muitheme.options.palette
import materialui.styles.palette.PaletteType
import materialui.styles.palette.options.type
import materialui.styles.themeprovider.themeProvider
import org.w3c.dom.events.Event
import react.*
import react.dom.b
import react.dom.div
import styled.css
import styled.injectGlobal
import styled.styledDiv

val AppIndex = xComponent<AppIndexProps>("AppIndex") { props ->
    injectGlobal(Styles.global.toString())
    injectGlobal(baaahs.app.ui.controls.Styles.global.toString())

    val webClient = props.webClient
    observe(webClient)

    val dragNDrop by state { DragNDrop() }
    val myAppContext by state {
        jsObject<AppContext> {
            this.showResources = props.showResources
            this.dragNDrop = dragNDrop
        }
    }

    var appDrawerOpen by state { false }
    var shaderEditorDrawerOpen by state { false }
    var layoutEditorDialogOpen by state { false }

    val handleAppDrawerToggle =
        useCallback(appDrawerOpen) { event: Event -> appDrawerOpen = !appDrawerOpen }

    val handleShaderEditorDrawerToggle =
        useCallback(shaderEditorDrawerOpen) { event: Event -> shaderEditorDrawerOpen = !shaderEditorDrawerOpen }
    val handleShaderEditorDrawerClose = useCallback { event: Event -> shaderEditorDrawerOpen = false }

    val handleLayoutEditorDialogToggle =
        useCallback(layoutEditorDialogOpen) { event: Event -> layoutEditorDialogOpen = !layoutEditorDialogOpen }
    val handleLayoutEditorDialogClose = useCallback { layoutEditorDialogOpen = false }

    val undoStack = props.undoStack
    val handleUndo = handler("handleUndo", undoStack) { event: Event ->
        undoStack.undo().also {
            webClient.onShowEdit(it)
        }
        Unit
    }

    val handleRedo = handler("handleRedo", undoStack) { event: Event ->
        undoStack.redo().also {
            webClient.onShowEdit(it)
        }
        Unit
    }

    val handleShowEdit = useCallback { newShow: Show, newShowState: ShowState ->
        val newShowWithState = newShow.withState(newShowState)
        undoStack.changed(newShowWithState)
        webClient.onShowEdit(newShowWithState)
    }

    val show = webClient.show
    val showState = webClient.showState

    val handleShowStateChange = useCallback { newShowState: ShowState ->
        webClient.onShowStateChange(newShowState)
    }

    var editMode by state { false }
    val handleEditModeChange = useCallback(editMode) { event: Event -> editMode = !editMode }

    var darkMode by state { false }
    val handleDarkModeChange = useCallback(darkMode) { event: Event -> darkMode = !darkMode }

    val theme = createMuiTheme {
        palette {
            type = if (darkMode) PaletteType.dark else PaletteType.light
        }
    }
    val themeStyles = ThemeStyles(theme)

    themeProvider(theme) {
        cssBaseline { }

        div(+Styles.root and if (appDrawerOpen) themeStyles.appDrawerOpen else themeStyles.appDrawerClosed) {
            appBar(themeStyles.appToolbar on AppBarStyle.root) {
                attrs.position = AppBarPosition.relative

                toolbar {
                    iconButton {
                        attrs.color = ButtonColor.inherit
                        attrs.edge = IconButtonEdge.start
                        attrs.onClickFunction = handleAppDrawerToggle
                        icon(Menu)
                    }

                    typographyH6(themeStyles.title on TypographyStyle.root) {
                        show?.let { b { +show.title }; +" — " }
                        +"Sparkle Motion™"
                    }

                    styledDiv {
                        if (!editMode) css { opacity = 0 }
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

                        iconButton(Styles.buttons on IconButtonStyle.root) {
                            icon(Save)
                            attrs.disabled = !webClient.showIsModified
//                            attrs.onClickFunction = handleSave

                            typographyH6 { +"Save" }
                        }

                        iconButton(Styles.buttons on IconButtonStyle.root) {
                            icon(FileCopy)
                            attrs.disabled = !webClient.showIsModified
//                            attrs.onClickFunction = handleSaveAs

                            typographyH6 { +"Save As…" }
                        }

                        iconButton(Styles.buttons on IconButtonStyle.root) {
                            icon(Close)
//                            attrs.onClickFunction = handleClose

                            typographyH6 { +"Close" }
                        }
                    }
                }
            }

            appDrawer {
                attrs.open = appDrawerOpen
                attrs.onClose = handleAppDrawerToggle
                attrs.editMode = editMode
                attrs.onEditModeChange = handleEditModeChange
                attrs.onShaderEditorDrawerToggle = handleShaderEditorDrawerToggle
                attrs.onLayoutEditorDialogToggle = handleLayoutEditorDialogToggle
                attrs.darkMode = darkMode
                attrs.onDarkModeChange = handleDarkModeChange
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
                    paper {
                        container {
                            circularProgress {}
                            typographyH6 { +"Loading Show…" }
                        }
                    }
                } else {
                    appContext.Provider {
                        attrs.value = myAppContext

                        showUi {
                            attrs.show = webClient.openShow!!
                            attrs.showState = showState
                            attrs.onShowStateChange = handleShowStateChange
                            attrs.editMode = editMode
                            attrs.onEdit = handleShowEdit
                        }

                        portal {
                            // Shader Editor drawer
                            drawer {
                                attrs.anchor = DrawerAnchor.right
                                attrs.variant = DrawerVariant.persistent
//                              attrs.elevation = 100
                                attrs.open = shaderEditorDrawerOpen
                                attrs.onClose = handleShaderEditorDrawerClose
                                attrs.classes(Styles.fullHeight.name)

                                shaderEditorWindow {
                                    attrs.filesystems = props.filesystems
                                    attrs.onAddToPatch = { shader ->
                                        val newPatch = AutoWirer(props.showResources.plugins).autoWire(shader.src)
                                        val editor = ShowEditor(show, showState).editScene(showState.selectedScene) {
                                            editPatchSet(showState.selectedPatchSet) {
                                                if (this.patchMappings.isEmpty()) {
                                                    addPatch {
                                                        links = newPatch.links.toMutableList()
                                                        surfaces = newPatch.surfaces
                                                    }
                                                } else {
                                                    editPatch(0) {
                                                        links = newPatch.links.toMutableList()
                                                        surfaces = newPatch.surfaces
                                                    }
                                                }
                                            }
                                        }
                                        handleShowEdit(editor.getShow(), editor.getShowState())
                                    }
                                }
                            }

                            // Layout Editor dialog
                            layoutEditorDialog {
                                attrs.open = layoutEditorDialogOpen
                                attrs.layouts = show.layouts
                                attrs.onApply = { newLayouts ->
                                    val editor = ShowEditor(show, showState).editLayouts {
                                        copyFrom(newLayouts)
                                    }
                                    handleShowEdit(editor.getShow(), editor.getShowState())
                                }
                                attrs.onClose = handleLayoutEditorDialogClose
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
    var undoStack: UndoStack<ShowWithState>
    var filesystems: List<SaveAsFs>
    var showResources: ShowResources
}

fun RBuilder.appIndex(handler: RHandler<AppIndexProps>): ReactElement =
    child(AppIndex, handler = handler)
