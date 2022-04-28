package baaahs.ui.gridlayout

import baaahs.app.ui.layout.DragNDropContext
import baaahs.app.ui.layout.GridLayoutContext
import baaahs.app.ui.layout.dragNDropContext
import baaahs.window
import baaahs.y
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
import react.dom.events.DragEventHandler
import styled.inlineStyles
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.reflect.KClass

external interface GridLayoutState : State {
    var id: String
    var gridLayout: GridLayout

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

class GridLayout(
    props: GridLayoutProps, context: DragNDropContext
) : RComponent<GridLayoutProps, GridLayoutState>(props) {
    override fun GridLayoutState.init(props: GridLayoutProps) {
        id = props.id
        gridLayout = this@GridLayout

        activeDrag = null
        layout = synchronizeLayoutWithChildren(
            props.layout,
            props.children?.asArray() ?: emptyArray(),
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

    private val context: GridLayoutContext = context.gridLayoutContext
    private val rootElementRef = createRef<HTMLElement>()
    val rootElement get() = rootElementRef.current
    val isBounded get() = props.isBounded == true

    var dragEnterCounter = 0

    override fun componentDidMount() {
        state.mounted = true

        onLayoutMaybeChanged(state.layout, props.layout)

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
                        state.activeDrag != nextState.activeDrag ||
                        state.mounted != nextState.mounted ||
                        state.droppingPosition != nextState.droppingPosition
                )
    }

    override fun componentDidUpdate(prevProps: GridLayoutProps, prevState: GridLayoutState, snapshot: Any) {
        if (state.activeDrag != null) {
            val newLayout = state.layout
            val oldLayout = prevState.layout

            onLayoutMaybeChanged(newLayout, oldLayout)
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
            oldDragItem = l.copy()
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
    fun onDragItem(i: String, x: Int, y: Int, gridDragEvent: GridDragEvent) {
        val e = gridDragEvent.e
        val node = gridDragEvent.node
        val oldDragItem = state.oldDragItem
        var layout = state.layout
        val cols = props.cols
        val allowOverlap = props.allowOverlap == true
        val preventCollision = props.preventCollision
        val l = layout.find(i)
            ?: state.oldDragItem?.let { if (it.i == i) it else null }
            ?: return run {
                console.log("GridLayout(${props.id}),onDragItem() couldn't find item $i")
            }

        // Move the element to the dragged location.
        val isUserAction = true
        layout = layout.moveElement(
            l,
            x,
            y,
            isUserAction,
            preventCollision,
            compactType(props),
            cols!!,
            allowOverlap
        )

        // Create placeholder (display only)
        val placeholder = placeholderLayoutItem(l)

        props.onDrag(layout, oldDragItem, l, placeholder, e, node)

        setState {
            layout = if (allowOverlap) layout else layout.compact(compactType(props), cols)
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

        if (state.activeDrag == null) return

        val oldDragItem = state.oldDragItem
        var layout = state.layout
        val cols = props.cols
        val allowOverlap = props.allowOverlap == true
        val preventCollision = props.preventCollision
        val l = layout.find(i)
            ?: state.oldDragItem?.let { if (it.i == i) it else null }
            ?: return run {
                console.log("GridLayout(${props.id}),onDragStop() couldn't find layout $i")
            }

        // Move the element here
        val isUserAction = true
        layout = layout.moveElement(
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
        val newLayout = if (allowOverlap) layout else layout.compact(compactType(props), cols)
        val oldLayout = state.oldLayout
        setState {
            activeDrag = null
            layout = newLayout
            this.oldDragItem = null
            this.oldLayout = null
        }

        onLayoutMaybeChanged(newLayout, oldLayout)
    }

    fun onLayoutMaybeChanged(newLayout: Layout, oldLayout_: Layout?) {
        val oldLayout = oldLayout_ ?: state.layout

        if (!isEqual(oldLayout, newLayout)) {
            props.onLayoutChange?.invoke(newLayout)
        }
    }

    fun onResizeStart(i: String, w: Int, h: Int, gridResizeEvent: GridResizeEvent) {
        val e = gridResizeEvent.e
        val node = gridResizeEvent.node
        val layout = state.layout
        val l = layout.find(i)
            ?: return run {
                console.log("GridLayout(${props.id}),onResizeStart() couldn't find layout $i")
            }

        setState {
            oldResizeItem = l.copy()
            oldLayout = state.layout
        }

        props.onResizeStart(layout, l, l, null, e, node)
    }

    fun onResize(i: String, w: Int, h: Int, gridResizeEvent: GridResizeEvent) {
        val e = gridResizeEvent.e
        val node = gridResizeEvent.node
        val layout = state.layout
        val oldResizeItem = state.oldResizeItem
        val cols = props.cols!!
        val allowOverlap = props.allowOverlap!!
        val preventCollision = props.preventCollision!!

        val (newLayout, l) = layout.withLayoutItem(i) { l ->
            // Something like quad tree should be used
            // to find collisions faster
            val hasCollisions: Boolean
            if (preventCollision && !allowOverlap) {
                val collisions = layout.getAllCollisions(l.copy(w = w, h = h))
                    .filter { layoutItem -> layoutItem.i != l.i }
                hasCollisions = collisions.isNotEmpty()

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
        val placeholder = l.toStatic()

        props.onResize(newLayout, oldResizeItem, l, placeholder, e, node)

        val compactType = compactType(props)

        // Re-compact the newLayout and set the drag placeholder.
        setState {
            this.layout = if (allowOverlap) newLayout else {
                newLayout.compact(compactType, cols)
            }
            this.activeDrag = placeholder
        }
    }

    private fun placeholderLayoutItem(l: LayoutItem) =
        LayoutItem(l.x, l.y, l.w, l.h, l.i, isPlaceholder = true)

    fun onResizeStop(i: String, w: Int, h: Int, gridResizeEvent: GridResizeEvent) {
        val e = gridResizeEvent.e
        val node = gridResizeEvent.node
        val layout = state.layout
        val oldResizeItem = state.oldResizeItem
        val cols = props.cols!!
        val allowOverlap = props.allowOverlap!!
        val l = layout.find(i)!!

        props.onResizeStop(layout, oldResizeItem, l, null, e, node)

        // Set state
        val newLayout = if (allowOverlap) layout else layout.compact(compactType(props), cols)
        val oldLayout = state.oldLayout
        setState {
            this.activeDrag = null
            this.layout = newLayout
            this.oldResizeItem = null
            this.oldLayout = null
        }

        onLayoutMaybeChanged(newLayout, oldLayout)
    }

    val containerWidth get() = props.width!!
    val cols get() = props.cols!!
    val margin get() = props.margin!!
    val containerPadding get() = props.containerPadding ?: props.margin!!
    val maxRows get() = props.maxRows
    val rowHeight get() = props.rowHeight!!

    /**
     * Create a placeholder object.
     * @return {Element} Placeholder div.
     */
    fun placeholder(): ReactElement<*>? {
        val activeDrag = state.activeDrag
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
        val l = child?.key?.let { state.layout.find(it) }
            ?: return run {
                console.log("GridLayout(${props.id}),processGridItem() couldn't find layout ${child?.key}")
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
                layerY.roundToInt(), layerX.roundToInt(),
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
                this.layout = Layout(layout.items + listOf(newItem))
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

    fun removeDroppingPlaceholder() {
        val droppingItem = props.droppingItem
        val cols = props.cols!!
        val layout = state.layout

        val newLayout = Layout(layout.items.filter { l -> l.i != droppingItem?.i })
            .compact(compactType(props), cols)

        setState {
            this.layout = newLayout
            this.droppingDOMNode = null
            this.activeDrag = null
            this.droppingPosition = undefined
        }
    }

    fun onItemEnter(layoutItem: LayoutItem/*, i: String, x: Int, y: Int*/) {
        val offGridItem = layoutItem.copy(x = -1, y = -1)
        setState {
            oldDragItem = offGridItem
            activeDrag = placeholderLayoutItem(layoutItem)
            oldLayout = state.layout
            layout = Layout(state.layout.items + offGridItem)
        }
        console.log("GridLayout ${props.id}: ${layoutItem.i} entering", layoutItem)
    }

    fun onItemExit(): LayoutItem? {
        console.log("GridLayout ${props.id}: ${state.activeDrag?.i} exiting", state.activeDrag)
        return state.oldDragItem
            .also {
                setState {
//                    state.oldDragItem?.let { oldDragItem ->
//                        layout = state.layout.filter { it.i != oldDragItem.i }
//                    }
                    oldLayout =  null
                    oldDragItem = null
                    activeDrag = null
                }
            }
    }

    fun onDragLeave(e: DragEvent<*>) {
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
        e.preventDefault() // Prevent any browser native action
        e.stopPropagation()
        dragEnterCounter++
    }

    fun onDrop(e: DragEvent<*>) {
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
    }

    companion object : RStatics<GridLayoutProps, GridLayoutState, GridLayout, Context<DragNDropContext>>(GridLayout::class) {
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
                compactType = CompactType.vertical
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
                var newLayoutBase: MutableList<LayoutItem>? = null

                if (prevState.activeDrag == null) {
                    // Legacy support for compactType
                    // Allow parent to set layout directly.
                    if (
                        !isEqual(nextProps.layout, prevState.propsLayout) ||
                        nextProps.compactType !== prevState.compactType
                    ) {
                        newLayoutBase = nextProps.layout?.items?.toMutableList()
                    } else if (!childrenEqual(
                            nextProps.children?.asArray(),
                            prevState.children?.asArray()
                        )
                    ) {
                        // If children change, also regenerate the layout. Use our state
                        // as the base in case because it may be more up to date than
                        // what is in props.
                        newLayoutBase = prevState.layout.items.toMutableList()
                    }

                    // We need to regenerate the layout.
                    if (newLayoutBase != null) {
                        val newLayout = synchronizeLayoutWithChildren(
                            Layout(newLayoutBase),
                            nextProps.children?.asArray() ?: emptyArray(),
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

        fun synchronizeLayoutWithChildren(
            initialLayout_: Layout?,
            children: ReactChildren,
            cols: Int,
            compactType: CompactType,
            allowOverlap: Boolean?
        ): Layout {
            val initialLayout = initialLayout_ ?: Layout()

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
                    val g = (child.props.asDynamic()["data-grid"] ?: child.props.asDynamic()._grid)
                        .unsafeCast<LayoutItem?>()

                    // Hey, this item has a data-grid property, use it.
                    if (g != null) {
                        // FIXME clone not really necessary here
                        layoutItems.add(g.extend { this.i = child.key!! }.copy())
                    } else {
                        // Nothing provided: ensure this is added to the bottom
                        // FIXME clone not really necessary here
                        layoutItems.add(
                            LayoutItem(
                                0, Layout(layoutItems).bottom(),
                                1, 1,
                                key
                            )
                        )
                    }
                }
            }

            // Correct the layout.
            val correctedLayout = Layout(layoutItems).correctBounds(cols)
            return if (allowOverlap == true)
                correctedLayout
            else
                correctedLayout.compact(compactType, cols)
        }

        // Legacy support for verticalCompact: false
        fun compactType(
            props: GridLayoutProps // ?{ verticalCompact: boolean, compactType: CompactType }
        ): CompactType {
            return /*props.verticalCompact === false ? null :*/ props.compactType ?: CompactType.vertical
        }

        private val noop: DragEventHandler<*> = { }

        private fun <T: Any> T.extend(block: T.() -> Unit): T =
            Object.assign(jso(), this, jso { block() })

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
