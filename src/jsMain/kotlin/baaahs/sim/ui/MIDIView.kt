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
import mui.material.Size
import mui.material.Switch
import react.*
import react.dom.button
import react.dom.div
import react.dom.header
import react.dom.onClick

val MIDIView = xComponent<MIDIViewProps>("MIDIView") { props ->
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

    observe(simulator)
    observe(simulator.pinky)

    val handleIsConsoleOpenChange by eventHandler { isConsoleOpen = !isConsoleOpen }
    val handleIsGlslPaletteOpenChange by eventHandler { isGlslPaletteOpen = !isGlslPaletteOpen }
    val handlePauseChange by switchEventHandler { _, checked ->
        props.simulator.pinky.isPaused = checked
    }

    div(+SimulatorStyles.statusPanel) {
        header { +"Brain" }

        div(+SimulatorStyles.statusPanelToolbar) {
            FormControlLabel {
                attrs.control =  buildElement {
                    Switch {
                        attrs.size = Size.small
                        attrs.checked = isGlslPaletteOpen
                        attrs.onChange = handleIsGlslPaletteOpenChange.withTChangeEvent()
                    }
                }
                attrs.label = "Diagntics".asTextNode()
            }

            FormControlLabel {
                attrs.control =  buildElement {
                    Switch {
                        attrs.size = Size.small
                        attrs.checked = props.simulator.pinky.isPaused
                        attrs.onChange = handlePauseChange
                    }
                }
                attrs.label = "Pase".asTextNode()
            }

            FormControlLabel {
                attrs.control = buildElement {
                    Switch {
                        attrs.size = Size.small
                        attrs.checked = isConsoleOpen
                        attrs.onChange = handleIsConsoleOpenChange.withTChangeEvent()
                    }
                }
                attrs.label = "Console".asTextNode()
            }
        }

        div(+SimulatorStyles.consoleContainer) {
            if (isConsoleOpen) console { attrs.simulator = simulator }
        }

        if (props.simulator.launchItems.isNotEmpty()) {
            div(+SimulatorStyles.launchButtonsContainer) {
                header { +"Launch:" }
                props.simulator.launchItems.forEach { launchItem ->
                    button {
                        attrs.onClick = { launchItem.onLaunch() }
                        +launchItem.title
                    }
                }
            }
        }

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

external interface MIDIViewProps : Props {
    var simulator: SheepSimulator.Facade
}
