package baaahs.app.ui.layout.port

import baaahs.ui.className
import external.clsx.clsx
import external.react_draggable.DraggableCore
import external.react_draggable.DraggableCoreProps
import external.react_draggable.DraggableData
import external.react_grid_layout.PositionParams
import external.react_resizable.*
import kotlinx.js.Object
import kotlinx.js.jso
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.events.MouseEvent
import react.*
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

external interface GridItemProps : Props {
    var children: ReactElement<*>
    var cols: Int
    var containerWidth: Double
    var margin: Array<Int>
    var containerPadding: Array<Int>
    var rowHeight: Double
    var maxRows: Int
    var isDraggable: Boolean
    var isResizable: Boolean
    var isBounded: Boolean
    var static: Boolean?
    var useCSSTransforms: Boolean?
    var usePercentages: Boolean?
    var transformScale: Double
    var droppingPosition: DroppingPosition?

    var className: String
    var style: Any?
    // Draggability
    var cancel: String?
    var handle: String

    var x: Int
    var y: Int
    var w: Int
    var h: Int

    var minW: Int?
    var maxW: Int?
    var minH: Int?
    var maxH: Int?
    var i: String

    var resizeHandles: List<ResizeHandleAxis>?
    var resizeHandle: ResizeHandle?

    var onDrag: GridItemCallback<GridDragEvent>?
    var onDragStart: GridItemCallback<GridDragEvent>?
    var onDragStop: GridItemCallback<GridDragEvent>?
    var onResize: GridItemCallback<GridResizeEvent>?
    var onResizeStart: GridItemCallback<GridResizeEvent>?
    var onResizeStop: GridItemCallback<GridResizeEvent>?
}

class GridItem(props: GridItemProps) : RComponent<GridItemProps, GridItemState>(props) {
//    static defaultProps: DefaultProps = {
//        className: "",
//        cancel: "",
//        handle: "",
//        minH: 1,
//        minW: 1,
//        maxH: Infinity,
//        maxW: Infinity,
//        transformScale: 1
//    }
//
//    state: State = {
//        resizing: null,
//        dragging: null,
//        className: ""
//    }

    val elementRef = createRef<HTMLDivElement>()

    override fun shouldComponentUpdate(nextProps: GridItemProps, nextState: GridItemState): Boolean {
        // We can't deeply compare children. If the developer memoizes them, we can
        // use this optimization.
        if (this.props.children !== nextProps.children) return true
        if (this.props.droppingPosition !== nextProps.droppingPosition) return true
        // TODO memoize these calculations so they don't take so long?
        val oldPosition = calcGridItemPosition(
            this.getPositionParams(this.props),
            this.props.x,
            this.props.y,
            this.props.w,
            this.props.h,
            this.state
        )
        val newPosition = calcGridItemPosition(
            this.getPositionParams(nextProps),
            nextProps.x,
            nextProps.y,
            nextProps.w,
            nextProps.h,
            nextState
        )
        return (
                !fastPositionEqual(oldPosition, newPosition) ||
                        this.props.useCSSTransforms !== nextProps.useCSSTransforms
                )
            .also { console.log("GridItem(${props.i}): shouldComponentUpdate=$it ... ${nextProps.isDraggable}") }
    }

    override fun componentDidMount() {
        this.moveDroppingItem(jso {})
    }

    override fun componentDidUpdate(prevProps: GridItemProps, prevState: GridItemState, snapshot: Any) {
        this.moveDroppingItem(prevProps)
    }

    // When a droppingPosition is present, this means we should fire a move event, as if we had moved
    // this element by `x, y` pixels.
    fun moveDroppingItem(prevProps: GridItemProps) {
        val droppingPosition = this.props.droppingPosition
            ?: return

        val node = this.elementRef.current
            ?: return // Can't find DOM node (are we unmounted?)

        val prevDroppingPosition = prevProps.droppingPosition ?: jso {
                this.left = 0
                this.top = 0
        }
        val dragging = this.state.dragging

        val shouldDrag =
            (dragging != null && droppingPosition.left != prevDroppingPosition.left) ||
                    droppingPosition.top != prevDroppingPosition.top

        if (dragging == null) {
            this.onDragStart(droppingPosition.e, jso {
                    this.node = node
                    this.deltaX = droppingPosition.left
                    this.deltaY = droppingPosition.top
            })
        } else if (shouldDrag) {
            val deltaX = droppingPosition.left - dragging.left
            val deltaY = droppingPosition.top - dragging.top

            this.onDrag(droppingPosition.e, jso {
                    this.node = node
                    this.deltaX = deltaX
                    this.deltaY = deltaY
            })
        }
    }

