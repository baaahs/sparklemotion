package baaahs.visualizer

import baaahs.Color
import baaahs.fixtures.PixelArrayRemoteConfig
import baaahs.fixtures.RemoteConfig
import baaahs.io.ByteArrayReader
import baaahs.model.FakeModelEntity
import baaahs.ui.Observable
import kotlin.math.min

data class FakeItemVisualizer(
    override var item: FakeModelEntity,
    override val title: String = item.title,
    override var isEditing: Boolean = false,
    override var mapperIsRunning: Boolean = false,
    override var selected: Boolean = false
) : ItemVisualizer<FakeModelEntity>, Observable() {
    override val obj: VizObj
        get() = TODO()

    lateinit var remoteConfig: PixelArrayRemoteConfig
    lateinit var pixelColors: List<Color>

    override fun isApplicable(newItem: Any): FakeModelEntity? {
        return if (newItem::class == item::class) newItem as FakeModelEntity else null
    }

    override fun applyStyles() {
    }

    override fun update(newItem: FakeModelEntity) {
        item = newItem
    }

    override fun receiveRemoteConfig(remoteConfig: RemoteConfig) {
        this.remoteConfig = remoteConfig as PixelArrayRemoteConfig
    }

    override fun receiveRemoteFrameData(reader: ByteArrayReader) {
        pixelColors = readColors(reader)
    }

    fun readColors(reader: ByteArrayReader): List<Color> {
        val pixelCount = reader.readInt()
        val minPixCount = min(remoteConfig.pixelCount, pixelCount)
        return buildList {
            repeat(minPixCount) {
                remoteConfig.pixelFormat.readColor(reader) { r, g, b ->
                    add(Color(r, g, b))
                }
            }
        }
    }
}