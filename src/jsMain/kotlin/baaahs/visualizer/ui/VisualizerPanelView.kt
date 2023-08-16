package baaahs.visualizer.ui

import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import baaahs.util.useResizeListener
import baaahs.visualizer.Visualizer
import kotlinx.css.*
import react.PropsWithChildren
import react.RBuilder
import react.RHandler
import react.dom.div
import styled.StyleSheet
import web.html.HTMLElement

private val VisualizerPanelView = xComponent<VisualizerPanelProps>("VisualizerPanel") { props ->
    val container = ref<HTMLElement>()
    observe(props.visualizer)

    useResizeListener(container) { _, _ ->
        props.visualizer.resize()
    }

    onMount(props.visualizer, container.current) {
        props.visualizer.container = container.current

        withCleanup {
            props.visualizer.container = null
        }
    }

    div(+Styles.visualizerPanel) {
        ref = container

        props.children?.let { +it }
    }
}

external interface VisualizerPanelProps : PropsWithChildren {
    var visualizer: Visualizer.Facade
}

fun RBuilder.visualizerPanel(handler: RHandler<VisualizerPanelProps>) =
    child(VisualizerPanelView, handler = handler)

object Styles : StyleSheet("visualizer-ui", isStatic = true) {
    val visualizerPanel by css {
        flex = Flex.GROW
        position = Position.relative

        canvas {
            position = Position.absolute
        }
    }
}