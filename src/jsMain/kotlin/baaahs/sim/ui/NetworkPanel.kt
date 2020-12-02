package baaahs.sim.ui

import baaahs.sim.FakeNetwork
import baaahs.ui.*
import baaahs.ui.SimulatorStyles.networkPacketLossRate
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
        val network = props.network

        styledTable {
            attrs { id = "networkView" }
            css { +SimulatorStyles.section }
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
                        css { +networkPacketLossRate }
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
                    td(+SimulatorStyles.dataWithUnit) { +(network?.packetsReceived?.toString() ?: "") }
                }

                tr {
                    td { +"Packets dropped:" }
                    td(+SimulatorStyles.dataWithUnit) { +(network?.packetsDropped?.toString() ?: "") }
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