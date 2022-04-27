package baaahs.ui.gridlayout

import baaahs.app.ui.layout.DragNDropContext
import baaahs.app.ui.layout.GridLayoutContext
import baaahs.app.ui.layout.dragNDropContext
import baaahs.geom.Vector2D
import baaahs.ui.className
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
        parentContainerRef = (createRef<GridLayout>().unsafeCast<MutableRefObject<GridLayout>>())
            .also { it.current = props.parentContainer }
        resizing = null
        dragging = null
//        className = props.className
    }

    private val context: GridLayoutContext = context.gridLayoutContext

    private val elementRef = createRef<HTMLDivElement>()

    override fun shouldComponentUpdate(nextProps: GridItemProps, nextState: GridItemState): Boolean {
        // We can't deeply compare children. If the developer memoizes them, we can
        // use this optimization.
        if (this.props.children !== nextProps.children) return true
        if (this.props.droppingPosition !== nextProps.droppingPosition) return true
        // TODO memoize these calculations so they don't take so long?
        val positionParams = state.parentContainer.getPositionParams()
        val oldPosition = calcGridItemPosition(
            positionParams,
            this.props.x,
            this.props.y,
            this.props.w,
            this.props.h,
            this.state
        )
        val newPosition = calcGridItemPosition(
            positionParams,
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
            val deltaX = droppingPosition.left - dragging.x
            val deltaY = droppingPosition.top - dragging.y

            this.onDrag(droppingPosition.e, jso {
                    this.node = node
                    this.deltaX = deltaX.roundToInt()
                    this.deltaY = deltaY.roundToInt()
            })
        }
    }

    private fun GridLayout.getPositionParams(): PositionParams {
        val gridLayout = this
        return jso {
            this.cols = gridLayout.cols
            this.containerPadding = gridLayout.containerPadding
            this.containerWidth = gridLayout.containerWidth.roundToInt()
            this.margin = gridLayout.margin
            this.maxRows = gridLayout.maxRows
            this.rowHeight = gridLayout.rowHeight
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
        val containerWidth = state.parentContainer.containerWidth
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
        val cols = state.parentContainer.cols
        val x = props.x
        val minW = props.minW ?: 1
        val minH = props.minH ?: 1
        val maxW = props.maxW ?: Int.MAX_VALUE
        val maxH = props.maxH ?: Int.MAX_VALUE
        val transformScale = props.transformScale
        val resizeHandles = props.resizeHandles
        val resizeHandle = props.resizeHandle
        val positionParams = state.parentContainer.getPositionParams()

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
            resizeHandles?.let { this.resizeHandles = it.toTypedArray() }
            this.handle = resizeHandle
        }, child)
    }

    fun MouseEvent.findContainer(callbackData: DraggableData): GridLayout? {
        var element = target as Element?
        val dragging = callbackData.node

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
            console.log("GridLayout: transfer ${layoutItem?.i} from",
                previousContainer.props.id,
                "to", container.props.id
            )
            if (layoutItem != null) {
                calculatePxPositionInLayout(container, draggingNode).also { px ->
                    calcGridPosition(
                        previousContainer.getPositionParams(),
                        px.y.roundToInt(), px.x.roundToInt(), this.props.w, this.props.h
                    ).also { gridXY ->
                        layoutItem.x = gridXY.x
                        layoutItem.y = gridXY.y
                        layoutItem.w = 1
                        layoutItem.h = 1
                    }
                }
                container.onItemEnter(layoutItem)
            }
            state.parentContainerRef.current = container
            val updatedDragging = calculatePxPositionInLayout(container, draggingNode)
            state.dragging = updatedDragging
            setState {
                dragging = updatedDragging
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
    fun onDragStart(e: MouseEvent, callbackData: DraggableData): Any {
        val container = e.findContainer(callbackData)
            ?: return Unit
        val node = callbackData.node
        maybeSwitchParentContainer(container, node)

        val newPosition = calculatePxPositionInLayout(container, node)

        this.setState {
            this.dragging = newPosition
        }

        // Call callback with this data
        val layoutItem = calcGridPosition(
            state.parentContainer.getPositionParams(),
            newPosition.y.roundToInt(),
            newPosition.x.roundToInt(),
            this.props.w,
            this.props.h
        )

        return container.onDragStart(
            this.props.i, layoutItem.x, layoutItem.y, jso {
                this.e = e
                this.node = node
                this.newPosition = newPosition
            })
    }

    private fun HTMLElement.getPosition(): Vector2D =
        getBoundingClientRect().let {
            Vector2D(it.left, it.top)
        }

    private fun HTMLElement.getPositionMinusScroll(): Vector2D =
        getPosition() - Vector2D(scrollLeft, scrollTop)

    private fun calculatePxPositionInLayout(gridLayout: GridLayout, node: HTMLElement): Vector2D {
        val nodePos = node.getPosition()
        val parent = gridLayout.rootElement ?: error("No root element for ${gridLayout.props.id}.")
        val parentPos = parent.getPositionMinusScroll()
        return (nodePos - parentPos) / props.transformScale
    }

    /**
     * onDrag event handler
     * @param  {Event}  e             event data
     * @param  {Object} callbackData  an object with node, delta and position information
     */
    fun onDrag(e: MouseEvent, callbackData: DraggableData): Any {
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


        var top = dragging.y.roundToInt() + deltaY
        var left = dragging.x.roundToInt() + deltaX

        val isBounded = props.isBounded
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
                    offsetParent.clientHeight - calcGridItemWHPx(h, rowHeight, margin[1].toDouble())
                top = clamp(top, 0, bottomBoundary.roundToInt())

                val colWidth = calcGridColWidth(positionParams)
                val rightBoundary =
                    containerWidth - calcGridItemWHPx(w, colWidth, margin[0].toDouble())
                left = clamp(left, 0, rightBoundary.roundToInt())
            }
        }

        val newPosition = Vector2D(left, top)
        this.setState {
            this.dragging = newPosition
        }

        // Call callback with this data
        val position = calcGridPosition(positionParams, top, left, w, h)
        container.onDragItem(
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
        this.setState { this.dragging = null }

        val position = calcGridPosition(
            state.parentContainer.getPositionParams(),
            top.roundToInt(), left.roundToInt(), w, h)

        container.onDragStop(
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
        val layoutItemSize = calcWH(
            state.parentContainer.getPositionParams(),
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
            this.resizing = if (handler === props.onResizeStop) null else size
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

        val positionParams = state.parentContainer.getPositionParams()
//        console.log("${props.i}.parent.getPositionParams() -> ", positionParams)
        val pos = calcGridItemPosition(positionParams, x, y, w, h, state)
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
                }
            )
            // We can set the width and height on the child, but unfortunately we can't set the position.
            this.style = Object.assign(jso(),
                props.style,
                child.props.style,
                createStyle(pos)
            )
        })

        // Resizable support. This is usually on but the user can toggle it off.
        newChild = mixinResizable(newChild, pos, isResizable)

        // Draggable support. This is always on, except for with placeholders.
        newChild = mixinDraggable(newChild, isDraggable)

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

// Workaround for bug in kotlin-react RStatics to force it to be initialized early.
@Suppress("unused")
private val defaultPropsWorkaround = GridItem.defaultProps

external interface GridItemState : State {
    var parentContainerRef: MutableRefObject<GridLayout>
    var dragging: Vector2D?
    var resizing: Size?
}

val GridItemState.parentContainer get() = parentContainerRef.current!!

typealias GridItemCallback<Data> = (i: String, w: Int, h: Int, data: Data) -> Any
