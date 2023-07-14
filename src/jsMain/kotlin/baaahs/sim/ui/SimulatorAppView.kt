package baaahs.sim.ui

import baaahs.SheepSimulator
import baaahs.app.ui.Themes
import baaahs.document
import baaahs.sim.HostedWebApp
import baaahs.ui.components.UiComponentStyles
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import external.mosaic.MosaicParent
import external.mosaic.MosaicWindow
import external.mosaic.MosaicWindowProps
import external.mosaic.mosaic
import js.core.jso
import mui.system.Breakpoint
import react.*
import react.dom.div
import react.dom.html.ReactHTML
import styled.injectGlobal
import web.cssom.MediaQuery
import web.cssom.matchMedia

enum class SimulatorWindows {
    Visualizer,
    Console,
    UI
}

val simulatorContext = createContext<SimulatorContext>(jso {})

external interface SimulatorContext {
    var styles: ThemedSimulatorStyles
    var uiComponentStyles: UiComponentStyles
}

val SimulatorAppView = xComponent<SimulatorAppProps>("SimulatorApp") { props ->
    val theme = Themes.Light
    val simulatorStyles = ThemedSimulatorStyles(theme)
    injectGlobal(simulatorStyles.global)
    val uiComponentStyles = UiComponentStyles(theme)

    val mySimulatorContext = memo {
        jso<SimulatorContext> {
            this.styles = simulatorStyles
            this.uiComponentStyles = uiComponentStyles
        }
    }

    val small = matchMedia(
        MediaQuery(
            theme.breakpoints.down(Breakpoint.md)
                .replace("@media ", "")
        )
    ).matches

    var currentNode by state<MosaicParent<SimulatorWindows>> {
        if (small) {
            jso {
                direction = "column"
                splitPercentage = 20
                first = SimulatorWindows.Visualizer
                second = SimulatorWindows.UI
            }
        } else {
            jso {
                direction = "row"
                splitPercentage = 15
                first = jso<MosaicParent<SimulatorWindows>> {
                    direction = "column"
                    splitPercentage = 50
                    first = SimulatorWindows.Visualizer
                    second = SimulatorWindows.Console
                }
                second = SimulatorWindows.UI
            }
        }
    }

    simulatorContext.Provider {
        attrs.value = mySimulatorContext

        div(+SimulatorStyles.app) {
            mosaic<SimulatorWindows> {
                attrs.value = currentNode
                attrs.onChange = { newNode -> currentNode = newNode }
                attrs.className = "mosaic mosaic-blueprint-theme bp3-dark"
                attrs.zeroStateView = document.createElement("div")
                attrs.resize = js("{\"minimumPaneSizePercentage\": 1}")

                attrs.renderTile = { window, path ->
                    // TODO: Ugh, are there more idiomatic ways to do any of this? Yes! buildElement {}

                    createElement(MosaicWindow,
                        jso<MosaicWindowProps<SimulatorWindows>> {
                            this.draggable = false
                            this.title = window.name
                            this.path = path
                            this.renderPreview = { buildElement{ div {} } }
                            this.renderToolbar = { props, _ ->
                                buildElement { div(+SimulatorStyles.panelToolbar) { +props.title } }
                            }
                        },

                        createElement(
                            ReactHTML.div, jso { asDynamic()["className"] = +SimulatorStyles.windowContainer },
                            when (window) {
                                SimulatorWindows.Visualizer -> createElement(ModelSimulationView, jso {
                                    this.simulator = props.simulator
                                })
                                SimulatorWindows.Console -> createElement(StatusPanelView, jso {
                                    this.simulator = props.simulator
                                })
                                SimulatorWindows.UI -> createElement(WebClientWindowView, jso {
                                    this.hostedWebApp = props.hostedWebApp
                                })
                            }
                        )
                    )
                }
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
