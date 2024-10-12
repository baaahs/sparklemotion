package baaahs.app.ui.controls

import baaahs.app.ui.AppMode
import baaahs.app.ui.appContext
import baaahs.app.ui.preview.ClientPreview
import baaahs.control.OpenVisualizerControl
import baaahs.show.live.ControlProps
import baaahs.ui.*
import baaahs.ui.diagnostics.dmxDiagnostics
import baaahs.ui.diagnostics.patchDiagnostics
import baaahs.util.useResizeListener
import js.objects.jso
import kotlinx.css.rem
import materialui.icon
import mui.icons.material.Settings
import mui.material.*
import mui.system.sx
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.div
import react.dom.html.ReactHTML.p
import react.dom.onClick
import react.useContext
import web.cssom.Cursor
import web.dom.Element
import web.events.Event
import web.html.HTMLDivElement

private val VisualizerControlView = xComponent<VisualizerControlProps>("VisualizerControl") { props ->
    val appContext = useContext(appContext)

    val sceneProvider = appContext.sceneProvider
    observe(sceneProvider)
    val scene = sceneProvider.openSceneOrFallback
    val model = scene.model

    val rootEl = ref<Element>()

    val clientPreview = memo(model) {
        ClientPreview(model, appContext.showPlayer, appContext.clock, appContext.plugins)
    }

    clientPreview.visualizer.rotate = props.visualizerControl.rotate
    val visualizer = clientPreview.visualizer
    onMount(visualizer) {
        visualizer.container = rootEl.current as HTMLDivElement

        withCleanup {
            visualizer.container = null
            clientPreview.detach()
        }
    }

    useResizeListener(rootEl) { _, _ ->
        visualizer.resize()
    }

    var menuAnchor by state<Element?> { null }
    val showMenu by mouseEventHandler() { event -> menuAnchor = event.target as Element? }
    val hideMenu = callback { _: Event?, _: String? -> menuAnchor = null }

    var showPatchDiagnostics by state { false }
    val handleToggleShowPatchDiagnostics by handler { showPatchDiagnostics = !showPatchDiagnostics; menuAnchor = null }

    var showDmxDiagnostics by state { false }
    val handleToggleShowDmxDiagnostics by handler { showDmxDiagnostics = !showDmxDiagnostics; menuAnchor = null }

    Card {
        ref = rootEl
        attrs.className = -Styles.visualizerCard

        div(+Styles.visualizerMenuAffordance) {
            attrs.onClick = showMenu
            icon(Settings)
        }

        if (scene.isFallback) {
            div(+Styles.visualizerWarning) {
                +"No scene loaded."

                help {
                    attrs.iconSize = 1.25.rem
                    attrs.title { +"No scene loaded." }
                    attrs.child {
                        Typography {
                            markdown {
                                +"""
                                    A scene describes the physical layout and configuration of your lighting fixtures.
                                    No scene is currently loaded, so a simple generic scene is being shown in the
                                    visualizer.
                                """.trimIndent()
                            }

                            p {
                                +"You can "

                                Link {
                                    attrs.className = -baaahs.ui.Styles.helpAutoClose
                                    attrs.sx { cursor = Cursor.pointer }
                                    attrs.onClick = {
                                        appContext.webClient.appMode = AppMode.Scene
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
                attrs.onClick = handleToggleShowPatchDiagnostics.withMouseEvent()
                Checkbox { attrs.checked = showPatchDiagnostics }
                ListItemText { +if (showPatchDiagnostics) "Hide Patch Diagnostics" else "Show Patch Diagnostics" }
            }

            MenuItem {
                attrs.onClick = handleToggleShowDmxDiagnostics.withMouseEvent()
                Checkbox { attrs.checked = showDmxDiagnostics }
                ListItemText { +if (showDmxDiagnostics) "Hide DMX Diagnostics" else "Show DMX Diagnostics" }
            }
        }
    }

    if (showPatchDiagnostics && clientPreview != null) {
        patchDiagnostics {
            attrs.renderPlanMonitor = clientPreview.renderPlanMonitor
            attrs.onClose = handleToggleShowPatchDiagnostics
        }
    }

    if (showDmxDiagnostics && clientPreview != null) {
        dmxDiagnostics {
            attrs.onClose = handleToggleShowDmxDiagnostics
        }
    }
}

external interface VisualizerControlProps : Props {
    var controlProps: ControlProps
    var visualizerControl: OpenVisualizerControl
}

fun RBuilder.visualizerControl(handler: RHandler<VisualizerControlProps>) =
    child(VisualizerControlView, handler = handler)