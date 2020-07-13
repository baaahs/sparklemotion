package baaahs.app.ui

import baaahs.ShowResources
import baaahs.ShowState
import baaahs.ShowWithState
import baaahs.app.ui.Styles.buttons
import baaahs.client.WebClient
import baaahs.glshaders.AutoWirer
import baaahs.show.Show
import baaahs.show.ShowEditor
import baaahs.ui.*
import baaahs.util.UndoStack
import baaahs.withState
import kotlinext.js.jsObject
import kotlinx.css.*
import kotlinx.css.properties.Timing
import kotlinx.css.properties.s
import kotlinx.css.properties.transition
import kotlinx.html.js.onChangeFunction
import kotlinx.html.js.onClickFunction
import materialui.Menu
import materialui.Redo
import materialui.Undo
import materialui.components.drawer.drawer
import materialui.components.drawer.enums.DrawerAnchor
import materialui.components.drawer.enums.DrawerVariant
import materialui.components.formcontrollabel.formControlLabel
import materialui.components.iconbutton.enums.IconButtonEdge
import materialui.components.iconbutton.enums.IconButtonStyle.root
import materialui.components.iconbutton.iconButton
import materialui.components.paper.enums.PaperStyle
import materialui.components.paper.paper
import materialui.components.portal.portal
import materialui.components.svgicon.SvgIconProps
import materialui.components.switches.switch
import materialui.components.toolbar.toolbar
import org.w3c.dom.events.Event
import react.*
import react.dom.*
import styled.css
import styled.styledDiv
import styled.styledTd

val AppIndex = xComponent<AppIndexProps>("AppIndex") { props ->
    val webClient = props.webClient
    observe(webClient)

    val id = props.id
    println("AppIndex $id: about to render")

    var shaderEditorDrawerOpen by state { false }

    val handleShaderEditorDrawerToggle =
        useCallback(shaderEditorDrawerOpen) { event: Event ->
            console.log("handleShaderEditorDrawerToggle: I'll set shaderEditorDrawerOpen to", !shaderEditorDrawerOpen)
            shaderEditorDrawerOpen = !shaderEditorDrawerOpen
        }
    val handleShaderEditorDrawerClose =
        useCallback { event: Event ->
            shaderEditorDrawerOpen = false
        }

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
    println("AppIndex: show = ${show?.title} showState = $showState")

    val handleShowStateChange = useCallback { newShowState: ShowState ->
        webClient.onShowStateChange(newShowState)
    }

    var editMode by state { false }
    val handleEditModeChange = useCallback(editMode) { event: Event -> editMode = !editMode }

    styledDiv {
        css {
            width = 100.pct
            height = 5.vh
            color = Color.black
            backgroundColor = Color.pink
            textAlign = TextAlign.center
            fontSize = 2.em
            display = if (webClient.isConnected) Display.none else Display.block
        }
        +"Connecting…"
    }

    paper {
        table {
            tbody {
                tr {
                    td {
                        formControlLabel {
                            attrs.control {
                                switch {
                                    attrs.checked = editMode
                                    attrs.onChangeFunction = handleEditModeChange
                                }
                            }
                            attrs.label = "Design Mode".asTextNode()
                        }
                    }
                    styledTd {
                        if (!editMode) css { opacity = 0 }
                        css {
                            transition("opacity", duration = .5.s, timing = Timing.linear)
                        }

                        iconButton(root to buttons.getName()) {
                            icon(Undo) { }
                            attrs["disabled"] = !undoStack.canUndo()
                            attrs.onClickFunction = handleUndo

                            +"Undo"
                        }

                        iconButton(root to buttons.getName()) {
                            icon(Redo) { }
                            attrs["disabled"] = !undoStack.canRedo()
                            attrs.onClickFunction = handleRedo

                            +"Redo"
                        }

                        iconButton(root to buttons.getName()) {
                            attrs.edge = IconButtonEdge.end
                            attrs.onClickFunction = handleShaderEditorDrawerToggle
                            Menu { }
                            +"Shader Editor"
                        }
                    }
                }
            }
        }
    }

    if (show == null || showState == null) {
        paper {
            h1 { +"Loading Show…" }
        }
    } else {
        showUi {
            this.show = webClient.openShow!!
            this.showResources = props.showResources
            this.showState = showState
            this.onShowStateChange = handleShowStateChange
            this.editMode = editMode
            this.onChange = handleShowEdit
        }
    }

    portal {
        drawer {
            attrs.anchor = DrawerAnchor.right
            attrs.variant = DrawerVariant.persistent
//            attrs.elevation = 100
            attrs.open = shaderEditorDrawerOpen
            attrs.onClose = handleShaderEditorDrawerClose
            attrs.classes(Styles.fullHeight.getName())

            shaderEditorWindow {
                this.filesystems = props.filesystems
                this.onAddToPatch = { shader ->
                    val curShow = show!!
                    val curState = showState!!

                    val newPatch = AutoWirer(props.showResources.plugins).autoWire(shader.src)
                    val editor = ShowEditor(curShow, curState).editScene(curState.selectedScene) {
                        editPatchSet(curState.selectedPatchSet) {
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
    }
}

external interface AppIndexProps : RProps {
    var id: String
    var webClient: WebClient.Facade
    var undoStack: UndoStack<ShowWithState>
    var filesystems: List<SaveAsFs>
    var showResources: ShowResources
}

fun RBuilder.appIndex(handler: AppIndexProps.() -> Unit): ReactElement =
    child(AppIndex) { attrs { handler() } }

fun RBuilder.icon(icon: RComponent<SvgIconProps, RState>, handler: SvgIconProps.() -> Unit = { }): ReactElement {
    return child(createElement(icon, jsObject(handler)))
}
