package baaahs.ui.diagnostics

import baaahs.fixtures.RenderPlan
import baaahs.gl.glsl.GlslProgramImpl
import baaahs.gl.patch.DefaultValueNode
import baaahs.gl.patch.ExprNode
import baaahs.gl.patch.ProgramNode
import baaahs.show.live.LinkedPatch
import baaahs.show.live.OpenPatch
import baaahs.sim.ui.Styles
import baaahs.ui.asTextNode
import baaahs.ui.components.palette
import baaahs.ui.typographyH6
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import baaahs.util.Monitor
import kotlinx.css.UserSelect
import kotlinx.css.userSelect
import mui.material.Tab
import mui.material.Tabs
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.*
import styled.inlineStyles

val DagPaletteView = xComponent<DagPaletteProps>("DagPalette") { props ->
    observe(props.renderPlanMonitor)

    val renderPlans = props.renderPlanMonitor.value

    var selectedTabIndex by state { 0 }

    palette {
        typographyH6 { +"Directed Acyclic Graph, ish. One day." }

        div(+Styles.glslContentDiv) {
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
                                attrs.label =
                                    "${fixtureType.title}: ${programRenderPlan.renderTargets.size} fixtures".asTextNode()
                            }
                        }
                    }
                }

                val (_, renderPlan) = plans[selectedTabIndex]
                renderPlan.programs.forEach { programRenderPlan ->
                    val program = programRenderPlan.program as? GlslProgramImpl
                    val linkedProgram = program?.linkedProgram
                    if (linkedProgram != null) {
                        pre {
                            inlineStyles {
                                userSelect = UserSelect.all
                            }
                            dag {
                                attrs.rootNode = linkedProgram.rootNode
                            }

                            +DotDag(linkedProgram.rootNode).text
                        }

                        describe(linkedProgram.rootNode)
                    }
                }
            }
        }
    }
}


/*
* /* Example */
digraph {
    /* Note: HTML labels do not work in IE, which lacks support for <foreignObject> tags. */
    node [rx=5 ry=5 labelStyle="font: 300 14px 'Helvetica Neue', Helvetica"]
    edge [labelStyle="font: 300 14px 'Helvetica Neue', Helvetica"]
    A [labelType="html"
       label="A <span style='font-size:32px'>Big</span> <span style='color:red;'>HTML</span> Source!"];
    C;
    E [label="Bold Red Sink" style="fill: #f77; font-weight: bold"];
    A -> B -> C;
    B -> D [label="A blue label" labelStyle="fill: #55f; font-weight: bold;"];
    D -> E [label="A thick red edge" style="stroke: #f77; stroke-width: 2px;" arrowheadStyle="fill: #f77"];
    C -> E;
    A -> D [labelType="html" label="A multi-rank <span style='color:blue;'>HTML</span> edge!"];
}
  */
class DotDag(rootNode: ProgramNode) {
    private var nextNode = 0
    private val nodes = mutableMapOf<Any, String>()
    private val buf = StringBuilder()
    val text get() = buf.toString()

    init {
        buf.append("digraph {\n")
        visit(rootNode)
        buf.append("}\n")
    }

    fun declareNode(id: String, mapOf: Map<String, String>) {
        buf.append("    $id [${mapOf.entries.map { (k, v) -> "$k=\"$v\"" }.joinToString(" ")}];\n")
    }

    fun declareLink(fromId: String, toId: String, mapOf: Map<String, String>) {
        buf.append("    $fromId -> $toId [${mapOf.entries.map { (k, v) -> "$k=\"$v\"" }.joinToString(" ")}];\n")
    }

    fun visit(node: OpenPatch.DataSourceLink) =
        nodes.getOrPut(node) {
            "DS${nextNode++}".also {
                declareNode(it, mapOf("label" to node.title, "shape" to "rect"))
            }
        }

    fun visit(node: DefaultValueNode) =
        nodes.getOrPut(node) {
            "V${nextNode++}".also {
                declareNode(it, mapOf("label" to node.getExpression("pfx").s, "shape" to "ellipse"))
            }
        }

    fun visit(node: ExprNode) =
        nodes.getOrPut(node) {
            "E${nextNode++}".also {
                declareNode(it, mapOf("label" to node.getExpression("pfx").s, "shape" to "ellipse"))
            }
        }

    fun visit(node: LinkedPatch): String {
        val id = nodes.getOrPut(node) {
            "P${nextNode++}".also {
                declareNode(it, mapOf("label" to node.title, "shape" to "circle", "style" to "fill:pink"))
            }
        }

        node.incomingLinks.forEach { (linkId, toNode) ->
            val inputPort = node.shader.inputPorts.first { it.id == linkId }

            declareLink(visit(toNode), id, mapOf("label" to inputPort.title))
        }
        return id
    }

    fun visit(node: ProgramNode) =
        when (node) {
            is OpenPatch.DataSourceLink -> visit(node)
            is DefaultValueNode -> visit(node)
            is ExprNode -> visit(node)
            is LinkedPatch -> visit(node)
            else -> error("Huh? Unknown node type ${node::class}")
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
    child(DagPaletteView, handler = handler)