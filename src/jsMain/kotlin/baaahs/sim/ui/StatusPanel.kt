package baaahs.sim.ui

import baaahs.SheepSimulator
import baaahs.ui.asTextNode
import baaahs.ui.unaryPlus
import baaahs.ui.withTChangeEvent
import baaahs.ui.xComponent
import mui.material.FormControlLabel
import mui.material.Switch
import react.Props
import react.RBuilder
import react.RHandler
import react.buildElement
import react.dom.div

val StatusPanelView = xComponent<StatusPanelProps>("StatusPanel") { props ->
    var isConsoleOpen by state { false }
    var isGlslPaletteOpen by state { false }
    val simulator = props.simulator

    val handleIsConsoleOpenChange by eventHandler { isConsoleOpen = !isConsoleOpen }
    val handleIsGlslPaletteOpenChange by eventHandler { isGlslPaletteOpen = !isGlslPaletteOpen }

    div {
        div(+SimulatorStyles.statusPanelToolbar) {
            FormControlLabel {
                attrs.control = buildElement {
                    Switch {
                        attrs.checked = isConsoleOpen
                        attrs.onChange = handleIsConsoleOpenChange.withTChangeEvent()
                    }
                }
                attrs.label = "Open".asTextNode()
            }

            FormControlLabel {
                attrs.control =  buildElement {
                    Switch {
                        attrs.checked = isGlslPaletteOpen
                        attrs.onChange = handleIsGlslPaletteOpenChange.withTChangeEvent()
                    }
                }
                attrs.label = "Show GLSL".asTextNode()
            }
        }

        if (isConsoleOpen) console { attrs.simulator = simulator }
        if (isGlslPaletteOpen) generatedGlslPalette {
            attrs.renderPlanMonitor = simulator.pinky.fixtureManager.renderPlanMonitor
        }
    }
}

external interface StatusPanelProps : Props {
    var simulator: SheepSimulator.Facade
}

fun RBuilder.statusPanel(handler: RHandler<StatusPanelProps>) =
    child(StatusPanelView, handler = handler)