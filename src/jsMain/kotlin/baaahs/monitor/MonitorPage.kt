package baaahs.monitor

import baaahs.visualizer.Visualizer
import org.w3c.dom.HTMLDivElement
import react.RBuilder
import react.RComponent
import react.dom.div

class MonitorPage(props: Props) : RComponent<MonitorPage.Props, MonitorPage.State>(props) {
    private val container = react.createRef<HTMLDivElement>()

    override fun componentDidMount() {
        container.current?.appendChild(props.containerDiv)
        props.visualizer.resize()
    }

    override fun componentWillUnmount() {
        container.current?.removeChild(props.containerDiv)
    }

    override fun RBuilder.render() {
        div { ref = container }
    }

    class Props(
        var containerDiv: HTMLDivElement,
        var visualizer: Visualizer
    ) : react.Props

    class State : react.State
}