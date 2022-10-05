package external.react_resizable

import csstype.ClassName
import kotlinx.js.jso
import mui.material.SvgIcon
import org.w3c.dom.HTMLElement
import react.ReactElement
import react.Ref
import react.buildElement
import react.dom.svg.ReactSVG

typealias ResizeHandle =
            (resizeHandleAxis: ResizeHandleAxis, ref: Ref<HTMLElement>) -> ReactElement<*>

typealias ResizeHandleAxis = String

val ResizeHandleAxes = arrayOf(
    "s",
//    "w",
    "e",
//    "n",
//    "sw",
//    "nw",
    "se",
//    "ne"
)

fun buildResizeHandle(axis: ResizeHandleAxis, ref: Ref<HTMLElement>) = buildElement {
    SvgIcon {
        attrs.viewBox = "0 0 20 20"
        attrs.classes = jso {
            this.root = ClassName("app-ui-layout-resize-handle " +
                    "app-ui-layout-resize-handle-$axis react-resizable-handle")
        }

        when (axis.length) {
            1 -> { // edge (south)
                ReactSVG.path {
                    attrs.stroke = "#111"
                    attrs.fill = "#aaa"
                    attrs.d = "M0,19 L19,19 L19,17 L0,17 Z"
                }
                ReactSVG.path {
                    attrs.stroke = "#111"
                    attrs.fill = "#666"
                    attrs.d = "M10,19 L4,9 L16,9 Z"
                }
            }
            2 -> { // corner (south-east)
                ReactSVG.path {
                    attrs.stroke = "#111"
                    attrs.fill = "#aaa"
                    attrs.d = "M5,19 L19,19 L19,5 L17,5 L17,17 L5,17 Z"
                }
                ReactSVG.path {
                    attrs.stroke = "#111"
                    attrs.fill = "#666"
                    attrs.d = "M15,15 L5,15 L15,5 Z"
                }
            }
        }
    }
}
