package baaahs.sim

import baaahs.controller.SacnManager
import baaahs.fixtures.Fixture
import baaahs.fixtures.FixtureConfig
import baaahs.geom.Vector3F
import baaahs.io.ByteArrayReader
import baaahs.mapper.MappingSession
import baaahs.model.LightBar
import baaahs.model.PixelArray
import baaahs.model.PolyLine
import baaahs.visualizer.*

actual class LightBarSimulation actual constructor(
    val pixelArray: PixelArray,
    private val simulationEnv: SimulationEnv
) : FixtureSimulation {

    private val pixelLocations by lazy { pixelArray.calculatePixelLocations(59) }
    private val vizPixels by lazy {
        VizPixels(
            pixelLocations.map { it.toVector3() }.toTypedArray(),
            pixelVisualizationNormal,
            pixelArray.transformation
        )
    }

    override val mappingData: MappingSession.SurfaceData
        get() = MappingSession.SurfaceData(
            SacnManager.controllerTypeName,
            "wled-X${pixelArray.name}X",
            pixelArray.name,
            pixelLocations.size,
            pixelLocations.map { MappingSession.SurfaceData.PixelData(it) }
        )

    override val entityVisualizer: PixelArrayVisualizer<*> by lazy {
        when (pixelArray) {
            is LightBar -> LightBarVisualizer(pixelArray, simulationEnv, vizPixels)
            is PolyLine -> PolyLineVisualizer(pixelArray, simulationEnv)
            else -> error("unsupported?")
        }
    }

    val wledSimulator by lazy {
        val wledsSimulator = simulationEnv[WledsSimulator::class]
        wledsSimulator.createFakeWledDevice(pixelArray.name, vizPixels)
    }

    override val previewFixture: Fixture by lazy {
        Fixture(
            pixelArray,
            pixelLocations.size,
            pixelLocations,
            pixelArray.deviceType.defaultConfig,
            pixelArray.name,
            PixelArrayPreviewTransport(pixelArray.name, vizPixels)
        )
    }

    override fun launch() {
        wledSimulator.run()
    }

    override fun updateVisualizerWith(fixtureConfig: FixtureConfig, pixelCount: Int, pixelLocations: Array<Vector3F>) {
//        entityVisualizer.vizPixels = VizPixels(
//            pixelLocations.map { it.toVector3() }.toTypedArray(),
//            pixelVisualizationNormal,
//            pixelArray.transformation,
//            fixtureConfig as PixelArrayDevice.Config
//        )
    }

    override fun receiveRemoteVisualizationFrameData(reader: ByteArrayReader) {
//        entityVisualizer.vizPixels?.readColors(reader)
    }

    companion object {
        val pixelVisualizationNormal = three_ext.vector3FacingForward
    }
}