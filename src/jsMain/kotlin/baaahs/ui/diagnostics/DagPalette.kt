package baaahs.ui.diagnostics

import baaahs.fixtures.RenderPlan
import baaahs.gl.glsl.GlslProgramImpl
import baaahs.gl.patch.DefaultValueNode
import baaahs.gl.patch.ExprNode
import baaahs.gl.patch.ProgramNode
import baaahs.show.live.LinkedPatch
import baaahs.show.live.OpenPatch
import baaahs.sim.ui.Styles
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
import react.dom.*

val DagPalette = xComponent<DagPaletteProps>("DagPalette") { props ->
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

                    typographyH6 { +"Directed Acyclic Graph, ish. One day." }

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
                                val program = programRenderPlan.program as? GlslProgramImpl
                                val linkedProgram = program?.linkedProgram

                                if (linkedProgram != null) {
                                    describe(linkedProgram.rootNode)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun RBuilder.describe(node: OpenPatch.DataSourceLink) {
    div(+Styles.box) { +"Data Source: ${node.title}" }
}

private fun RBuilder.describe(node: DefaultValueNode) {
    div(+Styles.box) { +"Default Value: ${node.getExpression("pfx").s}" }
}

private fun RBuilder.describe(node: ExprNode) {
    div(+Styles.box) { +"Expression: ${node.getExpression("pfx").s}" }
}

private fun RBuilder.describe(node: LinkedPatch) {
    div(+Styles.box) {
        header { +"Patch: ${node.title}" }

        table {
            node.incomingLinks.forEach { (id, toNode) ->
                val inputPort = node.shader.inputPorts.first { it.id == id }

                tr {
                    th { +inputPort.title }
                    td { describe(toNode) }
                }
            }
        }
    }
}

private fun RBuilder.describe(node: ProgramNode) {
    when (node) {
        is OpenPatch.DataSourceLink -> describe(node)
        is DefaultValueNode -> describe(node)
        is ExprNode -> describe(node)
        is LinkedPatch -> describe(node)
        else -> error("Huh? Unknown node type ${node::class}")
    }
}

external interface DagPaletteProps : Props {
    var renderPlanMonitor: Monitor<RenderPlan?>
}

fun RBuilder.dagPalette(handler: RHandler<DagPaletteProps>) =
    child(DagPalette, handler = handler)