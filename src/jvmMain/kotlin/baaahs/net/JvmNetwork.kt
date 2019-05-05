package baaahs.net

import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.readBytes
import io.ktor.request.host
import io.ktor.routing.routing
import io.ktor.server.engine.ApplicationEngine
import io.ktor.websocket.webSocket
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.InetSocketAddress
import java.nio.ByteBuffer

class JvmNetwork(val httpServer: ApplicationEngine) : Network {
    companion object {
        private const val MAX_UDP_SIZE = 2048

        private val broadcastAddress = InetAddress.getByName("255.255.255.255")
    }

    override fun link(): Network.Link = RealLink()

    inner class RealLink() : Network.Link {
        private var defaultUdpSocket = DatagramSocket()
        private val networkScope = CoroutineScope(Dispatchers.IO)

        override val udpMtu = 1400

        override fun listenUdp(port: Int, udpListener: Network.UdpListener) {
            val socket = DatagramSocket(port)

            networkScope.launch {
                val data = ByteArray(MAX_UDP_SIZE)
                val packetIn = DatagramPacket(data, MAX_UDP_SIZE)
                while (true) {
                    socket.receive(packetIn)
                    udpListener.receive(
                        IpAddress(packetIn.address),
                        data.copyOfRange(packetIn.offset, packetIn.length)
                    )
                }
            }
        }

        override fun sendUdp(toAddress: Network.Address, port: Int, bytes: ByteArray) {
            val packetOut = DatagramPacket(bytes, 0, bytes.size, (toAddress as IpAddress).address, port)
            defaultUdpSocket.send(packetOut)
        }

        override fun broadcastUdp(port: Int, bytes: ByteArray) {
            val packetOut = DatagramPacket(bytes, 0, bytes.size, InetSocketAddress(broadcastAddress, port))
            defaultUdpSocket.send(packetOut)
        }

        override fun listenTcp(port: Int, tcpServerSocketListener: Network.TcpServerSocketListener) {
            httpServer.application.routing {
                webSocket("/sm/ws") {
                    try {
                        println("Connection from ${this.call.request.host()}…")
                        val tcpConnection = object : Network.TcpConnection {
                            override val fromAddress: Network.Address
                                get() = myAddress // TODO Fix
                            override val toAddress: Network.Address
                                get() = myAddress // TODO fix
                            override val port: Int
                                get() = port

                            override fun send(bytes: ByteArray) {
                                val frame = Frame.Binary(true, ByteBuffer.wrap(bytes.clone()))
                                GlobalScope.launch {
                                    this@webSocket.send(frame)
                                }
                            }
                        }
                        val tcpListener = tcpServerSocketListener.incomingConnection(tcpConnection)
                        tcpListener.connected(tcpConnection)

                        while (true) {
                            val frame = incoming.receive()
                            if (frame is Frame.Binary) {
                                val bytes = frame.readBytes()
                                tcpListener.receive(tcpConnection, bytes)
                            } else {
                                println("wait huh? received weird data: $frame")
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }

        override fun connectTcp(
            toAddress: Network.Address,
            port: Int,
            tcpListener: Network.TcpListener
        ): Network.TcpConnection {
            TODO("JvmNetwork.connectTcp not implemented")
        }

        override val myAddress = IpAddress.mine()

    }

    class IpAddress(val address: InetAddress) : Network.Address {
        companion object {
            fun mine() = IpAddress(InetAddress.getLocalHost())
        }
    }
}
