package baaahs.sim.ui

import baaahs.SheepSimulator
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import baaahs.visualizer.ui.visualizerPanel
import kotlinx.html.js.onChangeFunction
import materialui.components.formcontrollabel.formControlLabel
import materialui.components.switches.switch
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.div

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
        div(+SimulatorStyles.vizToolbar) {
            formControlLabel {
                attrs.control {
                    switch {
                        attrs.checked = rotate
                        attrs.onChangeFunction = onRotateChange
                    }
                }
                attrs.label { +"Rotate" }
            }
        }
    }

    visualizerPanel {
        attrs.visualizer = visualizer
    }
}

external interface ModelSimulationProps : Props {
    var simulator: SheepSimulator.Facade
}

fun RBuilder.modelSimulation(handler: RHandler<ModelSimulationProps>) =
    child(ModelSimulationView, handler = handler)