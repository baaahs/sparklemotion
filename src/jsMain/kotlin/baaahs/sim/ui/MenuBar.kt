package baaahs.sim.ui

import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import kotlinx.html.id
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.div

private val MenuBarView = xComponent<MenuBarProps>("MenuBar") { props ->

    div(+SimulatorStyles.menuBar) {
        div(+SimulatorStyles.title) {
            +"Sparkle Motion"
        }
        div(+SimulatorStyles.menu) {
            attrs.id = "launcher"
        }
    }
}

external interface MenuBarProps : Props {
}

fun RBuilder.menuBar(handler: RHandler<MenuBarProps>) =
    child(MenuBarView, handler = handler)