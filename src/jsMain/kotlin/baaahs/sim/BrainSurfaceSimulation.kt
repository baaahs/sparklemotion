package baaahs.sim

import baaahs.BrainManager
import baaahs.fixtures.Fixture
import baaahs.geom.Vector3F
import baaahs.geom.toVector3F
import baaahs.io.ByteArrayReader
import baaahs.mapper.MappingSession
import baaahs.model.Model
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
        val brainsSimulator = simulationEnv[BrainsSimulator::class]
        brainsSimulator.createBrain(surface.name, vizPixels)
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
            surface.deviceType,
            surface.name,
            PixelArrayPreviewTransport(surface.name, vizPixels)
        )
    }

    override fun launch() {
        brain.run {}
    }

    override fun receiveRemoteVisualizationFixtureInfo(reader: ByteArrayReader) {
        val pixelCount = reader.readInt()
        val pixelLocations = (0 until pixelCount).map {
            Vector3F.parse(reader).toVector3()
        }.toTypedArray()

        entityVisualizer.vizPixels = VizPixels(
            pixelLocations,
            entityVisualizer.surfaceGeometry.panelNormal
        )
    }

    override fun receiveRemoteVisualizationFrameData(reader: ByteArrayReader) {
        entityVisualizer.vizPixels?.readColors(reader)
    }
}