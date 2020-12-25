package baaahs.show.live

import baaahs.gl.patch.Component
import baaahs.gl.patch.ProgramLinker
import baaahs.gl.patch.ProgramNode
import baaahs.gl.patch.ShaderComponent
import baaahs.gl.shader.OpenShader
import baaahs.gl.shader.OutputPort
import baaahs.show.ShaderChannel

class LinkedShaderInstance(
    val shader: OpenShader,
    val incomingLinks: Map<String, ProgramNode>,
    val shaderChannel: ShaderChannel,
    val priority: Float
) : ProgramNode {
    override val title: String get() = shader.title
    override val outputPort: OutputPort get() = shader.outputPort

    override fun getNodeId(programLinker: ProgramLinker): String = programLinker.idFor(shader.shader)

    override fun traverse(programLinker: ProgramLinker, depth: Int) {
        programLinker.visit(shader)
        incomingLinks.forEach { (_, link) -> programLinker.visit(link) }
    }

    override fun buildComponent(id: String, index: Int, findUpstreamComponent: (ProgramNode) -> Component): Component {
        return ShaderComponent(id, index, this, findUpstreamComponent)
    }
}