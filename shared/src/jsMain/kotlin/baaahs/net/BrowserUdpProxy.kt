package baaahs.net

import baaahs.io.ByteArrayReader
import baaahs.io.ByteArrayWriter
import baaahs.util.Logger

internal class BrowserUdpProxy(
    link: Network.Link, address: BrowserNetwork.BrowserAddress, port: Int
) : Network.WebSocketListener {
    private var udpListener: Network.UdpListener? = null

    val tcpConnection = link.connectWebSocket(address, port, "/sm/udpProxy", this)
    var connected: Boolean = false
    val toSend = mutableListOf<ByteArray>()

    override fun connected(tcpConnection: Network.TcpConnection) {
        connected = true

        toSend.forEach { tcpConnection.send(it) }
        toSend.clear()
    }

    override suspend fun receive(tcpConnection: Network.TcpConnection, bytes: ByteArray) {
        try {
            if (bytes.isEmpty()) return

            ByteArrayReader(bytes).apply {
                val op = readByte()
                when (op) {
                    Network.UdpProxy.RECEIVE_OP.code.toByte() -> {
                        val fromAddress = UdpProxyAddress(readBytesWithSize())
                        val fromPort = readInt()
                        val data = readBytesWithSize()
                        logger.debug { "UDP: Received ${data.size} bytes ${msgId(data)} from $fromAddress:$fromPort" }
                        udpListener!!.receive(fromAddress, fromPort, data)
                    }

                    else -> {
                        logger.debug { "UDP: Huh? unknown op $op: $bytes" }
                    }
                }
            }
        } catch (e: Exception) {
            logger.error(e) { "Error receiving WebSocket command" }
            throw e
        }
    }

    override fun reset(tcpConnection: Network.TcpConnection) {
        TODO("UdpProxy.reset not implemented")
    }

    fun listenUdp(port: Int, udpListener: Network.UdpListener): Network.UdpSocket {
        if (this.udpListener != null) {
            throw IllegalStateException("UDP proxy is already listening")
        }

        this.udpListener = udpListener

        if (port != 0) {
            throw IllegalArgumentException("UDP proxy can't listen on a specific port, sorry!")
        }

        tcpConnectionSend(ByteArrayWriter().apply {
            writeByte(Network.UdpProxy.LISTEN_OP.code.toByte())
            logger.debug { "UDP: Listen" }
        }.toBytes())

        return UdpSocketProxy(port)
    }

    inner class UdpSocketProxy(requestedPort: Int) : Network.UdpSocket {
        override val serverPort = requestedPort // TODO: this is probably wrong

        override fun sendUdp(toAddress: Network.Address, port: Int, bytes: ByteArray) {
            if (toAddress !is UdpProxyAddress) {
                throw IllegalArgumentException("UDP proxy can't send to $toAddress!")
            }

            tcpConnectionSend(ByteArrayWriter().apply {
                writeByte(Network.UdpProxy.SEND_OP.code.toByte())
                writeBytesWithSize(toAddress.bytes)
                writeInt(port)
                writeBytesWithSize(bytes)
                logger.debug { "UDP: Sent ${bytes.size} bytes ${msgId(bytes)} to $toAddress:$port" }
            }.toBytes())
        }

        override fun broadcastUdp(port: Int, bytes: ByteArray) {
            tcpConnectionSend(ByteArrayWriter().apply {
                writeByte(Network.UdpProxy.BROADCAST_OP.code.toByte())
                writeInt(port)
                writeBytesWithSize(bytes)
                logger.debug { "UDP: Broadcast ${bytes.size} bytes ${msgId(bytes)} to *:$port" }
            }.toBytes())
        }

        override fun close() {
            tcpConnection.close()
        }

    }

    private fun tcpConnectionSend(bytes: ByteArray) {
        if (connected) {
            tcpConnection.send(bytes)
        } else {
            toSend.add(bytes)
        }
    }

    private fun msgId(data: ByteArray): String {
        return "msgId=${((data[0].toInt() and 0xff) * 256) or (data[1].toInt() and 0xff)}"
    }

    private data class UdpProxyAddress(val bytes: ByteArray) : Network.Address {

        override fun asString(): String {
            return bytes.joinToString(".") { it.toInt().and(0xff).toString() }
        }

        override fun toString(): String = asString()

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class.js != other::class.js) return false

            other as UdpProxyAddress

            if (!bytes.contentEquals(other.bytes)) return false

            return true
        }

        override fun hashCode(): Int {
            return bytes.contentHashCode()
        }
    }

    companion object {
        val logger = Logger<BrowserUdpProxy>()
    }
}