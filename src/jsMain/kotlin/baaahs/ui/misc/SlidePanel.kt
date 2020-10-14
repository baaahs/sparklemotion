package baaahs.ui.misc

import baaahs.jsx.useResizeListener
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import kotlinx.css.*
import kotlinx.css.properties.s
import kotlinx.css.properties.transition
import org.w3c.dom.HTMLDivElement
import react.*
import react.dom.div
import styled.StyleSheet
import styled.css
import styled.styledDiv
import kotlin.math.min

val SlidePanel = xComponent<SlidePanelProps>("SlidePanel") { props ->
    val rootEl = ref<HTMLDivElement?>()
    val sliderEl = ref<HTMLDivElement?>()
    val panelEls = (0..10).map { ref<HTMLDivElement?>() }

    onMount(props.panels) {
        resize(props, rootEl, panelEls)
    }
    useResizeListener(rootEl) { resize(props, rootEl, panelEls) }

    val visiblePanelIndex = min(props.index ?: 0, props.panels.size)
    val marginWidth = props.margins ?: 0.px

    div(+SlidePanelStyles.root) {
        ref = rootEl

        styledDiv {
            ref = sliderEl
            css { +SlidePanelStyles.slideContainer }
            css.left = -(100.pct + marginWidth) * visiblePanelIndex

            props.panels.forEachIndexed { index, panel ->
                styledDiv {
                    ref = panelEls[index]

                    css { +SlidePanelStyles.slidePanel }
                    css.left = (100.pct + marginWidth) * index

                    panel()
                }
            }
        }
    }
}

private fun resize(
    props: SlidePanelProps,
    rootEl: RMutableRef<HTMLDivElement?>,
    panelEls: List<RMutableRef<HTMLDivElement?>>
) {
    props.panels.forEachIndexed { index, _ ->
        val div = panelEls[index].current!!
        val rootDiv = rootEl.current!!
        div.style.width = rootDiv.clientWidth.px.toString()
        div.style.height = rootDiv.clientHeight.px.toString()
    }
}

object SlidePanelStyles : StyleSheet("ui-misc-SlidePanel", isStatic = true) {
    val root by css {
        position = Position.relative
        display = Display.flex
    }

    val slideContainer by css {
        position = Position.absolute
        top = 0.px
        transition(::left, duration = .75.s)
        width = 100.pct
        flex(1.0)
    }

    val slidePanel by css {
        position = Position.absolute
        top = 0.px
        width = 100.pct
    }
}

external interface SlidePanelProps : RProps {
    var panels: List<RBuilder.() -> Unit>
    var index: Int?
    var margins: LinearDimension?
}

fun RBuilder.slidePanel(handler: RHandler<SlidePanelProps>) =
    child(SlidePanel, handler = handler)