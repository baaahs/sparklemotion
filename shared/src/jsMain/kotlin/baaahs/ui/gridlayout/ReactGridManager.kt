package baaahs.ui.gridlayout

import baaahs.geom.Vector2I
import baaahs.ui.unaryPlus
import js.objects.jso
import react.ReactNode
import react.RefCallback
import react.buildElement
import react.dom.div
import web.dom.document
import web.events.Event
import web.events.addEventListener
import web.events.removeEventListener
import web.html.HTMLElement
import web.uievents.MouseButton
import web.uievents.MouseEvent
import web.uievents.PointerEvent
import web.uievents.TouchEvent

class ReactGridManager(
    model: GridModel,
    val styles: Grid2Styles,
    private val debugFn: (String) -> Unit,
    val render: (String) -> ReactNode,
    onChange: (GridModel) -> Unit
) : GridManager(model, onChange) {
    init {
        println("XXX New ReactGridManager!!!")
        println(model.stringify())
    }
    val reactNodeWrappers get() = nodeWrappers
        .mapValues { (_, nodeWrapper) -> nodeWrapper as ReactNodeWrapper }
    override val placeholder = ReactPlaceholder()

    override fun createNodeWrapper(node: Node): NodeWrapper =
        ReactNodeWrapper(node)

    override fun debug(s: String) = debugFn(s)

    inner class ReactPlaceholder : Placeholder() {
        val ref = RefCallback<HTMLElement> { el -> this.mounted(el) }
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

    inner class ReactNodeWrapper(
        node: Node
    ) : NodeWrapper(node) {
        val ref = RefCallback<HTMLElement> { el -> this.mounted(el) }
        var el: HTMLElement? = null
        val reactNode by lazy {
            buildElement {
                div(+styles.gridItem) {
                    this.key = node.id
                    this.ref = this@ReactNodeWrapper.ref
                    child(render(node.id))
                }
            }
        }
        private var listenersRegistered = false

        private val handlePointerDown: (PointerEvent) -> Unit = ::onPointerDown
        private val handlePointerMove: (PointerEvent) -> Unit = ::onPointerMove
        private val handlePointerUp: (PointerEvent) -> Unit = ::onPointerUp
        private val handlePointerCancel: (PointerEvent) -> Unit = ::onPointerCancel

        fun mounted(el: HTMLElement?) {
            this.el?.let { if (it != el) unmount(it) }
            this.el = el
            if (el != null) {
                applyStyle()
                manageEventListeners(enabled = isEditable)
            }
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

        override fun applyStyle() {
            val el = el ?: return
            val bounds = effectiveBounds ?: return
            el.applyBounds(bounds)
            el.classList.toggle(+styles.dragging, isDragging)

//            el.style.zIndex = (viewable.layer + if (viewable.isDragging) 100 else 0).toString()
            //        el.addEventListener(DragEvent.DRAG_START, ::onDragStart)
        }

        private val preventDefault = { e: Event -> e.preventDefault() }
        fun onPointerDown(e: PointerEvent) {
            println("isEditable = $isEditable button = ${e.button}")
            if (e.button != MouseButton.MAIN) return
            if (onPointerDown(Vector2I(e.clientX, e.clientY))) {
                document.addEventListener(TouchEvent.TOUCH_MOVE, preventDefault, jso { passive = false })
                el!!.addEventListener(PointerEvent.Companion.POINTER_MOVE, handlePointerMove)
                println("\npointer down on ${node.id}")
                println("pointerDown $pointerDown")
                el!!.setPointerCapture(e.pointerId)
                e.stopPropagation()
                e.preventDefault()
            }
        }

        fun onPointerMove(e: MouseEvent) {
            onPointerMove(Vector2I(e.clientX, e.clientY))
            e.stopPropagation()
            e.preventDefault()
        }

        fun onPointerUp(e: PointerEvent) {
            println("\npointer up on ${node.id}")
            onPointerUp(Vector2I(e.clientX, e.clientY))
            document.removeEventListener(TouchEvent.TOUCH_MOVE, preventDefault, jso { passive = false })
            el?.removeEventListener(PointerEvent.Companion.POINTER_MOVE, handlePointerMove)
            el?.releasePointerCapture(e.pointerId)
            e.stopPropagation()
            e.preventDefault()
        }

        fun onPointerCancel(e: PointerEvent) {
            println("\npointer cancel on ${node.id}")
            onPointerCancel()
            el?.removeEventListener(PointerEvent.Companion.POINTER_MOVE, handlePointerMove)
            el?.releasePointerCapture(e.pointerId)
            e.stopPropagation()
            e.preventDefault()
        }
    }

    fun HTMLElement.applyBounds(bounds: Rect) {
        style.left = "${bounds.left}px"
        style.top = "${bounds.top}px"
        style.width = "${bounds.width}px"
        style.height = "${bounds.height}px"
    }
}