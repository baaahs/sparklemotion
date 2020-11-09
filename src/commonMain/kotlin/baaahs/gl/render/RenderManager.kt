package baaahs.gl.render

import baaahs.fixtures.DeviceType
import baaahs.fixtures.Fixture
import baaahs.getBang
import baaahs.gl.GlContext
import baaahs.model.Model

class RenderManager(
    private val model: Model,
    private val createContext: () -> GlContext
) {
    private val renderEngines = model.allEntities.map { it.deviceType }.distinct()
        .associateWith { deviceType -> RenderEngine(createContext(), model, deviceType) }

    fun getEngineFor(deviceType: DeviceType): RenderEngine =
        renderEngines.getBang(deviceType, "render engine")

    fun draw() {
        renderEngines.values.forEach { it.draw() }
    }

    fun addFixture(fixture: Fixture): RenderTarget {
        return getEngineFor(fixture.deviceType).addFixture(fixture)
    }

    fun removeRenderTarget(renderTarget: RenderTarget) {
        getEngineFor(renderTarget.fixture.deviceType).removeRenderTarget(renderTarget)
    }
}