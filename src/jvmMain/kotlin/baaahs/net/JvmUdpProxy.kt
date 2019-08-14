package baaahs.net

import baaahs.io.ByteArrayReader
import baaahs.io.ByteArrayWriter
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.readBytes
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.launch
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.nio.ByteBuffer

class JvmUdpProxy {
    suspend fun handle(
        incoming: ReceiveChannel<Frame>,
        outgoing: SendChannel<Frame>
    ) {
        var socket: DatagramSocket? = null
        while (true) {
            val listenThread = Thread {
                val data = ByteArray(JvmNetwork.MAX_UDP_SIZE)
                while (true) {
                    val packetIn = DatagramPacket(data, JvmNetwork.MAX_UDP_SIZE)
                    socket!!.receive(packetIn)
                    val frame = Frame.Binary(
                        true,
                        ByteBuffer.wrap(ByteArrayWriter().apply {
                            writeByte(Network.UdpProxy.RECEIVE_OP.toByte())
                            writeBytes(packetIn.address.address)
                            writeInt(packetIn.port)
                            writeBytes(data, packetIn.offset, packetIn.length)
                        }.toBytes())
                    )
                    GlobalScope.launch {
                        outgoing.send(frame)
                    }
                }
            }


            val frame = incoming.receive()
            if (frame is Frame.Binary) {
                val bytes = frame.readBytes()
                ByteArrayReader(bytes).apply {
                    val op = readByte()
                    when (op) {
                        Network.UdpProxy.LISTEN_OP.toByte() -> {
                            socket = DatagramSocket() // We'll take any port the system gives us.
                            listenThread.start()
                            println("UDP: Listening on ${socket!!.localPort}")
                        }
                        Network.UdpProxy.SEND_OP.toByte() -> {
                            val toAddress = readBytes()
                            val toPort = readInt()
                            val data = readBytes()
                            val packet = DatagramPacket(
                                data,
                                0,
                                data.size,
                                InetAddress.getByAddress(toAddress),
                                toPort
                            )
                            socket!!.send(packet)
                            //                                                println("UDP: Sent ${data.size} to $toAddress:$toPort")
                        }
                        Network.UdpProxy.BROADCAST_OP.toByte() -> {
                            val toPort = readInt()
                            val data = readBytes()
                            val packet = DatagramPacket(
                                data,
                                0,
                                data.size,
                                JvmNetwork.broadcastAddress,
                                toPort
                            )
                            socket!!.send(packet)
                            //                                                println("UDP: Broadcast ${data.size} to *:$toPort")
                        }
                    }
                }
            } else {
                println("wait huh? received weird data: $frame")
            }
        }
    }
}