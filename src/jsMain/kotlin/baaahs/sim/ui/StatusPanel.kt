package baaahs.sim.ui

import baaahs.SheepSimulator
import baaahs.ui.asTextNode
import baaahs.ui.diagnostics.patchDiagnostics
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

    observe(simulator)
    observe(simulator.pinky)

    val handleIsConsoleOpenChange by eventHandler { isConsoleOpen = !isConsoleOpen }
    val handleIsGlslPaletteOpenChange by eventHandler { isGlslPaletteOpen = !isGlslPaletteOpen }
    val handlePauseChange by switchEventHandler { _, checked ->
        props.simulator.pinky.isPaused = checked
    }

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

            FormControlLabel {
                attrs.control =  buildElement {
                    Switch {
                        attrs.checked = props.simulator.pinky.isPaused
                        attrs.onChange = handlePauseChange
                    }
                }
                attrs.label = "Paused".asTextNode()
            }
        }

        if (isConsoleOpen) console { attrs.simulator = simulator }
        if (isGlslPaletteOpen) patchDiagnostics {
            attrs.renderPlanMonitor = simulator.pinky.fixtureManager.renderPlanMonitor
            attrs.onClose = handleIsGlslPaletteOpenChange as () -> Unit
        }
    }
}

external interface StatusPanelProps : Props {
    var simulator: SheepSimulator.Facade
}

fun RBuilder.statusPanel(handler: RHandler<StatusPanelProps>) =
    child(StatusPanelView, handler = handler)