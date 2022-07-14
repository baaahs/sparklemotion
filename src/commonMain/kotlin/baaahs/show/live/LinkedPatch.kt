package baaahs.show.live

import baaahs.app.ui.patchmod.PatchMod
import baaahs.gl.patch.Component
import baaahs.gl.patch.ProgramLinker
import baaahs.gl.patch.ProgramNode
import baaahs.gl.patch.ShaderComponent
import baaahs.gl.shader.OpenShader
import baaahs.gl.shader.OutputPort
import baaahs.show.Stream

class LinkedPatch(
    val shader: OpenShader,
    val incomingLinks: Map<String, ProgramNode>,
    val stream: Stream,
    val priority: Float,
    val injectedPorts: Set<String> = emptySet(),
    val patchMods: List<PatchMod> = emptyList()
) : ProgramNode {
    override val title: String get() = shader.title
    override val outputPort: OutputPort get() = shader.outputPort

    override fun getNodeId(programLinker: ProgramLinker): String = programLinker.idFor(shader.shader)

    override fun traverse(programLinker: ProgramLinker) {
        programLinker.visit(shader)
        incomingLinks.forEach { (inputPortId, link) ->
            var redirectableLink = link
            patchMods.forEach { patchMod ->
                val oldLink = redirectableLink
                redirectableLink = patchMod.maybeWrapLink(inputPortId, redirectableLink)
                    ?: redirectableLink

                if (redirectableLink !== oldLink)
                    println("Redirected $oldLink via $redirectableLink.")
            }
            programLinker.visit(redirectableLink)
        }
    }

    override fun buildComponent(id: String, index: Int, findUpstreamComponent: (ProgramNode) -> Component): Component {
        return ShaderComponent(id, index, this, findUpstreamComponent)
    }

    override fun toString(): String = "LinkedPatch(shader=${shader.title})"
}