package baaahs.net

import baaahs.io.ByteArrayReader
import baaahs.io.ByteArrayWriter
import baaahs.logger
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
import java.lang.RuntimeException
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.time.Duration

class JvmNetwork : Network {
    private val link = RealLink()

    companion object {
        const val MAX_UDP_SIZE = 2048

        val myAddress = InetAddress.getLocalHost()
        val broadcastAddress = InetAddress.getByName("255.255.255.255")
    }

    override fun link(): RealLink = link

    inner class RealLink() : Network.Link {
        private val networkScope = CoroutineScope(Dispatchers.IO)

        override val udpMtu = 1400

        override fun listenUdp(port: Int, udpListener: Network.UdpListener): Network.UdpSocket {
            val socket = JvmUdpSocket(port)
            Thread {
                val data = ByteArray(MAX_UDP_SIZE)
                while (true) {
                    val packetIn = DatagramPacket(data, MAX_UDP_SIZE)
                    socket.udpSocket.receive(packetIn)
                    networkScope.launch {
                        try {
                            udpListener.receive(
                                IpAddress(packetIn.address),
                                packetIn.port,
                                data.copyOfRange(packetIn.offset, packetIn.length)
                            )
                        } catch (e: Exception) {
                            RuntimeException("Error handling UDP packet", e).printStackTrace()
                        }
                    }
                }
            }.start()
            return socket
        }

        inner class JvmUdpSocket(override val serverPort: Int) : Network.UdpSocket {
            internal var udpSocket = DatagramSocket(serverPort)

            override fun sendUdp(toAddress: Network.Address, port: Int, bytes: ByteArray) {
                val packetOut = DatagramPacket(bytes, 0, bytes.size, (toAddress as IpAddress).address, port)
                udpSocket.send(packetOut)
            }

            override fun broadcastUdp(port: Int, bytes: ByteArray) {
                val packetOut = DatagramPacket(bytes, 0, bytes.size, InetSocketAddress(broadcastAddress, port))
                udpSocket.send(packetOut)
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
                        webSocketListener.connected(tcpConnection)

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
                            JvmUdpProxy().handle(incoming, outgoing)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        }

        override val myAddress = IpAddress.mine()

    }

    data class IpAddress(val address: InetAddress) : Network.Address {
        companion object {
            fun mine(): IpAddress {
                val envIp: String? = System.getenv("sparklemotion_ip")
                envIp?.let {
                    return IpAddress(InetAddress.getByName(it))
                }
                return IpAddress(InetAddress.getLocalHost())
            }
        }

        override fun toString(): String {
            return "IpAddress($address)"
        }
    }
}
