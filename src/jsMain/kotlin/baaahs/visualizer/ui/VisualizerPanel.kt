package baaahs.visualizer.ui

import baaahs.ui.BComponent
import baaahs.ui.Observable
import baaahs.ui.Observer
import baaahs.visualizer.Visualizer
import kotlinx.css.height
import kotlinx.css.pct
import kotlinx.html.id
import kotlinx.html.js.onMouseDownFunction
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.events.Event
import react.RBuilder
import react.RProps
import react.RState
import styled.css
import styled.styledDiv

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
        styledDiv {
            ref = container
            css {
                +"sheepView"
                height = 100.pct
            }
            attrs.id = "sheepView"
            attrs.onMouseDownFunction = { event: Event ->
                props.visualizer.onMouseDown(event.asDynamic())
            }
        }
    }

    class Props(
        var visualizer: Visualizer.Facade
    ) : RProps

    class State : RState
}