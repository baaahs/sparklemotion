package baaahs.ui.gridlayout

import baaahs.window
import external.lodash.isEqual
import kotlinx.css.LinearDimension
import kotlinx.css.height
import kotlinx.js.Object
import kotlinx.js.jso
import org.w3c.dom.HTMLElement
import org.w3c.dom.events.Event
import react.*
import react.dom.*
import react.dom.events.DragEvent
import styled.inlineStyles
import kotlin.math.min
import kotlin.math.roundToInt

external interface GridLayoutState : State {
    var activeDrag: LayoutItem?
    var layout: Layout
    var mounted: Boolean
    var oldDragItem: LayoutItem?
    var oldLayout: Layout?
    var oldResizeItem: LayoutItem?
    var droppingDOMNode: ReactElement<*>?
    var droppingPosition: DroppingPosition?

    // Mirrored props
    var children: ReactNode?
    var compactType: CompactType?
    var propsLayout: Layout?
}

val layoutClassName = "react-grid-layout"
val isFirefox = window.navigator.userAgent.contains("firefox", true)

class GridLayout(props: GridLayoutProps) : RComponent<GridLayoutProps, GridLayoutState>(props) {
    override fun GridLayoutState.init(props: GridLayoutProps) {

        activeDrag = null
        layout = synchronizeLayoutWithChildren(
            props.layout,
            props.children.unsafeCast<ReactChildren>(),
            props.cols!!,
            // Legacy support for verticalCompact: false
            compactType(props),
            props.allowOverlap
        )
        mounted = false
        oldDragItem = null
        oldLayout = null
        oldResizeItem = null
        droppingDOMNode = null
        children = null
    }

    var dragEnterCounter = 0

    override fun componentDidMount() {
        state.mounted = true

        onLayoutMaybeChanged(state.layout, props.layout)
    }

    override fun shouldComponentUpdate(nextProps: GridLayoutProps, nextState: GridLayoutState): Boolean {
        return (
                // NOTE: this is almost always unequal. Therefore the only way to get better performance
                // from SCU is if the user intentionally memoizes children. If they do, and they can
                // handle changes properly, performance will increase.
                props.children !== nextProps.children ||
                        !isEqual(props, nextProps) ||
//                        !fastRGLPropsEqual(propsD, nextProps, ::isEqual) ||
                        this.state.activeDrag != nextState.activeDrag ||
                        this.state.mounted != nextState.mounted ||
                        this.state.droppingPosition != nextState.droppingPosition
                )
    }

    override fun componentDidUpdate(prevProps: GridLayoutProps, prevState: GridLayoutState, snapshot: Any) {
        if (this.state.activeDrag != null) {
            val newLayout = this.state.layout
            val oldLayout = prevState.layout

            this.onLayoutMaybeChanged(newLayout, oldLayout)
        }
    }

    /**
     * Calculates a pixel value for the container.
     * @return {String} Container height in pixels.
     */
    fun containerHeight(): String? {
        if (!props.autoSize!!) return null
        val nbRow = bottom(this.state.layout)
        val containerPadding = props.containerPadding
        val containerPaddingY = if (containerPadding != null)
            containerPadding[1] else props.margin!!.get(1)
        val pixels = nbRow * props.rowHeight!! +
                (nbRow - 1) * props.margin!![1] +
                containerPaddingY * 2
        return "${pixels}px"
    }

    /**
     * When dragging starts
     * @param {String} i Id of the child
     * @param {Number} x X position of the move
     * @param {Number} y Y position of the move
     * @param {Event} e The mousedown event
     * @param {Element} node The current dragging DOM element
     */
    fun onDragStart(i: String, x: Int, y: Int, gridDragEvent: GridDragEvent): Any {
        val e = gridDragEvent.e
        val node = gridDragEvent.node
        val layout = this.state.layout
        val l = getLayoutItem(layout, i)
            ?: return Unit

        setState {
            oldDragItem = cloneLayoutItem(l)
            oldLayout = layout
        }

        return props.onDragStart(layout, l, l, null, e, node)
    }

