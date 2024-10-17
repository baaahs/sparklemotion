package baaahs.monitor

import baaahs.visualizer.Visualizer
import react.RBuilder
import react.RComponent
import react.State
import react.dom.div
import web.html.HTMLDivElement

class MonitorPage(props: MonitorPageProps) : RComponent<MonitorPageProps, State>(props) {
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
}

external interface MonitorPageProps : react.Props {
    var containerDiv: HTMLDivElement
    var visualizer: Visualizer
}
