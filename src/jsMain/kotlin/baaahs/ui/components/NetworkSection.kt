package baaahs.ui.components

import react.*
import react.dom.*

class NetworkSection(props: Props) : RComponent<NetworkSection.Props, RNetworkDisplay>(props) {

    override fun RNetworkDisplay.init(props: Props) {
        state = props.networkDisplay
    }

    override fun componentDidMount() {
        props.networkDisplay.addStateListener(this::setMyState)
    }

    override fun componentWillUnmount() {
        props.networkDisplay.removeStateListener(this::setMyState)
    }

    private fun setMyState(state: Map<String, Any>) {
        setState({ oldState ->
            state.forEach { (k, v) -> oldState.asDynamic()[k] = v }
            oldState
        }, {})
    }

    fun onChange() = forceUpdate()

    override fun RBuilder.render() {
        table("simulatorSection") {
            style(content = "table-layout: fixed; width: 100%;")

            val data = props.networkDisplay
            tbody {
                tr { th { attrs.colSpan = "2"; style(content = "text-align: left;"); +"Network" } }
                tr { td { +"Packet loss rate:" }; td { +"${data.packetLossRate}" } }
                tr { td { +"Packets received:" }; td { +"${data.packetsReceived}" } }
                tr { td { +"Packets dropped:" }; td { +"${data.packetsDropped}" } }
            }
        }
    }

    interface Props : RProps {
        var networkDisplay: RNetworkDisplay
    }
}

fun RBuilder.networkSection(handler: NetworkSection.Props.() -> Unit): ReactElement {
    return child(NetworkSection::class) {
        this.attrs(handler)
    }
}
