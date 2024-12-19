@file:JsModule("react-resizable")

package external.react_resizable

import baaahs.ui.gridlayout.Position
import org.w3c.dom.events.MouseEvent
import react.ElementType
import react.PropsWithClassName
import react.ReactElement
import web.html.HTMLElement

external val Resizable : ElementType<ResizableProps>

external interface ResizeCallbackData {
    var node: HTMLElement
    var size: Position // {width: number, height: number}
    var handle: ResizeHandleAxis
}

//external enum class ResizeHandleAxis {
//    s, w, e, n, sw, nw, se, ne
//}

external interface ResizableProps : PropsWithClassName {
    var children: ReactElement<*>
    var width: Int
    var height: Int
    // Either a ReactElement to be used as handle, or a function returning an element that is fed the handle's location as its first argument.
    var handle: ResizeHandle?
    // If you change this, be sure to update your css
    var handleSize: Array<Int>? // = [10, 10]
    var lockAspectRatio: Boolean? // = false
    var axis: String? // 'both' | 'x' | 'y' | 'none' = 'both'
    var minConstraints: Array<Number>? // = [10, 10]
    var maxConstraints: Array<Number>? // = [Infinity, Infinity]
    var onResizeStop: (e: MouseEvent, data: ResizeCallbackData) -> Any
    var onResizeStart: (e: MouseEvent, data: ResizeCallbackData) -> Any
    var onResize: (e: MouseEvent, data: ResizeCallbackData) -> Any
    var transformScale: Double
    var draggableOpts: dynamic
    var resizeHandles: Array<ResizeHandleAxis>? // = ['se']
}
