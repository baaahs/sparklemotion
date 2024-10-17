package baaahs.app.ui.dev

import baaahs.app.ui.CommonIcons
import baaahs.app.ui.appContext
import baaahs.gl.RootToolchain
import baaahs.ui.asTextNode
import baaahs.ui.components.palette
import baaahs.ui.withoutEvent
import baaahs.ui.xComponent
import baaahs.window
import js.objects.jso
import materialui.icon
import mui.material.*
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.html.TdAlign
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
    val handleToggleShowToolchainStats by mouseEventHandler {
        showToolchainStats = !showToolchainStats; menuAnchor = null
    }

    var showGlContexts by state { false }
    val handleToggleShowGlContexts by mouseEventHandler {
        showGlContexts = !showGlContexts; menuAnchor = null
    }

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

    Tooltip {
        attrs.title = "Dev Mode Menu".asTextNode()

        IconButton {
            attrs.onClick = showMenu
            icon(CommonIcons.DeveloperMode)
        }
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
                attrs.onClick = handleToggleShowToolchainStats
                Checkbox { attrs.checked = showToolchainStats }
                ListItemText { +if (showToolchainStats) "Hide Toolchain Stats" else "Show Toolchain Stats" }
            }

            MenuItem {
                attrs.onClick = handleToggleShowGlContexts
                Checkbox { attrs.checked = showGlContexts }
                ListItemText { +if (showGlContexts) "Hide GL Contexts" else "Show GL Contexts" }
            }
        }
    }

    if (showToolchainStats) {
        palette {
            attrs.title = "Toolchain Stats"
            attrs.initialWidth = window.innerWidth / 4
            attrs.initialHeight = window.innerHeight * 2 / 3
            attrs.onClose = handleToggleShowToolchainStats.withoutEvent()

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

    if (showGlContexts) {
        devGlContexts {
            attrs.onClose = handleToggleShowGlContexts.withoutEvent()
        }
    }
}

external interface DevModeToolbarMenuProps : Props {
}

fun RBuilder.devModeToolbarMenu(handler: RHandler<DevModeToolbarMenuProps>) =
    child(DevModeToolbarMenuView, handler = handler)