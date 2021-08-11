package baaahs.sim.ui

import baaahs.Pinky
import baaahs.ui.*
import kotlinx.css.*
import react.*
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
                    +pinky.fixtureManager.pixelCount.toString()
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
}

fun RBuilder.pinkyPanel(handler: RHandler<PinkyPanelProps>): ReactElement =
    child(PinkyPanel::class, handler = handler)