    /**
     * Each drag movement create a new dragelement and move the element to the dragged location
     * @param {String} i Id of the child
     * @param {Number} x X position of the move
     * @param {Number} y Y position of the move
     * @param {Event} e The mousedown event
     * @param {Element} node The current dragging DOM element
     */
    fun onDrag(i: String, x: Int, y: Int, gridDragEvent: GridDragEvent) {
        val e = gridDragEvent.e
        val node = gridDragEvent.node
        val oldDragItem = this.state.oldDragItem
        var layout = this.state.layout
        val cols = props.cols
        val allowOverlap = props.allowOverlap == true
        val preventCollision = props.preventCollision
        val l = getLayoutItem(layout, i)
            ?: return

        // Create placeholder (display only)
        val placeholder = jso<PlaceholderLayoutItem> {
            this.w = l.w
            this.h = l.h
            this.x = l.x
            this.y = l.y
            this.placeholder = true
            this.i = i
        }

        // Move the element to the dragged location.
        val isUserAction = true
        layout = moveElement(
            layout,
            l,
            x,
            y,
            isUserAction,
            preventCollision,
            compactType(props),
            cols!!,
            allowOverlap
        )

        props.onDrag(layout, oldDragItem, l, placeholder, e, node)

        this.setState {
            layout = if (allowOverlap) layout else compact(layout, compactType(props), cols)
            activeDrag = placeholder
        }
    }

    /**
     * When dragging stops, figure out which position the element is closest to and update its x and y.
     * @param  {String} i Index of the child.
     * @param {Number} x X position of the move
     * @param {Number} y Y position of the move
     * @param {Event} e The mousedown event
     * @param {Element} node The current dragging DOM element
     */
    fun onDragStop(i: String, x: Int, y: Int, gridDragEvent: GridDragEvent) {
        val e = gridDragEvent.e
        val node = gridDragEvent.node

        if (this.state.activeDrag == null) return

        val oldDragItem = this.state.oldDragItem
        var layout = this.state.layout
        val cols = props.cols
        val allowOverlap = props.allowOverlap == true
        val preventCollision = props.preventCollision
        val l = getLayoutItem(layout, i)
            ?: return

        // Move the element here
        val isUserAction = true
        layout = moveElement(
            layout,
            l,
            x,
            y,
            isUserAction,
            preventCollision,
            compactType(props),
            cols!!,
            allowOverlap
        )

        props.onDragStop(layout, oldDragItem, l, null, e, node)

        // Set state
        val newLayout = if (allowOverlap) layout else compact(layout, compactType(props), cols)
        val oldLayout = this.state.oldLayout
        this.setState {
            activeDrag = null
            layout = newLayout
            this.oldDragItem = null
            this.oldLayout = null
        }

        this.onLayoutMaybeChanged(newLayout, oldLayout)
    }

    fun onLayoutMaybeChanged(newLayout: Layout, oldLayout_: Layout?) {
        val oldLayout = oldLayout_ ?: this.state.layout

        if (!isEqual(oldLayout, newLayout)) {
            props.onLayoutChange?.invoke(newLayout)
        }
    }

    fun onResizeStart(i: String, w: Int, h: Int, gridResizeEvent: GridResizeEvent) {
        val e = gridResizeEvent.e
        val node = gridResizeEvent.node
        val layout = this.state.layout
        val l = getLayoutItem(layout, i)
            ?: return

        this.setState {
            oldResizeItem = cloneLayoutItem(l)
            oldLayout = state.layout
        }

        props.onResizeStart(layout, l, l, null, e, node)
    }

