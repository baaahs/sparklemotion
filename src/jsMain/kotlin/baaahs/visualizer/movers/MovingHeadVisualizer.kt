package baaahs.visualizer.movers

import baaahs.dmx.Dmx
import baaahs.io.ByteArrayReader
import baaahs.model.MovingHead
import baaahs.util.Clock
import baaahs.visualizer.BaseEntityVisualizer
import baaahs.visualizer.EntityAdapter
import baaahs.visualizer.EntityStyle
import three.js.Group
import three.js.Object3D
import three_ext.clear

class MovingHeadVisualizer(
    movingHead: MovingHead,
    adapter: EntityAdapter
) : BaseEntityVisualizer<MovingHead>(movingHead) {
    private val holder = Group()
    override val obj: Object3D
        get() = holder

    private val clock = adapter.simulationEnv[Clock::class]
    private val physicalModel = PhysicalModel(movingHead.adapter, clock)

    private val sharpyVisualizer = SharpyVisualizer(movingHead.adapter, adapter.units)

    init {
        update(item)
    }

    override fun applyStyle(entityStyle: EntityStyle) {
        sharpyVisualizer.applyStyle(entityStyle)
    }

    override fun isApplicable(newItem: Any): MovingHead? =
        newItem as? MovingHead

    override fun update(newItem: MovingHead) {
        super.update(newItem)

        holder.clear()

        sharpyVisualizer.updateGeometry(item.adapter.visualizerInfo)
        holder.add(sharpyVisualizer.group)
    }

    override fun receiveRemoteFrameData(reader: ByteArrayReader) {
        val channelCount = reader.readShort().toInt()
        val dmxBuffer = Dmx.Buffer(ByteArray(channelCount))
        repeat(channelCount) { i ->
            dmxBuffer[i] = reader.readByte()
        }
        val adapterBuffer = item.adapter.newBuffer(dmxBuffer)
        receivedUpdate(adapterBuffer)
    }

    fun receivedUpdate(adapterBuffer: MovingHead.Buffer) {
        val state = physicalModel.update(adapterBuffer)
        sharpyVisualizer.update(state)
    }
}
