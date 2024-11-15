package baaahs.sim.ui

import baaahs.SheepSimulator
import baaahs.sm.brain.sim.BrainSimulator
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import mui.material.Button
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.b
import react.dom.div
import react.dom.hr
import styled.css
import styled.styledDiv

private val BrainsConsoleView = xComponent<BrainsConsoleProps>("BrainsConsole") { props ->
    val simulator = observe(props.simulator)
    val brainSimulators = observe(simulator.brainSimulatorManager)
        .brainSimulators

    var selectedBrain by state<BrainSimulator.Facade?> { null }
    val brainSelectionListener by handler { brainSimulator: BrainSimulator.Facade ->
        selectedBrain = brainSimulator
    }

    val handleResetAll by mouseEventHandler(brainSimulators) {
        brainSimulators.forEach { it.reset() }
    }

    val handleNewBrain by mouseEventHandler(simulator) {
        simulator.newBrain()
    }

    styledDiv {
        css { +SimulatorStyles.consoleContainer }
        b { +"Brains:" }
        div {
            brainSimulators.forEach { brain ->
                brainIndicator {
                    attrs.brainSimulator = brain
                    attrs.onSelect = brainSelectionListener
                }
            }
        }

        div(+SimulatorStyles.selection) {
            selectedBrain?.let { selectedBrain ->
                hr {}
                b { +"Brain: ${selectedBrain.id}" }
                div { +"State: ${selectedBrain.state.name}" }
                div { +"Link: ${selectedBrain.link?.myAddress?.asString() ?: "None"}" }
                div { +"Entity: ${selectedBrain.modelEntityName ?: "Unknown"}" }
                Button {
                    attrs.onClick = handleResetAll
                    +"Reset All"
                }
            }

            simulator.visualizer.selectedEntity?.let {
                +"Selected: ${it.title}"
            }
        }

        Button {
            attrs.onClick = handleNewBrain
            +"New Brain"
        }
    }
}

external interface BrainsConsoleProps : Props {
    var simulator: SheepSimulator.Facade
}

fun RBuilder.brainsConsole(handler: RHandler<BrainsConsoleProps>) =
    child(BrainsConsoleView, handler = handler)