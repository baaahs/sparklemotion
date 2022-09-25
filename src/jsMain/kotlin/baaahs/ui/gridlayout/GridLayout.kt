package baaahs.ui.gridlayout

import baaahs.app.ui.layout.DragNDropContext
import baaahs.app.ui.layout.GridLayoutContext
import baaahs.app.ui.layout.dragNDropContext
import baaahs.geom.Vector2D
import baaahs.replace
import baaahs.ui.gridlayout.CompactType.Companion.determineFrom
import baaahs.window
import baaahs.y
import external.lodash.isEqual
import kotlinx.css.*
import kotlinx.css.Position
import kotlinx.js.jso
import org.w3c.dom.HTMLElement
import org.w3c.dom.events.Event
import react.*
import react.dom.*
import react.dom.events.DragEvent
import react.dom.events.DragEventHandler
import styled.inlineStyles
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.reflect.KClass

external interface GridLayoutState : State {
    var id: String
    var gridLayout: GridLayout

    var draggingPlaceholder: LayoutItem?
    var layout: Layout
    var mounted: Boolean
    var oldDragItem: LayoutItem?
    var originalDragItem: LayoutItem?
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

class GridLayout(
    props: GridLayoutProps, context: DragNDropContext
) : RComponent<GridLayoutProps, GridLayoutState>(props) {
    override fun GridLayoutState.init(props: GridLayoutProps) {
        id = props.id
        gridLayout = this@GridLayout

        draggingPlaceholder = null
        layout = synchronizeLayoutWithChildren(
            props.layout ?: Layout(emptyList(), props.cols!!, props.maxRows),
            props.children?.asArray() ?: emptyArray(),
            props.cols!!,
            props.maxRows,
            // Legacy support for verticalCompact: false
            CompactType.None,
            props.allowOverlap
        )
        mounted = false
        oldDragItem = null
        oldLayout = null
        oldResizeItem = null
        droppingDOMNode = null
        children = null
    }

    private val context: GridLayoutContext = context.gridLayoutContext
    private val rootElementRef = createRef<HTMLElement>()
    private val rootElement get() = rootElementRef.current
    val isBounded get() = props.isBounded == true
    private val debugDiv = createRef<HTMLElement>()

    var dragEnterCounter = 0

    override fun componentDidMount() {
        state.mounted = true

        onLayoutMaybeChanged(state.layout, props.layout, state.draggingPlaceholder != null)

        context.registerLayout(props.id, state)
    }

    override fun componentWillUnmount() {
        context.unregisterLayout(props.id)
    }

    override fun shouldComponentUpdate(nextProps: GridLayoutProps, nextState: GridLayoutState): Boolean {
        return (
                // NOTE: this is almost always unequal. Therefore the only way to get better performance
                // from SCU is if the user intentionally memoizes children. If they do, and they can
                // handle changes properly, performance will increase.
                props.children !== nextProps.children ||
                        !isEqual(props, nextProps) ||
//                        !fastRGLPropsEqual(propsD, nextProps, ::isEqual) ||
                        state.draggingPlaceholder != nextState.draggingPlaceholder ||
                        state.mounted != nextState.mounted ||
                        state.droppingPosition != nextState.droppingPosition
                )
    }

    override fun componentDidUpdate(prevProps: GridLayoutProps, prevState: GridLayoutState, snapshot: Any) {
        if (state.draggingPlaceholder != null) {
            val newLayout = state.layout
            val oldLayout = prevState.layout

            onLayoutMaybeChanged(newLayout, oldLayout, true)
        }
    }

    /**
     * Calculates a pixel value for the container.
     * @return {String} Container height in pixels.
     */
    fun containerHeight(): String? {
        if (!props.autoSize!!) return null
        val nbRow = state.layout.bottom()
        val padding = props.containerPadding ?: props.margin!!
        val pixels = nbRow * props.rowHeight!! +
                (nbRow - 1) * props.margin!!.second +
                padding.y * 2
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
        val layout = state.layout
        val l = layout.find(i)
            ?: return run {
                console.log("GridLayout(${props.id}),onDragStart() couldn't find layout $i")
            }

        setState {
            this.oldDragItem = l
            this.oldLayout = layout
            this.originalDragItem = l
        }

        return props.onDragStart(layout, l, l, null, e, node)
    }

    fun onItemEnter(layoutItem: LayoutItem/*, i: String, x: Int, y: Int*/) {
        val enteringItem = layoutItem
        val newLayout = Layout(state.layout.items + enteringItem, state.layout.cols, state.layout.rows)
        setState {
            this.oldDragItem = enteringItem
            this.draggingPlaceholder = placeholderLayoutItem(layoutItem)
            this.oldLayout = newLayout
            this.layout = newLayout
        }
        console.log("GridLayout ${props.id}: ${layoutItem.i} entering", layoutItem)
    }

    fun onItemExit(): LayoutItem? {
        console.log("GridLayout ${props.id}: ${state.draggingPlaceholder?.i} exiting", state.draggingPlaceholder)
        val draggingItem = state.oldDragItem
        return draggingItem
            ?.also {
                setState {
//                    state.oldDragItem?.let { oldDragItem ->
//                        layout = state.layout.filter { it.i != oldDragItem.i }
//                    }
                    this.oldDragItem = null
                    this.layout = state.oldLayout!!.removeItem(draggingItem.i)
                    this.draggingPlaceholder = null
                }
            }
    }

    /**
     * Each drag movement create a new dragelement and move the element to the dragged location
     * @param {String} i Id of the child
     * @param {Number} x X position of the move
     * @param {Number} y Y position of the move
     * @param {Event} e The mousedown event
     * @param {Element} node The current dragging DOM element
     */
    fun onDragItem(i: String, x: Int, y: Int, gridDragEvent: GridDragEvent) {
        val e = gridDragEvent.e
        val node = gridDragEvent.node
        val oldDragItem = state.oldDragItem
        val allowOverlap = props.allowOverlap == true
        val preventCollision = props.preventCollision!!
        val oldLayout = state.oldLayout!!
        val l = oldLayout.find(i)
            ?: return run {
                console.log("GridLayout(${props.id}),onDragItem() couldn't find item $i")
            }

        val compactType = l.determineFrom(x, y)

        // Move the element to the dragged location.
        val newLayout = oldLayout.moveElement(
            l, x, y, preventCollision, compactType, allowOverlap
        )
        val newItem = newLayout.find(i) ?: return

        // Create placeholder (display only)
        val placeholder = placeholderLayoutItem(newItem)

        if (debug) {
            debugDiv.current?.innerText = """
            onDragItem($i, $x, $y)
              l(${l.x}, ${l.y})
              newItem(${newItem.x}, ${newItem.y})
              placeholder(${placeholder.x}, ${placeholder.y})
        """.trimIndent()
        }

        props.onDrag(newLayout, oldDragItem, newItem, placeholder, e, node)

        setState {
            this.layout = if (allowOverlap) newLayout else newLayout.compact(CompactType.None)
            this.draggingPlaceholder = placeholder
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
        val oldLayout = state.oldLayout
            ?: return run {
                console.log("GridLayout.onDragStop", props.id, i, "no oldLayout?")
            }

        console.log("GridLayout", props.id, "onDragStop", i, state.draggingPlaceholder)
        if (state.draggingPlaceholder == null) return
        val oldDragItem = state.oldDragItem
        val allowOverlap = props.allowOverlap == true
        val preventCollision = props.preventCollision!!
        val l = oldLayout.find(i)
            ?: return run {
                console.log("GridLayout(${props.id}),onDragStop() couldn't find layout $i")
            }

        val compactType = l.determineFrom(x, y)

        // Move the element here
        var newLayout = oldLayout.moveElement(
            l, x, y, preventCollision, compactType, allowOverlap
        )
        val newItem = newLayout.find(i) ?: return

        props.onDragStop(newLayout, oldDragItem, newItem, null, e, node)
        newLayout = if (allowOverlap) newLayout else newLayout.compact(CompactType.None)

        setState {
            this.draggingPlaceholder = null
            this.layout = newLayout
            this.oldDragItem = null
            this.oldLayout = null
            this.originalDragItem = null
        }

        onLayoutMaybeChanged(newLayout, oldLayout, false)
    }

    private fun onLayoutMaybeChanged(newLayout: Layout, oldLayout_: Layout?, stillDragging: Boolean) {
        val canonicalNewLayout = newLayout.canonicalize()
        val oldLayout = (oldLayout_ ?: state.layout).canonicalize()

        if (canonicalNewLayout != oldLayout) {
            // Don't use the canonicalized version, order instability breaks updating the data model.
            props.onLayoutChange?.invoke(newLayout, stillDragging)
        }
    }

    private fun onResizeStart(i: String, w: Int, h: Int, gridResizeEvent: GridResizeEvent) {
        val e = gridResizeEvent.e
        val node = gridResizeEvent.node
        val layout = state.layout
        val l = layout.find(i)
            ?: return run {
                console.log("GridLayout(${props.id}),onResizeStart() couldn't find layout $i")
            }

        setState {
            this.oldResizeItem = l.copy()
            this.oldLayout = state.layout
        }

        props.onResizeStart(layout, l, l, null, e, node)
    }

    private fun onResize(i: String, w: Int, h: Int, gridResizeEvent: GridResizeEvent) {
        val e = gridResizeEvent.e
        val node = gridResizeEvent.node
        val layout = state.layout
        val oldResizeItem = state.oldResizeItem
        val allowOverlap = props.allowOverlap!!
        val preventCollision = props.preventCollision!!

        val newListItems = ArrayList(layout.items)
        newListItems.replace({ it.i == i }) { l ->
            var newItem = l
            // Something like quad tree should be used
            // to find collisions faster
            val hasCollisions: Boolean
            if (preventCollision && !allowOverlap) {
                val collisions = layout.getAllCollisions(newItem.copy(w = w, h = h))
                    .filter { layoutItem -> layoutItem.i != newItem.i }
                hasCollisions = collisions.isNotEmpty()

                // If we're colliding, we need adjust the placeholder.
                if (hasCollisions) {
                    // adjust w && h to maximum allowed space
                    var leastX = Int.MAX_VALUE
                    var leastY = Int.MAX_VALUE
                    collisions.forEach { layoutItem ->
                        if (layoutItem.x > newItem.x) leastX = min(leastX, layoutItem.x)
                        if (layoutItem.y > newItem.y) leastY = min(leastY, layoutItem.y)
                    }

                    if (leastX != Int.MAX_VALUE) newItem = newItem.copy(w = leastX - newItem.x)
                    if (leastY != Int.MAX_VALUE) newItem = newItem.copy(h = leastY - newItem.y)
                }
            } else hasCollisions = false

            if (!hasCollisions) {
                // Set new width and height.
                newItem = newItem.copy(w = w, h = h)
            }

            newItem
        }

        val newLayout = Layout(newListItems, layout.cols, layout.rows)

        val l = newLayout.find(i)
            ?: return

        // Create placeholder element (display only)
        val placeholder = l.toStatic()

        props.onResize(newLayout, oldResizeItem, l, placeholder, e, node)

        val compactType = CompactType.None

        // Re-compact the newLayout and set the drag placeholder.
        setState {
            this.layout = if (allowOverlap) newLayout else newLayout.compact(CompactType.None)
            this.draggingPlaceholder = placeholder
        }
    }

    private fun placeholderLayoutItem(l: LayoutItem) =
        LayoutItem(l.x, l.y, l.w, l.h, l.i, isPlaceholder = true)

    private fun onResizeStop(i: String, w: Int, h: Int, gridResizeEvent: GridResizeEvent) {
        val e = gridResizeEvent.e
        val node = gridResizeEvent.node
        val layout = state.layout
        val oldResizeItem = state.oldResizeItem
        val allowOverlap = props.allowOverlap!!
        val l = layout.find(i)!!

        props.onResizeStop(layout, oldResizeItem, l, null, e, node)

        // Set state
        val newLayout = if (allowOverlap) layout else layout.compact(CompactType.None)
        val oldLayout = state.oldLayout
        setState {
            this.draggingPlaceholder = null
            this.layout = newLayout
            this.oldResizeItem = null
            this.oldLayout = null
        }

        onLayoutMaybeChanged(newLayout, oldLayout, false)
    }

    val containerWidth get() = props.width!!
    val cols get() = props.cols!!
    val margin get() = props.margin!!
    val containerPadding get() = props.containerPadding ?: props.margin!!
    private val maxRows get() = props.maxRows
    val rowHeight get() = props.rowHeight!!

    /**
     * Create a placeholder object.
     * @return {Element} Placeholder div.
     */
    fun placeholder(): ReactElement<*>? {
        val activeDrag = state.draggingPlaceholder
            ?: return null

        // {...this.state.activeDrag} is pretty slow, actually
        return buildElement {
            gridItem {
                attrs.parentContainer = this@GridLayout
                attrs.w = activeDrag.w
                attrs.h = activeDrag.h
                attrs.x = activeDrag.x
                attrs.y = activeDrag.y
                attrs.i = activeDrag.i
                attrs.className = "react-grid-placeholder"
//                attrs.containerWidth = props.width!!
//                attrs.cols = props.cols!!
//                attrs.margin = props.margin!!
//                attrs.containerPadding = props.containerPadding ?: props.margin!!
//                attrs.maxRows = props.maxRows
//                attrs.rowHeight = props.rowHeight!!
                attrs.isDraggable = false
                attrs.isResizable = false
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
        val l = child?.key?.let { itemId ->
            state.layout.find(itemId)
                ?: state.originalDragItem?.let { if (it.i == itemId) it else null }
        }
            ?: return run {
                console.log("GridLayout(${props.id}).processGridItem() couldn't find layout ${child?.key}")
                null
            }
        val disableDrag = props.disableDrag!!
        val disableResize = props.disableResize!!
        val useCSSTransforms = props.useCSSTransforms!!
        val transformScale = props.transformScale!!
        val draggableCancel = props.draggableCancel
        val draggableHandle = props.draggableHandle!!
        val resizeHandle = props.resizeHandle

        val mounted = state.mounted
        val droppingPosition = state.droppingPosition

        // Determine user manipulations possible.
        val draggable = !disableDrag && l.isDraggable && !l.isStatic
        val resizable = !disableResize && l.isResizable && !l.isStatic

        return buildElement {
            gridItem {
                attrs.parentContainer = this@GridLayout
//                attrs.containerWidth = width!!
//                attrs.cols = cols
//                attrs.margin = margin
//                attrs.containerPadding = containerPadding ?: margin
//                attrs.maxRows = maxRows
//                attrs.rowHeight = rowHeight
                attrs.cancel = draggableCancel
                attrs.handle = draggableHandle
//                attrs.onDragStop = ::onDragStop
//                attrs.onDragStart = ::onDragStart
//                attrs.onDrag = ::onDrag
                attrs.onResizeStart = ::onResizeStart
                attrs.onResize = ::onResize
                attrs.onResizeStop = ::onResizeStop
                attrs.isDraggable = draggable
                attrs.isResizable = resizable
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
                attrs.static = l.isStatic
                attrs.droppingPosition = if (isDroppingItem) droppingPosition else undefined
                attrs.resizeHandle = resizeHandle

                child(child)
            }
        }
    }

    // Called while dragging an element. Part of browser native drag/drop API.
    // Native event target might be the layout itself, or an element within the layout.
    fun onDragOver(e: DragEvent<*>): Boolean {
        console.log("GridLayout", props.id, "onDragOver")
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

        val transformScale = props.transformScale!!
        // Allow user to customize the dropping item or short-circuit the drop based on the results
        // of the `onDragOver(e: Event)` callback.
        val onDragOverResult = props.onDropDragOver?.invoke(e.asDynamic())
        if (onDragOverResult == null) {
            if (state.droppingDOMNode != null) {
                removeDroppingPlaceholder()
            }
            return false
        }
        val finalDroppingItem: DroppingItem = props.droppingItem!!.copy(
            i = onDragOverResult.i,
            item = onDragOverResult.item,
            w = onDragOverResult.w,
            h = onDragOverResult.h
        )

        val layout = state.layout
        // This is relative to the DOM element that this event fired for.
        val layerX = nativeEvent.layerX
        val layerY = nativeEvent.layerY
        val droppingPosition = DroppingPosition(
            (layerX / transformScale).roundToInt(),
            (layerY / transformScale).roundToInt(),
            e.asDynamic()
        )

        if (state.droppingDOMNode == null) {
            val positionParams = getPositionParams()

            val calculatedGridPosition = positionParams.calcGridPosition(
                layerX.roundToInt(), layerY.roundToInt(),
                finalDroppingItem.w, finalDroppingItem.h
            )

            setState {
                this.droppingDOMNode = buildElement {
                    div { key = finalDroppingItem.i }
                }
                this.droppingPosition = droppingPosition
                val newItem = LayoutItem(
                    calculatedGridPosition.x, calculatedGridPosition.y,
                    1, 1, // TODO: calculate w/h
                    finalDroppingItem.i,
                    isDraggable = true
                )
                this.layout = Layout(layout.items + listOf(newItem), layout.cols, layout.rows)
            }
        } else state.droppingPosition?.let { droppingPos ->
            val left = droppingPos.left
            val top = droppingPos.top
            val shouldUpdatePosition = left != layerX.roundToInt() || top != layerY.roundToInt()
            if (shouldUpdatePosition) {
                setState {
                    this.droppingPosition = droppingPos
                }
            }
        }
        return false
    }

    fun getPositionParams(): PositionParams =
        PositionParams(margin, containerPadding, containerWidth.roundToInt(), cols, rowHeight, maxRows)

    fun calculatePxPositionInLayout(node: HTMLElement): Vector2D {
        val nodePos = node.getPosition()
        val parent = rootElement ?: error("No root element for ${props.id}.")
        val parentPos = parent.getPositionMinusScroll()
        return (nodePos - parentPos) / props.transformScale!!
    }

    fun removeDroppingPlaceholder() {
        val droppingItem = props.droppingItem
        val layout = state.layout

        val newLayout = Layout(layout.items.filter { l -> l.i != droppingItem?.i }, layout.cols, layout.rows)
            .compact(CompactType.None)

        setState {
            this.layout = newLayout
            this.droppingDOMNode = null
            this.draggingPlaceholder = null
            this.droppingPosition = undefined
        }
    }

    fun onDragLeave(e: DragEvent<*>) {
        console.log("GridLayout", props.id, "onDragLeave")
        e.preventDefault() // Prevent any browser native action
        e.stopPropagation()
        dragEnterCounter--

        // onDragLeave can be triggered on each layout's child.
        // But we know that count of dragEnter and dragLeave events
        // will be balanced after leaving the layout's container
        // so we can increase and decrease count of dragEnter and
        // when it'll be equal to 0 we'll remove the placeholder
        if (dragEnterCounter == 0) {
            removeDroppingPlaceholder()
        }
    }

    fun onDragEnter(e: DragEvent<*>) {
        console.log("GridLayout", props.id, "onDragEnter")
        e.preventDefault() // Prevent any browser native action
        e.stopPropagation()
        dragEnterCounter++
    }

    fun onDrop(e: DragEvent<*>) {
        console.log("GridLayout", props.id, "onDrop")
        e.preventDefault() // Prevent any browser native action
        e.stopPropagation()
        val droppingItem = props.droppingItem!!
        val layout = state.layout
        val item = layout.find(droppingItem.i)

        // reset dragEnter counter on drop
        dragEnterCounter = 0

        removeDroppingPlaceholder()

        props.onDrop!!.invoke(layout, item, e as Event)
    }

    override fun RBuilder.render() {
        val className = props.className
//        val style = propsD.style
        val isDroppable = props.isDroppable!!
        console.log("GridLayout", props.id, "isDroppable", isDroppable)
//        val innerRef = props.innerRef

        val mergedClassName = //clsx(layoutClassName, className)
            "$layoutClassName $className"

        div(mergedClassName) {
            ref = rootElementRef
            attrs["data-grid-layout-container"] = props.id
            inlineStyles {
                containerHeight()?.let { height = LinearDimension(it) }
                // TODO: merge in propsD.style here
            }
            attrs.onDrop = if (isDroppable) ::onDrop else noop
            attrs.onDragLeave = if (isDroppable) ::onDragLeave else noop
            attrs.onDragEnter = if (isDroppable) ::onDragEnter else noop
            attrs.onDragOver = if (isDroppable) ::onDragOver else noop

            props.children?.asArray()?.forEach { child ->
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

        if (debug) {
            div {
                ref = debugDiv
                inlineStyles {
                    position = Position.absolute
                    bottom = 0.px
                    right = 0.px
                    whiteSpace = WhiteSpace.pre
                }
            }
        }
    }

    companion object : RStatics<GridLayoutProps, GridLayoutState, GridLayout, Context<DragNDropContext>>(GridLayout::class) {
        const val debug = true

        init {
            displayName = GridLayout::class.simpleName

            contextType = dragNDropContext

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
                layout = Layout()
                margin = 10 to 10
                isBounded = false
                disableDrag = false
                disableResize = false
                allowOverlap = false
                isDroppable = false
                useCSSTransforms = true
                transformScale = 1.0
//        verticalCompact = true
                compactType = CompactType.Vertical
                preventCollision = false
                droppingItem = DroppingItem(
                    "__dropping-elem__",
                    Unit,
                    h = 1,
                    w = 1
                )
                onLayoutChange = {}.asDynamic()
                onDragStart = {}.asDynamic()
                onDrag = {}.asDynamic()
                onDragStop = {}.asDynamic()
                onResizeStart = {}.asDynamic()
                onResize = {}.asDynamic()
                onResizeStop = {}.asDynamic()
                onDrop = {}.asDynamic()
                onDropDragOver = {}.asDynamic()

                // Workaround for apparent React bug?
                this.asDynamic()["defaultsApplied"] = true
            }

            getDerivedStateFromProps = { nextProps, prevState ->
                if (prevState.draggingPlaceholder == null) {
                    var baseLayout: Layout? = null

                    // Legacy support for compactType
                    // Allow parent to set layout directly.
                    if (
                        nextProps.layout != prevState.propsLayout ||
                        nextProps.compactType != prevState.compactType
                    ) {
                        baseLayout = nextProps.layout
                    } else if (!childrenEqual(
                            nextProps.children?.asArray(),
                            prevState.children?.asArray()
                        )
                    ) {
                        // If children change, also regenerate the layout. Use our state
                        // as the base in case because it may be more up to date than
                        // what is in props.
                        baseLayout = prevState.layout
                    }

                    // We need to regenerate the layout.
                    if (baseLayout != null) {
                        val newLayout = synchronizeLayoutWithChildren(
                            baseLayout,
                            nextProps.children?.asArray() ?: emptyArray(),
                            nextProps.cols!!,
                            nextProps.maxRows,
                            CompactType.None,
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

        fun synchronizeLayoutWithChildren(
            initialLayout: Layout,
            children: ReactChildren,
            cols: Int,
            rows: Int,
            compactType: CompactType,
            allowOverlap: Boolean?
        ): Layout {
            // Generate one layout item per child.
            val layoutItems = mutableListOf<LayoutItem>()

            children.forEach { child: ReactElement<*> ->
                // Child may not exist
                val key = child.key
                    ?: return@forEach

                // Don't overwrite if it already exists.
                val exists = initialLayout.find(key)
                if (exists != null) {
                    layoutItems.add(exists.copy())
                } else {
                    // Nothing provided: ensure this is added to the bottom
                    // FIXME clone not really necessary here
                    layoutItems.add(
                        LayoutItem(
                            0, rows - 1,
                            1, 1,
                            key
                        )
                    )
                }
            }

            // Correct the layout.
            val correctedLayout = Layout(layoutItems, cols, rows).correctBounds()
            return if (allowOverlap == true)
                correctedLayout
            else
                correctedLayout.compact(CompactType.None)
        }

        private val noop: DragEventHandler<*> = { }

        fun ReactNode.asArray(): ReactChildren =
            if (this is Array<*>) {
                unsafeCast<ReactChildren>()
            } else if (isValidElement(this)) {
                arrayOf(this.unsafeCast<ReactElement<*>>())
            } else emptyArray()

        /**
         * Comparing React `children` is a bit difficult. This is a good way to compare them.
         * This will catch differences in keys, order, and length.
         */
        fun childrenEqual(a: ReactChildren?, b: ReactChildren?): Boolean {
            return isEqual(
                a?.map { c -> c.key },
                b?.map { c -> c.key }
            )
        }
    }
}

fun RBuilder.gridLayout(handler: RHandler<GridLayoutProps>) =
    child(GridLayout::class.unsafeCast<KClass<GridLayout>>(), handler)

// Workaround for bug in kotlin-react RStatics to force it to be initialized early.
@Suppress("unused")
private val defaultPropsWorkaround = GridLayout.defaultProps