    fun onResize(i: String, w: Int, h: Int, gridResizeEvent: GridResizeEvent) {
        val e = gridResizeEvent.e
        val node = gridResizeEvent.node
        val layout = this.state.layout
        val oldResizeItem = this.state.oldResizeItem
        val cols = props.cols!!
        val allowOverlap = props.allowOverlap!!
        val preventCollision = props.preventCollision!!

        val (newLayout, l) = withLayoutItem(layout, i) { l ->
            // Something like quad tree should be used
            // to find collisions faster
            val hasCollisions: Boolean
            if (preventCollision && !allowOverlap) {
                val collisions = getAllCollisions(layout, l.extend { this.w = w; this.h = h })
                    .filter { layoutItem -> layoutItem.i != l.i }
                hasCollisions = collisions.size > 0

                // If we're colliding, we need adjust the placeholder.
                if (hasCollisions) {
                    // adjust w && h to maximum allowed space
                    var leastX = Int.MAX_VALUE
                    var leastY = Int.MAX_VALUE
                    collisions.forEach { layoutItem ->
                        if (layoutItem.x > l.x) leastX = min(leastX, layoutItem.x)
                        if (layoutItem.y > l.y) leastY = min(leastY, layoutItem.y)
                    }

                    if (leastX != Int.MAX_VALUE) l.w = leastX - l.x
                    if (leastY != Int.MAX_VALUE) l.h = leastY - l.y
                }
            } else hasCollisions = false

            if (!hasCollisions) {
                // Set new width and height.
                l.w = w
                l.h = h
            }

            l
        }

        // Shouldn't ever happen, but typechecking makes it necessary
        if (l == null) return

        // Create placeholder element (display only)
        val placeholder = jso<PlaceholderLayoutItem> {
            this.w = l.w
            this.h = l.h
            this.x = l.x
            this.y = l.y
            this.static = true
            this.i = i
        }

        props.onResize(newLayout, oldResizeItem, l, placeholder, e, node)

        val compactType = compactType(props)

        // Re-compact the newLayout and set the drag placeholder.
        setState {
            this.layout = if (allowOverlap) newLayout else {
                compact(newLayout, compactType, cols)
            }
            this.activeDrag = placeholder
        }
    }

    fun onResizeStop(i: String, w: Int, h: Int, gridResizeEvent: GridResizeEvent) {
        val e = gridResizeEvent.e
        val node = gridResizeEvent.node
        val layout = this.state.layout
        val oldResizeItem = this.state.oldResizeItem
        val cols = props.cols!!
        val allowOverlap = props.allowOverlap!!
        val l = getLayoutItem(layout, i)!!

        props.onResizeStop(layout, oldResizeItem, l, null, e, node)

        // Set state
        val newLayout = if (allowOverlap) layout else compact(layout, compactType(props), cols)
        val oldLayout = this.state.oldLayout
        setState {
            this.activeDrag = null
            this.layout = newLayout
            this.oldResizeItem = null
            this.oldLayout = null
        }

        this.onLayoutMaybeChanged(newLayout, oldLayout)
    }

    /**
     * Create a placeholder object.
     * @return {Element} Placeholder div.
     */
    fun placeholder(): ReactElement<*>? {
        val activeDrag = this.state.activeDrag
            ?: return null

        // {...this.state.activeDrag} is pretty slow, actually
        return buildElement {
            child(GridItem::class) {
                attrs.w = activeDrag.w
                attrs.h = activeDrag.h
                attrs.x = activeDrag.x
                attrs.y = activeDrag.y
                attrs.i = activeDrag.i
                attrs.className = "react-grid-placeholder"
                attrs.containerWidth = props.width!!
                attrs.cols = props.cols!!
                attrs.margin = props.margin!!
                attrs.containerPadding = props.containerPadding ?: props.margin!!
                attrs.maxRows = props.maxRows
                attrs.rowHeight = props.rowHeight!!
                attrs.isDraggable = false
                attrs.isResizable = false
                attrs.isBounded = false
                attrs.useCSSTransforms = props.useCSSTransforms
                attrs.transformScale = props.transformScale!!

                div {}
            }
        }
    }

