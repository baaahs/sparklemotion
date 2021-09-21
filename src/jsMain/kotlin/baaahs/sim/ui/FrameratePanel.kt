package baaahs.sim.ui

import baaahs.sim.ui.SimulatorStyles.section
import baaahs.ui.BComponent
import baaahs.ui.Observable
import baaahs.ui.Observer
import baaahs.ui.unaryPlus
import baaahs.util.Framerate
import kotlinx.css.*
import kotlinx.html.id
import react.Props
import react.RBuilder
import react.RHandler
import react.State
import react.dom.*
import styled.css
import styled.styledTable
import styled.styledTh
import kotlin.math.roundToInt

class FrameratePanel(props: FrameratePanelProps) : BComponent<FrameratePanelProps, State>(props), Observer {
    override fun observing(props: FrameratePanelProps, state: State): List<Observable?> {
        return listOf(
            props.pinkyFramerate,
            props.visualizerFramerate
        )
    }

    override fun RBuilder.render() {
        val pinkyFramerate = props.pinkyFramerate
        val visualizerFramerate = props.visualizerFramerate

        styledTable {
            attrs { id = "framerateView" }
            css { +section }
            css { tableLayout = TableLayout.fixed; width = LinearDimension("100%") }

            tbody {
                tr {
                    styledTh {
                        attrs.colSpan = "2"; css.textAlign = TextAlign.left
                        +"Effective Framerate"
                    }
                    th { +"Elapsed" }
                }

                tr {
                    td { +"Pinky:" }
                    td(+SimulatorStyles.dataWithUnit) { +"${pinkyFramerate.fps}fps" }
                    td(+SimulatorStyles.dataWithUnit) { +"${pinkyFramerate.elapsedMs}ms" }
                }

                tr {
                    td { +"  average:" }
                    td(+SimulatorStyles.dataWithUnit) { +"${pinkyFramerate.averageFps}fps" }
                    td(+SimulatorStyles.dataWithUnit) { +"${pinkyFramerate.averageElapsedMs.roundToInt()}ms" }
                }

                tr {
                    td { +"Visualizer:" }
                    td(+SimulatorStyles.dataWithUnit) { +"${visualizerFramerate.fps}fps" }
                    td(+SimulatorStyles.dataWithUnit) { +"${visualizerFramerate.elapsedMs}ms" }
                }
            }
        }
    }
}

external interface FrameratePanelProps : Props {
    var pinkyFramerate: Framerate
    var visualizerFramerate: Framerate
}

fun RBuilder.frameratePanel(handler: RHandler<FrameratePanelProps>) =
    child(FrameratePanel::class, handler = handler)
