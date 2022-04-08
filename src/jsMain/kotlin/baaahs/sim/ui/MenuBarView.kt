package baaahs.sim.ui

import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import kotlinx.html.js.onClickFunction
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.button
import react.dom.div
import react.useContext

private val MenuBarView = xComponent<MenuBarProps>("MenuBar") { props ->
    val styles = useContext(simulatorContext).styles

    div(+styles.menuBar) {
        div(+SimulatorStyles.title) {
            +"Sparkle Motion"
        }

        div(+SimulatorStyles.menu) {
            props.launchItems.forEach { launchItem ->
                button {
                    attrs.onClickFunction = { launchItem.onLaunch()}
                    +launchItem.title
                }
            }
        }
    }
}

data class LaunchItem(val title: String, val onLaunch: () -> Unit)

external interface MenuBarProps : Props {
    var launchItems: List<LaunchItem>
}

fun RBuilder.menuBar(handler: RHandler<MenuBarProps>) =
    child(MenuBarView, handler = handler)