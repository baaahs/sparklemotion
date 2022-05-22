package baaahs.ui.diagnostics

import baaahs.app.ui.appContext
import baaahs.fixtures.RenderPlan
import baaahs.sim.ui.Styles
import baaahs.ui.*
import baaahs.ui.components.palette
import baaahs.util.Monitor
import mui.material.Tab
import mui.material.Tabs
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.*
import react.useContext

val GeneratedGlslPaletteView = xComponent<GeneratedGlslPaletteProps>("GeneratedGlslPalette") { props ->
    val appContext = useContext(appContext)
    val styles = appContext.allStyles.uiComponents

    observe(props.renderPlanMonitor)

    val renderPlans = props.renderPlanMonitor.value

    var selectedTabIndex by state { 0 }

    palette {
        typographyH6 { +"Generated GLSL" }

        if (renderPlans == null) {
            i { +"No plansz!" }
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

            div(+Styles.glslContentDiv and styles.codeContainer) {
                pre(+styles.code) {
                    val (_, renderPlan) = plans[selectedTabIndex]
                    renderPlan.programs.forEachIndexed { index, programRenderPlan ->
                        if (index > 0) hr {}

                        programRenderPlan.program?.fragShader?.source
                            ?.let { glsl ->
                                glsl.split("\n").forEach { line ->
                                    code { +"    "; +line; +"\n" }
                                }
                            }
                            ?: i { +"No program!" }
                    }
                }
            }
        }
//        div(+Styles.glslCodeSheet) {
//            Paper {
//            }
//        }
    }
}

external interface GeneratedGlslPaletteProps : Props {
    var renderPlanMonitor: Monitor<RenderPlan?>
}

fun RBuilder.generatedGlslPalette(handler: RHandler<GeneratedGlslPaletteProps>) =
    child(GeneratedGlslPaletteView, handler = handler)