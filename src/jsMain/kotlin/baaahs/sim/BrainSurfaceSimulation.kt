package baaahs.sim

import baaahs.device.PixelArrayDevice
import baaahs.fixtures.Fixture
import baaahs.fixtures.FixtureConfig
import baaahs.geom.Vector3F
import baaahs.geom.toVector3F
import baaahs.io.ByteArrayReader
import baaahs.mapper.MappingSession
import baaahs.model.Model
import baaahs.sm.brain.BrainManager
import baaahs.sm.brain.sim.BrainSimulatorManager
import baaahs.visualizer.*

actual class BrainSurfaceSimulation actual constructor(
    val surface: Model.Surface,
    private val simulationEnv: SimulationEnv
) : FixtureSimulation {
    val surfaceGeometry by lazy { SurfaceGeometry(surface) }

    val pixelPositions by lazy {
        val pixelArranger = simulationEnv[PixelArranger::class]
        pixelArranger.arrangePixels(surfaceGeometry, surface.expectedPixelCount)
    }

    val vizPixels by lazy { VizPixels(pixelPositions, surfaceGeometry.panelNormal) }

    val brain by lazy {
        val brainSimulatorManager = simulationEnv[BrainSimulatorManager::class]
        brainSimulatorManager.createBrain(surface.name, vizPixels)
    }

    override val mappingData: MappingSession.SurfaceData
        by lazy {
            MappingSession.SurfaceData(
                BrainManager.controllerTypeName,
                brain.id,
                surface.name,
                pixelPositions.size,
                pixelPositions.map {
                    MappingSession.SurfaceData.PixelData(it.toVector3F(), null, null)
                }, null, null, null
            )
        }

    override val entityVisualizer: SurfaceVisualizer by lazy { SurfaceVisualizer(surfaceGeometry, vizPixels) }

    override val previewFixture: Fixture by lazy {
        Fixture(
            surface,
            pixelPositions.size,
            pixelPositions.map { it.toVector3F() },
            surface.deviceType.defaultConfig,
            surface.name,
            PixelArrayPreviewTransport(surface.name, vizPixels)
        )
    }

    override fun launch() {
        brain.run {}
    }

    override fun updateVisualizerWith(fixtureConfig: FixtureConfig, pixelCount: Int, pixelLocations: Array<Vector3F>) {
        entityVisualizer.vizPixels = VizPixels(
            pixelLocations.map { it.toVector3() }.toTypedArray(),
            entityVisualizer.surfaceGeometry.panelNormal,
            fixtureConfig as PixelArrayDevice.Config
        )
    }

    override fun receiveRemoteVisualizationFrameData(reader: ByteArrayReader) {
        entityVisualizer.vizPixels?.readColors(reader)
    }
}