package baaahs.sim.ui

import baaahs.Pinky
import baaahs.ui.BComponent
import baaahs.ui.Observable
import baaahs.ui.Observer
import baaahs.ui.SimulatorStyles
import baaahs.ui.SimulatorStyles.beatsDiv
import baaahs.util.percent
import kotlinx.html.id
import react.*
import react.dom.*
import styled.css
import styled.styledDiv
import styled.styledSpan
import kotlin.math.roundToInt

class PinkyPanel(props: PinkyPanelProps) : BComponent<PinkyPanelProps, RState>(props), Observer {
    override fun observing(props: PinkyPanelProps, state: RState): List<Observable?> {
        return listOf(props.pinky, props.pinky.stageManager)
    }

    override fun RBuilder.render() {
        val pinky = props.pinky
        val currentShowTitle = pinky.stageManager.currentShow?.title
        styledDiv {
            css { +SimulatorStyles.section }
            b { +"Pinky:" }
            div {
                attrs.id = "pinkyView" // TODO: kill
                +"Current Show: "
                b {
                    if (currentShowTitle == null) {
                        i { +"none" }
                    } else {
                        +currentShowTitle
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
                    +pinky.pixelCount.toString()
                }

            }
        }
    }
}

external interface PinkyPanelProps : RProps {
    var pinky: Pinky.Facade
}

fun RBuilder.pinkyPanel(handler: RHandler<PinkyPanelProps>): ReactElement =
    child(PinkyPanel::class, handler = handler)
