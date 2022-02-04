package baaahs.sim

import baaahs.device.PixelArrayDevice
import baaahs.fixtures.Fixture
import baaahs.fixtures.FixtureConfig
import baaahs.geom.Vector3F
import baaahs.io.ByteArrayReader
import baaahs.mapper.MappingSession
import baaahs.model.Model
import baaahs.randomDelay
import baaahs.sm.brain.BrainManager
import baaahs.sm.brain.sim.BrainSimulatorManager
import baaahs.util.globalLaunch
import baaahs.visualizer.*
import three_ext.toVector3F

actual class BrainSurfaceSimulation actual constructor(
    private val surface: Model.Surface,
    private val simulationEnv: SimulationEnv,
    adapter: EntityAdapter
) : FixtureSimulation {
    private val surfaceGeometry by lazy { SurfaceGeometry(surface) }

    private val pixelPositions by lazy {
        val pixelArranger = simulationEnv[PixelArranger::class]
        pixelArranger.arrangePixels(surfaceGeometry, surface.expectedPixelCount)
    }

    private val vizPixels by lazy { VizPixels(pixelPositions, surfaceGeometry.panelNormal, surface.transformation) }

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

    override val itemVisualizer: SurfaceVisualizer by lazy {
        SurfaceVisualizer(surface, surfaceGeometry, vizPixels)
    }

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

    override fun start() {
        globalLaunch {
            randomDelay(1000)
            brain.start()
        }
    }

    override fun stop() {
        brain.stop()
    }

    override fun updateVisualizerWith(fixtureConfig: FixtureConfig, pixelCount: Int, pixelLocations: Array<Vector3F>) {
        itemVisualizer.vizPixels = VizPixels(
            pixelLocations.map { it.toVector3() }.toTypedArray(),
            itemVisualizer.surfaceGeometry.panelNormal,
            surface.transformation,
            fixtureConfig as PixelArrayDevice.Config
        )
    }

    override fun receiveRemoteVisualizationFrameData(reader: ByteArrayReader) {
        itemVisualizer.vizPixels?.readColors(reader)
    }
}