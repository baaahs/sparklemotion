package baaahs.visualizer.ui

import baaahs.ui.BComponent
import baaahs.ui.Observable
import baaahs.ui.Observer
import baaahs.ui.unaryPlus
import baaahs.visualizer.Visualizer
import kotlinx.css.*
import org.w3c.dom.HTMLDivElement
import react.RBuilder
import react.RProps
import react.RState
import react.dom.div
import styled.StyleSheet

class VisualizerPanel(props: Props) : BComponent<VisualizerPanel.Props, VisualizerPanel.State>(props), Observer {
    private val container = react.createRef<HTMLDivElement>()

    override fun observing(props: Props, state: State): List<Observable?> {
        return listOf(props.visualizer)
    }

    override fun componentDidMount() {
        props.visualizer.container = container.current
    }

    override fun componentWillUnmount() {
        props.visualizer.container = null
    }

    override fun RBuilder.render() {
        div(+Styles.visualizerPanel) {
            ref = container
        }
    }

    class Props(
        var visualizer: Visualizer.Facade
    ) : RProps

    class State : RState
}

object Styles : StyleSheet("visualizer-ui", isStatic = true) {
    val visualizerPanel by css {
        height = 100.pct
        position = Position.relative

        span {
            fontWeight = FontWeight.bold
            position = Position.absolute
            left = 1.em
            bottom = 2.em
        }
    }
}