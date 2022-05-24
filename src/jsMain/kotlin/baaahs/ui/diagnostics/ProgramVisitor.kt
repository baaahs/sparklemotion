package baaahs.ui.diagnostics

import baaahs.device.FixtureType
import baaahs.gl.patch.DefaultValueNode
import baaahs.gl.patch.ExprNode
import baaahs.gl.patch.LinkedProgram
import baaahs.gl.patch.ProgramNode
import baaahs.gl.shader.InputPort
import baaahs.show.live.LinkedPatch
import baaahs.show.live.OpenPatch

abstract class ProgramVisitor {
    abstract fun visitFixtureType(fixtureType: FixtureType)

    abstract fun visitDataSource(node: OpenPatch.DataSourceLink)

    abstract fun visitDefault(node: DefaultValueNode)

    abstract fun visitExpr(node: ExprNode)

    private fun visitPatchInternal(node: LinkedPatch) {
        visitPatch(node)

        node.incomingLinks.forEach { (linkId, toNode) ->
            val inputPort = node.shader.inputPorts.first { it.id == linkId }

            visitNode(toNode)
            visitLink(toNode, inputPort, node)
        }
    }

    abstract fun visitPatch(node: LinkedPatch)

    abstract fun visitLink(fromNode: ProgramNode, inputPort: InputPort, toNode: ProgramNode)

    abstract fun visitLink(fromNode: ProgramNode, toNode: FixtureType)

    open fun visitNode(node: ProgramNode) =
        when (node) {
            is OpenPatch.DataSourceLink -> visitDataSource(node)
            is DefaultValueNode -> visitDefault(node)
            is ExprNode -> visitExpr(node)
            is LinkedPatch -> visitPatchInternal(node)
            else -> error("Huh? Unknown node type ${node::class}")
        }

    open fun visit(fixtureType: FixtureType, program: LinkedProgram) {
        visitFixtureType(fixtureType)
        visitNode(program.rootNode)
        visitLink(program.rootNode, fixtureType)
    }
}