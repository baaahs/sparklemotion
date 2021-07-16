package baaahs.sim

import baaahs.BrainManager
import baaahs.fixtures.Fixture
import baaahs.fixtures.FixtureManager
import baaahs.fixtures.ResultView
import baaahs.fixtures.Transport
import baaahs.geom.toVector3F
import baaahs.mapper.MappingSession
import baaahs.model.Model
import baaahs.visualizer.PixelArranger
import baaahs.visualizer.SurfaceGeometry
import baaahs.visualizer.SurfaceVisualizer
import baaahs.visualizer.TexturePixelsVisualizer

class DirectSurfaceSimulation(
    val surface: Model.Surface,
    private val simulationEnv: SimulationEnv
) : FixtureSimulation {
    val surfaceGeometry by lazy { SurfaceGeometry(surface) }

    val pixelPositions by lazy {
        val pixelArranger = simulationEnv[PixelArranger::class]
        pixelArranger.arrangePixels(surfaceGeometry, surface.expectedPixelCount)
    }

    override val mappingData: MappingSession.SurfaceData
            by lazy {
                MappingSession.SurfaceData(
                    BrainManager.controllerTypeName,
                    "xxx",
                    surface.name,
                    pixelPositions.map {
                        MappingSession.SurfaceData.PixelData(it.toVector3F(), null, null)
                    }, null, null, null
                )
            }

    val pixelsVisualizer by lazy { TexturePixelsVisualizer(pixelPositions, surfaceGeometry.panelNormal) }

    override val entityVisualizer: SurfaceVisualizer
        by lazy { SurfaceVisualizer(surfaceGeometry, pixelsVisualizer) }

    override val previewFixture: Fixture by lazy {
        val transport = PixelArrayDirectTransport(this)
        Fixture(
            surface,
            pixelPositions.size,
            pixelPositions.map { it.toVector3F() },
            surface.deviceType,
            surface.name,
            transport
        )
    }

    override fun launch() {
        simulationEnv.get(FixtureManager::class).fixturesChanged(
            listOf(previewFixture), emptyList()
        )
    }
}

class PixelArrayDirectTransport(
    private val directSurfaceSimulation: DirectSurfaceSimulation
) : Transport {
    override val name: String
        get() = TODO("PixelArrayDirectTransport.name not implemented")

    override fun send(fixture: Fixture, resultViews: List<ResultView>) {
        directSurfaceSimulation.pixelsVisualizer.receivedFrame(resultViews)
    }

}