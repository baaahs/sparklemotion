package baaahs.visualizer.remote

import baaahs.fixtures.Fixture
import baaahs.io.ByteArrayWriter
import baaahs.net.Network
import baaahs.util.Logger

class RemoteVisualizerServer(
    private vararg val remoteVisualizables: RemoteVisualizable
) : Network.WebSocketListener {
    private val id = "Remote Visualizer ${nextId++}"
    lateinit var tcpConnection: Network.TcpConnection
    private val outBuf = ByteArrayWriter(1024)

    override fun connected(tcpConnection: Network.TcpConnection) {
        this.tcpConnection = tcpConnection

        remoteVisualizables.forEach { it.addRemoteVisualizer(listener) }
        logger.info { "$id: connected from ${tcpConnection.fromAddress.asString()}." }
    }

    override fun receive(tcpConnection: Network.TcpConnection, bytes: ByteArray) {
        TODO("not implemented")
    }

    override fun reset(tcpConnection: Network.TcpConnection) {
        logger.info { "$id: connection reset." }
        remoteVisualizables.forEach { it.removeRemoteVisualizer(listener) }
    }

    private val listener = object : Listener {
        override fun sendFixtureInfo(fixture: Fixture) {
            if (fixture.modelEntity != null) {
                outBuf.reset()
                outBuf.writeByte(Opcode.PixelLocations.byteValue)
                outBuf.writeString(fixture.modelEntity.name)
                outBuf.writeInt(fixture.pixelCount)
                fixture.pixelLocations.forEach { it.serialize(outBuf) }
                tcpConnection.send(outBuf.toBytes())
            }
        }

        override fun sendFrameData(fixture: Fixture, block: (ByteArrayWriter) -> Unit) {
            if (fixture.modelEntity != null) {
                outBuf.reset()
                outBuf.writeByte(Opcode.PixelColors.byteValue)
                outBuf.writeString(fixture.modelEntity.name)
                block(outBuf)
                tcpConnection.send(outBuf.toBytes())
            }
        }
    }

    companion object {
        private var nextId = 0
        private val logger = Logger<RemoteVisualizerServer>()
    }

    enum class Opcode {
        PixelLocations,
        PixelColors;

        val byteValue: Byte get() = ordinal.toByte()

        companion object {
            val values = values()
            fun get(i: Byte) = values[i.toInt()]
        }
    }

    interface Listener {
        fun sendFixtureInfo(fixture: Fixture)
        fun sendFrameData(fixture: Fixture, block: (ByteArrayWriter) -> Unit)
    }
}