    fun getPositionParams(props: GridItemProps = this.props): PositionParams {
        return jso {
                this.cols = props.cols
                this.containerPadding = props.containerPadding
                this.containerWidth = props.containerWidth.roundToInt()
                this.margin = props.margin
                this.maxRows = props.maxRows
                this.rowHeight = props.rowHeight
        }
    }

    /**
     * This is where we set the grid item's absolute placement. It gets a little tricky because we want to do it
     * well when server rendering, and the only way to do that properly is to use percentage width/left because
     * we don't know exactly what the browser viewport is.
     * Unfortunately, CSS Transforms, which are great for performance, break in this instance because a percentage
     * left is relative to the item itself, not its container! So we cannot use them on the server rendering pass.
     *
     * @param  {Object} pos Position object with width, height, left, top.
     * @return {Object}     Style object.
     */
    fun createStyle(pos: Position): dynamic { // [key: string]: ?string } {
        val usePercentages = this.props.usePercentages ?: false
        val containerWidth = this.props.containerWidth
        val useCSSTransforms = this.props.useCSSTransforms ?: true

        val style: dynamic
        // CSS Transforms support (default)
        if (useCSSTransforms) {
            style = setTransform(pos)
        } else {
            // top,left (slow)
            style = setTopLeft(pos)

            // This is used for server rendering.
            if (usePercentages) {
                style.left = perc(pos.left / containerWidth)
                style.width = perc(pos.width / containerWidth)
            }
        }

        return style
    }

    /**
     * Mix a Draggable instance into a child.
     * @param  {Element} child    Child element.
     * @return {Element}          Child wrapped in Draggable.
     */
    fun mixinDraggable(
        child: ReactElement<*>,
        isDraggable: Boolean
    ): ReactElement<DraggableCoreProps> {
        return createElement(DraggableCore, jso {
            this.disabled = !isDraggable
            this.onStart = ::onDragStart
            this.onDrag = ::onDrag
            this.onStop = ::onDragStop
            this.handle = props.handle
            this.cancel =
                ".react-resizable-handle${if (props.cancel?.isNotBlank() == true) ",${props.cancel}" else ""}"
            this.scale = props.transformScale
            this.nodeRef = elementRef
        }, child)
            .also { console.log("DraggableCore for ${props.i}: ", it.props) }
    }

    /**
     * Mix a Resizable instance into a child.
     * @param  {Element} child    Child element.
     * @param  {Object} position  Position object (pixel values)
     * @return {Element}          Child wrapped in Resizable.
     */
    fun mixinResizable(
        child: ReactElement<*>,
        position: Position,
        isResizable: Boolean
    ): ReactElement<ResizableProps> {
        val cols = props.cols
        val x = props.x
        val minW = props.minW ?: 1
        val minH = props.minH ?: 1
        val maxW = props.maxW ?: Int.MAX_VALUE
        val maxH = props.maxH ?: Int.MAX_VALUE
        val transformScale = props.transformScale
        val resizeHandles = props.resizeHandles
        val resizeHandle = props.resizeHandle
        val positionParams = this.getPositionParams()

        // This is the max possible width - doesn't go to infinity because of the width of the window
        val maxWidth = calcGridItemPosition(
            positionParams,
            0,
            0,
            cols - x,
            0
        ).width

        // Calculate min/max valraints using our min & maxes
        val mins = calcGridItemPosition(positionParams, 0, 0, minW, minH)
        val maxes = calcGridItemPosition(positionParams, 0, 0, maxW, maxH)
        val minConstraints = arrayOf(mins.width, mins.height)
        val maxConstraints = arrayOf(
                min(maxes.width, maxWidth),
                min(maxes.height, Int.MAX_VALUE)
        )
        return createElement(Resizable, jso {
            // These are opts for the resize handle itself
            this.draggableOpts = jso {
                jso {
                    disabled = !isResizable
                }
            }
            if (!isResizable) {
                this.className = "react-resizable-hide".className
            }
            this.width = position.width
            this.height = position.height
            this.minConstraints = minConstraints
            this.maxConstraints = maxConstraints
            this.onResizeStop = ::onResizeStop
            this.onResizeStart = ::onResizeStart
            this.onResize = ::onResize
            this.transformScale = transformScale
            this.resizeHandles = resizeHandles?.toTypedArray()
            this.handle = resizeHandle
        }, child)
    }

