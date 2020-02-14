package baaahs.net

import baaahs.Logger
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
import net.posick.mDNS.MulticastDNSService
import net.posick.mDNS.ServiceInstance
import net.posick.mDNS.ServiceName
import org.xbill.DNS.MulticastDNSUtils
import org.xbill.DNS.Name
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.time.Duration

class JvmNetwork : Network {
    private val link = RealLink()

    companion object {
        const val MAX_UDP_SIZE = 1450
        //const val MAX_UDP_SIZE = 4096

        val logger = Logger("JvmNetwork")
//        val myAddress = InetAddress.getLocalHost()
        val myAddress = InetAddress.getByName("127.0.0.1")
        val broadcastAddress = InetAddress.getByName("255.255.255.255")
        var myHostname = MulticastDNSUtils.getHostName()

        val networkScope = CoroutineScope(Dispatchers.IO)

        fun msgId(data: ByteArray): String {
            return "msgId=${((data[0].toInt() and 0xff) * 256) or (data[1].toInt() and 0xff)}"
        }

    }

    override fun link(): RealLink = link

    inner class RealLink() : Network.Link {

        override val udpMtu = MAX_UDP_SIZE
        private val mdns = JvmMdnsService()

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

        override fun mdns(): Network.Mdns = mdns

        inner class JvmUdpSocket(override val serverPort: Int) : Network.UdpSocket {
            internal var udpSocket = DatagramSocket(serverPort)

            init {
//                println("Trying to set send buffer size to ${4*MAX_UDP_SIZE}")
//                udpSocket.sendBufferSize = 4*MAX_UDP_SIZE;
                println("Send buffer size is ${udpSocket.sendBufferSize}")
            }

            override fun sendUdp(toAddress: Network.Address, port: Int, bytes: ByteArray) {
                // println("Sending ${bytes.size} bytes to ${toAddress}")
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
            val httpServer = embeddedServer(Netty, port, configure = {
                // Let's give brains lots of time for OTA download:
                responseWriteTimeoutSeconds = 3000
            }) {
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
                                    this@webSocket.flush()
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
                            JvmUdpProxy().handle(this)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        }

        override val myAddress = IpAddress.mine()

        inner class JvmMdnsService() : Network.Mdns {
            private val svc = MulticastDNSService()

            override fun register(type: String, proto: String, port: Int, txt: Map<String, String>): Network.MdnsRegisteredService? {
                val name = myHostname.replace(Regex("\\.local\\.?$"), "")
                val domain = "local."
                val serviceName = ServiceName("$name.$type.$proto.$domain")
                var hostname = myHostname
                if (!hostname.endsWith(".")) {
                    hostname += ".local."
                }
                val inst = ServiceInstance(serviceName, 0, 0, port, Name(hostname), InetAddress.getAllByName(myHostname), txt)

                val regInst = svc.register(inst)
                if (regInst != null) {
                    println("Service successfully registered: \n\t$regInst")
                    return JvmRegisteredService(regInst)
                }
                println("Unable to register service: \n\t$inst")
                return null
            }

            override fun unregister(inst: Network.MdnsRegisteredService?) {
                inst?.unregister()
            }

            inner class JvmRegisteredService(private val inst: ServiceInstance) : Network.MdnsRegisteredService {

                override fun unregister() {
                    svc.unregister(inst)
                }

                override fun updateTXT(txt: Map<String, String>) {
                    inst.addText(txt)
                }

                override fun updateTXT(key: String, value: String) {
                    inst.addText(key, value)
                }

            }

        }

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
