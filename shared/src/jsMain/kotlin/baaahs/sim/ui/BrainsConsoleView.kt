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
    val simulator = props.simulator
    observe(simulator)
    observe(simulator.fixturesSimulator)

    var selectedBrain by state<BrainSimulator.Facade?> { null }
    val brainSelectionListener by handler { brainSimulator: BrainSimulator.Facade ->
        selectedBrain = brainSimulator
    }

    val handleResetAll by mouseEventHandler() {
        simulator.fixturesSimulator.brains.forEach { it.reset() }
    }

    styledDiv {
        css { +SimulatorStyles.consoleContainer }
        b { +"Brains:" }
        div {
            simulator.fixturesSimulator.brains.forEach { brain ->
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
    }
}

external interface BrainsConsoleProps : Props {
    var simulator: SheepSimulator.Facade
}

fun RBuilder.brainsConsole(handler: RHandler<BrainsConsoleProps>) =
    child(BrainsConsoleView, handler = handler)