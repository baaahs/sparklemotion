package baaahs.gl.patch

import baaahs.ShowRunner
import baaahs.device.FixtureType
import baaahs.fixtures.FixtureTypeRenderPlan
import baaahs.fixtures.ProgramRenderPlan
import baaahs.fixtures.RenderPlan
import baaahs.gl.glsl.CompilationException
import baaahs.gl.glsl.FeedResolver
import baaahs.gl.glsl.GlslException
import baaahs.gl.render.RenderManager
import baaahs.gl.render.RenderTarget
import baaahs.glsl.GuruMeditationError
import baaahs.show.DataSource
import baaahs.show.Stream
import baaahs.show.live.ActivePatchSet
import baaahs.show.live.OpenPatch
import baaahs.util.Logger

class ProgramResolver(
    renderTargets: Collection<RenderTarget>,
    private val activePatchSet: ActivePatchSet,
    private val renderManager: RenderManager
) {
    val portDiagrams = renderTargets
        .groupBy { it.fixture.fixtureType }
        .mapValues { (_, renderTargets) ->
            val patchSetsByKey = mutableMapOf<String, PatchSet>()
            val renderTargetsByPatchSetKey = mutableMapOf<String, MutableList<RenderTarget>>()

            renderTargets.forEach { renderTarget ->
                val patchSet = activePatchSet.forFixture(renderTarget.fixture)
                val key = patchSet.joinToString(":") { it.serial.toString(16) }

                patchSetsByKey[key] = patchSet
                renderTargetsByPatchSetKey.getOrPut(key) { mutableListOf() }
                    .add(renderTarget)
            }

            patchSetsByKey.map { (key, patchSet) ->
                PortDiagram(patchSet) to
                        renderTargetsByPatchSetKey[key]!! as List<RenderTarget>
            }
        }

    fun createRenderPlan(
        dataSources: Map<String, DataSource>,
        feedResolver: FeedResolver
    ): RenderPlan {
        return RenderPlan(
            portDiagrams.mapValues { (fixtureType, devicePortDiagrams) ->
                val programsRenderPlans = devicePortDiagrams.map { (portDiagram, renderTargets) ->
                    val linkedPatch = portDiagram.resolvePatch(
                        Stream.Main,
                        fixtureType.resultContentType,
                        dataSources
                    )
                    val program = linkedPatch?.let {
                        buildProgram(it, fixtureType, feedResolver)
                    }

                    ProgramRenderPlan(program, renderTargets)
                }

                FixtureTypeRenderPlan(programsRenderPlans)
            }
        )
    }

    private fun buildProgram(
        linkedProgram: LinkedProgram,
        fixtureType: FixtureType,
        feedResolver: FeedResolver
    ) = try {
        renderManager.compile(fixtureType, linkedProgram, feedResolver)
    } catch (e: GlslException) {
        logger.error(e) { "Error preparing program" }
        if (e is CompilationException) {
            e.source?.let { ShowRunner.logger.info { it } }
        }

        renderManager.compile(
            fixtureType, GuruMeditationError(fixtureType).linkedProgram, feedResolver
        )
    }

    companion object {
        private val logger = Logger<ProgramResolver>()

        fun buildPortDiagram(vararg patches: OpenPatch) = PortDiagram(patches.toList())
    }
}

private typealias PatchSet = List<OpenPatch>