package baaahs.gl.render

import baaahs.device.DeviceType
import baaahs.fixtures.Fixture
import baaahs.fixtures.RenderPlan
import baaahs.getBang
import baaahs.gl.GlContext
import baaahs.gl.glsl.FeedResolver
import baaahs.gl.glsl.GlslProgram
import baaahs.gl.patch.LinkedPatch
import baaahs.model.Model
import baaahs.util.Logger

class RenderManager(
    private val model: Model,
    private val createContext: () -> GlContext
) {
    private val renderEngines = model.allEntities.map { it.deviceType }.distinct()
        .associateWith { deviceType ->
            val gl = createContext()
            ModelRenderEngine(
                gl, model, deviceType, resultDeliveryStrategy = pickResultDeliveryStrategy(gl)
            )
        }

    fun getEngineFor(deviceType: DeviceType): ModelRenderEngine =
        renderEngines.getBang(deviceType, "render engine")

    suspend fun draw() {
        // If there are multiple RenderEngines, let them parallelize the render step...
        renderEngines.values.forEach { it.draw() }

        // ... before transferring results back to CPU memory.
        renderEngines.values.forEach { it.finish() }
    }

    fun addFixture(fixture: Fixture): FixtureRenderTarget {
        return getEngineFor(fixture.deviceType).addFixture(fixture)
    }

    fun removeRenderTarget(renderTarget: FixtureRenderTarget) {
        getEngineFor(renderTarget.fixture.deviceType).removeRenderTarget(renderTarget)
    }

    fun compile(deviceType: DeviceType, linkedPatch: LinkedPatch, feedResolver: FeedResolver): GlslProgram {
        return getEngineFor(deviceType).compile(linkedPatch, feedResolver)
    }

    fun setRenderPlan(renderPlan: RenderPlan) {
        renderEngines.forEach { (deviceType, engine) ->
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