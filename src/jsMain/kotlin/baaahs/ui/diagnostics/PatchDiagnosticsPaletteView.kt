package baaahs.ui.diagnostics

import baaahs.app.ui.editor.betterSelect
import baaahs.device.FixtureType
import baaahs.fixtures.ProgramRenderPlan
import baaahs.fixtures.RenderPlan
import baaahs.gl.glsl.GlslProgramImpl
import baaahs.ui.asTextNode
import baaahs.ui.components.palette
import baaahs.ui.xComponent
import baaahs.util.Monitor
import baaahs.window
import mui.material.Tab
import mui.material.Tabs
import mui.material.TabsVariant
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.i

val PatchDiagnosticsView = xComponent<PatchDiagnosticsProps>("PatchDiagnostics") { props ->
    observe(props.renderPlanMonitor)

    val renderPlans = props.renderPlanMonitor.value
    val subjects = renderPlans?.flatMap { (fixtureType, fixtureTypeRenderPlans) ->
        fixtureTypeRenderPlans.programs.map {
            Subject(fixtureType, it)
        }
    } ?: emptyList()

    var selectedSubject by state { subjects.firstOrNull() }
    var selectedDiagnostic by state { "DAG" }

    onChange("plans change", renderPlans) {
        selectedSubject = subjects.firstOrNull { it.toString() == selectedSubject.toString() }
    }

    palette {
        attrs.title = "Patch Diagnostics"
        attrs.initialWidth = window.innerWidth / 3
        attrs.initialHeight = window.innerHeight * 2 / 3
        attrs.onClose = props.onClose

        if (renderPlans == null) {
            i { +"No patch!" }
        } else {
            betterSelect<Subject?> {
                attrs.values = subjects
                attrs.value = selectedSubject
                attrs.onChange = { selectedSubject = it }
            }

            Tabs {
                attrs.value = selectedDiagnostic
                attrs.variant = TabsVariant.scrollable
                attrs.onChange = { _, value ->
                    selectedDiagnostic = value
                }

                Tab { attrs.value = "DAG"; attrs.label = "DAG".asTextNode() }
                Tab { attrs.value = "GLSL"; attrs.label = "GLSL".asTextNode() }
                Tab { attrs.value = "Feeds"; attrs.label = "Feeds".asTextNode() }
                Tab { attrs.value = "DOT"; attrs.label = "DOT".asTextNode() }
                Tab { attrs.value = "Track Candidates"; attrs.label = "Track Candidates".asTextNode() }
                Tab { attrs.value = "Linked Program"; attrs.label = "Linked Program".asTextNode() }
            }

            selectedSubject?.let { subject ->
                val programRenderPlan = subject.programRenderPlan
                val linkedProgram = programRenderPlan.linkedProgram
                val program = programRenderPlan.program as? GlslProgramImpl

                when (selectedDiagnostic) {
                    "DAG" ->
                        if (linkedProgram != null) {
                            dag {
                                attrs.fixtureType = subject.fixtureType
                                attrs.linkedProgram = linkedProgram
                            }
                        } else i { +"No program!" }

                    "GLSL" ->
                        if (program != null) {
                            glsl {
                                attrs.fixtureType = subject.fixtureType
                                attrs.source = programRenderPlan.source ?: program.fragShader.source
                            }
                        } else i { +"No program!" }

                    "Feeds" ->
                        if (program != null) {
                            feeds {
                                attrs.fixtureType = subject.fixtureType
                                attrs.program = program
                            }
                        } else i { +"No program!" }

                    "DOT" ->
                        if (linkedProgram != null) {
                            dot {
                                attrs.fixtureType = subject.fixtureType
                                attrs.linkedProgram = linkedProgram
                            }
                        } else i { +"No program!" }

                    "Track Candidates" ->
                        if (programRenderPlan.portDiagram != null) {
                            trackCandidates {
                                attrs.fixtureType = subject.fixtureType
                                attrs.portDiagram = programRenderPlan.portDiagram
                            }
                        } else i { +"No program!" }

                    "Linked Program" ->
                        if (programRenderPlan.linkedProgram != null) {
                            linkedProgram {
                                attrs.fixtureType = subject.fixtureType
                                attrs.linkedProgram = programRenderPlan.linkedProgram
                            }
                        } else i { +"No program!" }
                }
            }
        }
    }
}

private data class Subject(
    val fixtureType: FixtureType,
    val programRenderPlan: ProgramRenderPlan
) {
    override fun toString() =
        "${fixtureType.title}: ${programRenderPlan.renderTargets.size} fixtures"
}

external interface PatchDiagnosticsProps : Props {
    var renderPlanMonitor: Monitor<RenderPlan?>
    var onClose: (() -> Unit)?
}

fun RBuilder.patchDiagnostics(handler: RHandler<PatchDiagnosticsProps>) =
    child(PatchDiagnosticsView, handler = handler)