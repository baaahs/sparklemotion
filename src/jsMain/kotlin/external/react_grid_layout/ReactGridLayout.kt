package external.react_grid_layout

import baaahs.app.ui.layout.port.CompactType
import baaahs.app.ui.layout.port.Layout
import baaahs.app.ui.layout.port.LayoutItem
import external.react_resizable.ResizeHandleAxis
import kotlinx.js.jso
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLElement
import org.w3c.dom.events.Event
import org.w3c.dom.events.MouseEvent
import react.*
import kotlin.math.max
import kotlin.math.roundToInt

@JsModule("react-grid-layout")
external val ReactGridLayout: ElementType<GridLayoutProps>

@JsModule("react-grid-layout")
open external class ReactGridLayoutClass(props: GridLayoutProps) : Component<GridLayoutProps, State> {
    open fun onDrag(i: String, x: Number, y: Number, event: baaahs.app.ui.layout.port.GridDragEvent)

    override fun render(): ReactNode?
}

external interface GridLayoutProps : PropsWithChildren {
    /** Class applied to top-level div. */
    var className: String?

    var style: dynamic

    /**
     * This allows setting the initial width on the server side.
     *
     * This is required unless using the HOC <WidthProvider> or similar
     */
    var width: Double?

    /** If true, the container height swells and contracts to fit contents */
    var autoSize: Boolean? // = true

    /* Number of columns in this layout.*/
    var cols: Int? // = 12

    /**
     * A CSS selector for tags that will not be draggable.
     * For example: draggableCancel:'.MyNonDraggableAreaClassName'
     * If you forget the leading . it will not work.
     * .react-resizable-handle" is always prepended to this value.
     */
    var draggableCancel: String? // = ''

    /**
     * A CSS selector for tags that will act as the draggable handle.
     * For example: draggableHandle:'.MyDragHandleClassName'
     * If you forget the leading . it will not work.
     */
    var draggableHandle: String? // = ''

    // Compaction type.
    var compactType: CompactType? // ?('vertical' | 'horizontal') = 'vertical';

    /**
     * Layout is an array of object with the format:
     *     {x: number, y: number, w: number, h: number}
     * The index into the layout must match the key used on each item component.
     * If you choose to use custom keys, you can specify that key in the layout
     * array objects like so:
     *     {i: string, x: number, y: number, w: number, h: number}
     */
    var layout: List<LayoutItem>? // = null, // If not provided, use data-grid props on children

    /** Margin between items [x, y] in px.*/
    var margin: Array<Int>? // ?[number, number] = [10, 10],

    // Padding inside the container [x, y] in px
    var containerPadding: Array<Int>? // ?[number, number] = margin,

    // Rows have a static height, but you can change this based on breakpoints
// if you like.
    var rowHeight: Double? // = 150,

    var maxRows: Int // = Infinity

    // Configuration of a dropping element. Dropping element is a "virtual" element
// which appears when you drag over some element from outside.
// It can be changed by passing specific parameters:
    var droppingItem: DroppingItem?

    //
// Flags
//
    var isDraggable: Boolean? // = true
    var isResizable: Boolean? // = true
    var isBounded: Boolean? // = false

    // Uses CSS3 translate() instead of position top/left.
// This makes about 6x faster paint performance
    var useCSSTransforms: Boolean? // = true

    // If parent DOM node of ResponsiveReactGridLayout or ReactGridLayout has "transform: scale(n)" css property,
// we should set scale coefficient to avoid render artefacts while dragging.
    var transformScale: Double? // = 1

    // If true, grid can be placed one over the other.
// If set, implies `preventCollision`.
    var allowOverlap: Boolean? // = false

    // If true, grid items won't change position when being
// dragged over. If `allowOverlap` is still false,
// this simply won't allow one to drop on an existing object.
    var preventCollision: Boolean? // = false

    // If true, droppable elements (with `draggable={true}` attribute)
// can be dropped on the grid. It triggers "onDrop" callback
// with position and event object as parameters.
// It can be useful for dropping an element in a specific position
//
// NOTE: In case of using Firefox you should add
// `onDragStart={e => e.dataTransfer.setData('text/plain', '')}` attribute
// along with `draggable={true}` otherwise this feature will work incorrect.
// onDragStart attribute is required for Firefox for a dragging initialization
// @see https://bugzilla.mozilla.org/show_bug.cgi?id=568313
    var isDroppable: Boolean? // = false

    // Defines which resize handles should be rendered
// Allows for any combination of:
// 's' - South handle (bottom-center)
// 'w' - West handle (left-center)
// 'e' - East handle (right-center)
// 'n' - North handle (top-center)
// 'sw' - Southwest handle (bottom-left)
// 'nw' - Northwest handle (top-left)
// 'se' - Southeast handle (bottom-right)
// 'ne' - Northeast handle (top-right)
    var resizeHandles: List<ResizeHandleAxis>? // <'s' | 'w' | 'e' | 'n' | 'sw' | 'nw' | 'se' | 'ne'> = ['se']

    // Custom component for resize handles
// See `handle` as used in https://github.com/react-grid-layout/react-resizable#resize-handle
// Your component should have the class `.react-resizable-handle`, or you should add your custom
// class to the `draggableCancel` prop.
    var resizeHandle: (axis: ResizeHandleAxis, ref: Ref<HTMLElement>) -> ReactElement<*>
    // ... or ((resizeHandleAxis: ResizeHandleAxis, ref: ReactRef<HTMLElement>) => ReactElement<any>),

//
// Callbacks
//

    // Callback so you can save the layout.
// Calls back with (currentLayout) after every drag or resize stop.
    var onLayoutChange: ((Layout) -> Unit)?

//
// All callbacks below have signature (layout, oldItem, newItem, placeholder, e, element).
// 'start' and 'stop' callbacks pass `undefined` for 'placeholder'.
//

    /** Calls when drag starts. */
    var onDragStart: ItemCallback

    /** Calls on each drag movement. */
    var onDrag: ItemCallback

    /** Calls when drag is complete. */
    var onDragStop: ItemCallback

    /** Calls when resize starts. */
    var onResizeStart: ItemCallback

    /** Calls when resize movement happens. */
    var onResize: ItemCallback

    /** Calls when resize is complete. */
    var onResizeStop: ItemCallback

    //
    // Dropover functionality
    //

    /** Calls when an element has been dropped into the grid from outside. */
    var onDrop: ((layout: baaahs.app.ui.layout.port.Layout, item: LayoutItem?, e: Event) -> Unit)?

    // Calls when an element is being dragged over the grid from outside as above.
// This callback should return an object to dynamically change the droppingItem size
// Return false to short-circuit the dragover
    var onDropDragOver: ((e: DragOverEvent) -> DroppingItem? /* or false */)?

    // Ref for getting a reference for the grid's wrapping div.
// You can use this instead of a regular ref and the deprecated `ReactDOM.findDOMNode()`` function.
// Note that this type is React.Ref<HTMLDivElement> in TypeScript, Flow has a bug here
// https://github.com/facebook/flow/issues/8671#issuecomment-862634865
    var innerRef: Ref<HTMLDivElement>?
}