    /**
     * onDragStart event handler
     * @param  {Event}  e             event data
     * @param  {Object} callbackData  an object with node, delta and position information
     */
    fun onDragStart(e: MouseEvent, callbackData: DraggableData): Any {
        console.log("onDragStart(${props.i})")
        val node = callbackData.node
        val onDragStart = this.props.onDragStart
        val transformScale = this.props.transformScale
        if (onDragStart == null) return Unit

        val newPosition: PartialPosition = jso { top = 0; left = 0 }

        // TODO: this wont work on nested parents
        val offsetParent = node.offsetParent
            ?: return Unit
        val parentRect = offsetParent.getBoundingClientRect()
        val clientRect = node.getBoundingClientRect()
        val cLeft = clientRect.left / transformScale
        val pLeft = parentRect.left / transformScale
        val cTop = clientRect.top / transformScale
        val pTop = parentRect.top / transformScale
        newPosition.left = (cLeft - pLeft + offsetParent.scrollLeft).roundToInt()
        newPosition.top = (cTop - pTop + offsetParent.scrollTop).roundToInt()
        this.setState {
                this.dragging = newPosition
        }

        // Call callback with this data
        val layoutItem = calcXY(
            this.getPositionParams(),
            newPosition.top,
            newPosition.left,
            this.props.w,
            this.props.h
        )

        return onDragStart.invoke(
            this.props.i, layoutItem.x, layoutItem.y, jso {
                this.e = e
                this.node = node
                this.newPosition = newPosition
            })
    }

    /**
     * onDrag event handler
     * @param  {Event}  e             event data
     * @param  {Object} callbackData  an object with node, delta and position information
     */
    fun onDrag(e: MouseEvent, callbackData: DraggableData): Any {
        console.log("onDrag(${props.i})")
        val node = callbackData.node
        val deltaX = callbackData.deltaX
        val deltaY = callbackData.deltaY

        val onDrag = this.props.onDrag
            ?: return Unit

        val dragging = state.dragging
            ?: throw Error("onDrag called before onDragStart.")
        var top = dragging.top + deltaY
        var left = dragging.left + deltaX

        val isBounded = props.isBounded
        val i = props.i
        val w = props.w
        val h = props.h
        val containerWidth = props.containerWidth
        val positionParams = this.getPositionParams()

        // Boundary calculations; keeps items within the grid
        if (isBounded) {
            val offsetParent = node.offsetParent

            if (offsetParent != null) {
                val margin = props.margin
                val rowHeight = props.rowHeight
                val bottomBoundary =
                    offsetParent.clientHeight - calcGridItemWHPx(h, rowHeight, margin[1].toDouble())
                top = clamp(top, 0, bottomBoundary.roundToInt())

                val colWidth = calcGridColWidth(positionParams)
                val rightBoundary =
                containerWidth - calcGridItemWHPx(w, colWidth, margin[0].toDouble())
                left = clamp(left, 0, rightBoundary.roundToInt())
            }
        }

        val newPosition: PartialPosition = jso { this.top = top; this.left = left }
        this.setState {
            this.dragging = newPosition
        }

        // Call callback with this data
        val position = calcXY(positionParams, top, left, w, h)
        onDrag.invoke(
            i, position.x, position.y, jso {
                this.e = e
                this.node = node
                this.newPosition = newPosition
            }
        )
        return Unit
    }

    /**
     * onDragStop event handler
     * @param  {Event}  e             event data
     * @param  {Object} callbackData  an object with node, delta and position information
     */
    fun onDragStop(e: MouseEvent, callbackData: DraggableData): Any {
        console.log("onDragStop(${props.i})")
        val onDragStop = this.props.onDragStop
            ?: return Unit

        val dragging = state.dragging
            ?: throw Error("onDragEnd called before onDragStart.")
        val w = props.w
        val h = props.h
        val i = props.i
        val left = dragging.left
        val top = dragging.top
        val newPosition: PartialPosition = jso { this.top = top; this.left = left }
        this.setState { this.dragging = null }

        val position = calcXY(this.getPositionParams(), top, left, w, h)

        onDragStop.invoke(
            i, position.x, position.y,
            jso {
                    this.e = e
                    this.node = node
                    this.newPosition = newPosition
            }
        )
        return Unit
    }

