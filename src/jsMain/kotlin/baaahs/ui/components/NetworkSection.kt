package baaahs.ui.components

import react.*
import react.dom.*

class NetworkSection : RComponent<NetworkSection.Props, NetworkSection.State>() {

    override fun componentDidMount() {
        props.networkDisplay.addListener(this::onChange)
    }

    override fun componentWillUnmount() {
        props.networkDisplay.removeListener(this::onChange)
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

    interface Props: RProps {
        var networkDisplay: RNetworkDisplay
    }
    interface State: RState
}

fun RBuilder.networkSection(handler: NetworkSection.Props.() -> Unit): ReactElement {
    return child(NetworkSection::class) {
        this.attrs(handler)
    }
}
