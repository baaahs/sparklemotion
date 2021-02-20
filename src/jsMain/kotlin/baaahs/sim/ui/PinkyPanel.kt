package baaahs.sim.ui

import baaahs.Pinky
import baaahs.app.ui.appContext
import baaahs.ui.BComponent
import baaahs.ui.Observable
import baaahs.ui.Observer
import react.*
import react.dom.*
import styled.css
import styled.styledDiv

class PinkyPanel(props: PinkyPanelProps) : BComponent<PinkyPanelProps, PinkyPanelState>(props), Observer {
    override fun observing(props: PinkyPanelProps, state: PinkyPanelState): List<Observable?> {
        return listOf(props.pinky, props.pinky.stageManager)
    }

    override fun RBuilder.render() {
        val appContext = useContext(appContext)
        val styles = appContext.allStyles.simUi
        val pinky = props.pinky

        if (state.showGlsl) {
        }

        val currentShowTitle = pinky.stageManager.currentShow?.title
        styledDiv {
            css { +styles.section }
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

external interface PinkyPanelState : RState {
    var showGlsl: Boolean
}

fun RBuilder.pinkyPanel(handler: RHandler<PinkyPanelProps>): ReactElement =
    child(PinkyPanel::class, handler = handler)
