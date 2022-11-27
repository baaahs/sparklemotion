package baaahs.app.ui.controls

import baaahs.app.ui.appContext
import baaahs.app.ui.preview.ClientPreview
import baaahs.control.OpenVisualizerControl
import baaahs.show.live.ControlProps
import baaahs.ui.diagnostics.dmxDiagnostics
import baaahs.ui.diagnostics.patchDiagnostics
import baaahs.ui.unaryMinus
import baaahs.ui.unaryPlus
import baaahs.ui.withMouseEvent
import baaahs.ui.xComponent
import baaahs.util.useResizeListener
import dom.Element
import dom.html.HTMLDivElement
import kotlinx.js.jso
import materialui.icon
import mui.icons.material.Settings
import mui.material.*
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.div
import react.dom.onClick
import react.useContext
import web.events.Event

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
    val showMenu by mouseEventHandler() { event -> menuAnchor = event.target as Element? }
    val hideMenu = callback { _: Event?, _: String? -> menuAnchor = null }

    var showPatchDiagnostics by state { false }
    val handleToggleShowPatchDiagnostics by handler { showPatchDiagnostics = !showPatchDiagnostics; menuAnchor = null }

    var showDmxDiagnostics by state { false }
    val handleToggleShowDmxDiagnostics by handler { showDmxDiagnostics = !showDmxDiagnostics; menuAnchor = null }

    Card {
        ref = rootEl
        attrs.classes = jso { this.root = -Styles.visualizerCard }

        div(+Styles.visualizerMenuAffordance) {
            attrs.onClick = showMenu
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