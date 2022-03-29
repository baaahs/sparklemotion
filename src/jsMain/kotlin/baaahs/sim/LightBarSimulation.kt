package baaahs.sim

import baaahs.controller.ControllerId
import baaahs.controller.SacnManager
import baaahs.device.PixelArrayDevice
import baaahs.fixtures.Fixture
import baaahs.fixtures.PixelArrayFixture
import baaahs.fixtures.RemoteConfig
import baaahs.geom.Vector3F
import baaahs.io.ByteArrayReader
import baaahs.mapper.MappingSession
import baaahs.model.LightBar
import baaahs.model.PixelArray
import baaahs.model.PolyLine
import baaahs.visualizer.*

actual class LightBarSimulation actual constructor(
    val pixelArray: PixelArray,
    private val simulationEnv: SimulationEnv,
    private val adapter: EntityAdapter
) : FixtureSimulation {

    private val pixelLocations by lazy { pixelArray.calculatePixelLocalLocations(59) }
    private val vizPixels by lazy {
        VizPixels(
            pixelLocations.map { it.toVector3() }.toTypedArray(),
            pixelVisualizationNormal,
            pixelArray.transformation,
            PixelArrayDevice.PixelFormat.default
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
        val wledsSimulator = simulationEnv[WledsSimulator::class]
        wledsSimulator.createFakeWledDevice(ControllerId(SacnManager.controllerTypeName, pixelArray.name), vizPixels)
    }

    override fun createPreviewFixture(): Fixture =
        PixelArrayFixture(
            pixelArray,
            pixelLocations.size,
            pixelArray.name,
            PixelArrayPreviewTransport(pixelArray.name, vizPixels),
            PixelArrayDevice.PixelFormat.default,
            1f,
            pixelLocations
        )

    override fun start() {
        wledSimulator.start()
    }

    override fun stop() {
        wledSimulator.stop()
    }

    override fun updateVisualizerWith(remoteConfig: RemoteConfig, pixelCount: Int, pixelLocations: Array<Vector3F>) {
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