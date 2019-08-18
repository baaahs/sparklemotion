package baaahs.net

import baaahs.Logger
import baaahs.io.ByteArrayReader
import baaahs.io.ByteArrayWriter
import baaahs.net.JvmNetwork.Companion.networkScope
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.readBytes
import io.ktor.websocket.DefaultWebSocketServerSession
import kotlinx.coroutines.launch
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.nio.ByteBuffer

class JvmUdpProxy {
    companion object {
        val logger = Logger("JvmUdpProxy")
    }
    
    suspend fun handle(
        session: DefaultWebSocketServerSession
    ) {
        var socket : DatagramSocket? = null
        val listenThread = Thread {
            val data = ByteArray(JvmNetwork.MAX_UDP_SIZE)
            val packetIn = DatagramPacket(data, JvmNetwork.MAX_UDP_SIZE)
            while (true) {
//                logger.debug { "UDP: wait for packet" }
                socket!!.receive(packetIn)
//                logger.debug { "UDP: received packet!" }
                val frame = Frame.Binary(true, ByteBuffer.wrap(ByteArrayWriter().apply {
                    writeByte(Network.UdpProxy.RECEIVE_OP.toByte())
                    writeBytes(packetIn.address.address)
                    writeInt(packetIn.port)
                    writeBytes(data, packetIn.offset, packetIn.length)
//                    logger.debug { "UDP: Receive ${packetIn.length} ${msgId(data)} from ${packetIn.address}:${packetIn.port}" }
                }.toBytes()))
                networkScope.launch {
//                    logger.debug { "UDP: Receive: forward ${packetIn.length} ${msgId(data)} from ${packetIn.address}:${packetIn.port}" }
                    session.send(frame)
                    session.flush()
//                    logger.debug { "UDP: Receive: forwarded! ${packetIn.length} ${msgId(data)} from ${packetIn.address}:${packetIn.port}" }
                }
            }
        }

        while (true) {
//            logger.debug { "UDP: wait for WebSocket command" }
            val frame = session.incoming.receive()
//            logger.debug { "UDP: received for WebSocket command!" }
            if (frame is Frame.Binary) {
                val bytes = frame.readBytes()
                ByteArrayReader(bytes).apply {
                    val op = readByte()
                    when (op) {
                        Network.UdpProxy.LISTEN_OP.toByte() -> {
                            socket = DatagramSocket() // We'll take any port the system gives us.
                            listenThread.start()
                            logger.debug { "UDP: Listening on ${socket!!.localPort}" }
                        }
                        Network.UdpProxy.SEND_OP.toByte() -> {
                            val toAddress = readBytes()
                            val toPort = readInt()
                            val data = readBytes()
                            val toInetAddress = InetAddress.getByAddress(toAddress)
                            val packet = DatagramPacket(data, 0, data.size, toInetAddress, toPort)
                            networkScope.launch {
//                                logger.debug { "UDP: Will send ${data.size} ${msgId(data)} to $toInetAddress:$toPort" }
                                socket!!.send(packet)
//                                logger.debug { "UDP: Sent ${data.size} ${msgId(data)} to $toInetAddress:$toPort" }
                            }
                        }
                        Network.UdpProxy.BROADCAST_OP.toByte() -> {
                            val toPort = readInt()
                            val data = readBytes()
                            val packet = DatagramPacket(data, 0, data.size,
                                JvmNetwork.broadcastAddress, toPort)
                            networkScope.launch {
//                                logger.debug { "UDP: Will broadcast ${data.size} ${msgId(data)} to *:$toPort" }
                                socket!!.send(packet)
//                                logger.debug { "UDP: Broadcast ${data.size} ${msgId(data)} to *:$toPort" }
                            }
                        }
                        else -> {
                            logger.warn { "UDP: Huh? unknown op $op: $bytes" }
                        }
                    }
                }
            } else {
                logger.warn { "wait huh? received weird data: $frame" }
            }
        }
    }
}