external interface DroppingItem {
    /** id of an element */
    var i: String

    /** width of an element */
    var w: Int

    /** height of an element */
    var h: Int
}

//external interface LayoutItem {
//    /** id of element */
//    var i: String
//
//    /** x position of element */
//    var x: Int
//
//    /** y position of element */
//    var y: Int
//
//    /** width of element */
//    var w: Int
//
//    /** height of element */
//    var h: Int
//
//    /** min width of element */
//    var minW: Int?
//
//    /** min height of element */
//    var minH: Int?
//
//    /** max width of element */
//    var maxW: Int?
//
//    /** max height of element */
//    var maxH: Int?
//
//    var static: Boolean?
//    var isBounded: Boolean?
//    var isDraggable: Boolean?
//    var isResizable: Boolean?
//}

external class DragOverEvent : MouseEvent {
    val nativeEvent: LayerEvent
}

external class LayerEvent : Event {
    var layerX: Double
    var layerY: Double
}


// Helper for generating column width
fun calcGridColWidth(positionParams: PositionParams): Double {
    val margin = positionParams.margin
    val containerPadding = positionParams.containerPadding
    val containerWidth = positionParams.containerWidth
    val cols = positionParams.cols

    return (containerWidth - margin[0] * (cols - 1) - containerPadding[0] * 2) / cols.toDouble()
}

// This can either be called:
// calcGridItemWHPx(w, colWidth, margin[0])
// or
// calcGridItemWHPx(h, rowHeight, margin[1])
fun calcGridItemWHPx(
    gridUnits: Int,
    colOrRowSize: Double,
    marginPx: Int
): Double {
    // 0 * Infinity === NaN, which causes problems with resize contraints
//    if (!isFinite(gridUnits)) return gridUnits
    return colOrRowSize * gridUnits + max(0, gridUnits - 1) * marginPx
}

private fun isFinite(number: Double): Boolean =
    number == Double.NEGATIVE_INFINITY || number == Double.POSITIVE_INFINITY

// Ported from react_grid_layout/calculateUtils/calcGridItemPosition:
fun calcGridItemPosition(
    positionParams: PositionParams,
    x: Int, y: Int, w: Int, h: Int
): baaahs.app.ui.layout.port.Position {
    val margin = positionParams.margin
    val containerPadding = positionParams.containerPadding
    val rowHeight = positionParams.rowHeight
    val colWidth = calcGridColWidth(positionParams)

    return jso {
//    // If resizing, use the exact width and height as returned from resizing callbacks.
//    if (state && state.resizing) {
//        out.width = Math.round(state.resizing.width);
//        out.height = Math.round(state.resizing.height);
//    }
//    // Otherwise, calculate from grid units.
//    else {
        width = calcGridItemWHPx(w, colWidth, margin[0]).roundToInt()
        height = calcGridItemWHPx(h, rowHeight, margin[1]).roundToInt()
//    }

//        // If dragging, use the exact width and height as returned from dragging callbacks.
//        if (state && state.dragging) {
//            top = Math.round(state.dragging.top);
//            left = Math.round(state.dragging.left);
//        }
//        // Otherwise, calculate from grid units.
//        else {
        top = ((rowHeight + margin[1]) * y + containerPadding[1]).roundToInt()
        left = ((colWidth + margin[0]) * x + containerPadding[0]).roundToInt()
//        }
    }
}

external interface PositionParams {
    var margin: Array<Int>
    var containerPadding: Array<Int>
    var containerWidth: Int
    var cols: Int
    var rowHeight: Double
    var maxRows: Int
}
