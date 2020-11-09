package baaahs.gl.patch

import baaahs.RenderPlan
import baaahs.ShowRunner
import baaahs.gl.glsl.CompilationException
import baaahs.gl.glsl.GlslException
import baaahs.gl.glsl.Resolver
import baaahs.gl.render.RenderManager
import baaahs.gl.render.RenderTarget
import baaahs.glsl.GuruMeditationError
import baaahs.show.ShaderChannel
import baaahs.show.Surfaces
import baaahs.show.live.ActiveSet
import baaahs.show.live.OpenPatch

class PatchResolver(
    val renderTargets: Collection<RenderTarget>,
    val activeSet: ActiveSet
) {
    val portDiagrams: Map<Surfaces, PortDiagram> = run {
        val activePatchHolders = activeSet.getPatchHolders()
        println("active patches = ${activePatchHolders.map { it.title }}")

        val patchesBySurfaces = mutableMapOf<Surfaces, MutableList<OpenPatch>>()
        activePatchHolders.forEach { openPatchHolder ->
            openPatchHolder.patches.forEach { patch ->
                patchesBySurfaces.getOrPut(patch.surfaces) { arrayListOf() }
                    .add(patch)
            }
        }

        patchesBySurfaces.mapValues { (_, openPatches) ->
            buildPortDiagram(*openPatches.toTypedArray())
        }
    }

    fun createRenderPlan(renderManager: RenderManager, resolver: Resolver): RenderPlan {
        val linkedPatches = portDiagrams.mapValues { (_, portDiagram) ->
            portDiagram.resolvePatch(ShaderChannel.Main, ContentType.ColorStream)
        }
        val activeDataSources = mutableSetOf<String>()
        val programs = linkedPatches.mapNotNull { (_, linkedPatch) ->
            try {
                linkedPatch?.let { it to it.createProgram(renderManager, resolver) }
            } catch (e: GlslException) {
                ShowRunner.logger.error("Error preparing program", e)
                if (e is CompilationException) {
                    e.source?.let { ShowRunner.logger.info { it } }
                }
                return GuruMeditationError.createRenderPlan(renderManager)
            }
        }

        return RenderPlan(programs, activeSet)
    }

    companion object {
        fun buildPortDiagram(vararg patches: OpenPatch): PortDiagram {
            val portDiagram = PortDiagram()
            patches.forEach { patch ->
                portDiagram.add(patch)
            }
            return portDiagram
        }
    }
}