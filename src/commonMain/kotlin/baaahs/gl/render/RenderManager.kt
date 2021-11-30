package baaahs.gl.render

import baaahs.device.DeviceType
import baaahs.fixtures.Fixture
import baaahs.fixtures.RenderPlan
import baaahs.gl.GlContext
import baaahs.gl.glsl.FeedResolver
import baaahs.gl.glsl.GlslProgram
import baaahs.gl.patch.LinkedPatch
import baaahs.util.CacheBuilder
import baaahs.util.Logger

class RenderManager(
    private val createContext: () -> GlContext
) {
    private val renderEngines = CacheBuilder<DeviceType, ModelRenderEngine> { deviceType ->
        val gl = createContext()
        ModelRenderEngine(
            gl, deviceType, resultDeliveryStrategy = pickResultDeliveryStrategy(gl)
        )
    }

    private fun engineFor(deviceType: DeviceType): ModelRenderEngine =
        renderEngines.getBang(deviceType, "render engine")

    suspend fun draw() {
        // If there are multiple RenderEngines, let them parallelize the render step...
        renderEngines.values.forEach { it.draw() }

        // ... before transferring results back to CPU memory.
        renderEngines.values.forEach { it.finish() }
    }

    fun addFixture(fixture: Fixture): FixtureRenderTarget {
        return engineFor(fixture.deviceType).addFixture(fixture)
    }

    fun removeRenderTarget(renderTarget: FixtureRenderTarget) {
        engineFor(renderTarget.fixture.deviceType).removeRenderTarget(renderTarget)
    }

    fun compile(deviceType: DeviceType, linkedPatch: LinkedPatch, feedResolver: FeedResolver): GlslProgram {
        return engineFor(deviceType).compile(linkedPatch, feedResolver)
    }

    fun setRenderPlan(renderPlan: RenderPlan) {
        renderEngines.all.forEach { (deviceType, engine) ->
            val deviceTypeRenderPlan = renderPlan.deviceTypes[deviceType]
            engine.setRenderPlan(deviceTypeRenderPlan)
            if (deviceTypeRenderPlan == null) {
                logger.debug { "No render plan for ${deviceType.title}" }
            }
        }
    }

    fun logStatus() {
        renderEngines.values.forEach { it.logStatus() }
    }

    companion object {
        private val logger = Logger<RenderManager>()
    }
}