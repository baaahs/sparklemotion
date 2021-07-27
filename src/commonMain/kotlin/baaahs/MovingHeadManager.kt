package baaahs

import baaahs.dmx.Dmx
import baaahs.dmx.DmxManager
import baaahs.fixtures.Fixture
import baaahs.fixtures.FixtureManager
import baaahs.fixtures.MovingHeadDevice
import baaahs.fixtures.Transport
import baaahs.model.Model
import baaahs.util.Logger
import baaahs.visualizer.remote.RemoteVisualizable
import baaahs.visualizer.remote.RemoteVisualizerServer
import baaahs.visualizer.remote.RemoteVisualizers

class MovingHeadManager(
    private val fixtureManager: FixtureManager,
    private val dmxManager: DmxManager,
    model: Model
) : RemoteVisualizable {
    private val remoteVisualizers = RemoteVisualizers()

    private val fixtures = model.movingHeads.map { movingHead ->
        val movingHeadBuffer = movingHead.adapter.newBuffer(dmxManager.dmxUniverse, movingHead.baseDmxChannel)

        Fixture(movingHead, 1, emptyList(), MovingHeadDevice, transport = object : Transport {
            override val name: String
                get() = "DMX Transport"

            override fun deliverBytes(byteArray: ByteArray) {
                val params = movingHead.adapter.newBuffer(Dmx.Buffer(byteArray))
                movingHeadBuffer.pan = params.pan
                movingHeadBuffer.tilt = params.tilt
                movingHeadBuffer.colorWheelPosition = params.colorWheelPosition
                movingHeadBuffer.dimmer = params.dimmer

                remoteVisualizers.sendFrameData(movingHead) { outBuf ->
                    val dmxBuffer = movingHeadBuffer.dmxBuffer
                    outBuf.writeShort(dmxBuffer.channelCount)
                    repeat(dmxBuffer.channelCount) { i ->
                        outBuf.writeByte(dmxBuffer[i])
                    }
                }
            }
        })
    }

    init {
        fixtureManager.addFrameListener {
            dmxManager.dmxUniverse.sendFrame()
        }
    }

    suspend fun start() {
        fixtureManager.fixturesChanged(fixtures, emptyList())
    }

    override fun addRemoteVisualizer(listener: RemoteVisualizerServer.Listener) {
        remoteVisualizers.addListener(listener)

        fixtures.forEach { fixture -> remoteVisualizers.sendFixtureInfo(fixture) }
    }

    override fun removeRemoteVisualizer(listener: RemoteVisualizerServer.Listener) {
        remoteVisualizers.removeListener(listener)
    }

    companion object {
        private val logger = Logger<MovingHeadManager>()
    }
}
