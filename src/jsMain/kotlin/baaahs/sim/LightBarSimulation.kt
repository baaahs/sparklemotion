package baaahs.sim

import baaahs.controller.SacnManager
import baaahs.device.PixelFormat
import baaahs.fixtures.Fixture
import baaahs.fixtures.PixelArrayFixture
import baaahs.fixtures.RemoteConfig
import baaahs.io.ByteArrayReader
import baaahs.mapper.MappingSession
import baaahs.model.LightBar
import baaahs.model.PixelArray
import baaahs.model.PolyLine
import baaahs.visualizer.*

actual class LightBarSimulation actual constructor(
    val pixelArray: PixelArray,
    private val adapter: EntityAdapter
) : FixtureSimulation {

    private val pixelLocations by lazy { pixelArray.calculatePixelLocalLocations(59) }
    private val vizPixels by lazy {
        VizPixels(
            pixelLocations.map { it.toVector3() }.toTypedArray(),
            pixelVisualizationNormal,
            pixelArray.transformation,
            PixelFormat.default
        )
    }

    override val mappingData: MappingSession.SurfaceData
        get() {
            val pixelsVecs = pixelLocations.map { MappingSession.SurfaceData.PixelData(it) }
            return MappingSession.SurfaceData(
                SacnManager.controllerTypeName,
                "wled-X${pixelArray.name}X",
                pixelArray.name,
                pixelLocations.size,
                pixelsVecs
            )
        }

    override val itemVisualizer: PixelArrayVisualizer<*> by lazy {
        when (pixelArray) {
            is LightBar -> LightBarVisualizer(pixelArray, adapter, vizPixels)
            is PolyLine -> PolyLineVisualizer(pixelArray, adapter, vizPixels)
            else -> error("unsupported?")
        }
    }

    val wledSimulator by lazy {
        val wledsSimulator = adapter.simulationEnv[WledsSimulator::class]
        wledsSimulator.createFakeWledDevice(pixelArray.name, vizPixels)
    }

    override val previewFixture: Fixture by lazy {
        PixelArrayFixture(
            pixelArray,
            pixelLocations.size,
            pixelArray.name,
            PixelArrayPreviewTransport(pixelArray.name, vizPixels),
            PixelFormat.default,
            1f,
            pixelLocations
        )
    }

    override fun start() {
        wledSimulator.start()
    }

    override fun stop() {
        wledSimulator.stop()
    }

    override fun updateVisualizerWith(remoteConfig: RemoteConfig) {
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