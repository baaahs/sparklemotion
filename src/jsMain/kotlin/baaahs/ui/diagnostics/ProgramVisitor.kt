package baaahs.ui.diagnostics

import baaahs.gl.patch.DefaultValueNode
import baaahs.gl.patch.ExprNode
import baaahs.gl.patch.LinkedProgram
import baaahs.gl.patch.ProgramNode
import baaahs.gl.shader.InputPort
import baaahs.show.live.LinkedPatch
import baaahs.show.live.OpenPatch

open class ProgramVisitor {
    open fun visitDataSource(node: OpenPatch.DataSourceLink) {}

    open fun visitDefault(node: DefaultValueNode) {}

    open fun visitExpr(node: ExprNode) {}

    open fun visitPatchInternal(node: LinkedPatch) {
        visitPatch(node)

        node.incomingLinks.forEach { (linkId, toNode) ->
            val inputPort = node.shader.inputPorts.first { it.id == linkId }

            visitNode(toNode)
            visitLink(toNode, inputPort, node)
        }
    }

    open fun visitPatch(node: LinkedPatch) {}

    open fun visitLink(fromNode: ProgramNode, inputPort: InputPort, toNode: ProgramNode) {}

    open fun visitNode(node: ProgramNode) =
        when (node) {
            is OpenPatch.DataSourceLink -> visitDataSource(node)
            is DefaultValueNode -> visitDefault(node)
            is ExprNode -> visitExpr(node)
            is LinkedPatch -> visitPatchInternal(node)
            else -> error("Huh? Unknown node type ${node::class}")
        }

    open fun visit(program: LinkedProgram) {
        visitNode(program.rootNode)
    }
}