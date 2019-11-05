package baaahs.ui.components

import baaahs.*
import kotlinx.html.InputType
import kotlinx.html.id
import kotlinx.html.js.onClickFunction
import kotlinx.html.js.onMouseOverFunction
import kotlinx.html.title
import org.w3c.dom.Element
import react.*
import react.dom.*

class SimulatorApp : RComponent<SimulatorApp.Props, SimulatorApp.State>() {
    override fun componentDidMount() {
        props.pinkyDisplay.brains.addListener(this::onChange)
    }

    override fun componentWillUnmount() {
        props.pinkyDisplay.brains.removeListener(this::onChange)
    }

    fun onChange() = forceUpdate()

    override fun RBuilder.render() {
        div { attrs.id = "sheepView" }

        div {
            attrs.id = "simulatorView"

            h3 { +"BAAAHS Simulator FUCK YEAH" }

            div("simulatorSection") { attrs.id = "launcher" }

            networkSection { networkDisplay = props.networkDisplay }

            pinkySection {
                pinkyDisplay = props.pinkyDisplay
                visualizerDisplay = props.visualizerDisplay
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
                div {
                    props.pinkyDisplay.brains.map { (brainId, brainUiModel) ->
                        div("brain-box brain-offline") {
                            attrs {
                                title = brainId.toString()
                                onMouseOverFunction = {
                                    setState { selectedBrain = brainUiModel }
                                }
                            }
                        }
                    }
                }
                div { // brain details
                    state.selectedBrain?.run {
                        hr {}
                        b { +"Brain $brainId"}
                        div { +"Surface: ${surface?.describe()}"}
                        div {
                            button { +"Reset"; attrs { onClickFunction = { reset() } } }
                        }
                    }
                }
            }
        }
    }

    interface Props : RProps {
        var networkDisplay: RNetworkDisplay
        var pinkyDisplay: RPinkyDisplay
        var brainDisplay: RBrainDisplay
        var visualizerDisplay: RVisualizerDisplay
    }

    companion object {
        @JsName("render")
        fun render(node: Element): Display {
            val display = object : Display {
                private val networkDisplay = RNetworkDisplay()
                private val pinkyDisplay = RPinkyDisplay()
                private val brainDisplay = RBrainDisplay()
                private val visualizerDisplay = RVisualizerDisplay()

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
                        this.networkDisplay = display.forNetwork() as RNetworkDisplay
                        this.pinkyDisplay = display.forPinky() as RPinkyDisplay
                        this.brainDisplay = display.forBrain() as RBrainDisplay
                        this.visualizerDisplay = display.forVisualizer() as RVisualizerDisplay
                    }
                }
            }
        }
    }

    interface State : RState {
        var selectedBrain: BrainUiModel?
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