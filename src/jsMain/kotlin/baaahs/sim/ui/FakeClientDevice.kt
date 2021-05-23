package baaahs.sim.ui

import baaahs.sim.HostedWebApp
import baaahs.ui.SimulatorStyles
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import external.react_draggable.Draggable
import external.react_draggable.DraggableData
import kotlinx.css.height
import kotlinx.css.pct
import kotlinx.css.properties.scale
import kotlinx.css.properties.transform
import kotlinx.css.properties.translate
import kotlinx.css.px
import kotlinx.css.width
import kotlinx.html.js.onClickFunction
import org.w3c.dom.HTMLElement
import org.w3c.dom.Node
import org.w3c.dom.events.MouseEvent
import react.*
import react.dom.div
import react.dom.i
import styled.inlineStyles

const val BORDER_WIDTH = 28

val FakeClientDevice = xComponent<FakeClientDeviceProps>("FakeClientDevice") { props ->
    var isOpen by state { true }
    var zoom by state { .5f }
    val clientDeviceContentRef = useRef<HTMLElement?>(null)

    val handleZoomOut by eventHandler { zoom *= .5f }
    val handleZoomIn by eventHandler { zoom *= 2f }

    val handleClose by eventHandler(props.hostedWebApp) {
        isOpen = false
        props.hostedWebApp.onClose()
    }

    val handleDragStart: (MouseEvent, DraggableData) -> Boolean by handler { e, draggableData ->
        val draggableNode = draggableData.node
        val contentNode = clientDeviceContentRef.current
        var eventNode = e.target

        while (eventNode != null) {
            when {
                eventNode === contentNode -> return@handler false
                eventNode === draggableNode -> return@handler true
                else -> eventNode = (eventNode as? Node)?.parentNode
            }
        }
        return@handler false
    }

    if (!isOpen) {
        div {}
    } else {
        Draggable {
            attrs.onStart = handleDragStart

            div(+SimulatorStyles.fakeClientDevicePad) {
                inlineStyles {
                    width = (props.width * zoom + BORDER_WIDTH * 2).px
                    height = (props.height * zoom + BORDER_WIDTH * 2).px
                }

                div(+SimulatorStyles.fakeClientDeviceControls) {
                    i("fas fa-search-minus ${+SimulatorStyles.fakeClientDeviceIconButton}") {
                        attrs.onClickFunction = handleZoomOut
                    }

                    i("fas fa-search-plus ${+SimulatorStyles.fakeClientDeviceIconButton}") {
                        attrs.onClickFunction = handleZoomIn
                    }

                    i("fas fa-times-circle ${+SimulatorStyles.fakeClientDeviceIconButton}") {
                        attrs.onClickFunction = handleClose
                    }
                }
                div(+SimulatorStyles.fakeClientDeviceHomeButton) {}
                div(+SimulatorStyles.fakeClientDeviceContent) {
                    ref = clientDeviceContentRef

                    inlineStyles {
                        width = props.width.px
                        height = props.height.px

                        transform {
                            translate(((1 - zoom) / 2 * -100).pct, ((1 - zoom) / 2 * -100).pct)
                            scale(zoom)
                        }
                    }

                    child(props.hostedWebApp.render())
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

fun RBuilder.fakeClientDevice(handler: RHandler<FakeClientDeviceProps>) =
    child(FakeClientDevice, handler = handler)