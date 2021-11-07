package baaahs.sim

import baaahs.controller.SacnManager
import baaahs.device.PixelArrayDevice
import baaahs.fixtures.Fixture
import baaahs.fixtures.FixtureConfig
import baaahs.geom.Vector3F
import baaahs.io.ByteArrayReader
import baaahs.mapper.MappingSession
import baaahs.model.PixelArray
import baaahs.visualizer.LightBarVisualizer
import baaahs.visualizer.VizPixels
import baaahs.visualizer.toVector3
import three.js.Vector3

actual class LightBarSimulation actual constructor(
    val pixelArray: PixelArray,
    private val simulationEnv: SimulationEnv
) : FixtureSimulation {

    private val pixelLocations by lazy { pixelArray.calculatePixelLocations(59) }
    private val vizPixels by lazy {
        VizPixels(pixelLocations.map { it.toVector3() }.toTypedArray(), pixelVisualizationNormal)
    }

    override val mappingData: MappingSession.SurfaceData
        get() = MappingSession.SurfaceData(
            SacnManager.controllerTypeName,
            "wled-X${pixelArray.name}X",
            pixelArray.name,
            pixelLocations.size,
            pixelLocations.map { MappingSession.SurfaceData.PixelData(it) }
        )

    override val entityVisualizer: LightBarVisualizer by lazy {
        LightBarVisualizer(pixelArray, vizPixels)
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
        entityVisualizer.vizPixels = VizPixels(
            pixelLocations.map { it.toVector3() }.toTypedArray(),
            pixelVisualizationNormal,
            fixtureConfig as PixelArrayDevice.Config
        )
    }

    override fun receiveRemoteVisualizationFrameData(reader: ByteArrayReader) {
        entityVisualizer.vizPixels?.readColors(reader)
    }

    companion object {
        val pixelVisualizationNormal = Vector3(0, 0, 1)
    }
}