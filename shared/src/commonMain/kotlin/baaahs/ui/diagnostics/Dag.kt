package baaahs.ui.diagnostics

import baaahs.device.FixtureType
import baaahs.gl.patch.DefaultValueNode
import baaahs.gl.patch.ExprNode
import baaahs.gl.patch.ProgramNode
import baaahs.gl.shader.InputPort
import baaahs.show.live.LinkedPatch
import baaahs.show.live.OpenPatch

class DotDag(
    includePatchMods: Boolean = false
) : Dag(includePatchMods) {
    private val buf = StringBuilder()
    val text get() = buf.toString()

    override fun declareNode(id: String, label: String, shape: String, style: String?) {
        buf.append("    $id [${attrs(label, shape, style)}];\n")
    }

    override fun declareLink(fromId: String, toId: String, label: String) {
        buf.append("    $fromId -> $toId [style=\"fill: none; stroke: #66f; stroke-width: 3px;\"];\n")
    }

    private fun attrs(label: String, shape: String, style: String?) =
        mapOf("label" to label, "shape" to shape, "style" to style)
            .filterValues { it != null }.entries.joinToString(" ") { (k, v) -> "$k=\"$v\"" }
}

abstract class Dag(
    private val includePatchMods: Boolean = false
) : ProgramVisitor() {
    private var nextNode = 0
    private val nodes = mutableMapOf<Any, String>()

    abstract fun declareNode(id: String, label: String, shape: String, style: String? = null)

    abstract fun declareLink(fromId: String, toId: String, label: String)

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

    override fun visitFeed(node: OpenPatch.FeedLink) {
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