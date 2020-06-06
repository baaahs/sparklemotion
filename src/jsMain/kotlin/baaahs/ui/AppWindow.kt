package baaahs.ui

import baaahs.gadgets.Slider
import baaahs.jsx.RangeSlider
import baaahs.jsx.sim.store
import baaahs.show.Control
import baaahs.show.SampleData
import baaahs.ui.gadgets.patchSetList
import baaahs.ui.gadgets.sceneList
import external.ErrorBoundary
import kotlinx.html.js.onClickFunction
import kotlinx.html.role
import materialui.Menu
import materialui.components.button.button
import materialui.components.drawer.drawer
import materialui.components.drawer.enums.DrawerAnchor
import materialui.components.drawer.enums.DrawerVariant
import materialui.components.iconbutton.enums.IconButtonEdge
import materialui.components.iconbutton.iconButton
import materialui.components.portal.portal
import materialui.components.svgicon.SvgIconProps
import materialui.components.toolbar.toolbar
import org.w3c.dom.events.Event
import react.*
import react.dom.div
import react.dom.p
import react.dom.pre
import kotlin.reflect.KClass

val ErrorDisplay = functionalComponent<ErrorDisplayProps> { props ->
    div {
        attrs.role = "alert"
        p { +"Something went wrong:" }
        pre { +(props.error.message ?: "Unknown error") }
        pre { +props.componentStack }
        button { attrs.onClickFunction = { props.resetErrorBoundary() } }
    }
}

external interface ErrorDisplayProps : RProps {
    var error: Error
    var componentStack: String
    var resetErrorBoundary: () -> Unit
}

val AppWindow = functionalComponent<AppWindowProps> {
    val contextState = useContext(store).state
    val pubSub = contextState.simulator.getPubSub()

    val preact = Preact()
    var shaderEditorDrawerOpen by preact.state { false }

    val handleShaderEditorDrawerToggle = useCallback(shaderEditorDrawerOpen) { event: Event ->
        console.log("handleShaderEditorDrawerToggle: I'll set shaderEditorDrawerOpen to", !shaderEditorDrawerOpen)
        shaderEditorDrawerOpen = !shaderEditorDrawerOpen
    }
    val handleShaderEditorDrawerClose = useCallback { event: Event ->
        shaderEditorDrawerOpen = false
    }

    val show = SampleData.sampleShow
    var currentScene by preact.state { show.scenes.first() }
    var currentPatchSet by preact.state { currentScene.patchSets.first() }
    val currentLayoutName = "default"
    val currentLayout = show.layouts.map[currentLayoutName] ?: error("no such layout $currentLayoutName")

    val layoutRenderers = show.layouts.panelNames.associateWith { mutableListOf<ControlRenderer>() }
    val controlRenderers: Map<Control, ControlRenderer> = show.controls.associateWith { control ->
        val renderer: ControlRenderer = {
            when (control.type) {
                "SceneList" -> {
                    sceneList {
                        this.pubSub = pubSub
                        this.scenes = show.scenes
                        this.currentScene = currentScene
                        onSelect = { currentScene = it }
                    }
                }
                "PatchList" -> {
                    patchSetList {
                        this.pubSub = pubSub
                        this.patchSets = currentScene.patchSets
                        this.currentPatchSet = currentPatchSet
                        onSelect = { currentPatchSet = it }
                    }
                }
                "Slider" -> {
                    RangeSlider {
                        attrs.pubSub = pubSub
                        attrs.gadget = Slider(control.name)
                    }
                }
            }
            p { +"Control: ${control.name}" }
        }
        renderer
    }

    fun addControlsToPanels(layoutControls: Map<String, List<Control>>) {
        layoutControls.forEach { (panelName, controls) ->
            controls.forEach { control ->
                val panelItems = layoutRenderers[panelName] ?: error("unknown panel $panelName")
                val controlRenderer = controlRenderers[control] ?: error("no such control ${control.name} among ${controlRenderers.keys}")
                panelItems.add(controlRenderer)
            }
        }
    }

    addControlsToPanels(show.controlLayout)
    addControlsToPanels(currentPatchSet.controlLayout)

    ErrorBoundary {
        attrs.FallbackComponent = ErrorDisplay

        toolbar {
            iconButton {
                attrs.edge = IconButtonEdge.end
                attrs.onClickFunction = handleShaderEditorDrawerToggle
                Menu { }
                +"Shader Editor"
            }
        }

        showLayout {
            this.layout = currentLayout
            layoutControls = layoutRenderers
        }

        portal {
            drawer {
                attrs.anchor = DrawerAnchor.right
                attrs.variant = DrawerVariant.persistent
//            attrs.elevation = 100
                attrs.open = shaderEditorDrawerOpen
                attrs.onClose = handleShaderEditorDrawerClose

                shaderEditorWindow { }
            }
        }
    }
}

external interface AppWindowProps : RProps

fun RBuilder.appWindow(handler: AppWindowProps.() -> Unit): ReactElement =
    child(AppWindow) { attrs { handler() } }

fun RBuilder.icon(icon: RClass<SvgIconProps>, handler: SvgIconProps.() -> Unit = { }): ReactElement =
    child(icon::class as KClass<out Component<SvgIconProps, *>>) { attrs { handler() } }