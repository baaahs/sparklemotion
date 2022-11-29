package baaahs.sim.ui

import baaahs.SheepSimulator
import baaahs.ui.unaryPlus
import baaahs.ui.withTChangeEvent
import baaahs.ui.xComponent
import baaahs.visualizer.ui.visualizerPanel
import mui.material.FormControlLabel
import mui.material.Size
import mui.material.Switch
import react.*
import react.dom.div
import react.dom.header

val ModelSimulationView = xComponent<ModelSimulationProps>("ModelSimulation") { props ->
    val visualizer = props.simulator.visualizer
    var rotate by state { visualizer.rotate }

    onChange("rotate sync", rotate) {
        visualizer.rotate = rotate
    }

    val onRotateChange by eventHandler {
        rotate = !rotate
    }

    div(+SimulatorStyles.modelSimulation) {
        header { +"Simulation" }

        visualizerPanel {
            attrs.visualizer = visualizer

            div(+SimulatorStyles.vizToolbar) {
                FormControlLabel {
                    attrs.control = Switch.create {
                        size = Size.small
                        checked = rotate
                        onChange = onRotateChange.withTChangeEvent()
                    }
                    attrs.label = buildElement { +"Rotate" }
                }
            }
        }
    }
}

external interface ModelSimulationProps : Props {
    var simulator: SheepSimulator.Facade
}

fun RBuilder.modelSimulation(handler: RHandler<ModelSimulationProps>) =
    child(ModelSimulationView, handler = handler)