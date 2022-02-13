package baaahs.sim.ui

import baaahs.Pinky
import baaahs.ui.*
import external.react_draggable.Draggable
import kotlinx.js.jso
import materialui.icon
import mui.base.Portal
import mui.material.Paper
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.div
import react.dom.header
import react.dom.i
import react.dom.pre

val GeneratedGlslPalette = xComponent<GeneratedGlslPaletteProps>("GeneratedGlslPalette") { props ->
    observe(props.pinky.stageManager)

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
                        val renderPlan = props.pinky.stageManager.currentRenderPlan
                        if (renderPlan == null) {
                            i { +"No plans!" }
                        } else {
                            renderPlan.forEach { (fixtureType, fixtureTypeRenderPlans) ->
                                fixtureTypeRenderPlans.forEach { programRenderPlan ->
                                    header {
                                        +"${fixtureType.title}: ${programRenderPlan.renderTargets.size} fixtures"
                                    }

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
}

external interface GeneratedGlslPaletteProps : Props {
    var pinky: Pinky.Facade
}

fun RBuilder.generatedGlslPalette(handler: RHandler<GeneratedGlslPaletteProps>) =
    child(GeneratedGlslPalette, handler = handler)