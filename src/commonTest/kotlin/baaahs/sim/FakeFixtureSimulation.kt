package baaahs.sim

import baaahs.Color
import baaahs.fixtures.Fixture
import baaahs.fixtures.PixelArrayRemoteConfig
import baaahs.fixtures.RemoteConfig
import baaahs.io.ByteArrayReader
import baaahs.mapper.MappingSession
import baaahs.visualizer.FakeItemVisualizer
import kotlin.math.min

class FakeFixtureSimulation(
    override val itemVisualizer: FakeItemVisualizer,
    override val mappingData: MappingSession.SurfaceData? = null,
    override val previewFixture: Fixture? = null
) : FixtureSimulation {

    override fun updateVisualizerWith(remoteConfig: RemoteConfig) {
        itemVisualizer.remoteConfig = remoteConfig as PixelArrayRemoteConfig
    }

    override fun receiveRemoteVisualizationFrameData(reader: ByteArrayReader) {
        itemVisualizer.pixelColors = readColors(reader)
    }

    fun readColors(reader: ByteArrayReader): List<Color> {
        val pixelCount = reader.readInt()
        val minPixCount = min(itemVisualizer.remoteConfig.pixelCount, pixelCount)
        return buildList {
            repeat(minPixCount) {
                itemVisualizer.remoteConfig.pixelFormat.readColor(reader) { r, g, b ->
                    add(Color(r, g, b))
                }
            }
        }
    }
}