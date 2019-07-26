package baaahs.net

import baaahs.io.ByteArrayReader
import baaahs.io.ByteArrayWriter
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.readBytes
import io.ktor.request.host
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
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
import java.time.Duration

class JvmNetwork : Network {
    private val link = RealLink()

    companion object {
        private const val MAX_UDP_SIZE = 2048

        val myAddress = InetAddress.getLocalHost()
        //        val myAddress = InetAddress.getByName("10.0.1.10")
        private val broadcastAddress = InetAddress.getByName("255.255.255.255")
    }

    override fun link(): RealLink = link

    inner class RealLink() : Network.Link {
        private var defaultUdpSocket = DatagramSocket()
        //        private var defaultUdpSocket = DatagramSocket(0, InetAddress.getByName("10.0.1.10"))
        private val networkScope = CoroutineScope(Dispatchers.IO)

        override val udpMtu = 1400

        override fun listenUdp(port: Int, udpListener: Network.UdpListener): Network.UdpSocket {
            val socket = DatagramSocket(port)
            Thread {
                val data = ByteArray(MAX_UDP_SIZE)
                while (true) {
                    val packetIn = DatagramPacket(data, MAX_UDP_SIZE)
                    socket.receive(packetIn)
                    networkScope.launch {
                        udpListener.receive(
                            IpAddress(packetIn.address),
                            packetIn.port,
                            data.copyOfRange(packetIn.offset, packetIn.length)
                        )
                    }
                }
            }.start()
            return JvmUdpSocket(socket.localPort)
        }

        inner class JvmUdpSocket(override val serverPort: Int) : Network.UdpSocket {
            override fun sendUdp(toAddress: Network.Address, port: Int, bytes: ByteArray) {
                // broadcastUdp(port, bytes);
                // return
                val packetOut = DatagramPacket(bytes, 0, bytes.size, (toAddress as IpAddress).address, port)
                defaultUdpSocket.send(packetOut)
            }

            override fun broadcastUdp(port: Int, bytes: ByteArray) {
                val packetOut = DatagramPacket(bytes, 0, bytes.size, InetSocketAddress(broadcastAddress, port))
                defaultUdpSocket.send(packetOut)
            }
        }

        override fun startHttpServer(port: Int): KtorHttpServer =
            KtorHttpServer(port).also { it.httpServer.start(false) }

        override fun connectWebSocket(
            toAddress: Network.Address,
            port: Int,
            path: String,
            webSocketListener: Network.WebSocketListener
        ): Network.TcpConnection {
            TODO("JvmNetwork.connectWebSocket not implemented")
        }

        inner class KtorHttpServer(val port: Int) : Network.HttpServer {
            val httpServer = embeddedServer(Netty, port) {
                install(io.ktor.websocket.WebSockets) {
                    pingPeriod = Duration.ofSeconds(15)
                    timeout = Duration.ofSeconds(15)
                    maxFrameSize = Long.MAX_VALUE
                    masking = false
                }
            }

            val application: Application get() = httpServer.application

            override fun listenWebSocket(
                path: String,
                onConnect: (incomingConnection: Network.TcpConnection) -> Network.WebSocketListener
            ) {
                httpServer.application.routing {
                    webSocket(path) {
                        val tcpConnection = object : Network.TcpConnection {
                            override val fromAddress: Network.Address
                                get() = myAddress // TODO Fix
                            override val toAddress: Network.Address
                                get() = myAddress // TODO fix
                            override val port: Int
                                get() = this@KtorHttpServer.port

                            override fun send(bytes: ByteArray) {
                                val frame = Frame.Binary(true, ByteBuffer.wrap(bytes.clone()))
                                GlobalScope.launch {
                                    this@webSocket.send(frame)
                                }
                            }
                        }

                        println("Connection from ${this.call.request.host()}…")
                        val webSocketListener = onConnect(tcpConnection)

                        try {
                            while (true) {
                                val frame = incoming.receive()
                                if (frame is Frame.Binary) {
                                    val bytes = frame.readBytes()
                                    webSocketListener.receive(tcpConnection, bytes)
                                } else {
                                    println("wait huh? received weird data: $frame")
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            close(e)
                        } finally {
                            webSocketListener.reset(tcpConnection)
                        }
                    }

                    webSocket("/sm/udpProxy") {
                        try {
                            var socket : DatagramSocket? = null
                            while (true) {
                                val listenThread = Thread {
                                    val data = ByteArray(MAX_UDP_SIZE)
                                    while (true) {
                                        val packetIn = DatagramPacket(data, MAX_UDP_SIZE)
                                        socket!!.receive(packetIn)
                                        val frame = Frame.Binary(true, ByteBuffer.wrap(ByteArrayWriter().apply {
                                            writeByte('R'.toByte())
                                            writeBytes(packetIn.address.address)
                                            writeInt(packetIn.port)
                                            writeBytes(data, packetIn.offset, packetIn.length)
                                        }.toBytes()))
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
                                            'L'.toByte() -> {
                                                socket = DatagramSocket() // We'll take any port the system gives us.
                                                listenThread.start()
                                                println("UDP: Listening on ${socket!!.localPort}")
                                            }
                                            'S'.toByte() -> {
                                                val toAddress = readBytes()
                                                val toPort = readInt()
                                                val data = readBytes()
                                                val packet = DatagramPacket(data, 0, data.size, InetAddress.getByAddress(toAddress), toPort)
                                                socket!!.send(packet)
                                                println("UDP: Sent ${data.size} to $toAddress:$toPort")
                                            }
                                            'B'.toByte() -> {
                                                val toPort = readInt()
                                                val data = readBytes()
                                                val packet = DatagramPacket(data, 0, data.size, broadcastAddress, toPort)
                                                socket!!.send(packet)
                                                println("UDP: Broadcast ${data.size} to *:$toPort")
                                            }
                                        }
                                    }
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
        }

        override val myAddress = IpAddress.mine()

    }

    class IpAddress(val address: InetAddress) : Network.Address {
        companion object {
            fun mine() = IpAddress(InetAddress.getLocalHost())
//            fun mine() = IpAddress(InetAddress.getByName("10.0.1.10"))
        }
    }
}
