package baaahs.sim.ui

import baaahs.SheepSimulator
import baaahs.app.ui.AppMode
import baaahs.client.WebClient
import baaahs.sim.HostedWebApp
import baaahs.ui.*
import baaahs.ui.Styles
import baaahs.visualizer.ui.visualizerPanel
import kotlinx.css.rem
import mui.material.*
import mui.system.sx
import react.*
import react.dom.div
import react.dom.header
import react.dom.html.ReactHTML
import web.cssom.Cursor

val ModelSimulationView = xComponent<ModelSimulationProps>("ModelSimulation") { props ->
    val visualizer = observe(props.simulator.visualizer)
    var rotate by state { visualizer.rotate }

    onChange("rotate sync", rotate) {
        visualizer.rotate = rotate
    }

    val onRotateChange by eventHandler { rotate = !rotate }

    val fixturesSimulator = observe(props.simulator.fixturesSimulator)
    val handleStart by mouseEventHandler(fixturesSimulator) {
        fixturesSimulator.start()
    }

    div(+SimulatorStyles.modelSimulation) {
        header { +"Simulation" }

        visualizerPanel {
            attrs.visualizer = visualizer

            if (!fixturesSimulator.isStarted) {
                div(+SimulatorStyles.unstarted) {
                    Button {
                        attrs.onClick = handleStart
                        +"Start Simulation"
                    }
                }
            } else if (visualizer.haveScene) {
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
            } else {
                div(+SimulatorStyles.vizWarning) {
                    +"No scene loaded."

                    help {
                        attrs.iconSize = 1.25.rem
                        attrs.title { +"No scene loaded." }
                        attrs.child {
                            Typography {
                                markdown {
                                    +"""
                                        A scene describes the physical layout and configuration of your lighting
                                        fixtures. Sparkle Motion needs a scene to know how to render shows.
                                        
                                        No scene is currently loaded, so nothing will be shown in the hardware simulation.
                                    """.trimIndent()
                                }

                                val webApp = props.hostedWebApp
                                if (webApp is WebClient) {
                                    ReactHTML.p {
                                        +"You can "

                                        Link {
                                            attrs.className = -Styles.helpAutoClose
                                            attrs.sx { cursor = Cursor.pointer }
                                            attrs.onClick = {
                                                webApp.facade.appMode = AppMode.Scene
                                            }
                                            +"create or load a scene here"
                                        }

                                        +"."
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

external interface ModelSimulationProps : Props {
    var simulator: SheepSimulator.Facade
    var hostedWebApp: HostedWebApp
}

fun RBuilder.modelSimulation(handler: RHandler<ModelSimulationProps>) =
    child(ModelSimulationView, handler = handler)