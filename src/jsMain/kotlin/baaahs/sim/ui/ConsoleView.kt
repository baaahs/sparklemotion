package baaahs.sim.ui

import baaahs.SheepSimulator
import baaahs.app.ui.Themes
import baaahs.client.WebClient
import baaahs.sim.HostedWebApp
import baaahs.ui.asTextNode
import baaahs.ui.unaryMinus
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import js.objects.jso
import mui.material.CssBaseline
import mui.material.Paper
import mui.material.Tab
import mui.material.Tabs
import mui.material.styles.ThemeProvider
import react.Props
import react.dom.button
import react.dom.div
import react.dom.events.SyntheticEvent
import react.dom.header
import react.dom.onClick

val ConsoleView = xComponent<ConsoleProps>("Console") { props ->
    val webApp = props.mainWebApp
    val theme = if (webApp is WebClient) {
        if (webApp.facade.uiSettings.darkMode) Themes.Dark else Themes.Light
    } else Themes.Dark

    val simulator = props.simulator
    observe(simulator)
    observe(simulator.pinky)

    var selectedTab by state { ConsoleTabs.entries.first() }
    val handleTabSelect by handler { _: SyntheticEvent<*, *>, value: dynamic ->
        selectedTab = value
    }

    ThemeProvider {
        attrs.theme = theme
        CssBaseline {}

        Paper {
            attrs.classes = jso { this.root = -SimulatorStyles.statusPanel }

            header { +"Console" }

            Tabs {
                attrs.value = selectedTab
                attrs.onChange = handleTabSelect
                Tab {
                    attrs.label = "Pinky".asTextNode()
                    attrs.value = ConsoleTabs.Pinky
                }
                Tab {
                    attrs.label = "Brains".asTextNode()
                    attrs.value = ConsoleTabs.Brains
                }
            }

            when (selectedTab) {
                ConsoleTabs.Pinky -> {
                    pinkyConsole {
                        attrs.simulator = simulator
                    }
                }

                ConsoleTabs.Brains -> {
                    brainsConsole {
                        attrs.simulator = simulator
                    }
                }
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
        }
    }
}

data class LaunchItem(val title: String, val onLaunch: () -> Unit)

external interface ConsoleProps : Props {
    var simulator: SheepSimulator.Facade
    var mainWebApp: HostedWebApp
}

//fun RBuilder.console(handler: RHandler<ConsoleProps>) =
//    child(ConsoleView, handler = handler)