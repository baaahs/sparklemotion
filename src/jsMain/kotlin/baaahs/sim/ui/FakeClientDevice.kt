package baaahs.sim.ui

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
import mui.icons.material.Close
import mui.icons.material.ZoomIn
import mui.icons.material.ZoomOut
import mui.material.Button
import org.w3c.dom.Node
import org.w3c.dom.events.EventTarget
import org.w3c.dom.events.MouseEvent
import react.*
import react.dom.div
import react.dom.onClick
import styled.inlineStyles
import web.html.HTMLElement

const val BORDER_WIDTH = 28

val FakeClientDevice = xComponent<FakeClientDeviceProps>("FakeClientDevice") { props ->
    var isOpen by state { true }
    var zoom by state { .5f }
    val clientDeviceContentRef = useRef<HTMLElement>(null)

    val handleZoomOut by mouseEventHandler { zoom *= .5f }
    val handleZoomIn by mouseEventHandler { zoom *= 2f }

    val handleClose by mouseEventHandler(props.onClose) {
        isOpen = false
        props.onClose()
    }

    val handleDragStart: (MouseEvent, DraggableData) -> Boolean by handler { e, draggableData ->
        val draggableNode = draggableData.node
        val contentNode = clientDeviceContentRef.current
        var eventNode = e.target

        if (eventNode.hasClass(+SimulatorStyles.fakeClientDeviceHomeButton))
            return@handler false

        while (eventNode != null) {
            when {
                eventNode.hasClass(+SimulatorStyles.fakeClientDeviceControls) -> return@handler false
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
                    Button {
                        attrs.onClick = handleZoomOut
                        ZoomOut {}
                    }

                    Button {
                        attrs.onClick = handleZoomIn
                        ZoomIn {}
                    }

                    Button {
                        attrs.onClick = handleClose
                        Close {}
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

                    child(props.render())
                }
            }
        }
    }
}

private fun HTMLElement.classes() =
    buildList { classList.forEach { add(it) } }

private fun EventTarget?.hasClass(className: String) =
    (this as? HTMLElement?)?.classes()?.contains(className) ?: false

external interface FakeClientDeviceProps : Props {
    var name: String
    var width: Int
    var height: Int
    var render: () -> ReactElement<*>
    var onClose: () -> Unit
}

fun RBuilder.fakeClientDevice(handler: RHandler<FakeClientDeviceProps>) =
    child(FakeClientDevice, handler = handler)