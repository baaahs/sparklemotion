package baaahs.app.ui

import baaahs.*
import baaahs.net.Network
import baaahs.show.Show
import baaahs.ui.*
import baaahs.util.UndoStack
import kotlinext.js.jsObject
import kotlinx.css.*
import kotlinx.css.Color
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
import materialui.components.iconbutton.iconButton
import materialui.components.portal.portal
import materialui.components.svgicon.SvgIconProps
import materialui.components.switches.switch
import materialui.components.toolbar.toolbar
import org.w3c.dom.events.Event
import react.*
import react.dom.table
import react.dom.tbody
import react.dom.td
import react.dom.tr
import styled.css
import styled.styledDiv
import styled.styledTd

val AppIndex = xComponent<AppIndexProps>("AppIndex") { props ->
    val pubSub = props.pubSub
    val id = props.id
    println("AppIndex $id: about to render")

    var isConnected by state { pubSub.isConnected }
    val handlePubSubStateChange = useCallback(pubSub) {
        isConnected = pubSub.isConnected
    }
    sideEffect("pubSub changed", pubSub, handlePubSubStateChange) {
        pubSub.addStateChangeListener(handlePubSubStateChange)
        withCleanup { pubSub.removeStateChangeListener(handlePubSubStateChange) }
    }

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

    val undoStack by state { UndoStack<ShowWithState>() }

    var show by state<Show?> { null }
    var showState by state<ShowState?> { null }

    val showWithStateChannel = useRef<PubSub.Channel<ShowWithState>>(nuffin())
    sideEffect("showWithState subscription", pubSub) {
        val currentShowTopic = props.showResources.showWithStateTopic
        println("AppIndex $id: subscribe to ${currentShowTopic.name}")
        val channel = pubSub.subscribe(currentShowTopic) {
            show = it.show
            showState = it.showState
            undoStack.reset(it)
        }
        showWithStateChannel.current = channel

        withCleanup {
            println("AppIndex $id: unsubscribe from ${currentShowTopic.name}")
            channel.unsubscribe()
            showWithStateChannel.current = nuffin()
        }
    }

    val handleUndo = handler("handleUndo", undoStack) { event: Event ->
        undoStack.undo().also {
            show = it.show
            showState = it.showState
            showWithStateChannel.current.onChange(it)
        }
        Unit
    }

    val handleRedo = handler("handleRedo", undoStack) { event: Event ->
        undoStack.redo().also {
            show = it.show
            showState = it.showState
            showWithStateChannel.current.onChange(it)
        }
        Unit
    }

    val handleShowEdit = useCallback { newShow: Show, newShowState: ShowState ->
        show = newShow
        showState = newShowState

        val newShowWithState = newShow.withState(newShowState)
        undoStack.changed(newShowWithState)
        showWithStateChannel.current.onChange(newShowWithState)
    }

    val showStateChannel = useRef<PubSub.Channel<ShowState>>(nuffin())
    sideEffect("showState subscription", pubSub) {
        val channel = pubSub.subscribe(Topics.showState) {
            showState = it
        }
        showStateChannel.current = channel
        withCleanup {
            channel.unsubscribe()
            showStateChannel.current = nuffin()
        }
    }
    println("AppIndex: show = ${show?.title} showState = ${showState}")

    val handleShowStateChange = useCallback { newShowState: ShowState ->
        showState = newShowState
    }

    sideEffect("Validate ShowState", show, showState) {
        show?.let { show -> showState?.boundedBy(show)?.let { showState = it } }
    }

    sideEffect("sync ShowState", showState) {
        showState?.let { showStateChannel.current.onChange(it) }
    }

    sideEffect("Show -> ShowResources", show, props.showResources) {
        show?.let { props.showResources.switchTo(it) }
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
            display = if (isConnected) Display.none else Display.block
        }
        +"Connecting…"
    }

    toolbar {
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
                        iconButton {
                            attrs.edge = IconButtonEdge.end
                            attrs.onClickFunction = handleShaderEditorDrawerToggle
                            Menu { }
                            +"Shader Editor"
                        }

                        iconButton {
                            icon(Undo) { }
                            attrs["disabled"] = !undoStack.canUndo()
                            attrs.onClickFunction = handleUndo
                        }

                        iconButton {
                            icon(Redo) { }
                            attrs["disabled"] = !undoStack.canRedo()
                            attrs.onClickFunction = handleRedo
                        }
                    }
                }
            }
        }
    }

    if (show == null || showState == null) {
        styledDiv { +"Loading Show…" }
    } else {
        showUi {
            this.pubSub = props.pubSub
            this.show = show!!
            this.showResources = props.showResources
            this.showState = showState!!
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
            }
        }
    }
}

external interface AppIndexProps : RProps {
    var id: String
    var network: Network
    var pubSub: PubSub.Client
    var filesystems: List<SaveAsFs>
    var showResources: MutableShowResources
}

fun RBuilder.appIndex(handler: AppIndexProps.() -> Unit): ReactElement =
    child(AppIndex) { attrs { handler() } }

fun RBuilder.icon(icon: RComponent<SvgIconProps, RState>, handler: SvgIconProps.() -> Unit = { }): ReactElement {
    return child(createElement(icon, jsObject(handler)))
}
