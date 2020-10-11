package baaahs.sim.ui

import baaahs.Pinky
import baaahs.ui.*
import baaahs.ui.SimulatorStyles.beatsDiv
import baaahs.util.percent
import external.react_draggable.Draggable
import kotlinx.css.*
import kotlinx.html.id
import kotlinx.html.js.onChangeFunction
import materialui.components.formcontrollabel.formControlLabel
import materialui.components.paper.enums.PaperStyle
import materialui.components.paper.paper
import materialui.components.portal.portal
import materialui.components.switches.switch
import materialui.components.typography.typographyH6
import materialui.icon
import materialui.icons.Icons
import react.*
import react.dom.*
import styled.StyleSheet
import styled.css
import styled.styledDiv
import styled.styledSpan
import kotlin.math.roundToInt

class PinkyPanel(props: PinkyPanelProps) : BComponent<PinkyPanelProps, PinkyPanelState>(props), Observer {
    override fun observing(props: PinkyPanelProps, state: PinkyPanelState): List<Observable?> {
        return listOf(props.pinky, props.pinky.stageManager)
    }

    override fun RBuilder.render() {
        val pinky = props.pinky

        if (state.showGlsl) {
            portal {
                Draggable {
                    val randomStyleForHandle = "PinkyPanelHandle"
                    attrs.handle = ".$randomStyleForHandle"

                    div(+Styles.glslCodeSheet) {
                        div(+Styles.dragHandle and randomStyleForHandle) {
                            icon(Icons.DragIndicator)
                        }

                        paper(Styles.glslCodePaper on PaperStyle.root) {
                            attrs.elevation = 3

                            typographyH6 { +"Generated GLSL" }

                            div(+Styles.glslCodeDiv) {
                                props.pinky.stageManager.currentGlsl?.forEach { (surfaces, glsl) ->
                                    header { +surfaces.name }
                                    pre { +glsl }
                                }
                            }
                        }
                    }
                }
            }
        }

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

            formControlLabel {
                attrs.control {
                    switch {
                        attrs["size"] = "small"
                        attrs.checked = state.showGlsl
                        attrs.onChangeFunction = { state.showGlsl = !state.showGlsl}
                    }
                }
                attrs.label { +"Show GLSL" }
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

object Styles : StyleSheet("sim-pinky", isStatic = true) {
    val glslCodeSheet by css {
        position = Position.fixed
        left = 5.em
        bottom = 5.em
        zIndex = 100
        maxHeight = 50.vh
        maxWidth = 50.em
        display = Display.flex
        flexDirection = FlexDirection.column

        hover {
            descendants(baaahs.app.ui.Styles.dragHandle) {
                opacity = 1
            }
        }
    }

    val dragHandle by css {
        position = Position.absolute
        right = 5.px
        top = 5.px
        zIndex = 1
        cursor = Cursor.move
    }

    val glslCodePaper by css {
        padding(1.em)
        display = Display.flex
        flexDirection = FlexDirection.column
    }

    val glslCodeDiv by css {
        maxHeight = 50.vh
        maxWidth = 50.em
        overflow = Overflow.scroll
    }
}

external interface PinkyPanelProps : RProps {
    var pinky: Pinky.Facade
}

external interface PinkyPanelState : RState {
    var showGlsl: Boolean
}

fun RBuilder.pinkyPanel(handler: RHandler<PinkyPanelProps>): ReactElement =
    child(PinkyPanel::class, handler = handler)
