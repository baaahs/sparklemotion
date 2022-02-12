package baaahs.sim.ui

import baaahs.SheepSimulator
import baaahs.sm.brain.sim.BrainSimulator
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.b
import react.dom.div
import react.dom.hr
import styled.css
import styled.styledDiv

private val ConsoleView = xComponent<ConsoleProps>("Console") { props ->
    val simulator = props.simulator
    observe(simulator)
    observe(simulator.visualizer)
    observe(simulator.pinky)
    observe(simulator.fixturesSimulator)

    var selectedBrain by state<BrainSimulator.Facade?> { null }
    val brainSelectionListener by handler() { brainSimulator: BrainSimulator.Facade ->
        selectedBrain = brainSimulator
    }

    div(+SimulatorStyles.console) {
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
                        attrs.brainSelectionListener = brainSelectionListener
                    }
                }
            }
            div {
                selectedBrain?.let { selectedBrain ->
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

external interface ConsoleProps : Props {
    var simulator: SheepSimulator.Facade
}

fun RBuilder.console(handler: RHandler<ConsoleProps>) =
    child(ConsoleView, handler = handler)