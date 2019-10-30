package baaahs.ui.components

import baaahs.*
import baaahs.net.Network
import kotlinx.html.InputType
import kotlinx.html.id
import org.w3c.dom.Element
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.dom.*

class SimulatorApp : RComponent<SimulatorApp.Props, SimulatorApp.State>() {
    override fun componentDidMount() {
        println("componentDidMount")
    }

    override fun componentWillUnmount() {
        println("componentWillUnmount")
        super.componentWillUnmount()
    }

    override fun RBuilder.render() {
        div { attrs.id = "sheepView" }

        div {
            attrs.id = "simulatorView"

            h3 { +"BAAAHS Simulator FUCK YEAH" }

            div("simulatorSection") { attrs.id = "launcher" }

            table("simulatorSection") {
                attrs.id = "networkView"; style(content = "table-layout: fixed; width: 100%;")

                tbody {
                    tr { th { attrs.colSpan = "2"; style(content = "text-align: left;"); +"Network" } }
                    tr { td { +"Packet loss rate:" }; td { +"${props.networkDisplay.packetLossRate}" } }
                    tr { td { +"Packets received:" }; td { +"${props.networkDisplay.packetsReceived}" } }
                    tr { td { +"Packets dropped:" }; td { +"${props.networkDisplay.packetsDropped}" } }
                }
            }

            table("simulatorSection") {
                attrs.id = "networkView"; style(content = "table-layout: fixed; width: 100%;")

                thead {
                    tr { th { attrs.colSpan = "2"; style(content = "text-align: left;"); +"Effective frame rate" } }
                }
                tbody {
                    val showFrameRenderMs = props.pinkyDisplay.showFrameMs
                    tr { td { +"Show:" }; td { +"${1000 / showFrameRenderMs}fps" }; td { +"${showFrameRenderMs}ms" } }
                    val vizRenderMs = props.visualizerDisplay.renderMs
                    tr { td { +"Visualizer:" }; td { +"${1000 / vizRenderMs}fps" }; td { +"${vizRenderMs}ms" } }
                    tr { td { +"Brains:" }; td { +"tbd" }; td { +"tbd" } }
                }
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

    interface Props : RProps {
        var networkDisplay: NetworkDisplay
        var pinkyDisplay: PinkyDisplay
        var brainDisplay: BrainDisplay
        var visualizerDisplay: VisualizerDisplay
    }

    class RVisualizerDisplay(val onChange: () -> Unit): VisualizerDisplay {
        override var renderMs: Int = 0
            set(value) { field = value; onChange() }
    }

    class RNetworkDisplay(val onChange: () -> Unit): NetworkDisplay {
        override var packetLossRate: Float = 0f
            set(value) { field = value; onChange() }
        override var packetsReceived: Int = 0
            set(value) { field = value; onChange() }
        override var packetsDropped: Int = 0
            set(value) { field = value; onChange() }
    }

    class RPinkyDisplay(val onChange: () -> Unit): PinkyDisplay {
        override var brainCount: Int = 0
            set(value) { field = value; onChange() }
        override var beat: Int = 0
            set(value) { field = value; onChange() }
        override var bpm: Float = 0f
            set(value) { field = value; onChange() }
        override var beatConfidence: Float = 0f
            set(value) { field = value; onChange() }
        override var onShowChange: () -> Unit = {}
            set(value) { field = value; onChange() }
        override var selectedShow: Show? = null
            set(value) { field = value; onChange() }
        override var availableShows: List<Show> = emptyList()
            set(value) { field = value; onChange() }
        override var showFrameMs: Int = 0
            set(value) { field = value; onChange() }
        override var stats: Pinky.NetworkStats? = null
            set(value) { field = value; onChange() }
    }

    class RBrainDisplay(val onChange: () -> Unit): BrainDisplay {
        override var id: String? = null
            set(value) { field = value; onChange() }
        override var surface: Surface? = null
            set(value) { field = value; onChange() }
        override var onReset: suspend () -> Unit = {}
            set(value) { field = value; onChange() }

        override fun haveLink(link: Network.Link) {
        }
    }

    companion object {
        @JsName("render")
        fun render(node: Element): Display {
            val display = object : Display {
                private val networkDisplay = RNetworkDisplay { redraw() }
                private val pinkyDisplay = RPinkyDisplay { redraw() }
                private val brainDisplay = RBrainDisplay { redraw() }
                private val visualizerDisplay = RVisualizerDisplay { redraw() }

                override fun forNetwork(): NetworkDisplay = networkDisplay
                override fun forPinky(): PinkyDisplay = pinkyDisplay
                override fun forBrain(): BrainDisplay = brainDisplay
                override fun forVisualizer(): VisualizerDisplay = visualizerDisplay

                fun redraw() = doRender(this, node)
            }

            display.redraw()

            return display
        }

        private fun doRender(display: Display, node: Element) {
            render(node) {
                child(SimulatorApp::class) {
                    attrs {
                        this.networkDisplay = display.forNetwork()
                        this.pinkyDisplay = display.forPinky()
                        this.brainDisplay = display.forBrain()
                        this.visualizerDisplay = display.forVisualizer()
                    }
                }
            }
        }
    }

    interface State : RState {
//        var visualizerRenderMs: Int
//        override var brainCount: Int,
//        override var beat: Int,
//        override var bpm: Float,
//        override var beatConfidence: Float,
//        override var onShowChange: () -> Unit,
//        override var availableShows: List<Show>,
//        override var selectedShow: Show?,
//        override var showFrameMs: Int,
//        override var stats: Pinky.NetworkStats?
    } //, PinkyDisplay

}