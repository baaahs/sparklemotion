package baaahs

import baaahs.dmx.Dmx
import baaahs.fixtures.*
import baaahs.model.MovingHead
import baaahs.util.Logger
import baaahs.visualizer.remote.RemoteVisualizable
import baaahs.visualizer.remote.RemoteVisualizerServer
import baaahs.visualizer.remote.RemoteVisualizers

class MovingHeadManager(
    private val fixtureManager: FixtureManager,
    private val dmxUniverse: Dmx.Universe,
    movingHeads: List<MovingHead>
) : RemoteVisualizable {
    private val remoteVisualizers = RemoteVisualizers()

    private val fixtures = movingHeads.map { movingHead ->
        val movingHeadBuffer = movingHead.newBuffer(dmxUniverse)

        Fixture(movingHead, 1, emptyList(), MovingHeadDevice, transport = object : Transport {
            override val name: String
                get() = "DMX Transport"

            override fun send(fixture: Fixture, resultViews: List<ResultView>) {
                val params = MovingHeadDevice.getResults(resultViews)[0]
                movingHeadBuffer.pan = params.pan
                movingHeadBuffer.tilt = params.tilt
                movingHeadBuffer.colorWheelPosition = params.colorWheel
                movingHeadBuffer.dimmer = params.dimmer

                remoteVisualizers.sendFrameData(fixture) { outBuf ->
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
            dmxUniverse.sendFrame()
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
