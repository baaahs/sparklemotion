package baaahs.sim.ui

import baaahs.Brain
import baaahs.Brain.State.*
import baaahs.ui.BComponent
import baaahs.ui.Observable
import baaahs.ui.Observer
import baaahs.ui.SimulatorStyles.brainIndicator
import kotlinx.html.js.onClickFunction
import kotlinx.html.js.onMouseOverFunction
import react.RBuilder
import react.RProps
import react.RState
import react.ReactElement
import styled.css
import styled.styledDiv

class BrainIndicator(props: BrainIndicatorProps) : BComponent<BrainIndicatorProps, RState>(props), Observer {
    override fun observing(props: BrainIndicatorProps, state: RState): List<Observable?> {
        return listOf(props.brain)
    }

    override fun RBuilder.render() {
        styledDiv {
            css {
                +brainIndicator
                when (props.brain.state) {
                    Unknown -> +"unknown"
                    Link -> +"link"
                    Online -> +"online"
                }
            }
            attrs.onClickFunction = { props.brain.reset() }
            attrs.onMouseOverFunction = {
                props.brainSelectionListener(props.brain)
            }
        }
    }
}

external interface BrainIndicatorProps : RProps {
    var brain: Brain.Facade
    var brainSelectionListener: (Brain.Facade) -> Unit
}

fun RBuilder.brainIndicator(handler: BrainIndicatorProps.() -> Unit): ReactElement =
    child(BrainIndicator::class) { this.attrs(handler) }