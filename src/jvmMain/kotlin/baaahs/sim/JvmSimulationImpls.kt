package baaahs.sim

import baaahs.fixtures.Fixture
import baaahs.mapper.MappingSession
import baaahs.model.LightBar
import baaahs.model.Model
import baaahs.model.MovingHead
import baaahs.visualizer.EntityVisualizer

actual class BrainSurfaceSimulation actual constructor(
    surface: Model.Surface,
    simulationEnv: SimulationEnv
) : FixtureSimulation {
    override val mappingData: MappingSession.SurfaceData? = null
    override val entityVisualizer: EntityVisualizer get() = TODO("not implemented")
    override val previewFixture: Fixture get() = TODO("not implemented")
    override fun launch():Unit = TODO("not implemented")
}

actual class LightBarSimulation actual constructor(
    lightBar: LightBar,
    simulationEnv: SimulationEnv
) : FixtureSimulation {
    override val mappingData: MappingSession.SurfaceData? = null
    override val entityVisualizer: EntityVisualizer get() = TODO("not implemented")
    override val previewFixture: Fixture get() = TODO("not implemented")
    override fun launch():Unit = TODO("not implemented")
}

actual class MovingHeadSimulation actual constructor(
    movingHead: MovingHead,
    simulationEnv: SimulationEnv
) : FixtureSimulation {
    override val mappingData: MappingSession.SurfaceData? = null
    override val entityVisualizer: EntityVisualizer get() = TODO("not implemented")
    override val previewFixture: Fixture get() = TODO("not implemented")
    override fun launch():Unit = TODO("not implemented")
}