    /**
     * onResizeStop event handler
     * @param  {Event}  e             event data
     * @param  {Object} callbackData  an object with node and size information
     */
    fun onResizeStop(e: MouseEvent, callbackData: ResizeCallbackData) {
        return this.onResizeHandler(e, callbackData, props.onResizeStop)
    }

    /**
     * onResizeStart event handler
     * @param  {Event}  e             event data
     * @param  {Object} callbackData  an object with node and size information
     */
    fun onResizeStart(e: MouseEvent, callbackData: ResizeCallbackData) {
        return this.onResizeHandler(e, callbackData, props.onResizeStart)
    }

    /**
     * onResize event handler
     * @param  {Event}  e             event data
     * @param  {Object} callbackData  an object with node and size information
     */
    fun onResize(e: MouseEvent, callbackData: ResizeCallbackData) {
        return this.onResizeHandler(e, callbackData, props.onResize)
    }

    /**
     * Wrapper around drag events to provide more useful data.
     * All drag events call the function with the given handler name,
     * with the signature (index, x, y).
     *
     * @param  {String} handlerName Handler name to wrap.
     * @return {Function}           Handler function.
     */
    fun onResizeHandler(e: MouseEvent, callbackData: ResizeCallbackData, handler: GridItemCallback<GridResizeEvent>?) {
        if (handler == null) return
        val cols = props.cols
        val x = props.x
        val y = props.y
        val i = props.i
        val minH = props.minH ?: 1
        val maxH = props.maxH ?: Int.MAX_VALUE
        var minW = props.minW ?: 1
        var maxW = props.maxW ?: Int.MAX_VALUE

        val size = callbackData.size

        // Get new XY
        val layoutItemSize = calcWH(
            this.getPositionParams(),
            size.width,
            size.height,
            x,
            y
        )
        var w = layoutItemSize.w
        var h = layoutItemSize.h

        // minW should be at least 1 (TODO propTypes validation?)
        minW = max(minW, 1)

        // maxW should be at most (cols - x)
        maxW = min(maxW, cols - x)

        // Min/max capping
        w = clamp(w, minW, maxW)
        h = clamp(h, minH, maxH)

        this.setState {
            this.resizing = if (handler == ::onResizeStop) null else size
        }

        handler.invoke(i, w, h, jso {
            this.e = e
            this.node = node
            this.size = size
        })
    }

    override fun RBuilder.render() {
        val x = props.x
        val y = props.y
        val w = props.w
        val h = props.h
        val isDraggable = props.isDraggable
        val isResizable = props.isResizable
        val droppingPosition = props.droppingPosition
        val useCSSTransforms = props.useCSSTransforms

        val pos = calcGridItemPosition(
            getPositionParams(),
            x,
            y,
            w,
            h,
            state
        )
        val child = props.children.unsafeCast<ReactElement<GridItemProps>>()

        // Create the child element. We clone the existing element but modify its className and style.
        var newChild: ReactElement<*> = cloneElement(child, jso {
            this.ref = elementRef
            this.className = clsx(
                "react-grid-item",
                child.props.className,
                props.className,
                jso {
                    this.static = props.static
                    this.resizing = state.resizing
                    this["react-draggable"] = isDraggable
                    this["react-draggable-dragging"] = state.dragging
                    this.dropping = droppingPosition
                    this.cssTransforms = useCSSTransforms
                }
            )
            // We can set the width and height on the child, but unfortunately we can't set the position.
            this.style = Object.assign(jso(),
                props.style,
                child.props.style,
                createStyle(pos)
            )
        })

        console.log("$props.i: render, isDraggable=", isDraggable, ", isResizable=", isResizable)

        // Resizable support. This is usually on but the user can toggle it off.
        newChild = mixinResizable(newChild, pos, isResizable)

        // Draggable support. This is always on, except for with placeholders.
        newChild = mixinDraggable(newChild, isDraggable)

        child(newChild)
    }
}

external interface GridItemState : State {
    var dragging: PartialPosition?
    var resizing: Size?
}

typealias GridItemCallback<Data> = (i: String, w: Int, h: Int, data: Data) -> Any
