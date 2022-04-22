package external.react_resizable

import org.w3c.dom.HTMLElement
import react.ReactElement
import react.Ref

typealias ResizeHandle =
            (resizeHandleAxis: ResizeHandleAxis, ref: Ref<HTMLElement>) -> ReactElement<*>

typealias ResizeHandleAxis = String
val ResizeHandleAxes = arrayOf(
        "s", "w", "e", "n", "sw", "nw", "se", "ne"
)