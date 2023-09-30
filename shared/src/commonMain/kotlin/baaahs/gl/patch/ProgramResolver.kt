package baaahs.gl.patch

import baaahs.device.FixtureType
import baaahs.fixtures.FixtureTypeRenderPlan
import baaahs.fixtures.ProgramRenderPlan
import baaahs.fixtures.RenderPlan
import baaahs.gl.glsl.CompilationException
import baaahs.gl.glsl.FeedResolver
import baaahs.gl.glsl.GlslException
import baaahs.gl.render.RenderEngine
import baaahs.gl.render.RenderManager
import baaahs.gl.render.RenderTarget
import baaahs.glsl.GuruMeditationError
import baaahs.show.Feed
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
            val patchSetsByKey = mutableMapOf<String, List<OpenPatch>>()
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
        feeds: Map<String, Feed>,
        feedResolver: FeedResolver
    ): RenderPlan {
        return RenderPlan(
            portDiagrams.mapValues { (fixtureType, devicePortDiagrams) ->
                val programsRenderPlans = devicePortDiagrams.map { (portDiagram, renderTargets) ->
                    val linkedProgram = portDiagram.resolvePatch(
                        Stream.Main,
                        fixtureType.resultContentType,
                        feeds
                    )

                    var source: String? = null

                    renderTargets.groupBy { it.renderEngine }.map { (engine, engineTargets) ->
                        createProgramRenderPlan(linkedProgram, engine, feedResolver, source, fixtureType, engineTargets, portDiagram)
                    }
                }

                FixtureTypeRenderPlan(programsRenderPlans.flatten())
            }
        )
    }

    private fun createProgramRenderPlan(
        linkedProgram: LinkedProgram?,
        engine: RenderEngine,
        feedResolver: FeedResolver,
        source: String?,
        fixtureType: FixtureType,
        engineTargets: List<RenderTarget>,
        portDiagram: PortDiagram
    ): ProgramRenderPlan {
        var source1 = source
        val program = linkedProgram?.let {
            try {
                engine.compile(linkedProgram, feedResolver)
                    .bind()
            } catch (e: GlslException) {
                logger.error(e) { "Error preparing program." }
                if (e is CompilationException) {
                    source1 = e.source
                    e.source?.let { logger.error { it } }
                }

                engine.compile(GuruMeditationError(fixtureType).linkedProgram, feedResolver)
                    .bind()
            }
        }

        return ProgramRenderPlan(program, engineTargets, linkedProgram, source1, portDiagram)
    }


    companion object {
        private val logger = Logger<ProgramResolver>()

        fun buildPortDiagram(vararg patches: OpenPatch) = PortDiagram(patches.toList())
    }
}