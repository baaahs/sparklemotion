package baaahs.sim.ui

import baaahs.SheepSimulator
import baaahs.sim.ui.SimulatorStyles.console
import baaahs.sm.brain.sim.BrainSimulator
import baaahs.ui.BComponent
import baaahs.ui.Observable
import baaahs.ui.Observer
import react.*
import react.dom.b
import react.dom.div
import react.dom.hr
import styled.css
import styled.styledDiv

class ConsoleView(props: ConsoleProps) : BComponent<ConsoleProps, ConsoleState>(props), Observer {
    override fun observing(props: ConsoleProps, state: ConsoleState): List<Observable?> {
        return listOf(
            props.simulator,
            props.simulator.visualizer,
            props.simulator.pinky,
            props.simulator.fixturesSimulator
        )
    }

    private fun brainSelectionListener(brainSimulator: BrainSimulator.Facade) =
        setState { selectedBrain = brainSimulator }

    override fun RBuilder.render() {
        val simulator = props.simulator

        styledDiv {
            css { +console }

            networkPanel {
                attrs.network = simulator.network
            }

            frameratePanel {
                attrs.pinkyFramerate = simulator.pinky.framerate
                attrs.visualizerFramerate = simulator.visualizer.framerate
            }

            pinkyPanel {
                attrs.pinky = simulator.pinky
            }

            styledDiv {
                css { +SimulatorStyles.section }
                b { +"Brains:" }
                div {
                    simulator.fixturesSimulator.brains.forEach { brain ->
                         brainIndicator {
                             attrs.brainSimulator = brain
                             attrs.brainSelectionListener = ::brainSelectionListener
                         }
                    }
                }
                div {
                    val selectedBrain = state.selectedBrain
                    if (selectedBrain != null) {
                        hr {}
                        b { +"Brain ${selectedBrain.id}" }
                        div { +"Model Element: ${selectedBrain.modelElementName ?: "unknown"}" }
                    }
                }

                div {
                    simulator.visualizer.selectedEntity?.let {
                        +"Selected: ${it.title}"
                    }
                }
            }
        }
    }
}

external interface ConsoleProps : Props {
    var simulator: SheepSimulator.Facade
}

external interface ConsoleState : State {
    var selectedBrain: BrainSimulator.Facade?
}

fun RBuilder.console(handler: RHandler<ConsoleProps>) =
    child(ConsoleView::class, handler = handler)