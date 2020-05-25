package baaahs.sim.ui

import baaahs.Pinky
import baaahs.ui.BComponent
import baaahs.ui.Observable
import baaahs.ui.Observer
import baaahs.ui.SimulatorStyles
import baaahs.ui.SimulatorStyles.beatsDiv
import baaahs.ui.SimulatorStyles.showsDiv
import baaahs.util.percent
import kotlinx.html.id
import kotlinx.html.js.onChangeFunction
import org.w3c.dom.HTMLSelectElement
import react.RBuilder
import react.RProps
import react.RState
import react.ReactElement
import react.dom.*
import styled.css
import styled.styledDiv
import styled.styledSelect
import styled.styledSpan
import kotlin.math.roundToInt

class PinkyPanel(props: PinkyPanelProps) : BComponent<PinkyPanelProps, RState>(props), Observer {
    override fun observing(props: PinkyPanelProps, state: RState): List<Observable?> {
        return listOf(props.pinky)
    }

    override fun RBuilder.render() {
        val pinky = props.pinky
        val shows = pinky.shows

        styledDiv {
            css { +SimulatorStyles.section }
            b { +"Pinky:" }
            div {
                attrs.id = "pinkyView" // TODO: kill
                +"Current Show: "

                styledSelect {
                    css { +showsDiv }
                    attrs.onChangeFunction = { event ->
                        println("event.target = ${event.target}")
                        val select = event.target as HTMLSelectElement
                        val nextShow = shows[select.value.toInt()]
                        pinky.selectedShow = nextShow
                    }

                    val selectedShow = pinky.selectedShow
                    shows.forEachIndexed { index, show ->
                        option {
                            +show.name
                            attrs.key = index.toString()
                            attrs.value = index.toString()
                            if (selectedShow.name == show.name) {
                                attrs.selected = true
                            }
                        }
                    }
                }
            }

            br { }
            +"Brains online: "

            span {
                +"${pinky.brains.size}"
            }

            styledDiv {
                css { +beatsDiv }

                val beatData = pinky.beatData
                val beat = beatData.beatWithinMeasure(pinky.clock).toInt()
                val bpm = beatData.bpm
                val beatConfidence = beatData.confidence

                b { +"Beats: " }
                span { // beatConfidenceElement
                    +"[confidence: ${beatConfidence.percent()}]"
                }
                br { }
                styledDiv { +"1"; if (beat == 0) css { +"selected" } }
                styledDiv { +"2"; if (beat == 1) css { +"selected" } }
                styledDiv { +"3"; if (beat == 2) css { +"selected" } }
                styledDiv { +"4"; if (beat == 3) css { +"selected" } }
                styledSpan {
                    css {
                        +"bpmDisplay"
                        if (beat % 1 == 0) +"bpmDisplay-beatOn" else +"bpmDisplay-beatOff"
                    }
                    +"${bpm.roundToInt()} BPM"
                }

                br { }
                b { +"Data to Brains:" }
                br { }

                span {
                    val stats = pinky.networkStats
                    +"${stats.bytesSent} bytes / ${stats.packetsSent} packets per frame"
                }
            }
        }

        styledDiv {
            css { +"controls " }

            styledDiv {
                css { +"pixelCount " }
                +"Pixel count:"
                span {
                    attrs.id = "visualizerPixelCount"
                    +"n/a"
                }

            }
        }
    }
}

external interface PinkyPanelProps : RProps {
    var pinky: Pinky.Facade
}

fun RBuilder.pinkyPanel(handler: PinkyPanelProps.() -> Unit): ReactElement =
    child(PinkyPanel::class) { this.attrs(handler) }
