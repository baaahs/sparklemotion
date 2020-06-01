package baaahs.sim.ui

import baaahs.Brain
import baaahs.SheepSimulator
import baaahs.Show
import baaahs.ui.BComponent
import baaahs.ui.Observable
import baaahs.ui.Observer
import baaahs.ui.SimulatorStyles
import baaahs.ui.SimulatorStyles.console
import react.RBuilder
import react.RProps
import react.RState
import react.dom.b
import react.dom.div
import react.dom.hr
import react.setState
import styled.css
import styled.styledDiv

class Console(props: ConsoleProps) : BComponent<ConsoleProps, ConsoleState>(props), Observer {
    override fun observing(props: ConsoleProps, state: ConsoleState): List<Observable?> {
        return listOf(props.simulator)
    }

    private fun brainSelectionListener(brain: Brain.Facade) =
        setState { selectedBrain = brain }

    override fun RBuilder.render() {
        val simulator = props.simulator
        if (simulator == null) {
            +"Loading..."
            return
        }

        styledDiv {
            css { +console }

            networkPanel {
                network = simulator.network
            }

            frameratePanel {
                pinkyFramerate = simulator.pinky.framerate
                visualizerFramerate = simulator.visualizer.framerate
            }

            pinkyPanel {
                pinky = simulator.pinky
            }

            styledDiv {
                css { +SimulatorStyles.section }
                b { +"Brains:" }
                div {
                    simulator.brains.forEach { brain ->
                         brainIndicator {
                             this.brain = brain
                             brainSelectionListener = ::brainSelectionListener
                         }
                    }
                }
                div {
                    val selectedBrain = state.selectedBrain
                    if (selectedBrain != null) {
                        hr {}
                        b { +"Brain ${selectedBrain.id}" }
                        div { +"Surface: ${selectedBrain.surface.describe()}" }
                    }
                }

                div {
                    simulator.visualizer.selectedSurface?.let {
                        +"Selected: ${it.name}"
                    }
                }
            }
        }
    }
}

external interface ConsoleProps : RProps {
    var simulator: SheepSimulator.Facade?
}

external interface ConsoleState : RState {
    var selectedShow: Show
    var selectedBrain: Brain.Facade?
}