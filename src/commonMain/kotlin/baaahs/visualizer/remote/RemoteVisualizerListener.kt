package baaahs.visualizer.remote

import baaahs.BrainManager
import baaahs.Color
import baaahs.fixtures.Fixture
import baaahs.io.ByteArrayWriter
import baaahs.net.Network

class RemoteVisualizerListener(
    private val brainManager: BrainManager
) : Network.WebSocketListener {
    lateinit var tcpConnection: Network.TcpConnection

    override fun connected(tcpConnection: Network.TcpConnection) {
        this.tcpConnection = tcpConnection
        brainManager.addListeningVisualizer(this)
    }

    override fun receive(tcpConnection: Network.TcpConnection, bytes: ByteArray) {
        TODO("not implemented")
    }

    override fun reset(tcpConnection: Network.TcpConnection) {
        brainManager.removeListeningVisualizer(this)
    }

    fun sendPixelData(fixture: Fixture) {
        if (fixture.modelEntity != null) {
            val pixelLocations = fixture.pixelLocations

            val out = ByteArrayWriter(fixture.name.length + fixture.pixelCount * 3 * 4 + 20)
            out.writeByte(Opcode.PixelLocations.byteValue)
            out.writeString(fixture.name)
            out.writeInt(fixture.pixelCount)
            pixelLocations.forEach { it.serialize(out) }
            tcpConnection.send(out.toBytes())
        }
    }

    fun sendFrame(fixture: Fixture, colors: List<Color>) {
        if (fixture.modelEntity != null) {
            val out = ByteArrayWriter(fixture.name.length + colors.size * 3 + 20)
            out.writeByte(Opcode.PixelColors.byteValue)
            out.writeString(fixture.name)
            out.writeInt(colors.size)
            colors.forEach {
                it.serializeWithoutAlpha(out)
            }
            tcpConnection.send(out.toBytes())
        }
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
}