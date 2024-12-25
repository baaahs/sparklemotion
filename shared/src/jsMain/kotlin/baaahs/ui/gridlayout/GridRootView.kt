package baaahs.ui.gridlayout

import baaahs.app.ui.appContext
import baaahs.geom.Vector2I
import baaahs.only
import baaahs.show.live.ControlProps
import baaahs.ui.JsView
import baaahs.ui.addObserver
import baaahs.ui.render
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import baaahs.util.CacheBuilder
import baaahs.util.useResizeListener
import kotlinx.css.*
import kotlinx.css.Position
import mui.material.styles.Theme
import react.*
import react.dom.RDOMBuilder
import react.dom.div
import styled.StyleSheet
import styled.inlineStyles
import web.events.addEventListener
import web.events.removeEventListener
import web.html.HTMLElement
import web.uievents.MouseEvent
import web.uievents.PointerEvent

class Grid2Styles(val theme: Theme) : StyleSheet("app-ui-layout", isStatic = true) {
    val gridRoot by css {
        position = Position.absolute
        backgroundColor = Color.lightPink
        border = Border(1.px, BorderStyle.solid, Color.black)
        width = 100.pct
        height = 100.pct
    }

    val gridItem by css {
        position = Position.absolute
        backgroundColor = Color.pink
        border = Border(1.px, BorderStyle.solid, Color.black)
    }
}

class ViewableInfo(
    val viewable: Viewable,
    var el: HTMLElement? = null,
    var ref: Ref<HTMLElement>? = null
) {
    private var pointerDown: Vector2I? = null
    private var pointerDrag: Vector2I? = null

    init {
        viewable.addObserver {
            el?.let { applyStyle(it) }
        }
    }

    fun mounted(el: HTMLElement?) {
        this.el = el
        if (el != null) {
            applyStyle(el)
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

        el.removeEventListener(PointerEvent.POINTER_DOWN, handlePointerDown)
        el.removeEventListener(PointerEvent.POINTER_UP, handlePointerUp)
        el.addEventListener(PointerEvent.POINTER_DOWN, handlePointerDown)
        el.addEventListener(PointerEvent.POINTER_UP, handlePointerUp)
//        el.addEventListener(DragEvent.DRAG_START, ::onDragStart)
    }

    private val handlePointerUp = ::onPointerUp
    private val handlePointerMove = ::onPointerMove
    private val handlePointerDown = ::onPointerDown

    fun onPointerDown(e: MouseEvent) {
        el!!.addEventListener(PointerEvent.POINTER_MOVE, handlePointerMove)
        println("pointer down on ${viewable.id}")
        pointerDown = Vector2I(e.clientX, e.clientY)
        el!!.setPointerCapture((e as PointerEvent).pointerId)
        e.stopPropagation()
        e.preventDefault()
    }

    fun onPointerUp(e: MouseEvent) {
        println("pointer up on ${viewable.id}")
        el!!.removeEventListener(PointerEvent.POINTER_MOVE, handlePointerMove)
        el!!.releasePointerCapture((e as PointerEvent).pointerId)
        viewable.draggedBy(null)
        pointerDown = null
        e.stopPropagation()
        e.preventDefault()
    }

    fun onPointerMove(e: MouseEvent) {
        pointerDown?.let { pointerDown ->
            viewable.draggedBy(Vector2I(e.clientX, e.clientY) - pointerDown)
//            pointerDrag =
        }
        println("pointer move on ${viewable.id}")
        e.stopPropagation()
        e.preventDefault()
    }
}

private val GridRootView = xComponent<GridRootProps>("GridRoot") { props ->
    val appContext = useContext(appContext)
    val openShow = appContext.showManager.openShow
    val styles = appContext.allStyles.grid2
    val viewRoot = props.viewRoot
    val rootView = viewRoot.children.only("child")
    val rootRef = ref<HTMLElement>()

    val flatViewableInfos = memo(viewRoot, rootView) {
        buildList<ViewableInfo> {
            fun Viewable.process(layer: Int = 0) {
                val viewableInfo = ViewableInfo(this)
                val ref = RefCallback<HTMLElement> { el ->
                    viewableInfo.mounted(el)
                }
                viewableInfo.ref = ref
                add(viewableInfo)

                children.forEach { it.process(layer + 1) }
            }

            rootView.process()
        }
    }

    var layoutPxDimens by state<Pair<Int, Int>?> { null }
    useResizeListener(rootRef) { width, height ->
        layoutPxDimens = width to height
    }

    println("rootRef.current = ${rootRef.current}")

    onMount(viewRoot, layoutPxDimens) {
        println("onMount rootRef.current = ${rootRef.current} $layoutPxDimens")
        layoutPxDimens?.let { (width, height) ->
            viewRoot.layout(Rect(0, 0, width, height))
        }
//        if (rootRef.current != null) {
//            flatViewableInfos.forEach {
//                println("applying style to ${it.viewable.id}")
//                it.viewable.applyStyle(it.ref.current ?: error("No ref for ${it.viewable.id}."))
//            }
//        }
    }

    val controlViews = memo(openShow) {
        CacheBuilder<String, ReactNode?> { controlId ->
            val openControl = openShow?.allControls?.find { it.id == controlId }
                ?: return@CacheBuilder null

            buildElement {
                (openControl.getView(props.controlProps) as JsView).render(this)
            }
        }
    }
    div(+styles.gridRoot) {
        ref = rootRef
        viewRoot.bounds?.applyTo(this)
        +rootView.id

        flatViewableInfos.forEach { viewableInfo ->
            div(+styles.gridItem) {
                this.ref = viewableInfo.ref
                controlViews[viewableInfo.viewable.id]?.let { child(it) }
            }
        }
    }
}

fun Rect.applyTo(builder: RDOMBuilder<*>) {
    builder.inlineStyles {
        this.top = this@applyTo.top.px
        this.left = this@applyTo.left.px
        this.width = this@applyTo.width.px
        this.height = this@applyTo.height.px
    }
}

external interface GridRootProps : Props {
    var id: String
    var viewRoot: ViewRoot
    var controlProps: ControlProps
}

fun RBuilder.gridRoot(handler: RHandler<GridRootProps>) =
    child(GridRootView, handler = handler)