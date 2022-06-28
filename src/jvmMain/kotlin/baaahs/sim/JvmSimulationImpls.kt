package baaahs.sim

import baaahs.fixtures.Fixture
import baaahs.mapper.MappingSession
import baaahs.model.LightRing
import baaahs.model.Model
import baaahs.model.MovingHead
import baaahs.model.PixelArray
import baaahs.visualizer.EntityAdapter

actual class BrainSurfaceSimulation actual constructor(
    surface: Model.Surface,
    adapter: EntityAdapter
) : FixtureSimulation {
    override val mappingData: MappingSession.SurfaceData? = null
    override val itemVisualizer get() = TODO("not implemented")
    override val previewFixture: Fixture get() = TODO("not implemented")
    override fun start():Unit = TODO("not implemented")
}

actual class LightBarSimulation actual constructor(
    pixelArray: PixelArray,
    adapter: EntityAdapter
) : PixelArraySimulation(pixelArray, adapter) {
    override val mappingData: MappingSession.SurfaceData? = null
    override val itemVisualizer get() = TODO("not implemented")
    override val previewFixture: Fixture get() = TODO("not implemented")
    override fun start():Unit = TODO("not implemented")
}

actual class LightRingSimulation actual constructor(
    lightRing: LightRing,
    adapter: EntityAdapter
) : PixelArraySimulation(lightRing, adapter) {
    override val mappingData: MappingSession.SurfaceData? = null
    override val itemVisualizer get() = TODO("not implemented")
    override val previewFixture: Fixture get() = TODO("not implemented")
    override fun start():Unit = TODO("not implemented")
}

actual class MovingHeadSimulation actual constructor(
    movingHead: MovingHead,
    adapter: EntityAdapter
) : FixtureSimulation {
    override val mappingData: MappingSession.SurfaceData? = null
    override val itemVisualizer get() = TODO("not implemented")
    override val previewFixture: Fixture get() = TODO("not implemented")
    override fun start():Unit = TODO("not implemented")
}