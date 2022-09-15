package baaahs.ui.diagnostics

import baaahs.device.FixtureType
import baaahs.gl.patch.DefaultValueNode
import baaahs.gl.patch.ExprNode
import baaahs.gl.patch.ProgramNode
import baaahs.gl.shader.InputPort
import baaahs.show.live.LinkedPatch
import baaahs.show.live.OpenPatch
import external.dagre_d3.Graph
import kotlinx.js.jso

class Dag(
    private val includePatchMods: Boolean = false
) : ProgramVisitor() {
    private var nextNode = 0
    private val nodes = mutableMapOf<Any, String>()
    private val buf = StringBuilder()

    val graph = Graph(jso { directed = true })
        .also { it.setGraph(jso {}) }
    val text get() = buf.toString()

    private fun declareNode(id: String, label: String, shape: String, style: String? = null) {
        graph.setNode(id, jso {
            this.label = label
            this.shape = shape
            this.style = style
        })
        buf.append("    $id [${attrs(label, shape, style)}];\n")
    }

    private fun attrs(label: String, shape: String, style: String?) =
        mapOf("label" to label, "shape" to shape, "style" to style)
            .filterValues { it != null }.entries.map { (k, v) -> "$k=\"$v\"" }.joinToString(" ")

    private fun declareLink(fromId: String, toId: String, label: String) {
        graph.setEdge(fromId, toId, jso {
            this.label = label
            this.style = "fill: none; stroke: #66f; stroke-width: 3px; stroke-dasharray: 5, 5;"
        })
        buf.append("    $fromId -> $toId [style=\"fill: none; stroke: #66f; stroke-width: 3px;\"];\n")
    }

    override fun visitFixtureType(fixtureType: FixtureType) {
        nodes.getOrPut(fixtureType) {
            "FT${fixtureType.id}".also {
                declareNode(
                    it, fixtureType.title, "rect",
                    "fill: #aaffaa; stroke: #060; stroke-width: 3px;"
                )
            }
        }
    }

    override fun visitDataSource(node: OpenPatch.DataSourceLink) {
        nodes.getOrPut(node) {
            "DS${node.varName}".also {
                declareNode(
                    it, node.title, "rect",
                    "fill: #ffcc66; stroke: black; stroke-width: 1px;"
                )
            }
        }
    }

    override fun visitDefault(node: DefaultValueNode) {
        nodes.getOrPut(node) {
            "V${nextNode++}".also {
                declareNode(
                    it, node.getExpression("pfx").s, "ellipse",
                    "fill: #cccccc; stroke: black; stroke-width: 1px;"
                )
            }
        }
    }

    override fun visitExpr(node: ExprNode) {
        nodes.getOrPut(node) {
            "E${nextNode++}".also {
                declareNode(
                    it, node.getExpression("pfx").s, "ellipse",
                    "fill: #cccccc; stroke: black; stroke-width: 1px;"
                )
            }
        }
    }

    override fun visitPatch(node: LinkedPatch) {
        nodes.getOrPut(node) {
            "P${nextNode++}".also {
                val fill = if (node.isPatchMod) "fill:#eecbe7" else "fill:lightblue"
                declareNode(it, node.title, "ellipse", fill)
            }
        }
    }

    override fun getIncomingLinks(node: LinkedPatch): Map<String, ProgramNode> =
        if (includePatchMods)
            super.getIncomingLinks(node)
        else
            node.unmoddedIncomingLinks

    override fun visitLink(fromNode: ProgramNode, inputPort: InputPort, toNode: ProgramNode) {
        declareLink(nodes[fromNode]!!, nodes[toNode]!!, inputPort.title)
    }

    override fun visitLink(fromNode: ProgramNode, toNode: FixtureType) {
        declareLink(nodes[fromNode]!!, nodes[toNode]!!, toNode.resultContentType.title)
    }
}