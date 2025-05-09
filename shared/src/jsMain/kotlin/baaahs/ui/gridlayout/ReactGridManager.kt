package baaahs.ui.gridlayout

import baaahs.geom.Vector2I
import baaahs.ui.and
import baaahs.ui.unaryPlus
import baaahs.util.JsPlatform
import baaahs.util.Logger
import react.ReactNode
import react.Ref
import react.RefCallback
import react.RefObject
import react.buildElement
import react.dom.div
import react.dom.img
import react.dom.onPointerDown
import web.dom.document
import web.events.AddEventListenerOptions
import web.events.Event
import web.events.addEventListener
import web.events.removeEventListener
import web.geometry.DOMRect
import web.html.HTMLElement
import web.resize.ResizeObserver
import web.timers.Timeout
import web.timers.clearTimeout
import web.timers.setTimeout
import web.uievents.KeyboardEvent
import web.uievents.MouseButton
import web.uievents.PointerEvent
import web.uievents.TouchEvent
import kotlin.Boolean
import kotlin.Int
import kotlin.String
import kotlin.Unit
import kotlin.also
import kotlin.collections.buildList
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.emptyList
import kotlin.collections.forEach
import kotlin.collections.mapValues
import kotlin.error
import kotlin.getValue
import kotlin.lazy
import kotlin.let
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.milliseconds

