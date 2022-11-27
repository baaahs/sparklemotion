package baaahs.visualizer.ui

import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import baaahs.util.useResizeListener
import baaahs.visualizer.Visualizer
import dom.html.HTMLElement
import kotlinx.css.*
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.div
import styled.StyleSheet

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
    }
}

external interface VisualizerPanelProps : Props {
    var visualizer: Visualizer.Facade
}

fun RBuilder.visualizerPanel(handler: RHandler<VisualizerPanelProps>) =
    child(VisualizerPanelView, handler = handler)

object Styles : StyleSheet("visualizer-ui", isStatic = true) {
    val visualizerPanel by css {
        grow(Grow.GROW)
        position = Position.relative

        canvas {
            position = Position.absolute
        }

        span {
            fontWeight = FontWeight.bold
            position = Position.absolute
            left = 1.em
            bottom = 2.em
        }
    }
}