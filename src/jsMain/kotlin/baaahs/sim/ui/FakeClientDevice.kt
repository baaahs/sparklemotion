package baaahs.sim.ui

import baaahs.sim.HostedWebApp
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
import org.w3c.dom.Node
import org.w3c.dom.events.MouseEvent
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.div
import react.dom.i
import react.dom.onClick
import react.useRef
import styled.inlineStyles
import web.html.HTMLElement

const val BORDER_WIDTH = 28

val FakeClientDevice = xComponent<FakeClientDeviceProps>("FakeClientDevice") { props ->
    var isOpen by state { true }
    var zoom by state { .5f }
    val clientDeviceContentRef = useRef<HTMLElement>(null)

    val handleZoomOut by mouseEventHandler { zoom *= .5f }
    val handleZoomIn by mouseEventHandler { zoom *= 2f }

    val handleClose by mouseEventHandler(props.hostedWebApp) {
        isOpen = false
        props.hostedWebApp.onClose()
        props.onClose()
    }

    val handleDragStart: (MouseEvent, DraggableData) -> Boolean by handler { e, draggableData ->
        val draggableNode = draggableData.node
        val contentNode = clientDeviceContentRef.current
        var eventNode = e.target

        val classList = (eventNode as HTMLElement?)?.classes() ?: emptyList()
        if (classList.contains(+SimulatorStyles.fakeClientDeviceIconButton))
            return@handler false
        if (classList.contains(+SimulatorStyles.fakeClientDeviceHomeButton))
            return@handler false

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
                        attrs.onClick = handleZoomOut
                    }

                    i("fas fa-search-plus ${+SimulatorStyles.fakeClientDeviceIconButton}") {
                        attrs.onClick = handleZoomIn
                    }

                    i("fas fa-times-circle ${+SimulatorStyles.fakeClientDeviceIconButton}") {
                        attrs.onClick = handleClose
                    }
                }

                div(+SimulatorStyles.fakeClientDeviceHomeButton) {
                    attrs.onClick = handleClose
                }

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

private fun HTMLElement.classes() =
    buildList<String> { classList.forEach { add(it) } }

external interface FakeClientDeviceProps : Props {
    var name: String
    var width: Int
    var height: Int
    var hostedWebApp: HostedWebApp
    var onClose: () -> Unit
}

fun RBuilder.fakeClientDevice(handler: RHandler<FakeClientDeviceProps>) =
    child(FakeClientDevice, handler = handler)