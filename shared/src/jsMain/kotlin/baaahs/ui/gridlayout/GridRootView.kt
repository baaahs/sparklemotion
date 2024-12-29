package baaahs.ui.gridlayout

import baaahs.app.ui.appContext
import baaahs.show.GridLayout
import baaahs.show.live.ControlProps
import baaahs.ui.JsView
import baaahs.ui.render
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import baaahs.util.CacheBuilder
import baaahs.util.useResizeListener
import kotlinx.css.*
import react.*
import react.dom.RDOMBuilder
import react.dom.div
import styled.inlineStyles
import web.html.HTMLElement

private val GridRootView = xComponent<GridRootProps>("GridRoot") { props ->
    val appContext = useContext(appContext)
    val openShow = appContext.showManager.openShow
    val styles = appContext.allStyles.grid2
    val viewRoot = observe(props.viewRoot)
    val rootView = viewRoot.rootViewable
    val rootRef = ref<HTMLElement>()

    val flatViewableInfos = memo(viewRoot, rootView) {
        println("Recaluclate flatViewableInfos")
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

        div {
            viewRoot.bounds?.applyTo(this)
            +rootView.id

            println("Render ${flatViewableInfos.size} viewables.")
            flatViewableInfos.forEach { viewableInfo ->
                div(+styles.gridItem) {
                    this.key = viewableInfo.viewable.id
                    this.ref = viewableInfo.ref
                    controlViews[viewableInfo.viewable.id]?.let { child(it) }
                }
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
    var onLayoutChange: (newLayout: GridLayout, stillDragging: Boolean) -> Unit
}

fun RBuilder.gridRoot(handler: RHandler<GridRootProps>) =
    child(GridRootView, handler = handler)