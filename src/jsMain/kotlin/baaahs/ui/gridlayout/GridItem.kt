package baaahs.ui.gridlayout

import baaahs.SparkleMotion
import baaahs.app.ui.layout.DragNDropContext
import baaahs.app.ui.layout.GridLayoutContext
import baaahs.app.ui.layout.dragNDropContext
import baaahs.clamp
import baaahs.geom.Vector2D
import baaahs.ui.className
import baaahs.ui.isParentOf
import baaahs.x
import baaahs.y
import external.clsx.clsx
import external.react_draggable.DraggableCore
import external.react_draggable.DraggableCoreProps
import external.react_draggable.DraggableData
import external.react_resizable.*
import kotlinx.js.Object
import kotlinx.js.jso
import org.w3c.dom.Element
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLElement
import org.w3c.dom.events.MouseEvent
import org.w3c.dom.get
import react.*
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

external interface GridItemProps : Props {
    var children: ReactElement<*>
    var parentContainer: GridLayout
//    var cols: Int
//    var containerWidth: Double
//    var margin: Array<Int>
//    var containerPadding: Array<Int>
//    var rowHeight: Double
//    var maxRows: Int
    var isDraggable: Boolean
    var isResizable: Boolean

    // If false, don't even render resize handles etc.
    var isEverEditable: Boolean? // = true

    var static: Boolean?
    var useCSSTransforms: Boolean?
    var usePercentages: Boolean?
    var transformScale: Double
    var droppingPosition: DroppingPosition?
    var notDroppableHere: Boolean?

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

    var resizeHandle: ResizeHandle?

//    var onDrag: GridItemCallback<GridDragEvent>?
//    var onDragStart: GridItemCallback<GridDragEvent>?
//    var onDragStop: GridItemCallback<GridDragEvent>?
    var onResize: GridItemCallback<GridResizeEvent>?
    var onResizeStart: GridItemCallback<GridResizeEvent>?
    var onResizeStop: GridItemCallback<GridResizeEvent>?
}

