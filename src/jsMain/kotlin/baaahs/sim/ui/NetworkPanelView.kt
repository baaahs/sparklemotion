package baaahs.sim.ui

import baaahs.sim.FakeNetwork
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import baaahs.util.percent
import kotlinx.css.*
import kotlinx.html.id
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.*
import styled.css
import styled.inlineStyles
import styled.styledTd
import styled.styledTh
import web.prompts.prompt

private val NetworkPanelView = xComponent<NetworkPanelProps>("NetworkPanel") { props ->
    observe(props.network)

    val network = props.network

    table(+SimulatorStyles.section) {
        attrs { id = "networkView" }
        inlineStyles {
            tableLayout = TableLayout.fixed
            width = LinearDimension("100%")
        }

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
                    css { +SimulatorStyles.networkPacketLossRate }
                    attrs.onClick = {
                        network.packetLossRate = prompt(
                            "Packet loss rate (%):", "${(network.packetLossRate * 100).toInt()}"
                        )!!.toFloat() / 100
                    }
                    +network.packetLossRate.percent()
                }
            }

            tr {
                td { +"Packets received:" }
                td(+SimulatorStyles.dataWithUnit) { +(network.packetsReceived.toString() ?: "") }
            }

            tr {
                td { +"Packets dropped:" }
                td(+SimulatorStyles.dataWithUnit) { +(network.packetsDropped.toString() ?: "") }
            }

            tr {
                td { +"Packets queued:" }
                td(+SimulatorStyles.dataWithUnit) { +(network.packetsQueued.toString() ?: "") }
            }
        }
    }
}

external interface NetworkPanelProps : Props {
    var network: FakeNetwork.Facade
}

fun RBuilder.networkPanel(handler: RHandler<NetworkPanelProps>) =
    child(NetworkPanelView, handler = handler)