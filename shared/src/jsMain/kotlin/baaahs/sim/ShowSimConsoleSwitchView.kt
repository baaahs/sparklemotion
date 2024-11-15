package baaahs.sim

import baaahs.SheepSimulator
import baaahs.sim.ui.console
import baaahs.ui.*
import baaahs.ui.components.palette
import mui.material.FormControlLabel
import mui.material.Switch
import react.*

private val ShowSimConsoleSwitchView = xComponent<ShowSimConsoleSwitchProps>("ShowSimConsoleSwitch") { props ->
    var isOpen by state { false }

    val handleToggle by switchEventHandler { _, _ ->
        isOpen = !isOpen
    }

    FormControlLabel {
        attrs.control = buildElement {
            Switch {
                attrs.checked = isOpen
                attrs.onChange = handleToggle
            }
        }
        attrs.label = "Show Simulator Console".asTextNode()
    }

    if (isOpen) {
        palette {
            console {
                attrs.simulator = props.simulator
                attrs.mainWebApp = props.mainWebApp
            }
        }
    }
}

external interface ShowSimConsoleSwitchProps : Props {
    var simulator: SheepSimulator.Facade
    var mainWebApp: HostedWebApp
}

fun RBuilder.showSimConsoleSwitch(handler: RHandler<ShowSimConsoleSwitchProps>) =
    child(ShowSimConsoleSwitchView, handler = handler)