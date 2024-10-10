package baaahs.visualizer.remote

import baaahs.fixtures.Fixture
import baaahs.fixtures.FixtureManager
import baaahs.io.ByteArrayWriter
import baaahs.model.Model
import baaahs.net.Network
import baaahs.plugin.Plugins
import baaahs.util.Logger

class RemoteVisualizerServer(
    private val fixtureManager: FixtureManager,
    private val plugins: Plugins
) : Network.WebSocketListener {
    private val id = "Remote Visualizer ${nextId++}"
    lateinit var tcpConnection: Network.TcpConnection
    private val outBuf = ByteArrayWriter(1024)

    override fun connected(tcpConnection: Network.TcpConnection) {
        this.tcpConnection = tcpConnection

        fixtureManager.addRemoteVisualizerListener(listener)
        logger.info { "$id: connected from ${tcpConnection.fromAddress.asString()}." }
    }

    override suspend fun receive(tcpConnection: Network.TcpConnection, bytes: ByteArray) {
        TODO("not implemented")
    }

    override fun reset(tcpConnection: Network.TcpConnection) {
        logger.info { "$id: connection reset." }
        fixtureManager.removeRemoteVisualizerListener(listener)
    }

    private val listener = ListenerImpl()

    inner class ListenerImpl() : Listener {
        override fun sendFixtureInfo(fixture: Fixture) {
            if (fixture.modelEntity != null) {
                sendPacket(Opcode.FixtureInfo, fixture.modelEntity) {
                    outBuf.writeString(
                        plugins.json.encodeToString(
                            RemoteConfigWrapper.serializer(),
                            RemoteConfigWrapper(fixture.fixtureConfig)
                        )
                    )
                }
            }
        }

        override fun sendFrameData(entity: Model.Entity?, block: (ByteArrayWriter) -> Unit) {
            if (entity != null) {
                sendPacket(Opcode.FrameData, entity, block)
            }
        }

        private fun sendPacket(opcode: Opcode, entity: Model.Entity, block: (ByteArrayWriter) -> Unit) {
            outBuf.reset()
            outBuf.writeByte(opcode.byteValue)
            outBuf.writeString(entity.name)
            block(outBuf)
            tcpConnection.send(outBuf.copyBytes())
        }
    }

    companion object {
        private var nextId = 0
        private val logger = Logger<RemoteVisualizerServer>()
    }

    enum class Opcode {
        FixtureInfo,
        FrameData;

        val byteValue: Byte get() = ordinal.toByte()

        companion object {
            val values = values()
            fun get(i: Byte) = values[i.toInt()]
        }
    }

    interface Listener {
        fun sendFixtureInfo(fixture: Fixture)
        fun sendFrameData(entity: Model.Entity?, block: (ByteArrayWriter) -> Unit)
    }
}