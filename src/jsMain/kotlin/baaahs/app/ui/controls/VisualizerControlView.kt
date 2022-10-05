package baaahs.app.ui.controls

import baaahs.app.ui.appContext
import baaahs.app.ui.preview.ClientPreview
import baaahs.control.OpenVisualizerControl
import baaahs.show.live.ControlProps
import baaahs.ui.*
import baaahs.ui.diagnostics.dmxDiagnostics
import baaahs.ui.diagnostics.patchDiagnostics
import baaahs.util.useResizeListener
import kotlinx.html.js.onClickFunction
import kotlinx.html.org.w3c.dom.events.Event
import kotlinx.js.jso
import materialui.icon
import mui.icons.material.Settings
import mui.material.*
import org.w3c.dom.Element
import org.w3c.dom.HTMLDivElement
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.div
import react.useContext

private val VisualizerControlView = xComponent<VisualizerControlProps>("VisualizerControl") { props ->
    val appContext = useContext(appContext)

    val sceneManager = appContext.sceneManager
    observe(sceneManager)
    val model = sceneManager.openScene?.model

    val rootEl = ref<Element>()

    val clientPreview = memo(model) {
        model?.let {
            ClientPreview(it, appContext.showPlayer, appContext.clock, appContext.plugins)
        }
    }

    if (clientPreview != null) {
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
    }

    var menuAnchor by state<Element?> { null }
    val showMenu = callback { event: Event -> menuAnchor = event.target as Element? }
    val hideMenu = callback { _: Event?, _: String? -> menuAnchor = null }

    var showPatchDiagnostics by state { false }
    val handleToggleShowPatchDiagnostics by handler { showPatchDiagnostics = !showPatchDiagnostics; menuAnchor = null }

    var showDmxDiagnostics by state { false }
    val handleToggleShowDmxDiagnostics by handler { showDmxDiagnostics = !showDmxDiagnostics; menuAnchor = null }

    Card {
        ref = rootEl
        attrs.classes = jso { this.root = -Styles.controlRoot and Styles.visualizerCard }

        div(+Styles.visualizerMenuAffordance) {
            attrs.onClickFunction = showMenu
            icon(Settings)
        }

        if (model == null) {
            +"No scene loaded!"
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