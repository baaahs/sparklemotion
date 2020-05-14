package baaahs.sim.ui

import baaahs.ui.BComponent
import baaahs.ui.Observable
import baaahs.ui.Observer
import baaahs.ui.SimulatorStyles.section
import baaahs.util.Framerate
import kotlinx.css.*
import kotlinx.html.id
import react.RBuilder
import react.RProps
import react.RState
import react.dom.tbody
import react.dom.td
import react.dom.th
import react.dom.tr
import styled.css
import styled.styledTable
import styled.styledTh

class FrameratePanel(props: Props) : BComponent<FrameratePanel.Props, FrameratePanel.State>(props), Observer {
    override fun observing(props: Props, state: State): List<Observable?> {
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
                    td { +"${pinkyFramerate.fps}fps" }
                    td { +"${pinkyFramerate.elapsedMs}ms" }
                }

                tr {
                    td { +"  average:" }
                    td { +"${pinkyFramerate.averageFps}fps" }
                    td { +"${pinkyFramerate.averageElapsedMs}ms" }
                }

                tr {
                    td { +"Visualizer:" }
                    td { +"${visualizerFramerate.fps}fps" }
                    td { +"${visualizerFramerate.elapsedMs}ms" }
                }
            }
        }
    }

    class Props(
        var pinkyFramerate: Framerate,
        var visualizerFramerate: Framerate
    ) : RProps

    class State : RState

}