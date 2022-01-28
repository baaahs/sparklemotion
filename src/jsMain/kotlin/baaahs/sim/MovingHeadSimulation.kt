package baaahs.sim

import baaahs.fixtures.Fixture
import baaahs.fixtures.Transport
import baaahs.io.ByteArrayReader
import baaahs.io.ByteArrayWriter
import baaahs.mapper.ControllerId
import baaahs.mapper.MappingSession
import baaahs.model.MovingHead
import baaahs.visualizer.EntityAdapter
import baaahs.visualizer.movers.MovingHeadVisualizer

actual class MovingHeadSimulation actual constructor(
    private val movingHead: MovingHead,
    private val adapter: EntityAdapter
) : FixtureSimulation {
    private val dmxUniverse = adapter.simulationEnv[FakeDmxUniverse::class]

    override val mappingData: MappingSession.SurfaceData?
        get() = null

    private val dmxBufferReader = dmxUniverse.buffer(movingHead.baseDmxChannel, movingHead.adapter.dmxChannelCount)
    private val adapterBuffer = movingHead.adapter.newBuffer(dmxBufferReader)

    override val itemVisualizer: MovingHeadVisualizer by lazy {
        val visualizer = MovingHeadVisualizer(movingHead, adapter)
        dmxUniverse.listen {
            visualizer.receivedUpdate(adapterBuffer)
        }
        visualizer
    }

    private val buffer = dmxUniverse.writer(movingHead.baseDmxChannel, movingHead.adapter.dmxChannelCount)

    override val previewFixture: Fixture by lazy {
        Fixture(
            movingHead,
            1,
            listOf(movingHead.position),
            movingHead.deviceType.defaultConfig,
            movingHead.name,
            PreviewTransport()
        )
    }

    override fun launch() {
    }

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

        override val controllerId: ControllerId
            get() = TODO("not implemented")

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