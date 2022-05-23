package baaahs.ui.components

import baaahs.app.ui.appContext
import baaahs.geom.Vector2I
import baaahs.ui.and
import baaahs.ui.unaryMinus
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import external.react_draggable.Draggable
import external.react_resizable.Resizable
import external.react_resizable.ResizeCallbackData
import external.react_resizable.buildResizeHandle
import kotlinx.css.height
import kotlinx.css.px
import kotlinx.css.width
import kotlinx.js.jso
import materialui.icon
import mui.base.Portal
import mui.material.Paper
import org.w3c.dom.HTMLElement
import org.w3c.dom.events.MouseEvent
import react.PropsWithChildren
import react.RBuilder
import react.RHandler
import react.dom.div
import react.useContext
import styled.inlineStyles

private var nextId = 0

private val PaletteView = xComponent<PaletteProps>("Palette") { props ->
    val appContext = useContext(appContext)
    val styles = appContext.allStyles.uiComponents
    val frameRef = ref<HTMLElement>()

    var layoutDimens by state {
        Vector2I(props.initialWidth ?: 320, props.initialHeight ?: 240)
    }
//    useResizeListener(frameRef) {
//        layoutDimens = with(frameRef.current!!) {
//            Vector2I(clientWidth, clientHeight)
//        }
//    }

    val handleResize by handler { e: MouseEvent, data: ResizeCallbackData ->
        layoutDimens = Vector2I(data.size.width, data.size.height)
//        frameRef.current?.style?.width = "${data.size.width}px"
//        frameRef.current?.style?.height = "${data.size.height}px"
    }

    val styleForDragHandle = memo { "DragHandle${nextId++}" }

    Portal {
        Draggable {
            attrs.handle = ".$styleForDragHandle"

            Resizable {
                attrs.handle = ::buildResizeHandle
                attrs.width = layoutDimens.x
                attrs.height = layoutDimens.y
                attrs.onResize = handleResize

                div(+styles.paletteFrame) {
                    ref = frameRef
                    inlineStyles {
                        this.width = layoutDimens.x.px
                        this.height = layoutDimens.y.px
                    }

                    div(+styles.dragHandle and styleForDragHandle) {
                        icon(mui.icons.material.DragIndicator)
                    }

                    Paper {
                        attrs.classes = jso { root = -styles.paper }
                        attrs.elevation = 3

                        props.children()
                    }
                }
            }
        }
    }
}

external interface PaletteProps : PropsWithChildren {
    var initialWidth: Int?
    var initialHeight: Int?
}

fun RBuilder.palette(handler: RHandler<PaletteProps>) =
    child(PaletteView, handler = handler)