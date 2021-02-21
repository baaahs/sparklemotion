package baaahs.sim

import baaahs.HostedWebApp
import baaahs.app.ui.appContext
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import external.react_draggable.Draggable
import external.react_draggable.DraggableData
import kotlinx.css.*
import kotlinx.css.properties.boxShadow
import kotlinx.html.js.onClickFunction
import org.w3c.dom.HTMLElement
import org.w3c.dom.events.Event
import org.w3c.dom.events.MouseEvent
import react.*
import react.dom.div
import react.dom.i
import styled.StyleSheet
import styled.inlineStyles

const val BORDER_WIDTH = 10

val FakeClientDevice = xComponent<FakeClientDeviceProps>("LayoutEditorWindow") { props ->
    val appContext = useContext(appContext)
    val contentRef = ref<HTMLElement?> { null }

    var isOpen by state { true }
    var zoomFactor by state { 1f }
    val handleDragStart = handler("handleDragStart") { event: MouseEvent, data: DraggableData ->
        val draggableNode = data.node
        val contentNode = contentRef.current
        var eventNode = event.target as HTMLElement?

        while (eventNode != null) {
            if (eventNode == contentNode) {
                event.stopPropagation()
            } else if (eventNode == draggableNode) {
//                return true;
            } else {
                eventNode = eventNode.parentNode as HTMLElement?
            }
        }
    }
    val handleZoomIn = handler("handleZoomIn") { _: Event -> zoomFactor *= 1.5f }
    val handleZoomOut = handler("handleZoomOut") { _: Event -> zoomFactor /= 1.5f }
    val handleCloseClick = handler("handleCloseClick") { _: Event ->
        isOpen = false
        props.hostedWebApp.onClose()
    }
    
    
    Draggable {
        attrs.onStart = handleDragStart
        
        div(+FakeClientDeviceStyle.pad) { 
            inlineStyles {
                width = (props.width * zoomFactor + BORDER_WIDTH * 2).px
                height = (props.height * zoomFactor + BORDER_WIDTH * 2).px
            }

            div(+FakeClientDeviceStyle.controls) { 
                i("fas fa-search-minus ${FakeClientDeviceStyle.iconButton}") {
                    attrs.onClickFunction = handleZoomOut
                }

                i("fas fa-search-plus ${FakeClientDeviceStyle.iconButton}") {
                    attrs.onClickFunction = handleZoomIn
                }
                
                div(+FakeClientDeviceStyle.homeButton) {}
                
                div(+FakeClientDeviceStyle.content) {
                    ref = contentRef
                }
            }
        }
    }
}

external interface FakeClientDeviceProps : RProps {
    var name: String
    var width: Int
    var height: Int
    var hostedWebApp: HostedWebApp
    var onClose: () -> Unit
}

fun RBuilder.fakeClientDevice(handler: RHandler<FakeClientDeviceProps>): ReactElement =
    child(FakeClientDevice, handler = handler)

object FakeClientDeviceStyle : StyleSheet("baaahs-sim-FakeClientDevice", true) {
    val pad by css {
        zIndex = 1000
        position = Position.absolute
        border = "1px solid #404040"
        borderRadius = 8.px
        backgroundImage = Image("linear-gradient(to bottom right, #eee, #fff)")
        cursor = Cursor.grab
        padding = "28px 40px 28px 28px"
        color = Color.white
        right = 5.px
        bottom = 5.px
        boxShadow(rgba(0, 0, 0, 0.15), 1.px, 5.px)

    }

    val homeButton by css {
        position = Position.absolute
        width = 18.px
        height = 18.px
        right = 6.px
        top = 50.pct
        border = "1px solid black"
        borderRadius = 50.pct
        backgroundImage = Image("radial-gradient(#fff, #ddd)")
    }

    val controls by css {
        position = Position.absolute
        fontSize = 14.px
        top = 0.px
        padding(4.px)
        right = 0.px
        color = Color("#222")
        zIndex = 1
    }
    
    val content by css {
        width = 100.pct
        height = 100.pct
        overflow = Overflow.hidden
        cursor = Cursor.default
        position = Position.relative
        width = LinearDimension.auto
        height = LinearDimension.auto
        padding(2.px)
        border = "2px solid #373737"
        backgroundColor = Color("#4F4F4F")

        nav {
            marginBottom = 8.px
        }
    }
    
    val iconButton by css {
        color = Color("#222222")
        padding(4.px)
        marginLeft = 8.px
        cursor = Cursor.pointer

        hover {
            color = Color("#e3e3e3")
        }
    }
}