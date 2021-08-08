package baaahs.sim

import baaahs.controller.SacnManager
import baaahs.device.PixelArrayDevice
import baaahs.fixtures.Fixture
import baaahs.fixtures.FixtureConfig
import baaahs.geom.Vector3F
import baaahs.io.ByteArrayReader
import baaahs.mapper.MappingSession
import baaahs.model.LightBar
import baaahs.visualizer.LightBarVisualizer
import baaahs.visualizer.VizPixels
import baaahs.visualizer.toVector3
import three.js.Vector3

actual class LightBarSimulation actual constructor(
    val lightBar: LightBar,
    private val simulationEnv: SimulationEnv
) : FixtureSimulation {
    private val pixelCount = 59

    private val pixelLocations by lazy { lightBar.calculatePixelLocations(pixelCount) }
    private val vizPixels by lazy {
        VizPixels(pixelLocations.map { it.toVector3() }.toTypedArray(), pixelVisualizationNormal)
    }

    override val mappingData: MappingSession.SurfaceData
        get() = MappingSession.SurfaceData(
            SacnManager.controllerTypeName,
            "wled-X${lightBar.name}X",
            lightBar.name,
            pixelLocations.size,
            pixelLocations.map { MappingSession.SurfaceData.PixelData(it) }
        )

    override val entityVisualizer: LightBarVisualizer by lazy { LightBarVisualizer(lightBar, vizPixels) }

    val wledSimulator by lazy {
        val wledsSimulator = simulationEnv[WledsSimulator::class]
        wledsSimulator.createFakeWledDevice(lightBar.name, vizPixels)
    }

    override val previewFixture: Fixture by lazy {
        Fixture(
            lightBar,
            pixelCount,
            pixelLocations,
            lightBar.deviceType.defaultConfig,
            lightBar.name,
            PixelArrayPreviewTransport(lightBar.name, vizPixels)
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