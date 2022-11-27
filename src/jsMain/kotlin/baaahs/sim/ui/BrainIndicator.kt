package baaahs.sim.ui

import baaahs.sim.ui.SimulatorStyles.brainIndicator
import baaahs.sm.brain.sim.BrainSimulator
import baaahs.sm.brain.sim.BrainSimulator.State.*
import baaahs.ui.BComponent
import baaahs.ui.Observable
import baaahs.ui.Observer
import kotlinx.html.js.onMouseOverFunction
import react.Props
import react.RBuilder
import react.RHandler
import react.State
import react.dom.onClick
import styled.css
import styled.styledDiv

class BrainIndicator(props: BrainIndicatorProps) : BComponent<BrainIndicatorProps, State>(props), Observer {
    override fun observing(props: BrainIndicatorProps, state: State): List<Observable?> {
        return listOf(props.brainSimulator)
    }

    override fun RBuilder.render() {
        styledDiv {
            css {
                +brainIndicator
                when (props.brainSimulator.state) {
                    Unknown -> +"unknown"
                    Link -> +"link"
                    Online -> +"online"
                }
            }
            attrs.onClick = { props.brainSimulator.reset() }
            attrs.onMouseOverFunction = {
                props.brainSelectionListener(props.brainSimulator)
            }
        }
    }
}

external interface BrainIndicatorProps : Props {
    var brainSimulator: BrainSimulator.Facade
    var brainSelectionListener: (BrainSimulator.Facade) -> Unit
}

fun RBuilder.brainIndicator(handler: RHandler<BrainIndicatorProps>) =
    child(BrainIndicator::class, handler = handler)