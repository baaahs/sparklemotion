package baaahs.sim.ui

import baaahs.SheepSimulator
import baaahs.sim.FakeNetwork
import baaahs.ui.BComponent
import baaahs.ui.Facade
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

class NetworkPanel(props: Props) : BComponent<NetworkPanel.Props, NetworkPanel.State>(props), Facade.Observer {
    override fun observing(props: Props, state: State): List<Facade?> {
        return listOf(props.simulator?.network?.facade)
    }

    override fun RBuilder.render() {
        val fakeNetwork = props.simulator?.network?.facade ?: return

        styledTable {
            attrs { id = "networkView" }
            css { +"simulatorSection"; tableLayout = TableLayout.fixed; width = LinearDimension("100%") }

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
                        css { +"networkPacketLossRate" }
                        attrs.onClickFunction = {
                            fakeNetwork.packetLossRate =
                                kotlin.browser.window.prompt(
                                    "Packet loss rate (%):", "${(fakeNetwork.packetLossRate * 100).toInt()}"
                                )!!.toFloat() / 100
                        }
                        +"${(fakeNetwork.packetLossRate * 100f).toInt()}%"
                    }
                }

                tr {
                    td { +"Packets received:" }
                    td { +fakeNetwork.packetsReceived.toString() }
                }

                tr {
                    td { +"Packets dropped:" }
                    td { +fakeNetwork.packetsDropped.toString() }
                }
            }
        }
    }

    class Props(
        var simulator: SheepSimulator?
    ) : RProps

    class State(
        var fakeNetwork: FakeNetwork?
    ) : RState
}