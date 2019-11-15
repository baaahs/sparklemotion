package baaahs.ui.components

import kotlinx.html.id
import kotlinx.html.js.onChangeFunction
import org.w3c.dom.HTMLSelectElement
import react.*
import react.dom.*
import kotlin.math.roundToInt

class PinkySection : RComponent<PinkySection.Props, PinkySection.State>() {

    override fun componentDidMount() {
        props.pinkyDisplay.addListener(this::onPinkyDisplayChange)
        props.visualizerDisplay.addListener(this::onPinkyDisplayChange)
    }

    override fun componentWillUnmount() {
        props.pinkyDisplay.removeListener(this::onPinkyDisplayChange)
        props.visualizerDisplay.removeListener(this::onPinkyDisplayChange)
    }

    fun onPinkyDisplayChange() {
        forceUpdate()
    }

    override fun RBuilder.render() {
        val pinkyDisplay = props.pinkyDisplay
        table("simulatorSection") {
            style(content = "table-layout: fixed; width: 100%;")

            thead {
                tr { th { attrs.colSpan = "2"; style(content = "text-align: left;"); +"Effective frame rate" } }
            }
            tbody {
                val showFrameRenderMs = pinkyDisplay.showFrameMs
                tr { td { +"Show:" }; td { +"${1000 / showFrameRenderMs}fps" }; td { +"${showFrameRenderMs}ms" } }
                val vizRenderMs = props.visualizerDisplay.renderMs
                tr { td { +"Visualizer:" }; td { +"${1000 / vizRenderMs}fps" }; td { +"${vizRenderMs}ms" } }
                tr { td { +"Brains:" }; td { +"tbd" }; td { +"tbd" } }
            }
        }

        div("simulatorSection") {
            b { +"Pinky" }
            div {
                +"Current Show: "
                select("showsDiv") {
                    attrs {
                        val availableShows = pinkyDisplay.availableShows
                        onChangeFunction = {
                            val selectedShow = availableShows[(it.target as HTMLSelectElement).value.toInt()]
                            pinkyDisplay.selectShow(selectedShow)
                        }

                        availableShows.mapIndexed { i, show ->
                            option {
                                +show.name
                                attrs {
                                    key = "$i"; value = "$i"; selected = pinkyDisplay.selectedShow == show
                                }
                            }
                        }
                    }
                }

                br {}
                +"Brains online: ${pinkyDisplay.brainCount}"

                div {
                    attrs.id = "beatsDiv"
                    b { +"Beats: " }
                    +"[confidence: ${(pinkyDisplay.beatConfidence * 100).roundToInt()}%"

                    br{}
                    for (beat in 0..3) {
                        div(if (beat == pinkyDisplay.beat) "selected" else "") { +"${beat + 1}" }
                    }

                    val beatClass = if (pinkyDisplay.beat % 2 == 0) "bpmDisplay-beatOn" else "bpmDisplay-beatOff"
                    span(beatClass) { +"${pinkyDisplay.bpm.roundToInt()}BPM" }
                }

                br {}
                b { +"Data to Brains:" }
                br {}
                span {
                    +(pinkyDisplay.stats?.run { "$bytesSent bytes / $packetsSent packets per frame" } ?: "?")
                }
            }
        }
    }

    interface Props : RProps {
        var pinkyDisplay: RPinkyDisplay
        var visualizerDisplay: RVisualizerDisplay
    }

    interface State : RState

}

fun RBuilder.pinkySection(handler: PinkySection.Props.() -> Unit): ReactElement {
    return child(PinkySection::class) {
        this.attrs(handler)
    }
}
