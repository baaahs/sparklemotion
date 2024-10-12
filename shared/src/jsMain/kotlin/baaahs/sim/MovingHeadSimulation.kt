package baaahs.sim

import baaahs.controller.Controller
import baaahs.controller.NullController
import baaahs.device.MovingHeadDevice
import baaahs.fixtures.Fixture
import baaahs.fixtures.Transport
import baaahs.fixtures.TransportConfig
import baaahs.io.ByteArrayWriter
import baaahs.mapper.MappingSession
import baaahs.model.MovingHead
import baaahs.visualizer.EntityAdapter
import baaahs.visualizer.entity.ItemVisualizer
import baaahs.visualizer.movers.MovingHeadVisualizer

class MovingHeadSimulation(
    private val movingHead: MovingHead,
    private val adapter: EntityAdapter
) : FixtureSimulation {
    override val mappingData: MappingSession.SurfaceData?
        get() = null

    override val itemVisualizer: ItemVisualizer<*> get() = visualizer
    private val visualizer: MovingHeadVisualizer by lazy {
        MovingHeadVisualizer(movingHead, adapter)
    }

    override val previewFixture: Fixture? by lazy {
        Fixture(
            movingHead, 1, movingHead.name, PreviewTransport(), MovingHeadDevice,
            MovingHeadDevice.Config(movingHead.adapter)
        )
    }

    inner class PreviewTransport : Transport {
        private val dmxUniverse = adapter.simulationEnv[FakeDmxUniverse::class]
        private val dmxBufferReader = dmxUniverse.buffer(movingHead.baseDmxChannel, movingHead.adapter.dmxChannelCount)
        private val movingHeadBuffer = dmxUniverse.writer(movingHead.baseDmxChannel, movingHead.adapter.dmxChannelCount)
        private val adapterBuffer = movingHead.adapter.newBuffer(dmxBufferReader)

        init {
            dmxUniverse.listen {
                visualizer.receivedUpdate(adapterBuffer)
            }
        }

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