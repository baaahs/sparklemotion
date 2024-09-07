package baaahs.sim.ui

import baaahs.SheepSimulator
import baaahs.app.ui.AllStyles
import baaahs.app.ui.AppContext
import baaahs.app.ui.appContext
import baaahs.ui.*
import baaahs.ui.diagnostics.patchDiagnostics
import js.objects.jso
import mui.material.Button
import mui.material.FormControlLabel
import mui.material.Size
import mui.material.Switch
import react.*
import react.dom.div

private val PinkyConsoleView = xComponent<PinkyConsoleProps>("PinkyConsole") { props ->
    val simulator = props.simulator
    observe(simulator)
    observe(simulator.visualizer)
    observe(simulator.pinky)
    observe(simulator.fixturesSimulator)

    val simulatorContext = useContext(simulatorContext)
    val stubAppContext = memo(simulatorContext) {
        val allStyles = AllStyles(simulatorContext.styles.theme)
        jso<AppContext> {
            this.allStyles = allStyles
        }
    }

    var isShown by state { false }
    val handleShowConsole by mouseEventHandler { isShown = true }

    var isGlslPaletteOpen by state { false }
    val handleIsGlslPaletteOpenChange by handler { isGlslPaletteOpen = !isGlslPaletteOpen }

    val handlePauseChange by switchEventHandler { _, checked ->
        props.simulator.pinky.isPaused = checked
    }

    div(+SimulatorStyles.consoleContainer) {
        if (isShown) {
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
        } else {
            Button {
                attrs.classes = jso { this.root = -SimulatorStyles.showPinkyConsoleButton }
                attrs.onClick = handleShowConsole
                +"Show Pinky Console"
            }
        }
    }

    div(+SimulatorStyles.statusPanelToolbar) {
        FormControlLabel {
            attrs.control =  buildElement {
                Switch {
                    attrs.size = Size.small
                    attrs.checked = isGlslPaletteOpen
                    attrs.onChange = handleIsGlslPaletteOpenChange.withTChangeEvent()
                }
            }
            attrs.label = "Diagnostics".asTextNode()
        }

        FormControlLabel {
            attrs.control =  buildElement {
                Switch {
                    attrs.size = Size.small
                    attrs.checked = props.simulator.pinky.isPaused
                    attrs.onChange = handlePauseChange
                }
            }
            attrs.label = "Pause".asTextNode()
        }
    }

    if (isGlslPaletteOpen) {
        appContext.Provider {
            attrs.value = stubAppContext

            patchDiagnostics {
                attrs.renderPlanMonitor = simulator.pinky.fixtureManager.renderPlanMonitor
                attrs.onClose = handleIsGlslPaletteOpenChange
            }
        }
    }
}

enum class ConsoleTabs {
    Pinky, Brains
}

external interface PinkyConsoleProps : Props {
    var simulator: SheepSimulator.Facade
}

fun RBuilder.pinkyConsole(handler: RHandler<PinkyConsoleProps>) =
    child(PinkyConsoleView, handler = handler)