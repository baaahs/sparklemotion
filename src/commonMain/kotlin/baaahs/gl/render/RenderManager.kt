package baaahs.gl.render

import baaahs.device.FixtureType
import baaahs.fixtures.Fixture
import baaahs.fixtures.RenderPlan
import baaahs.gl.GlContext
import baaahs.gl.glsl.FeedResolver
import baaahs.gl.glsl.GlslProgram
import baaahs.gl.patch.LinkedProgram
import baaahs.util.CacheBuilder
import baaahs.util.Logger

class RenderManager(
    private val createContext: () -> GlContext
) {
    private val renderEngines = CacheBuilder<FixtureType, ModelRenderEngine> { fixtureType ->
        val gl = createContext()
        ModelRenderEngine(
            gl, fixtureType, resultDeliveryStrategy = pickResultDeliveryStrategy(gl)
        )
    }

    private fun engineFor(fixtureType: FixtureType): ModelRenderEngine =
        renderEngines.getBang(fixtureType, "render engine")

    suspend fun draw() {
        // If there are multiple RenderEngines, let them parallelize the render step...
        renderEngines.values.forEach { it.draw() }

        // ... before transferring results back to CPU memory.
        renderEngines.values.forEach { it.finish() }
    }

    fun addFixture(fixture: Fixture): FixtureRenderTarget {
        return engineFor(fixture.fixtureType).addFixture(fixture)
    }

    fun removeRenderTarget(renderTarget: FixtureRenderTarget) {
        engineFor(renderTarget.fixture.fixtureType).removeRenderTarget(renderTarget)
    }

    fun compile(fixtureType: FixtureType, linkedProgram: LinkedProgram, feedResolver: FeedResolver): GlslProgram {
        return engineFor(fixtureType).compile(linkedProgram, feedResolver)
    }

    fun setRenderPlan(renderPlan: RenderPlan) {
        renderEngines.all.forEach { (fixtureType, engine) ->
            val fixtureTypeRenderPlan = renderPlan.fixtureTypes[fixtureType]
            engine.setRenderPlan(fixtureTypeRenderPlan)
            if (fixtureTypeRenderPlan == null) {
                logger.debug { "No render plan for ${fixtureType.title}" }
            }
        }
    }

    fun logStatus() {
        renderEngines.values.forEach { it.logStatus() }
    }

    fun release() {
        renderEngines.values.forEach { it.release() }
        renderEngines.clear()
    }

    companion object {
        private val logger = Logger<RenderManager>()
    }
}