    /**
     * Given a grid item, set its style attributes & surround in a <Draggable>.
     * @param  {Element} child React element.
     * @return {Element}       Element wrapped in draggable and properly placed.
     */
    fun processGridItem(
        child: ReactElement<*>?,
        isDroppingItem: Boolean = false
    ): ReactElement<*>? {
        val l = child?.key?.let { getLayoutItem(this.state.layout, it) }
            ?: return null
        val width = props.width
        val cols = props.cols!!
        val margin = props.margin!!
        val containerPadding = props.containerPadding
        val rowHeight = props.rowHeight!!
        val maxRows = props.maxRows
        val isDraggable = props.isDraggable!!
        val isResizable = props.isResizable!!
        val isBounded = props.isBounded!!
        val useCSSTransforms = props.useCSSTransforms!!
        val transformScale = props.transformScale!!
        val draggableCancel = props.draggableCancel
        val draggableHandle = props.draggableHandle!!
        val resizeHandles = props.resizeHandles
        val resizeHandle = props.resizeHandle

        val mounted = this.state.mounted
        val droppingPosition = this.state.droppingPosition

        // Determine user manipulations possible.
        // If an item is static, it can't be manipulated by default.
        // Any properties defined directly on the grid item will take precedence.
        val draggable = (if (l.isDraggable != null) l.isDraggable else l.static != true && isDraggable) ?: false
        val resizable = (if (l.isResizable != null) l.isResizable else l.static != true && isResizable) ?: false
        val resizeHandlesOptions = l.resizeHandles ?: resizeHandles

        // isBounded set on child if set on parent, and child is not explicitly false
        val bounded = draggable && isBounded && l.isBounded != false

        return buildElement {
            child(GridItem::class) {
                attrs.containerWidth = width!!
                attrs.cols = cols
                attrs.margin = margin
                attrs.containerPadding = containerPadding ?: margin
                attrs.maxRows = maxRows
                attrs.rowHeight = rowHeight
                attrs.cancel = draggableCancel
                attrs.handle = draggableHandle
                attrs.onDragStop = ::onDragStop
                attrs.onDragStart = ::onDragStart
                attrs.onDrag = ::onDrag
                attrs.onResizeStart = ::onResizeStart
                attrs.onResize = ::onResize
                attrs.onResizeStop = ::onResizeStop
                attrs.isDraggable = draggable
                attrs.isResizable = resizable
                attrs.isBounded = bounded
                attrs.useCSSTransforms = useCSSTransforms && mounted
                attrs.usePercentages = !mounted
                attrs.transformScale = transformScale
                attrs.w = l.w
                attrs.h = l.h
                attrs.x = l.x
                attrs.y = l.y
                attrs.i = l.i
                attrs.minH = l.minH
                attrs.minW = l.minW
                attrs.maxH = l.maxH
                attrs.maxW = l.maxW
                attrs.static = l.static
                attrs.droppingPosition = if (isDroppingItem) droppingPosition else undefined
                attrs.resizeHandles = resizeHandlesOptions
                attrs.resizeHandle = resizeHandle

                child(child)
            }
        }
    }

