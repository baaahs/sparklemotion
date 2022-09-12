package baaahs.sim.ui

import baaahs.SheepSimulator
import baaahs.app.ui.AllStyles
import baaahs.app.ui.AppContext
import baaahs.app.ui.appContext
import baaahs.ui.asTextNode
import baaahs.ui.diagnostics.patchDiagnostics
import baaahs.ui.unaryPlus
import baaahs.ui.withTChangeEvent
import baaahs.ui.xComponent
import kotlinx.js.jso
import mui.material.FormControlLabel
import mui.material.Switch
import react.*
import react.dom.div

val StatusPanelView = xComponent<StatusPanelProps>("StatusPanel") { props ->
    var isConsoleOpen by state { false }
    var isGlslPaletteOpen by state { false }
    val simulator = props.simulator
    val simulatorContext = useContext(simulatorContext)
    val stubAppContext = memo(simulatorContext) {
        val allStyles = AllStyles(simulatorContext.styles.theme)
        jso<AppContext> {
            this.allStyles = allStyles
        }
    }

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
        if (isGlslPaletteOpen) {
            appContext.Provider {
                attrs.value = stubAppContext

                patchDiagnostics {
                    attrs.renderPlanMonitor = simulator.pinky.fixtureManager.renderPlanMonitor
                    attrs.onClose = handleIsGlslPaletteOpenChange as () -> Unit
                }
            }
        }
    }
}

external interface StatusPanelProps : Props {
    var simulator: SheepSimulator.Facade
}

fun RBuilder.statusPanel(handler: RHandler<StatusPanelProps>) =
    child(StatusPanelView, handler = handler)