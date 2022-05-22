package baaahs.app.ui.controls

import baaahs.app.ui.appContext
import baaahs.app.ui.preview.ClientPreview
import baaahs.control.OpenVisualizerControl
import baaahs.show.live.ControlProps
import baaahs.ui.*
import baaahs.ui.diagnostics.diagnosticsPalette
import baaahs.util.useResizeListener
import kotlinx.html.js.onClickFunction
import kotlinx.js.jso
import materialui.icon
import mui.icons.material.Settings
import mui.material.*
import org.w3c.dom.Element
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.events.Event
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

        useResizeListener(rootEl) {
            visualizer.resize()
        }
    }

    var menuAnchor by state<Element?> { null }
    val showMenu = callback { event: Event -> menuAnchor = event.target as Element? }
    val hideMenu = callback { _: Event?, _: String? -> menuAnchor = null }

    var showDiagnostics by state { false }
    val handleToggleShowDiagnostics by handler { showDiagnostics = !showDiagnostics; menuAnchor = null }

    Card {
        ref = rootEl
        attrs.classes = jso { this.root = -Styles.visualizerCard }

        div(+Styles.dagAffordance) {
            attrs.onClickFunction = handleToggleShowDiagnostics.withEvent()
            icon(Settings)
        }
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
                attrs.onClick = handleToggleShowDiagnostics.withMouseEvent()
                Checkbox { attrs.checked = showDiagnostics }
                ListItemText { +if (showDiagnostics) "Hide Diagnostics" else "Show Diagnostics" }
            }
        }
    }

    if (showDiagnostics && clientPreview != null) {
        diagnosticsPalette {
            attrs.renderPlanMonitor = clientPreview.renderPlanMonitor
        }
    }
}

external interface VisualizerControlProps : Props {
    var controlProps: ControlProps
    var visualizerControl: OpenVisualizerControl
}

fun RBuilder.visualizerControl(handler: RHandler<VisualizerControlProps>) =
    child(VisualizerControlView, handler = handler)