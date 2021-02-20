package baaahs.sim.ui

import baaahs.app.ui.appContext
import baaahs.sim.FakeNetwork
import baaahs.ui.BComponent
import baaahs.ui.Observable
import baaahs.ui.Observer
import baaahs.ui.unaryPlus
import baaahs.util.percent
import kotlinx.css.*
import kotlinx.html.id
import kotlinx.html.js.onClickFunction
import react.*
import react.dom.tbody
import react.dom.td
import react.dom.tr
import styled.css
import styled.styledTable
import styled.styledTd
import styled.styledTh

class NetworkPanel(props: NetworkPanelProps) : BComponent<NetworkPanelProps, RState>(props), Observer {
    override fun observing(props: NetworkPanelProps, state: RState): List<Observable?> {
        return listOf(props.network)
    }

    override fun RBuilder.render() {
        val appContext = useContext(appContext)
        val styles = appContext.allStyles.simUi
        val network = props.network

        styledTable {
            attrs { id = "networkView" }
            css { +styles.section }
            css { tableLayout = TableLayout.fixed; width = LinearDimension("100%") }

            tbody {
                tr {
                    styledTh {
                        attrs.colSpan = "2"; css.textAlign = TextAlign.left
                        +"Network"
                    }
                }

                tr {
                    td { +"Packet loss rate:" }
                    styledTd {
                        css { +styles.networkPacketLossRate }
                        if (network != null) {
                            attrs.onClickFunction = {
                                network.packetLossRate = baaahs.window.prompt(
                                    "Packet loss rate (%):", "${(network.packetLossRate * 100).toInt()}"
                                )!!.toFloat() / 100
                            }
                            +network.packetLossRate.percent()
                        }
                    }
                }

                tr {
                    td { +"Packets received:" }
                    td(+styles.dataWithUnit) { +(network?.packetsReceived?.toString() ?: "") }
                }

                tr {
                    td { +"Packets dropped:" }
                    td(+styles.dataWithUnit) { +(network?.packetsDropped?.toString() ?: "") }
                }
            }
        }
    }
}

external interface NetworkPanelProps : RProps {
    var network: FakeNetwork.Facade?
}

fun RBuilder.networkPanel(handler: RHandler<NetworkPanelProps>): ReactElement =
    child(NetworkPanel::class, handler = handler)