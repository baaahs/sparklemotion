package baaahs.ui.components

import baaahs.Pinky
import baaahs.PinkyDisplay
import baaahs.PubSub
import baaahs.Show
import kotlinx.html.InputType
import kotlinx.html.id
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.dom.*

class SimulatorApp : RComponent<SimulatorApp.Props, SimulatorApp.State>() {
    override fun RBuilder.render() {
        div { attrs.id = "sheepView" }

        div {
            attrs.id = "simulatorView"

            h3 { +"BAAAHS Simulator FUCK YEAH" }

            div("simulatorSection") { attrs.id = "launcher" }

            table("simulatorSection") {
                attrs.id = "networkView"; style(content = "table-layout: fixed; width: 100%;")

                tr { th { attrs.colSpan = "2"; style(content = "text-align: left;"); +"Network" } }
                tr { td { +"Packet loss rate:" }; td { attrs.id = "networkPacketLossRate" } }
                tr { td { +"Packets received:" }; td { attrs.id = "networkPacketsReceived" } }
                tr { td { +"Packets dropped:" }; td { attrs.id = "networkPacketsDropped" } }
            }

            table("simulatorSection") {
                attrs.id = "networkView"; style(content = "table-layout: fixed; width: 100%;")

                tr { th { attrs.colSpan = "2"; style(content = "text-align: left;"); +"Effective frame rate" } }

                tr { td { +"Show:" }; td { attrs.id = "showFramerate" }; td { attrs.id = "showElapsedMs" } }
                tr {
                    td { +"Visualizer:" }; td { attrs.id = "visualizerFramerate" }; td {
                    attrs.id = "visualizerElapsedMs"
                }
                }
                tr { td { +"Brains:" }; td { attrs.id = "brainsFramerate" }; td { attrs.id = "brainsElapsedMs" } }
            }

            div("simulatorSection") {
                b { +"Pinky" }
                div { attrs.id = "pinkyView" }
            }

            div("simulatorSection") {
                b { +"Visualizer:" }
                input { attrs { type = InputType.checkBox; id = "vizRotation"; checked = true } }
                label { attrs { htmlFor = "vizRotation" }; +"Rotate" }
                div { attrs.id = "selectionInfo" }
                div {
                    +"Pixel Count: "
                    span { attrs.id = "visualizerPixelCount"; +"n/a" }
                }
            }

            div("simulatorSection") {
                b { +"Brains:" }
                div { attrs.id = "brainsView" }
                div { attrs.id = "brainDetails" }
            }
        }
    }

    class Props(val pubSub: PubSub.Client) : RProps

    class State(
        override var brainCount: Int,
        override var beat: Int,
        override var bpm: Float,
        override var beatConfidence: Float,
        override var onShowChange: () -> Unit,
        override var availableShows: List<Show>,
        override var selectedShow: Show?,
        override var showFrameMs: Int,
        override var stats: Pinky.NetworkStats?
    ) : RState, PinkyDisplay
}