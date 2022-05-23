package baaahs.ui.diagnostics

import baaahs.app.ui.editor.betterSelect
import baaahs.device.FixtureType
import baaahs.fixtures.ProgramRenderPlan
import baaahs.fixtures.RenderPlan
import baaahs.ui.asTextNode
import baaahs.ui.components.palette
import baaahs.ui.typographyH6
import baaahs.ui.xComponent
import baaahs.util.Monitor
import baaahs.window
import mui.material.Tab
import mui.material.Tabs
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.i

val DiagnosticsPaletteView = xComponent<DiagnosticsPaletteProps>("DiagnosticsPalette") { props ->
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
        attrs.initialWidth = window.innerWidth / 3
        attrs.initialHeight = window.innerHeight * 2 / 3

        typographyH6 { +"Patch Diagnostics" }

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
                attrs.onChange = { _, value ->
                    selectedDiagnostic = value
                }

                Tab { attrs.value = "DAG"; attrs.label = "DAG".asTextNode() }
                Tab { attrs.value = "GLSL"; attrs.label = "GLSL".asTextNode() }
                Tab { attrs.value = "DOT"; attrs.label = "DOT".asTextNode() }
            }

            selectedSubject?.let { subject ->
                val program = subject.programRenderPlan.program
                if (program != null) {
                    when (selectedDiagnostic) {
                        "DAG" -> dag { attrs.program = program }
                        "GLSL" -> glsl { attrs.program = program }
                        "DOT" -> dot { attrs.program = program }
                    }
                } else {
                    i { +"No program!" }
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

external interface DiagnosticsPaletteProps : Props {
    var renderPlanMonitor: Monitor<RenderPlan?>
}

fun RBuilder.diagnosticsPalette(handler: RHandler<DiagnosticsPaletteProps>) =
    child(DiagnosticsPaletteView, handler = handler)