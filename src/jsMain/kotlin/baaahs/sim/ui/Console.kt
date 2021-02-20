package baaahs.sim.ui

import baaahs.Brain
import baaahs.SheepSimulator
import baaahs.app.ui.appContext
import baaahs.ui.BComponent
import baaahs.ui.Observable
import baaahs.ui.Observer
import react.*
import react.dom.b
import react.dom.div
import react.dom.hr
import styled.css
import styled.styledDiv

class Console(props: ConsoleProps) : BComponent<ConsoleProps, ConsoleState>(props), Observer {
    override fun observing(props: ConsoleProps, state: ConsoleState): List<Observable?> {
        return listOf(props.simulator, props.simulator.visualizer, props.simulator.pinky)
    }

    private fun brainSelectionListener(brain: Brain.Facade) =
        setState { selectedBrain = brain }

    override fun RBuilder.render() {
        val appContext = useContext(appContext)
        val styles = appContext.allStyles.simUi
        val simulator = props.simulator

        styledDiv {
            css { +styles.console }

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
                css { +styles.section }
                b { +"Brains:" }
                div {
                    simulator.brains.forEach { brain ->
                         brainIndicator {
                             attrs.brain = brain
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
                    simulator.visualizer.selectedSurface?.let {
                        +"Selected: ${it.name}"
                    }
                }
            }
        }
    }
}

external interface ConsoleProps : RProps {
    var simulator: SheepSimulator.Facade
}

external interface ConsoleState : RState {
    var selectedBrain: Brain.Facade?
}