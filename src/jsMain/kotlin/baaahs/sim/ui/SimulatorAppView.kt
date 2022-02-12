package baaahs.sim.ui

import baaahs.SheepSimulator
import baaahs.document
import baaahs.sim.HostedWebApp
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import external.mosaic.MosaicParent
import external.mosaic.MosaicWindow
import external.mosaic.MosaicWindowProps
import external.mosaic.mosaic
import kotlinext.js.jsObject
import react.*
import react.dom.div

enum class SimulatorWindows {
    Visualizer,
    Console,
    UI
}

val SimulatorAppView = xComponent<SimulatorAppProps>("SimulatorApp") { props ->
    var currentNode by state {
        jsObject<MosaicParent<SimulatorWindows>> {
            direction = "row"
            splitPercentage = 15
            first = jsObject<MosaicParent<SimulatorWindows>> {
                direction = "column"
                splitPercentage = 50
                first = SimulatorWindows.Visualizer
                second = SimulatorWindows.Console
            }
            second = SimulatorWindows.UI
        }
    }

    div(+SimulatorStyles.app) {
        menuBar {
            attrs.launchItems = props.simulator.launchItems
        }

        mosaic<SimulatorWindows> {
            attrs.value = currentNode
            attrs.onChange = { newNode -> currentNode = newNode }
            attrs.className = "mosaic mosaic-blueprint-theme bp3-dark"
            attrs.zeroStateView = document.createElement("div")
            attrs.resize = js("{\"minimumPaneSizePercentage\": 1}")

            attrs.renderTile = { window, path ->
                // TODO: Ugh, are there more idiomatic ways to do any of this? Yes! buildElement {}

                createElement(MosaicWindow,
                    jsObject<MosaicWindowProps<SimulatorWindows>> {
                        this.draggable = false
                        this.title = window.name
                        this.path = path
                        this.renderToolbar = { props, isDraggable ->
                            buildElement {
                                div(+SimulatorStyles.panelToolbar) {
                                    +props.title
                                }
                            }
                        }
                    },

                    createElement(
                        "div", jsObject() { asDynamic()["className"] = +SimulatorStyles.windowContainer },
                        when (window) {
                            SimulatorWindows.Visualizer -> createElement(ModelSimulationView, jsObject {
                                this.simulator = props.simulator
                            })
                            SimulatorWindows.Console -> createElement(StatusPanelView, jsObject {
                                this.simulator = props.simulator
                            })
                            SimulatorWindows.UI -> createElement(WebClientWindowView, jsObject {
                                this.hostedWebApp = props.hostedWebApp
                            })
                        }
                    )
                )
            }
        }
    }
}

external interface SimulatorAppProps : Props {
    var simulator: SheepSimulator.Facade
    var hostedWebApp: HostedWebApp
}

fun RBuilder.simulatorApp(handler: RHandler<SimulatorAppProps>) =
    child(SimulatorAppView, handler = handler)
