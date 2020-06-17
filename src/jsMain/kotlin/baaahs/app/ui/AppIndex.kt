package baaahs.app.ui

import baaahs.MutableShowResources
import baaahs.PubSub
import baaahs.ShowState
import baaahs.Topics
import baaahs.net.Network
import baaahs.show.Show
import baaahs.ui.*
import kotlinx.css.*
import kotlinx.html.js.onChangeFunction
import kotlinx.html.js.onClickFunction
import materialui.Menu
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
import kotlin.reflect.KClass

val AppIndex = functionalComponent<AppIndexProps> { props ->
    val pubSub = props.pubSub
    val id = props.id
    println("AppIndex $id: about to render")

    val preact = XBuilder()

    var isConnected by preact.state { pubSub.isConnected }
    val handlePubSubStateChange = useCallback(pubSub) {
        isConnected = pubSub.isConnected
    }
    preact.sideEffect("pubSub changed", pubSub, handlePubSubStateChange) {
        pubSub.addStateChangeListener(handlePubSubStateChange)
        withCleanup { pubSub.removeStateChangeListener(handlePubSubStateChange) }
    }

    var shaderEditorDrawerOpen by preact.state { false }

    val handleShaderEditorDrawerToggle =
        useCallback(shaderEditorDrawerOpen) { event: Event ->
            console.log("handleShaderEditorDrawerToggle: I'll set shaderEditorDrawerOpen to", !shaderEditorDrawerOpen)
            shaderEditorDrawerOpen = !shaderEditorDrawerOpen
        }
    val handleShaderEditorDrawerClose =
        useCallback { event: Event ->
            shaderEditorDrawerOpen = false
        }

    var show by preact.state<Show?> { null }
    val showChannel = useRef<PubSub.Channel<Show>>(nuffin())
    preact.sideEffect("currentShow subscription", pubSub) {
        val currentShowTopic = props.showResources.currentShowTopic
        println("AppIndex $id: subscribe to ${currentShowTopic.name}")
        val channel = pubSub.subscribe(currentShowTopic) {
            println("New incoming show: $it")
            show = it
            props.showResources.switchTo(it)
        }
        showChannel.current = channel

        withCleanup {
            println("AppIndex $id: unsubscribe from ${currentShowTopic.name}")
            channel.unsubscribe()
            showChannel.current = nuffin()
        }
    }

    var showState by preact.state<ShowState?> { null }
    val showStateChannel = useRef<PubSub.Channel<ShowState>>(nuffin())
    preact.sideEffect("showState subscription", pubSub) {
        val channel = pubSub.subscribe(Topics.showState) {
            println("New incoming showState: $it")
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
        showStateChannel.current.onChange(newShowState)
    }

    val handleShowChange = useCallback { newShow: Show ->
        show = newShow
        showChannel.current.onChange(newShow)
    }

    var editMode by preact.state { false }
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
                            attrs.label = "Edit Mode".asTextNode()
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
            this.onChange = handleShowChange
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

fun RBuilder.icon(icon: RClass<SvgIconProps>, handler: SvgIconProps.() -> Unit = { }): ReactElement =
    child(icon::class as KClass<out Component<SvgIconProps, *>>) { attrs { handler() } }