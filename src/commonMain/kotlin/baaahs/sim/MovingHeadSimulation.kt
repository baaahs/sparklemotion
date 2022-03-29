package baaahs.sim

import baaahs.controller.Controller
import baaahs.controller.NullController
import baaahs.fixtures.Fixture
import baaahs.fixtures.MovingHeadFixture
import baaahs.fixtures.Transport
import baaahs.fixtures.TransportConfig
import baaahs.io.ByteArrayReader
import baaahs.io.ByteArrayWriter
import baaahs.mapper.MappingSession
import baaahs.model.MovingHead
import baaahs.visualizer.EntityAdapter
import baaahs.visualizer.ItemVisualizer

interface MovingHeadVisualizerI : ItemVisualizer<MovingHead> {
    fun receivedUpdate(buffer: MovingHead.Buffer)
}

class MovingHeadSimulation(
    private val movingHead: MovingHead,
    private val adapter: EntityAdapter,
    simulationEnv: SimulationEnv
) : FixtureSimulation {
    private val dmxUniverse = simulationEnv[FakeDmxUniverse::class]

    override val mappingData: MappingSession.SurfaceData?
        get() = null

    private val dmxBufferReader = dmxUniverse.buffer(movingHead.baseDmxChannel, movingHead.adapter.dmxChannelCount)
    private val adapterBuffer = movingHead.adapter.newBuffer(dmxBufferReader)

    override val itemVisualizer: MovingHeadVisualizerI by lazy {
        val visualizer = adapter.createMovingHeadVisualizer(movingHead) as MovingHeadVisualizerI
        dmxUniverse.listen {
            visualizer.receivedUpdate(adapterBuffer)
        }
        visualizer
    }

    override fun createPreviewFixture(): Fixture =
        MovingHeadFixture(
            movingHead,
            1,
            movingHead.name,
            PreviewTransport(),
            movingHead.adapter
        )

    override fun receiveRemoteVisualizationFrameData(reader: ByteArrayReader) {
        val channelCount = reader.readShort().toInt()
        repeat(channelCount) { i ->
            dmxBufferReader[i] = reader.readByte()
        }

        itemVisualizer.receivedUpdate(adapterBuffer)
    }

    inner class PreviewTransport : Transport {
        private val movingHeadBuffer = dmxUniverse.writer(movingHead.baseDmxChannel, movingHead.adapter.dmxChannelCount)

        override val name: String
            get() = movingHead.name

        override val controller: Controller
            get() = NullController
        override val config: TransportConfig?
            get() = null

        override fun deliverBytes(byteArray: ByteArray) {
            for (i in byteArray.indices) {
                movingHeadBuffer[i] = byteArray[i]
            }
        }

        override fun deliverComponents(
            componentCount: Int,
            bytesPerComponent: Int,
            fn: (componentIndex: Int, buf: ByteArrayWriter) -> Unit
        ) {
            val buf = ByteArrayWriter()
            for (componentIndex in 0 until componentCount) {
                buf.offset = 0
                fn(componentIndex, buf)

                val bytes = buf.toBytes()
                for (i in 0 until bytesPerComponent) {
                    movingHeadBuffer[i] = bytes[i]
                }
            }
        }
    }
}