    // Called while dragging an element. Part of browser native drag/drop API.
    // Native event target might be the layout itself, or an element within the layout.
    fun onDragOver(e: DragEvent<*>): Boolean {
        e.preventDefault() // Prevent any browser native action
        e.stopPropagation()

        // we should ignore events from layout's children in Firefox
        // to avoid unpredictable jumping of a dropping placeholder
        // FIXME remove this hack
        val nativeEvent = e.nativeEvent.unsafeCast<LayerEvent>()
        if (
            isFirefox &&
            // $FlowIgnore can't figure this out
            !(nativeEvent.target as HTMLElement).classList.contains(layoutClassName)
        ) {
            return false
        }

        val droppingItem = props.droppingItem
        val onDropDragOver = props.onDropDragOver
        val margin = props.margin!!
        val cols = props.cols!!
        val rowHeight = props.rowHeight!!
        val maxRows = props.maxRows
        val width = props.width
        val containerPadding = props.containerPadding!!
        val transformScale = props.transformScale!!
        // Allow user to customize the dropping item or short-circuit the drop based on the results
        // of the `onDragOver(e: Event)` callback.
        val onDragOverResult = onDropDragOver?.invoke(e.asDynamic())
        if (onDragOverResult == null) {
            if (this.state.droppingDOMNode != null) {
                this.removeDroppingPlaceholder()
            }
            return false
        }
        val finalDroppingItem: DroppingItem = Object.assign(jso(), droppingItem, onDragOverResult)

        val layout = this.state.layout
        // This is relative to the DOM element that this event fired for.
        val layerX = nativeEvent.layerX
        val layerY = nativeEvent.layerY
        val droppingPosition = jso<DroppingPosition> {
            this.left = (layerX / transformScale).roundToInt()
            this.top = (layerY / transformScale).roundToInt()
            this.e = e.asDynamic()
        }

        if (this.state.droppingDOMNode == null) {
            val positionParams = jso<PositionParams> {
                this.cols = cols
                this.margin = margin
                this.maxRows = maxRows
                this.rowHeight = rowHeight
                this.containerWidth = width!!.roundToInt()
                this.containerPadding = containerPadding
            }

            val calculatedPosition = calcXY(
                positionParams,
                layerY.roundToInt(),
                layerX.roundToInt(),
                finalDroppingItem.w,
                finalDroppingItem.h
            )

            setState {
                this.droppingDOMNode = buildElement {
                    div { key = finalDroppingItem.i }
                }
                this.droppingPosition = droppingPosition
                this.layout = layout +
                        listOf(
                            Object.assign(jso(), finalDroppingItem, jso<LayoutItem>() {
                                this.x = calculatedPosition.x
                                this.y = calculatedPosition.y
                                this.static = false
                                this.isDraggable = true
                            })
                        )
            }
        } else state.droppingPosition?.let { droppingPos ->
            val left = droppingPos.left
            val top = droppingPos.top
            val shouldUpdatePosition = left != layerX.roundToInt() || top != layerY.roundToInt()
            if (shouldUpdatePosition) {
                this.setState {
                    this.droppingPosition = droppingPos
                }
            }
        }
        return false
    }

    fun removeDroppingPlaceholder() {
        val droppingItem = props.droppingItem
        val cols = props.cols!!
        val layout = this.state.layout

        val newLayout = compact(
            layout.filter { l -> l.i != droppingItem?.i },
            compactType(props),
            cols
        )

        setState {
            this.layout = newLayout
            this.droppingDOMNode = null
            this.activeDrag = null
            this.droppingPosition = undefined
        }
    }

    fun onDragLeave(e: DragEvent<*>) {
        e.preventDefault() // Prevent any browser native action
        e.stopPropagation()
        this.dragEnterCounter--

        // onDragLeave can be triggered on each layout's child.
        // But we know that count of dragEnter and dragLeave events
        // will be balanced after leaving the layout's container
        // so we can increase and decrease count of dragEnter and
        // when it'll be equal to 0 we'll remove the placeholder
        if (this.dragEnterCounter == 0) {
            this.removeDroppingPlaceholder()
        }
    }

    fun onDragEnter(e: DragEvent<*>) {
        e.preventDefault() // Prevent any browser native action
        e.stopPropagation()
        this.dragEnterCounter++
    }

    fun onDrop(e: DragEvent<*>) {
        e.preventDefault() // Prevent any browser native action
        e.stopPropagation()
        val droppingItem = props.droppingItem!!
        val layout = this.state.layout
        val item = layout.find { l -> l.i === droppingItem.i }

        // reset dragEnter counter on drop
        this.dragEnterCounter = 0

        this.removeDroppingPlaceholder()

        props.onDrop!!.invoke(layout, item, e as Event)
    }

