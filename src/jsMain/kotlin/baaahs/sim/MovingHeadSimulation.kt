package baaahs.sim

import baaahs.fixtures.Fixture
import baaahs.fixtures.MovingHeadDevice
import baaahs.fixtures.ResultView
import baaahs.fixtures.Transport
import baaahs.mapper.MappingSession
import baaahs.model.MovingHead
import baaahs.util.Clock
import baaahs.visualizer.EntityVisualizer
import baaahs.visualizer.movers.MovingHeadVisualizer

actual class MovingHeadSimulation actual constructor(
    val movingHead: MovingHead,
    private val simulationEnv: SimulationEnv
) : FixtureSimulation {
    private val dmxUniverse = simulationEnv[FakeDmxUniverse::class]

    override val mappingData: MappingSession.SurfaceData?
        get() = null

    override val entityVisualizer: EntityVisualizer by lazy {
        val clock = simulationEnv[Clock::class]
        MovingHeadVisualizer(movingHead, clock, dmxUniverse)
    }

    override val previewFixture: Fixture by lazy {
        val transport = PreviewTransport()

        Fixture(
            movingHead,
            1,
            listOf(movingHead.origin),
            movingHead.deviceType,
            movingHead.name,
            transport
        )
    }

    override fun launch() {
    }

    inner class PreviewTransport : Transport {
        private val movingHeadBuffer = movingHead.newBuffer(dmxUniverse)

        override val name: String
            get() = movingHead.name

        override fun send(fixture: Fixture, resultViews: List<ResultView>) {
            val params = MovingHeadDevice.getResults(resultViews)[0]
            movingHeadBuffer.pan = params.pan
            movingHeadBuffer.tilt = params.tilt
            movingHeadBuffer.colorWheelPosition = params.colorWheel
            movingHeadBuffer.dimmer = params.dimmer
        }
    }
}