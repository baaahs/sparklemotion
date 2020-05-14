package baaahs.sim.ui

import baaahs.Brain
import baaahs.SheepSimulator
import baaahs.Show
import baaahs.ui.*
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

class Console(props: Props) : BComponent<Console.Props, Console.State>(props), Observer {
    override fun observing(props: Props, state: State): List<Observable?> {
        return listOf(props.simulator)
    }

    private fun brainSelectionListener(brain: Brain.Facade) =
        setState { selectedBrain = brain}

    override fun RBuilder.render() {
        val simulator = props.simulator
        if (simulator == null) {
            +"Loading..."
            return
        }

        styledDiv {
            css { +console }

            add<NetworkPanel, NetworkPanel.Props>(
                NetworkPanel.Props(network = simulator.network)
            )

            add<FrameratePanel, FrameratePanel.Props>(
                FrameratePanel.Props(
                    pinkyFramerate = simulator.pinky.framerate,
                    visualizerFramerate = simulator.visualizer.framerate
                )
            )

            add<PinkyPanel, PinkyPanel.Props>(
                PinkyPanel.Props(pinky = simulator.pinky)
            )

            styledDiv {
                css { +SimulatorStyles.section }
                b { +"Brains:" }
                div {
                    simulator.brains.forEach { brain ->
                        add<BrainIndicator, BrainIndicator.Props>(
                            BrainIndicator.Props(
                                brain = brain,
                                brainSelectionListener = ::brainSelectionListener
                            )
                        )
                    }
                }
                div {
                    val selectedBrain = state.selectedBrain
                    if (selectedBrain != null) {
                        hr {}
                        b { +"Brain ${selectedBrain.id}"}
                        div { +"Surface: ${selectedBrain.surface.describe()}"}
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

    class Props(
        var simulator: SheepSimulator.Facade?
    ) : RProps

    class State(
        var selectedShow: Show,
        var selectedBrain: Brain.Facade? = null
    ) : RState

}