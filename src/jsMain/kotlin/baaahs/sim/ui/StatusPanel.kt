package baaahs.sim.ui

import baaahs.SheepSimulator
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import kotlinx.html.js.onChangeFunction
import materialui.components.formcontrollabel.formControlLabel
import materialui.components.switches.switch
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.div

val StatusPanelView = xComponent<StatusPanelProps>("StatusPanel") { props ->
    var isConsoleOpen by state { false }
    var isGlslPaletteOpen by state { false }
    val simulator = props.simulator

    val handleIsConsoleOpenChange by eventHandler { isConsoleOpen = !isConsoleOpen }
    val handleIsGlslPaletteOpenChange by eventHandler { isGlslPaletteOpen = !isGlslPaletteOpen }

    div {
        div(+SimulatorStyles.statusPanelToolbar) {
            formControlLabel {
                attrs.control {
                    switch {
                        attrs.checked = isConsoleOpen
                        attrs.onChangeFunction = handleIsConsoleOpenChange
                    }
                }
                attrs.label { +"Open" }
            }

            formControlLabel {
                attrs.control {
                    switch {
                        attrs.checked = isGlslPaletteOpen
                        attrs.onChangeFunction = handleIsGlslPaletteOpenChange
                    }
                }
                attrs.label { +"Show GLSL" }
            }
        }

        if (isConsoleOpen) console { attrs.simulator = simulator }
        if (isGlslPaletteOpen) generatedGlslPalette { attrs.pinky = simulator.pinky }
    }
}

external interface StatusPanelProps : Props {
    var simulator: SheepSimulator.Facade
}

fun RBuilder.statusPanel(handler: RHandler<StatusPanelProps>) =
    child(StatusPanelView, handler = handler)