    override fun RBuilder.render() {
        val className = props.className
//        val style = propsD.style
        val isDroppable = props.isDroppable!!
        val innerRef = props.innerRef

        val mergedClassName = //clsx(layoutClassName, className)
            "$layoutClassName $className"

        div(mergedClassName) {
            ref = innerRef
            inlineStyles {
                containerHeight()?.let { height = LinearDimension(it) }
                // TODO: merge in propsD.style here
            }
            attrs.onDrop = if (isDroppable) ::onDrop else noop
            attrs.onDragLeave = if (isDroppable) ::onDragLeave else noop
            attrs.onDragEnter = if (isDroppable) ::onDragEnter else noop
            attrs.onDragOver = if (isDroppable) ::onDragOver else noop

            (props.children.unsafeCast<Array<ReactElement<*>>>()).forEach { child ->
                processGridItem(child)?.let { child(it) }
            }
            val droppingDOMNode = state.droppingDOMNode
            if (isDroppable &&
                droppingDOMNode != null
            ) {
                processGridItem(droppingDOMNode, true)
                    ?.let { child(it) }
            }
            placeholder()?.let { child(it) }
        }
    }

    companion object : RStatics<GridLayoutProps, GridLayoutState, GridLayout, Nothing>(GridLayout::class) {
        init {
            displayName = GridLayout::class.simpleName

            defaultProps = jso {
                autoSize = true
                cols = 12
                className = ""
                style = {}
                draggableHandle = ""
                draggableCancel = ""
                containerPadding = null
                rowHeight = 150.0
                maxRows = Int.MAX_VALUE // infinite vertical growth
                layout = emptyList()
                margin = arrayOf(10, 10)
                isBounded = false
                isDraggable = true
                isResizable = true
                allowOverlap = false
                isDroppable = false
                useCSSTransforms = true
                transformScale = 1.0
//        verticalCompact = true
                compactType = CompactType.vertical
                preventCollision = false
                droppingItem = jso {
                    i = "__dropping-elem__"
                    h = 1
                    w = 1
                }
                resizeHandles = listOf("se")
                onLayoutChange = {}.asDynamic()
                onDragStart = {}.asDynamic()
                onDrag = {}.asDynamic()
                onDragStop = {}.asDynamic()
                onResizeStart = {}.asDynamic()
                onResize = {}.asDynamic()
                onResizeStop = {}.asDynamic()
                onDrop = {}.asDynamic()
                onDropDragOver = {}.asDynamic()
            }

            getDerivedStateFromProps = { nextProps, prevState ->
                var newLayoutBase: MutableList<LayoutItem>? = null

                if (prevState.activeDrag == null) {
                    // Legacy support for compactType
                    // Allow parent to set layout directly.
                    if (
                        !isEqual(nextProps.layout, prevState.propsLayout) ||
                        nextProps.compactType !== prevState.compactType
                    ) {
                        newLayoutBase = nextProps.layout?.toMutableList()
                    } else if (!childrenEqual(
                            nextProps.children.unsafeCast<ReactChildren>(),
                            prevState.children.unsafeCast<ReactChildren>()
                        )
                    ) {
                        // If children change, also regenerate the layout. Use our state
                        // as the base in case because it may be more up to date than
                        // what is in props.
                        newLayoutBase = prevState.layout.toMutableList()
                    }

                    // We need to regenerate the layout.
                    if (newLayoutBase != null) {
                        val newLayout = synchronizeLayoutWithChildren(
                            newLayoutBase,
                            nextProps.children.unsafeCast<ReactChildren>(),
                            nextProps.cols!!,
                            compactType(nextProps),
                            nextProps.allowOverlap
                        )

                        jso {
                            this.layout = newLayout
                            // We need to save these props to state for using
                            // getDerivedStateFromProps instead of componentDidMount (in which we would get extra rerender)
                            this.compactType = nextProps.compactType
                            this.children = nextProps.children
                            this.propsLayout = nextProps.layout
                        }
                    } else null
                } else null
            }
        }
    }
}