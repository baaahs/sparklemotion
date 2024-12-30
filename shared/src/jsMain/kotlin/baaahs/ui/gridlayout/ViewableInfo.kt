package baaahs.ui.gridlayout

import baaahs.geom.Vector2I
import baaahs.ui.addObserver
import react.Ref
import web.events.addEventListener
import web.events.removeEventListener
import web.html.HTMLElement
import web.uievents.MouseEvent
import web.uievents.PointerEvent
import baaahs.ui.unaryPlus

class RootInfo(
    private val viewRoot: ViewRoot,
    val styles: Grid2Styles
) {
    private var rootView: Viewable? = null
    var dragging: ViewableInfo? = null

    fun rootViewChanged(rootView: ViewRoot.RootViewable) {
        this.rootView = rootView
    }
}

class ViewableInfo(
    val viewable: Viewable,
    private val rootInfo: RootInfo
) {
    var el: HTMLElement? = null
    var ref: Ref<HTMLElement>? = null
    private var pointerDown: Vector2I? = null
    private var pointerDrag: Vector2I? = null
    private val styles = rootInfo.styles

    init {
        viewable.addObserver {
            el?.let { applyStyle(it) }
            el?.classList?.toggle(+styles.dragging, rootInfo.dragging == this)
        }
    }

    fun mounted(el: HTMLElement?) {
        this.el = el
        if (el != null) {
            applyStyle(el)
            if (viewable.id == "rotateTwist") {
                println("Applied style to ${viewable.id}: ${el.style} ${viewable.bounds}")
            }
        }
    }

    fun applyStyle(el: HTMLElement) {
        val bounds = viewable.bounds
        if (bounds != null) {
            el.style.left = "${bounds.left}px"
            el.style.top = "${bounds.top}px"
            el.style.width = "${bounds.width}px"
            el.style.height = "${bounds.height}px"
        }

        el.style.zIndex = (viewable.layer + if (viewable.isDragging) 100 else 0).toString()

        el.removeEventListener(PointerEvent.Companion.POINTER_DOWN, handlePointerDown)
        el.removeEventListener(PointerEvent.Companion.POINTER_UP, handlePointerUp)
        el.addEventListener(PointerEvent.Companion.POINTER_DOWN, handlePointerDown)
        el.addEventListener(PointerEvent.Companion.POINTER_UP, handlePointerUp)
//        el.addEventListener(DragEvent.DRAG_START, ::onDragStart)
    }

    private val handlePointerUp = ::onPointerUp
    private val handlePointerMove = ::onPointerMove
    private val handlePointerDown = ::onPointerDown

    fun onPointerDown(e: PointerEvent) {
        if (e.pointerId != 1) return
        el!!.addEventListener(PointerEvent.Companion.POINTER_MOVE, handlePointerMove)
        println("pointer down on ${viewable.id}")
        pointerDown = Vector2I(e.clientX, e.clientY)
        el!!.setPointerCapture(e.pointerId)
        e.stopPropagation()
        e.preventDefault()
    }

    fun onPointerUp(e: PointerEvent) {
        val wasDragging = rootInfo.dragging
        if (wasDragging != this)
            error("Huh? onPointerUp on ${viewable.id} but we were dragging ${wasDragging?.viewable?.id}.")

        println("pointer up on ${viewable.id}")
        el?.removeEventListener(PointerEvent.Companion.POINTER_MOVE, handlePointerMove)
        el?.releasePointerCapture((e as PointerEvent).pointerId)
        viewable.draggedBy(null)
        pointerDown = null
        e.stopPropagation()
        e.preventDefault()
    }

    fun onPointerMove(e: MouseEvent) {
        rootInfo.dragging = this
        pointerDown?.let { pointerDown ->
            viewable.draggedBy(Vector2I(e.clientX, e.clientY) - pointerDown)
//            pointerDrag =
        }
        println("pointer move on ${viewable.id}")
        e.stopPropagation()
        e.preventDefault()
    }
}