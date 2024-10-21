package baaahs.sim.ui

import baaahs.Pinky
import baaahs.ui.BComponent
import baaahs.ui.Observable
import baaahs.ui.Observer
import baaahs.ui.descendants
import kotlinx.css.*
import react.Props
import react.RBuilder
import react.RHandler
import react.State
import react.dom.*
import styled.StyleSheet
import styled.css
import styled.styledDiv

class PinkyPanel(props: PinkyPanelProps) : BComponent<PinkyPanelProps, PinkyPanelState>(props), Observer {
    override fun observing(props: PinkyPanelProps, state: PinkyPanelState): List<Observable?> {
        return listOf(props.pinky, props.pinky.stageManager, props.pinky.fixtureManager)
    }

    override fun RBuilder.render() {
        val pinky = props.pinky

        if (state.showGlsl) {
        }

        val currentShowTitle = pinky.stageManager.currentShow?.title
        styledDiv {
            css { +SimulatorStyles.section }
            b { +"Pinky:" }
            div {
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

            div {
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
                +"Fixture count:"
                span {
                    +pinky.fixtureManager.fixtureCount.toString()
                }
            }

            styledDiv {
                +"Pixel count:"
                span {
                    +pinky.fixtureManager.componentCount.toString()
                }
            }
        }
    }
}

object Styles : StyleSheet("sim-pinky", isStatic = true) {
    val glslCodeSheet by css {
        display = Display.flex
        flexDirection = FlexDirection.column

        hover {
            descendants(baaahs.app.ui.Styles, baaahs.app.ui.Styles::dragHandle) {
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
        padding = Padding(1.em)
        display = Display.flex
        flexDirection = FlexDirection.column
    }

    val contentDiv by css {
        overflow = Overflow.scroll
    }

    val box by css {
    }
}

external interface PinkyPanelProps : Props {
    var pinky: Pinky.Facade
}

external interface PinkyPanelState : State {
    var showGlsl: Boolean
}

fun RBuilder.pinkyPanel(handler: RHandler<PinkyPanelProps>) =
    child(PinkyPanel::class, handler = handler)
