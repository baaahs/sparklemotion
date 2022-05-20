package baaahs.sim.ui

import baaahs.fixtures.RenderPlan
import baaahs.ui.*
import baaahs.util.Monitor
import external.react_draggable.Draggable
import kotlinx.js.jso
import materialui.icon
import mui.base.Portal
import mui.material.Paper
import mui.material.Tab
import mui.material.Tabs
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.div
import react.dom.i
import react.dom.pre

val GeneratedGlslPalette = xComponent<GeneratedGlslPaletteProps>("GeneratedGlslPalette") { props ->
    observe(props.renderPlanMonitor)

    val renderPlans = props.renderPlanMonitor.value

    var selectedTabIndex by state { 0 }

    Portal {
        Draggable {
            val randomStyleForHandle = "PinkyPanelHandle"
            attrs.handle = ".$randomStyleForHandle"

            div(+Styles.glslCodeSheet) {
                div(+Styles.dragHandle and randomStyleForHandle) {
                    icon(mui.icons.material.DragIndicator)
                }

                Paper {
                    attrs.classes = jso { this.root = -Styles.glslCodePaper }
                    attrs.elevation = 3

                    typographyH6 { +"Generated GLSL" }

                    div(+Styles.glslCodeDiv) {
                        if (renderPlans == null) {
                            i { +"No plans!" }
                        } else {
                            val plans = renderPlans.entries.toList()

                            Tabs {
                                attrs.value = selectedTabIndex
                                attrs.onChange = { _, value ->
                                    selectedTabIndex = value
                                }

                                plans.forEachIndexed { index, (fixtureType, fixtureTypeRenderPlans) ->
                                    fixtureTypeRenderPlans.forEach { programRenderPlan ->
                                        Tab {
                                            attrs.value = index
                                            attrs.label = "${fixtureType.title}: ${programRenderPlan.renderTargets.size} fixtures".asTextNode()
                                        }
                                    }
                                }
                            }

                            val (_, renderPlan) = plans[selectedTabIndex]
                            renderPlan.programs.forEach { programRenderPlan ->
                                programRenderPlan.program?.fragShader?.source
                                    ?.let { glsl -> pre { +glsl } }
                                    ?: i { +"No program!" }
                            }
                        }
                    }
                }
            }
        }
    }
}

external interface GeneratedGlslPaletteProps : Props {
    var renderPlanMonitor: Monitor<RenderPlan?>
}

fun RBuilder.generatedGlslPalette(handler: RHandler<GeneratedGlslPaletteProps>) =
    child(GeneratedGlslPalette, handler = handler)