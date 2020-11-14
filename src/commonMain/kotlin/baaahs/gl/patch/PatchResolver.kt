package baaahs.gl.patch

import baaahs.ShowRunner
import baaahs.fixtures.DeviceType
import baaahs.fixtures.DeviceTypeRenderPlan
import baaahs.fixtures.ProgramRenderPlan
import baaahs.fixtures.RenderPlan
import baaahs.gl.glsl.CompilationException
import baaahs.gl.glsl.FeedResolver
import baaahs.gl.glsl.GlslException
import baaahs.gl.render.RenderManager
import baaahs.gl.render.RenderTarget
import baaahs.glsl.GuruMeditationError
import baaahs.show.ShaderChannel
import baaahs.show.live.ActivePatchSet
import baaahs.show.live.OpenPatch

class PatchResolver(
    private val renderManager: RenderManager,
    private val renderTargets: Collection<RenderTarget>,
    private val activePatchSet: ActivePatchSet
) {
    init {

    }
    val portDiagrams =
        renderTargets
            .groupBy { it.fixture.deviceType }
            .mapValues { (_, renderTargets) ->
                val patchSetsByKey = mutableMapOf<String, PatchSet>()
                val renderTargetsByKey = mutableMapOf<String, MutableList<RenderTarget>>()

                renderTargets.forEach { renderTarget ->
                    val patchSet = activePatchSet.activePatches
                        .filter { patch -> patch.matches(renderTarget.fixture) }
                    val key = patchSet.joinToString(":") { it.serial.toString(16) }

                    patchSetsByKey[key] = patchSet
                    renderTargetsByKey.getOrPut(key) { mutableListOf() }
                        .add(renderTarget)
                }

                patchSetsByKey.map { (key, patchSet) ->
                    PortDiagram(patchSet) to renderTargetsByKey[key]!! as List<RenderTarget>
                }
            }

    fun createRenderPlan(feedResolver: FeedResolver): RenderPlan {
        return RenderPlan(
            portDiagrams.mapValues { (deviceType, devicePortDiagrams) ->
                val programsRenderPlans = devicePortDiagrams.map { (portDiagram, renderTargets) ->
                    val linkedPatch = portDiagram.resolvePatch(ShaderChannel.Main, deviceType.resultContentType)
                    val program = linkedPatch?.let {
                        buildProgram(it, deviceType, feedResolver)
                    }

                    ProgramRenderPlan(program, renderTargets)
                }

                DeviceTypeRenderPlan(programsRenderPlans)
            }
        )
    }

    private fun buildProgram(
        linkedPatch: LinkedPatch,
        deviceType: DeviceType,
        feedResolver: FeedResolver
    ) = try {
        renderManager.compile(deviceType, linkedPatch, feedResolver)
    } catch (e: GlslException) {
        ShowRunner.logger.error("Error preparing program", e)
        if (e is CompilationException) {
            e.source?.let { ShowRunner.logger.info { it } }
        }

        renderManager.compile(
            deviceType, GuruMeditationError(deviceType).linkedPatch, feedResolver
        )
    }

    companion object {
        fun buildPortDiagram(vararg patches: OpenPatch) = PortDiagram(patches.toList())
    }
}

private typealias PatchSet = List<OpenPatch>