class GridItem(
    props: GridItemProps, context: DragNDropContext
) : RComponent<GridItemProps, GridItemState>(props) {
    override fun GridItemState.init(props: GridItemProps) {
        parentContainer = props.parentContainer
        resizing = null
        dragging = null
//        className = props.className
    }

    private val context: GridLayoutContext = context.gridLayoutContext

    private val elementRef = createRef<HTMLDivElement>()

    override fun shouldComponentUpdate(nextProps: GridItemProps, nextState: GridItemState): Boolean {
        // We can't deeply compare children. If the developer memoizes them, we can
        // use this optimization.
        if (props.children !== nextProps.children) return true
        if (props.droppingPosition !== nextProps.droppingPosition) return true
        // TODO memoize these calculations so they don't take so long?
        val positionParams = state.parentContainer.getPositionParams()
        val oldPosition = positionParams.calcGridItemPosition(
            props.x, props.y, props.w, props.h, state
        )
        val newPosition = positionParams.calcGridItemPosition(
            nextProps.x, nextProps.y, nextProps.w, nextProps.h, nextState
        )
        return oldPosition != newPosition ||
                props.useCSSTransforms !== nextProps.useCSSTransforms
    }

    override fun componentDidMount() {
        moveDroppingItem(jso {})
    }

    override fun componentDidUpdate(prevProps: GridItemProps, prevState: GridItemState, snapshot: Any) {
        moveDroppingItem(prevProps)
    }

    // When a droppingPosition is present, this means we should fire a move event, as if we had moved
    // this element by `x, y` pixels.
    private fun moveDroppingItem(prevProps: GridItemProps) {
        val droppingPosition = props.droppingPosition
            ?: return

        val node = elementRef.current
            ?: return // Can't find DOM node (are we unmounted?)

        val prevDroppingPosition = prevProps.droppingPosition ?: DroppingPosition(0, 0, jso {
            this.asDynamic()["isFakeMouseEvent"] = true
        })
        val dragging = state.dragging

        val shouldDrag =
            (dragging != null && droppingPosition.left != prevDroppingPosition.left) ||
                    droppingPosition.top != prevDroppingPosition.top

        if (dragging == null) {
            onDragStart(droppingPosition.e, jso {
                    this.node = node
                    this.deltaX = droppingPosition.left
                    this.deltaY = droppingPosition.top
            })
        } else if (shouldDrag) {
            val deltaX = droppingPosition.left - dragging.x
            val deltaY = droppingPosition.top - dragging.y

            onDrag(droppingPosition.e, jso {
                    this.node = node
                    this.deltaX = deltaX.roundToInt()
                    this.deltaY = deltaY.roundToInt()
            })
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
    private fun createStyle(pos: Position): dynamic { // [key: string]: ?string } {
        val usePercentages = props.usePercentages ?: false
        val containerWidth = state.parentContainer.containerWidth
        val useCSSTransforms = SparkleMotion.USE_CSS_TRANSFORM // props.useCSSTransforms ?: true

        val style: dynamic
        // CSS Transforms support (default)
        if (useCSSTransforms) {
            style = pos.setTransform()
        } else {
            // top,left (slow)
            style = pos.setTopLeft()

            // This is used for server rendering.
            if (usePercentages) {
                style.left = pct(pos.left / containerWidth)
                style.width = pct(pos.width / containerWidth)
            }
        }

        return style
    }

    private fun Position.setTopLeft(): dynamic {
        return jso {
            this.top = "${top}px"
            this.left = "${left}px"
            this.width = "${width}px"
            this.height = "${height}px"
            this.position = "absolute"
        }
    }

    private fun Position.setTransform(): dynamic {
        // Replace unitless items with px
        val translate = "translate(${left}px,${top}px)"
        return jso {
            transform = translate
            WebkitTransform = translate
            MozTransform = translate
            msTransform = translate
            OTransform = translate
            this.width = "${width}px"
            this.height = "${height}px"
            this.position = "absolute"
        }
    }

    /**
     * Helper to convert a number to a percentage String.
     *
     * @param  {Number} num Any number
     * @return {String}     That number as a percentage.
     */
    private fun pct(num: Number): String {
        return "${num.toDouble() * 100}%"
    }

    /**
     * Mix a Draggable instance into a child.
     * @param  {Element} child    Child element.
     * @return {Element}          Child wrapped in Draggable.
     */
    private fun mixinDraggable(
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
    }

    /**
     * Mix a Resizable instance into a child.
     * @param  {Element} child    Child element.
     * @param  {Object} position  Position object (pixel values)
     * @return {Element}          Child wrapped in Resizable.
     */
    private fun mixinResizable(
        child: ReactElement<*>,
        position: Position,
        isResizable: Boolean
    ): ReactElement<ResizableProps> {
        val cols = state.parentContainer.cols
        val x = props.x
        val minW = props.minW ?: 1
        val minH = props.minH ?: 1
        val maxW = props.maxW ?: Int.MAX_VALUE
        val maxH = props.maxH ?: Int.MAX_VALUE
        val transformScale = props.transformScale
        val resizeHandle = props.resizeHandle
        val positionParams = state.parentContainer.getPositionParams()

        // This is the max possible width - doesn't go to infinity because of the width of the window
        val maxWidth = positionParams.calcGridItemPosition(0, 0, cols - x, 0).width

        // Calculate min/max constraints using our min & maxes
        val mins = positionParams.calcGridItemPosition(0, 0, minW, minH)
        val maxes = positionParams.calcGridItemPosition(0, 0, maxW, maxH)
        val minConstraints = arrayOf<Number>(mins.width, mins.height)
        val maxConstraints = arrayOf<Number>(
                min(maxes.width, maxWidth).let { if (it == Int.MAX_VALUE) Double.POSITIVE_INFINITY else it },
                min(maxes.height, Int.MAX_VALUE).let { if (it == Int.MAX_VALUE) Double.POSITIVE_INFINITY else it }
        )
        return createElement(Resizable, jso {
            // These are opts for the resize handle itself
            this.draggableOpts = jso {
                disabled = !isResizable
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
            this.resizeHandles = ResizeHandleAxes
            this.handle = resizeHandle
        }, child)
    }

    var lastDragEl: Element? = null
    var lastDragOver: Element? = null
    private fun MouseEvent.findContainer(callbackData: DraggableData): GridLayout? {
        var element = target as Element?
        val dragging = callbackData.node
        if (dragging != lastDragEl || element != lastDragOver) {
            lastDragEl = dragging
            lastDragOver = element
        }

        while (element != null) {
            if (!dragging.isParentOf(element)) {
                val containerId = (element as? HTMLElement)?.dataset?.get("gridLayoutContainer")
                if (containerId != null) {
                    return context.findLayout(containerId).gridLayout
                }
            }

            element = element.parentElement
        }
        return null
    }

    private fun maybeSwitchParentContainer(container: GridLayout, draggingNode: HTMLElement): Boolean {
        val previousContainer = state.parentContainer
        if (previousContainer != container) {
            val layoutItem = previousContainer.onItemExit()
                ?: error("no item from onItemExit()?")

            console.log("GridLayout: transfer ${layoutItem.i} from",
                previousContainer.props.id,
                "to", container.props.id
            )
            val positionInContainerPx = container.calculatePxPositionInLayout(draggingNode)
            val positionParams = container.getPositionParams()
            val gridXY = positionParams.calcGridPosition(
                positionInContainerPx.x.roundToInt(), positionInContainerPx.y.roundToInt(),
                props.w, props.h
            )
            container.onItemEnter(
                layoutItem.copy(
                    x = gridXY.x,
                    y = gridXY.y,
                    w = 1,
                    h = 1
                )
            )
            val draggingSizeGridUnits = positionParams.calcWidthAndHeightInGridUnits(
                draggingNode.clientWidth, draggingNode.clientHeight,
                positionParams.cols, positionParams.maxRows
            ).let<LayoutItemSize, Size> {
                jso { this.width = it.width; this.height = it.height }
            }
            state.dragging = positionInContainerPx
            state.resizing = draggingSizeGridUnits
            setState {
                this.parentContainer = container
                this.dragging = positionInContainerPx
                this.resizing = draggingSizeGridUnits
            }
            return true
        }
        return false
    }

    /**
     * onDragStart event handler
     * @param  {Event}  e             event data
     * @param  {Object} callbackData  an object with node, delta and position information
     */
    private fun onDragStart(e: MouseEvent, callbackData: DraggableData): Any {
        val container = state.parentContainer
        val node = callbackData.node
        val newPxPosition = container.calculatePxPositionInLayout(node)

        setState {
            this.dragging = newPxPosition
            this.draggingFromContainer = container
        }

        // Call callback with this data
        val newGridPosition = container.getPositionParams()
            .calcGridPosition(
                newPxPosition.x.roundToInt(), newPxPosition.y.roundToInt(), props.w, props.h
            )

        context.dragging = true
        e.stopPropagation()
        e.preventDefault()
        return container.onDragStart(
            props.i, newGridPosition.x, newGridPosition.y, jso {
                this.e = e
                this.node = node
                this.newPosition = newPxPosition
            })
    }

    /**
     * onDrag event handler
     * @param  {Event}  e             event data
     * @param  {Object} callbackData  an object with node, delta and position information
     */
    private fun onDrag(e: MouseEvent, callbackData: DraggableData): Any {
        val container = e.findContainer(callbackData)
            ?: return Unit
        val node = callbackData.node
        val deltaX = callbackData.deltaX
        val deltaY = callbackData.deltaY

        if (maybeSwitchParentContainer(container, node)) {
            setState {
                this.dragging = Vector2D(
                    state.dragging!!.x + deltaX,
                    state.dragging!!.y + deltaY
                )
            }
            return Unit
        }

        val dragging = state.dragging
            ?: throw Error("onDrag called before onDragStart.")


        var left = dragging.x.roundToInt() + deltaX
        var top = dragging.y.roundToInt() + deltaY

        val isBounded = props.parentContainer.isBounded
        val i = props.i
        val w = props.w
        val h = props.h
        val containerWidth = state.parentContainer.containerWidth
        val positionParams = state.parentContainer.getPositionParams()

        // Boundary calculations; keeps items within the grid
        if (isBounded) {
            val offsetParent = node.offsetParent

            if (offsetParent != null) {
                val margin = state.parentContainer.margin
                val rowHeight = state.parentContainer.rowHeight
                val bottomBoundary =
                    offsetParent.clientHeight - calcGridItemWHPx(h, rowHeight, margin.y.toDouble())
                top = top.clamp(0, bottomBoundary.roundToInt())

                val colWidth = positionParams.calcGridColWidth()
                val rightBoundary =
                    containerWidth - calcGridItemWHPx(w, colWidth, margin.x.toDouble())
                left = left.clamp(0, rightBoundary.roundToInt())
            }
        }

        val newPosition = Vector2D(left, top)
        setState {
            this.dragging = newPosition
        }

        // Call callback with this data
        val gridPosition = positionParams.calcGridPosition(left, top, w, h)
        container.onDragItem(
            i, gridPosition.x, gridPosition.y, jso {
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
    private fun onDragStop(e: MouseEvent, callbackData: DraggableData): Any {
        val container = e.findContainer(callbackData)
            ?: return Unit
        maybeSwitchParentContainer(container, callbackData.node)

        val dragging = state.dragging
            ?: throw Error("onDragEnd called before onDragStart.")
        val w = props.w
        val h = props.h
        val i = props.i
        val left = dragging.x
        val top = dragging.y
        val newPosition = Vector2D(left, top)
        val draggingFromContainer = state.draggingFromContainer
        setState {
            this.dragging = null
            this.draggingFromContainer = null
        }

        val gridPosition = state.parentContainer.getPositionParams()
            .calcGridPosition(left.roundToInt(), top.roundToInt(), w, h)

        context.dragging = false

        container.onDragStop(
            i, gridPosition.x, gridPosition.y,
            jso {
                this.e = e
                this.node = node
                this.newPosition = newPosition
            }
        )
        if (draggingFromContainer != null && draggingFromContainer != container) {
            draggingFromContainer.onDragStop(
                i, gridPosition.x, gridPosition.y,
                jso {
                    this.e = e
                    this.node = node
                    this.newPosition = newPosition
                }
            )
        }
        return Unit
    }

    /**
     * onResizeStop event handler
     * @param  {Event}  e             event data
     * @param  {Object} callbackData  an object with node and size information
     */
    private fun onResizeStop(e: MouseEvent, callbackData: ResizeCallbackData) {
        return onResizeHandler(e, callbackData, "onResizeStop")
    }

    /**
     * onResizeStart event handler
     * @param  {Event}  e             event data
     * @param  {Object} callbackData  an object with node and size information
     */
    private fun onResizeStart(e: MouseEvent, callbackData: ResizeCallbackData) {
        return onResizeHandler(e, callbackData, "onResizeStart")
    }

    /**
     * onResize event handler
     * @param  {Event}  e             event data
     * @param  {Object} callbackData  an object with node and size information
     */
    private fun onResize(e: MouseEvent, callbackData: ResizeCallbackData) {
        return onResizeHandler(e, callbackData, "onResize")
    }

    /**
     * Wrapper around drag events to provide more useful data.
     * All drag events call the function with the given handler name,
     * with the signature (index, x, y).
     *
     * @param  {String} handlerName Handler name to wrap.
     * @return {Function}           Handler function.
     */
    private fun onResizeHandler(e: MouseEvent, callbackData: ResizeCallbackData, handlerName: String) {
        val handler = props.asDynamic()[handlerName] as GridItemCallback<GridResizeEvent>
            ?: return

        val cols = state.parentContainer.cols
        val x = props.x
        val y = props.y
        val i = props.i
        val minH = props.minH ?: 1
        val maxH = props.maxH ?: Int.MAX_VALUE
        var minW = props.minW ?: 1
        var maxW = props.maxW ?: Int.MAX_VALUE

        val node = callbackData.node
        val size = callbackData.size

        // Get new XY
        val layoutItemSize = state.parentContainer.getPositionParams()
            .calcWidthAndHeightInGridUnits(size.width, size.height, x, y)
        var w = layoutItemSize.width
        var h = layoutItemSize.height

        // minW should be at least 1 (TODO propTypes validation?)
        minW = max(minW, 1)

        // maxW should be at most (cols - x)
        maxW = min(maxW, cols - x)

        // Min/max capping
        w = w.clamp(minW, maxW)
        h = h.clamp(minH, maxH)

        setState {
            this.resizing = if (handlerName == "onResizeStop") null else size
        }

        handler.invoke(i, w, h, jso {
            this.e = e
            this.node = node
            this.size = size
        })
    }

    override fun RBuilder.render() {
        val isDraggable = props.isDraggable
        val isResizable = props.isResizable
        val isEverEditable = props.isEverEditable ?: true
        val droppingPosition = props.droppingPosition
        val useCSSTransforms = SparkleMotion.USE_CSS_TRANSFORM // props.useCSSTransforms

        val positionParams = state.parentContainer.getPositionParams()
//        console.log("${props.i}.parent.getPositionParams() -> ", positionParams)
        val pos = positionParams.calcGridItemPosition(props.x, props.y, props.w, props.h, state)
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
                    this["grid-item-resizing"] = state.resizing
                    this["react-draggable"] = isDraggable
                    this["react-draggable-dragging"] = state.dragging
                    this.dropping = droppingPosition
                    this.cssTransforms = useCSSTransforms
                    this["react-draggable-not-droppable-here"] = props.notDroppableHere == true
                }
            )
            // We can set the width and height on the child, but unfortunately we can't set the position.
            this.style = Object.assign(jso(),
                props.style,
                child.props.style,
                createStyle(pos)
            )
        })

        if (isEverEditable) {
            // Resizable support. This is usually on but the user can toggle it off.
            newChild = mixinResizable(newChild, pos, isResizable)

            // Draggable support. This is always on, except for with placeholders.
            newChild = mixinDraggable(newChild, isDraggable)
        }

        child(newChild)
    }

    companion object : RStatics<GridItemProps, GridItemState, GridItem, Context<DragNDropContext>>(GridItem::class) {
        init {
            displayName = GridItem::class.simpleName

            contextType = dragNDropContext

            defaultProps = jso {
                className = ""
                cancel = ""
                handle = ""
                minH = 1
                minW = 1
                maxH = Int.MAX_VALUE
                maxW = Int.MAX_VALUE
                transformScale = 1.0
            }
        }
    }
}

fun RBuilder.gridItem(handler: RHandler<GridItemProps>) =
    child(GridItem::class, handler = handler)

// Workaround for bug in kotlin-react RStatics to force it to be initialized early.
@Suppress("unused")
private val defaultPropsWorkaround = GridItem.defaultProps

external interface GridItemState : State {
    var parentContainer: GridLayout
    var dragging: Vector2D?
    var draggingFromContainer: GridLayout?
    var resizing: Size?
}

typealias GridItemCallback<Data> = (i: String, w: Int, h: Int, data: Data) -> Any
