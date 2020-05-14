package baaahs.sim.ui

import baaahs.sim.FakeNetwork
import baaahs.ui.BComponent
import baaahs.ui.Observable
import baaahs.ui.Observer
import baaahs.ui.SimulatorStyles
import baaahs.ui.SimulatorStyles.networkPacketLossRate
import baaahs.util.percent
import kotlinx.css.*
import kotlinx.html.id
import kotlinx.html.js.onClickFunction
import react.RBuilder
import react.RProps
import react.RState
import react.dom.tbody
import react.dom.td
import react.dom.tr
import styled.css
import styled.styledTable
import styled.styledTd
import styled.styledTh

class NetworkPanel(props: Props) : BComponent<NetworkPanel.Props, NetworkPanel.State>(props), Observer {
    override fun observing(props: Props, state: State): List<Observable?> {
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
                                network.packetLossRate = kotlin.browser.window.prompt(
                                    "Packet loss rate (%):", "${(network.packetLossRate * 100).toInt()}"
                                )!!.toFloat() / 100
                            }
                            +network.packetLossRate.percent()
                        }
                    }
                }

                tr {
                    td { +"Packets received:" }
                    td { +(network?.packetsReceived?.toString() ?: "") }
                }

                tr {
                    td { +"Packets dropped:" }
                    td { +(network?.packetsDropped?.toString() ?: "") }
                }
            }
        }
    }

    class Props(
        var network: FakeNetwork.Facade?
    ) : RProps

    class State : RState

}