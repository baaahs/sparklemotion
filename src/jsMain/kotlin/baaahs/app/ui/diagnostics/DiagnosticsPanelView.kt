package baaahs.app.ui.diagnostics

import baaahs.ui.*
import external.react_draggable.Draggable
import kotlinx.js.jso
import materialui.icon
import mui.base.Portal
import mui.icons.material.DragIndicator
import mui.material.Paper
import react.*
import react.dom.div
import react.dom.header
import react.dom.i
import react.dom.pre

private val DiagnosticsPanelView = xComponent<DiagnosticsPanelProps>("DiagnosticsPanel") { props ->
    observe(props.pinky.stageManager)

    Portal {
        Draggable {
            val randomStyleForHandle = "DiagnosticsPanelHandle"
            attrs.handle = ".$randomStyleForHandle"

            div(+Styles.glslCodeSheet) {
                div(+Styles.dragHandle and randomStyleForHandle) {
                    icon(DragIndicator)
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

external interface DiagnosticsPanelProps : Props {
}

fun RBuilder.diagnosticsPanel(handler: RHandler<DiagnosticsPanelProps>) =
    child(DiagnosticsPanelView, handler = handler)