class ReactGridManager(
    model: GridModel,
    val styles: GridManagerStyles,
    val renderNode: RenderNode,
    val renderContainerNode: RenderContainerNode,
    val renderEmptyCell: RenderEmptyCell?,
    private val rootRef: RefObject<HTMLElement>,
    onChange: (GridModel) -> Unit
) : GridManager(model, onChange) {
    private var selectedNodeId: String? = null
        set(value) {
            field = value
            reactNodeWrappers.values.forEach { it.applyStyle() }
        }

    var editMode = false
        set(value) {
            field = value
            reactNodeWrappers.values.forEach { it.applyStyle() }
        }
    init {
        logger.debug { "XXX New ReactGridManager!!!" }
        logger.debug { (model.stringify()) }
    }
    val reactNodeWrappers get() = nodeWrappers
        .mapValues { (_, nodeWrapper) -> nodeWrapper as ReactNodeWrapper }
    override val placeholder = ReactPlaceholder()

    override fun createNodeWrapper(node: Node): NodeWrapper =
        ReactNodeWrapper(node)

    private var transitionDisablerTimeout: Timeout? = null
    fun withTransitionsDisabled(block: () -> Unit) {
        rootRef.current?.classList?.add(+styles.disableTransitions)
        clearTimeout(transitionDisablerTimeout)
        transitionDisablerTimeout = setTimeout(100.milliseconds) {
            rootRef.current?.classList?.remove(+styles.disableTransitions)
        }
        block()
    }

    inner class ReactPlaceholder : Placeholder() {
        val ref = RefCallback<HTMLElement> { el -> this@ReactPlaceholder.mounted(el) }
        var el: HTMLElement? = null
        val reactNode by lazy {
            buildElement {
                div(+styles.placeholder) {
                    this.key = "__placeholder__"
                    this.ref = this@ReactPlaceholder.ref
                }
            }
        }

        fun mounted(el: HTMLElement?) {
            this.el = el
            applyStyle()
        }

        override fun applyStyle() {
            val el = el ?: return
            bounds?.let { el.applyBounds(it) }
            el.classList.toggle(+styles.placeholderActive, bounds != null)
        }
    }

    inner class EmptyCellWrapper(
        parentNode: Node,
        val cell: Vector2I,
        renderEmptyCell: RenderEmptyCell
    ) {
        val ref = RefCallback<HTMLElement> { el -> this@EmptyCellWrapper.mounted(el) }
        var el: HTMLElement? = null
        val reactNode = renderEmptyCell.render(parentNode, cell, ref)
        private var layoutBounds: Rect? = null
        private var dragOffset: Vector2I? = null
        private val isDragging get() = dragOffset != null

        val effectiveBounds
            get() =
                layoutBounds?.let { layoutBounds ->
                    dragOffset?.let { dragOffset ->
                        layoutBounds + dragOffset
                    } ?: layoutBounds
                }

        fun mounted(el: HTMLElement?) {
            this.el?.let { if (it != el) unmount(it) }
            this.el = el
            if (el != null) {
                applyStyle()
            }
        }

        fun unmount(el: HTMLElement) {
        }

        fun layout(container: GridContainer) {
            layoutBounds = container.calculateRegionBounds(cell.x, cell.y, 1, 1)
            applyStyle()
        }

        fun dragChildren(offset: Vector2I?) {
            dragOffset = offset
            applyStyle()
        }

        fun applyStyle() {
            val el = el ?: return
            val bounds = effectiveBounds ?: return
            el.applyBounds(bounds, if (isDragging) 100 else null)
        }
    }

    inner class ReactNodeWrapper(
        node: Node
    ) : NodeWrapper(node) {
        val ref = RefCallback<HTMLElement> { el -> this@ReactNodeWrapper.mounted(el) }
        var el: HTMLElement? = null

        // Must be lazy, or outer class won't be initialized yet.
        val reactNode by lazy {
            buildElement {
                div(+styles.gridItem and styles.notYetLayedOut) {
                    this.key = node.id
                    this.ref = this@ReactNodeWrapper.ref
                    if (node.isContainer) {
                        val childNode = buildElement {
                            var resizeObserver: ResizeObserver? = null
                            val innerMountedRef = RefCallback<HTMLElement> { el ->
                                if (el != null) {
                                    resizeObserver = ResizeObserver { _, _ ->
                                        logger.debug {
                                            "${node.id}.resizeObserver bounds:${el.getBoundingClientRect().toRect()}"
                                        }
                                        onResizeInnerContainer(el)
                                    }.also { it.observe(el) }
                                } else {
                                    resizeObserver?.disconnect()
                                }
                            }

                            div(+styles.gridContainer) { this.ref = innerMountedRef }
                        }
                        child(renderContainerNode.render(node, childNode))
                    } else {
                        child(renderNode.render(node))
                    }

//                    img(classes = +styles.gridResizeHandleTopLeft) {
//                        attrs.src = JsPlatform.imageUrl("/assets/resize-top-left-corner.svg")
//                        attrs.onPointerDown = ::onTopLeftResizeHandlePointerDown
//                    }
                    img(classes = +styles.gridResizeHandleBottomRight) {
                        attrs.src = JsPlatform.imageUrl("/assets/resize-bottom-right-corner.svg")
                        attrs.onPointerDown = ::onBottomRightResizeHandlePointerDown
                    }
                }
            }
        }

        // Must be lazy, or outer class won't be initialized yet.
        val emptyCells by lazy {
            renderEmptyCell?.let { render ->
                node.layout?.let { layout ->
                    buildList {
                        for (row in 0 until layout.rows) {
                            for (column in 0 until layout.columns) {
                                val cell = Vector2I(column, row)
                                add(EmptyCellWrapper(node, cell, render))
                            }
                        }
                    }
                }
            } ?: emptyList()
        }
        private var listenersRegistered = false

        private val handlePointerDown: (PointerEvent) -> Unit = ::onPointerDown
        private val handlePointerMove: (PointerEvent) -> Unit = ::onPointerMove
        private val handlePointerUp: (PointerEvent) -> Unit = ::onPointerUp
        private val handlePointerCancel: (PointerEvent) -> Unit = ::onPointerCancel
        private val handleKeyDown: (KeyboardEvent) -> Unit = ::onKeyDown

        fun mounted(el: HTMLElement?) {
//            println("${node.id}.mounted() bounds:${el?.getBoundingClientRect()?.toRect()}")
            this.el?.let { if (it != el) unmount(it) }
            this.el = el
            if (el != null) {
                applyStyle()
                manageEventListeners(enabled = isEditable)
            }
        }

        private fun onResizeInnerContainer(el: HTMLElement) {
            val rect = el.getBoundingClientRect()
            val rootEl = rootRef.current ?: return
            val rootRect = rootEl.getBoundingClientRect()
            val adjustedBounds = Rect(
                (rect.x - rootRect.x).roundToInt(),
                (rect.y - rootRect.y).roundToInt(),
                rect.width.roundToInt(),
                rect.height.roundToInt()
            )
            setTimeout(0.milliseconds) {
                setContainerBounds(adjustedBounds)
            }
        }

        fun setContainerBounds(innerBounds: Rect) {
            val layoutBounds = layoutBounds
            if (layoutBounds == null) {
                console.error("${node.id}.setContainerBounds($innerBounds): No layout bounds?!")
                return
            }

            containerInset = layoutBounds - innerBounds
            if (innerBounds.width == 0 || innerBounds.height == 0) return
            val layout = node.layout ?: error("${node.id}.setContainerBounds($innerBounds): No layout?!")
            val gridContainer = GridContainer(layout.columns, layout.rows, innerBounds.inset(margin), gap)
            layoutContainer(gridContainer)
        }

        override fun updateEditable() {
            manageEventListeners(enabled = isEditable)
        }

        fun manageEventListeners(enabled: Boolean) {
            if (enabled == listenersRegistered)
                return
            val el = el ?: return

            if (enabled) {
                el.addEventListener(PointerEvent.Companion.POINTER_DOWN, handlePointerDown)
                el.addEventListener(PointerEvent.Companion.POINTER_UP, handlePointerUp)
                el.addEventListener(PointerEvent.Companion.POINTER_CANCEL, handlePointerCancel)
            } else {
                el.removeEventListener(PointerEvent.Companion.POINTER_DOWN, handlePointerDown)
                el.removeEventListener(PointerEvent.Companion.POINTER_UP, handlePointerUp)
                el.removeEventListener(PointerEvent.Companion.POINTER_CANCEL, handlePointerCancel)
            }
            listenersRegistered = isEditable
        }

        fun unmount(el: HTMLElement) {
            manageEventListeners(enabled = false)
        }

        override fun visualDragChildren(offset: Vector2I?) {
            super.visualDragChildren(offset)
            emptyCells.forEach { cell -> cell.dragChildren(offset) }
        }

        override fun applyStyle() {
            val el = el ?: return
            setTimeout(50.milliseconds) {
                el.classList.toggle(+styles.notYetLayedOut, layoutBounds == null)
            }
            val bounds = effectiveBounds ?: return
            el.applyBounds(bounds, if (isDragging) 100 else null)
            el.classList.toggle(+styles.dragging, isDragging)
            el.classList.toggle(+styles.resizing, isResizing)
            el.classList.toggle(+styles.editing, node.id == selectedNodeId)

//            el.style.zIndex = (viewable.layer + if (viewable.isDragging) 100 else 0).toString()
            //        el.addEventListener(DragEvent.DRAG_START, ::onDragStart)
        }

        override fun layoutContainer(gridContainer: GridContainer) {
            super.layoutContainer(gridContainer)

            emptyCells.forEach { emptyCellWrapper ->
                emptyCellWrapper.layout(gridContainer)
            }
        }

        override fun mayContain(otherNodeWrapper: NodeWrapper): Boolean =
            node.isContainer && !otherNodeWrapper.node.isContainer

        private val preventDefault = { e: Event -> e.preventDefault() }

        fun onPointerDown(e: PointerEvent) {
            logger.debug { "isEditable = $isEditable button = ${e.button}" }
            selectedNodeId = null
            if (e.button != MouseButton.MAIN) return

            if (onPointerDown(Vector2I(e.clientX, e.clientY))) {
                addDraggingListeners()
                logger.debug { "\npointer down on ${node.id}" }
                logger.debug { "pointerDown $pointerDown" }
                el!!.setPointerCapture(e.pointerId)
//                e.stopPropagation()
//                e.preventDefault()
            }
        }

        fun onTopLeftResizeHandlePointerDown(e: react.dom.events.PointerEvent<*>) {
            console.log("onTopLeftResizeHandlePointerDown")
            onResizePointerDown(e, DraggingState.ResizeFromTopLeft)
        }

        fun onBottomRightResizeHandlePointerDown(e: react.dom.events.PointerEvent<*>) {
            console.log("onBottomRightResizeHandlePointerDown")
            onResizePointerDown(e, DraggingState.ResizeFromBottomRight)
        }

        private fun onResizePointerDown(e: react.dom.events.PointerEvent<*>, state: DraggingState) {
            selectedNodeId = null
            if (e.button != MouseButton.MAIN) return
            if (onPointerDown(Vector2I(e.clientX, e.clientY), state)) {
                addDraggingListeners()
                el!!.setPointerCapture(e.pointerId)
//                e.stopPropagation()
                e.preventDefault()
            }
        }

        fun onPointerMove(e: PointerEvent) {
            selectedNodeId = node.id
            onPointerMove(Vector2I(e.clientX, e.clientY))
//            e.stopPropagation()
//            e.preventDefault()
        }

        fun onPointerUp(e: PointerEvent) {
            logger.debug { "\npointer up on ${node.id}" }
            selectedNodeId = node.id
            onPointerUp(Vector2I(e.clientX, e.clientY))
            removeDraggingListeners()
            el?.releasePointerCapture(e.pointerId)
//            e.stopPropagation()
//            e.preventDefault()
        }

        override fun onPointerCancel() {
            removeDraggingListeners()
            super.onPointerCancel()
        }

        fun onPointerCancel(e: PointerEvent) {
            logger.debug { "\npointer cancel on ${node.id}" }
            el?.releasePointerCapture(e.pointerId)
            super.onPointerCancel()
//            e.stopPropagation()
//            e.preventDefault()
        }

        fun onKeyDown(e: KeyboardEvent) {
            console.log("onKeyDown", e)
            if (e.key == "Escape") {
                onPointerCancel()
                e.stopPropagation()
                e.preventDefault()
            }
        }

        private fun addDraggingListeners() {
            document.addEventListener(TouchEvent.TOUCH_MOVE, preventDefault, AddEventListenerOptions(passive = false))
            el!!.addEventListener(PointerEvent.POINTER_MOVE, handlePointerMove)
            document.addEventListener(KeyboardEvent.KEY_DOWN, handleKeyDown)
        }

        private fun removeDraggingListeners() {
            document.removeEventListener(TouchEvent.TOUCH_MOVE, preventDefault, AddEventListenerOptions(passive = false))
            el?.removeEventListener(PointerEvent.Companion.POINTER_MOVE, handlePointerMove)
            document.removeEventListener(KeyboardEvent.KEY_DOWN, handleKeyDown)
        }

        override fun toString(): String =
            "ReactNodeWrapper(node=${node.id} @${node.left},${node.top})"
    }

    fun HTMLElement.applyBounds(bounds: Rect, zIndex: Int? = null) {
        style.left = "${bounds.left}px"
        style.top = "${bounds.top}px"
        style.width = "${bounds.width}px"
        style.height = "${bounds.height}px"
        style.zIndex = zIndex?.toString() ?: ""
    }

    companion object {
        private val logger = Logger<ReactGridManager>()
    }
}

fun interface RenderNode {
    fun render(node: Node): ReactNode
}

fun interface RenderContainerNode {
    fun render(node: Node, childNode: ReactNode): ReactNode
}

fun interface RenderEmptyCell {
    fun render(parentNode: Node, cell: Vector2I, ref: Ref<HTMLElement>): ReactNode
}

fun DOMRect.toRect(): Rect =
    Rect(left.toInt(), top.toInt(), width.toInt(), height.toInt())