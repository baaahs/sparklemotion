package baaahs.app.ui.dev

import baaahs.app.ui.CommonIcons
import baaahs.app.ui.appContext
import baaahs.gl.RootToolchain
import baaahs.ui.components.palette
import baaahs.ui.unaryPlus
import baaahs.ui.withMouseEvent
import baaahs.ui.xComponent
import baaahs.window
import js.objects.jso
import materialui.icon
import mui.material.*
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.div
import react.dom.html.TdAlign
import react.dom.onClick
import react.useContext
import web.dom.Element
import web.events.Event
import web.timers.clearInterval
import web.timers.setInterval

private val DevModeToolbarMenuView = xComponent<DevModeToolbarMenuProps>("DevModeToolbarMenu") { props ->
    val appContext = useContext(appContext)
    val styles = appContext.allStyles.appUi

    var menuAnchor by state<Element?> { null }
    val showMenu by mouseEventHandler { event -> menuAnchor = event.target as Element? }
    val hideMenu = callback { _: Event?, _: String? -> menuAnchor = null }

    var showToolchainStats by state { false }
    val handleToggleShowToolchainStats by handler { showToolchainStats = !showToolchainStats; menuAnchor = null }

    onMount(showToolchainStats) {
        if (showToolchainStats) {
            val stats = (appContext.webClient.toolchain as RootToolchain).stats
            var lastStats = emptyList<Int>()
            val callback = setInterval({
                val curStats = stats.all.map { it.calls }
                if (curStats != lastStats) {
                    lastStats = curStats
                    forceRender()
                }
            }, timeout = 50)

            withCleanup {
                clearInterval(callback)
            }
        }
    }

    div(+styles.appToolbarDevMenuIcon) {
        attrs.onClick = showMenu
        icon(CommonIcons.Settings)
    }

    if (menuAnchor != null) {
        Menu {
            attrs.anchorEl = menuAnchor.asDynamic()
            attrs.anchorOrigin = jso {
                horizontal = "left"
                vertical = "bottom"
            }
            attrs.open = menuAnchor != null
            attrs.onClose = hideMenu

            MenuItem {
                attrs.onClick = handleToggleShowToolchainStats.withMouseEvent()
                Checkbox { attrs.checked = showToolchainStats }
                ListItemText { +if (showToolchainStats) "Hide Toolchain Stats" else "Show Toolchain Stats" }
            }
        }
    }

    if (showToolchainStats) {
        palette {
            attrs.title = "Toolchain Stats"
            attrs.initialWidth = window.innerWidth / 4
            attrs.initialHeight = window.innerHeight * 2 / 3
            attrs.onClose = handleToggleShowToolchainStats

            val stats = (appContext.webClient.toolchain as RootToolchain).stats
            Table {
                TableHead {
                    TableCell { +"Statistic" }
                    TableCell { +"Calls" }
                    TableCell { +"Average" }
                    TableCell { +"Total" }
                }
                TableBody {
                    stats.all.forEach { stat ->
                        TableRow {
                            TableCell { +stat.name }
                            TableCell {
                                attrs.align = TdAlign.right
                                +stat.calls.toString()
                            }
                            TableCell {
                                attrs.align = TdAlign.right
                                +(stat.averageTimeMs?.let { "${it}ms" } ?: "-")
                            }
                            TableCell {
                                attrs.align = TdAlign.right
                                +"${stat.elapsedTimeMs}ms"
                            }
                        }
                    }
                }
            }
        }
    }
}

external interface DevModeToolbarMenuProps : Props {
}

fun RBuilder.devModeToolbarMenu(handler: RHandler<DevModeToolbarMenuProps>) =
    child(DevModeToolbarMenuView, handler = handler)