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
import styled.css
import styled.styledDiv

class BrainIndicator(props: Props) : BComponent<BrainIndicator.Props, BrainIndicator.State>(props), Observer {
    override fun observing(props: Props, state: State): List<Observable?> {
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

    class Props(
        var brain: Brain.Facade,
        var brainSelectionListener: (Brain.Facade) -> Unit
    ) : RProps

    class State : RState

}