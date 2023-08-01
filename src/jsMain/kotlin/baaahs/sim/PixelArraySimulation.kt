package baaahs.sim

import baaahs.controller.SacnManager
import baaahs.device.EnumeratedPixelLocations
import baaahs.device.PixelArrayDevice
import baaahs.device.PixelFormat
import baaahs.fixtures.Fixture
import baaahs.geom.Vector3F
import baaahs.mapper.MappingSession
import baaahs.model.PixelArray
import baaahs.randomDelay
import baaahs.util.globalLaunch
import baaahs.visualizer.EntityAdapter
import baaahs.visualizer.VizPixels
import baaahs.visualizer.toVector3

actual abstract class PixelArraySimulation actual constructor(
    val pixelArray: PixelArray,
    adapter: EntityAdapter
) : FixtureSimulation {
    protected abstract val pixelLocations: List<Vector3F>

    protected val vizPixels by lazy {
        VizPixels(
            pixelLocations.map { it.toVector3() }.toTypedArray(),
            LightBarSimulation.pixelVisualizationNormal,
            pixelArray.transformation,
            PixelFormat.default,
            adapter.units.fromCm(VizPixels.undiffusedLedRangeCm),
            bothSides = true
        )
    }

    override val mappingData: MappingSession.SurfaceData
        get() = MappingSession.SurfaceData(
            SacnManager.controllerTypeName,
            "wled-X${pixelArray.name}X",
            pixelArray.name,
            pixelLocations.size,
            PixelFormat.RGB8,
            pixelLocations.map { MappingSession.SurfaceData.PixelData(it) },
        )

    private val wledSimulator by lazy {
        val wledsSimulator = adapter.simulationEnv[WledsSimulator::class]
        wledsSimulator.createFakeWledDevice(pixelArray.name, vizPixels)
    }

    override val previewFixture: Fixture by lazy {
        Fixture(
            pixelArray,
            pixelLocations.size,
            pixelArray.name,
            PixelArrayPreviewTransport(pixelArray.name, vizPixels),
            PixelArrayDevice,
            PixelArrayDevice.Config(
                pixelLocations.size, PixelFormat.default,
                1f,
                EnumeratedPixelLocations(pixelLocations)
            )
        )
    }
    override fun start() {
        globalLaunch {
            randomDelay(1000)
            wledSimulator.start()
        }
    }

    override fun stop() {
        wledSimulator.stop()
    }
}