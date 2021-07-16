package baaahs.sim

import baaahs.controller.WledManager
import baaahs.fixtures.Fixture
import baaahs.fixtures.PixelArrayDevice
import baaahs.fixtures.ResultView
import baaahs.fixtures.Transport
import baaahs.geom.Vector3F
import baaahs.io.ByteArrayReader
import baaahs.mapper.MappingSession
import baaahs.model.LightBar
import baaahs.visualizer.ColorBufferPixelsVisualizer
import baaahs.visualizer.LightBarVisualizer
import baaahs.visualizer.toVector3
import three.js.Vector3

/** LightBarSimulation doesn't actually simulate WLED communications. It prolly should at some point. */
actual class LightBarSimulation actual constructor(
    val lightBar: LightBar,
    private val simulationEnv: SimulationEnv
) : FixtureSimulation {
    private val pixelCount = 59

    private val pixelLocations by lazy { lightBar.getPixelLocations(pixelCount) }
    private val vizPixels by lazy {
        ColorBufferPixelsVisualizer(pixelLocations.map { it.toVector3() }.toTypedArray(), pixelVisualizationNormal)
    }

    override val mappingData: MappingSession.SurfaceData
        get() = MappingSession.SurfaceData(
            WledManager.controllerTypeName,
            "wled-X${lightBar.name}X",
            lightBar.name,
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
            lightBar.deviceType,
            lightBar.name,
            PreviewTransport()
        )
    }

    override fun launch() {
        wledSimulator.run()
    }

    override fun receiveRemoteVisualizationFixtureInfo(reader: ByteArrayReader) {
        val pixelCount = reader.readInt()
        val pixelLocations = (0 until pixelCount).map {
            Vector3F.parse(reader).toVector3()
        }.toTypedArray()

        entityVisualizer.vizPixels = ColorBufferPixelsVisualizer(pixelLocations, pixelVisualizationNormal)
    }

    override fun receiveRemoteVisualizationFrameData(reader: ByteArrayReader) {
        entityVisualizer.vizPixels?.readColors(reader)
    }

    inner class PreviewTransport : Transport {
        override val name: String
            get() = lightBar.name

        override fun send(fixture: Fixture, resultViews: List<ResultView>) {
            val resultColors = PixelArrayDevice.getColorResults(resultViews)
            for (i in 0 until pixelCount) {
                vizPixels[i] = resultColors[i]
            }
        }
    }

    companion object {
        val pixelVisualizationNormal = Vector3(0, 0, 1)
    }
}