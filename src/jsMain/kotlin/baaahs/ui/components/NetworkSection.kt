package baaahs.ui.components

import react.RBuilder
import react.RComponent
import react.RProps
import react.ReactElement
import react.dom.*

class NetworkSection(props: Props) : RComponent<NetworkSection.Props, RNetworkDisplay>(props) {

    override fun RNetworkDisplay.init(props: Props) {
        println("RNetworkDisplay.init($props) $this")
        state = props.networkDisplay
    }

    override fun componentWillReceiveProps(nextProps: Props) {
        println("componentWillReceiveProps $this")
    }

    override fun componentWillUpdate(nextProps: Props, nextState: RNetworkDisplay) {
        println("componentWillUpdate $this")
    }

    override fun componentDidMount() {
        props.networkDisplay.addStateListener(this::setMyState)
    }

    override fun componentWillUnmount() {
        props.networkDisplay.removeStateListener(this::setMyState)
    }

    private fun setMyState(state: RNetworkDisplay) {
        setState(state) {}
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
}

fun RBuilder.networkSection(handler: NetworkSection.Props.() -> Unit): ReactElement {
    return child(NetworkSection::class) {
        this.attrs(handler)
    }
}
