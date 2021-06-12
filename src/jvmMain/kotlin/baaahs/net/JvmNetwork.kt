package baaahs.net

import baaahs.util.Logger
import io.ktor.application.*
import io.ktor.content.*
import io.ktor.http.*
import io.ktor.http.cio.websocket.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.websocket.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.*
import java.nio.ByteBuffer
import java.time.Duration
import javax.jmdns.JmDNS
import javax.jmdns.ServiceEvent
import javax.jmdns.ServiceInfo
import javax.jmdns.ServiceListener
import kotlin.collections.List
import kotlin.collections.MutableMap
import kotlin.collections.arrayListOf
import kotlin.collections.copyOfRange
import kotlin.collections.forEach
import kotlin.collections.isNotEmpty
import kotlin.collections.iterator
import kotlin.collections.mutableMapOf
import kotlin.collections.set

class JvmNetwork : Network {
    private val link = RealLink()

    companion object {
        const val MAX_UDP_SIZE = 1450
        //const val MAX_UDP_SIZE = 4096

        val logger = Logger("JvmNetwork")
//        val myAddress = InetAddress.getLocalHost()
        val myAddress = InetAddress.getByName("127.0.0.1")
        val broadcastAddress = InetAddress.getByName("255.255.255.255")

        val networkScope = CoroutineScope(Dispatchers.IO)

        fun msgId(data: ByteArray): String {
            return "msgId=${((data[0].toInt() and 0xff) * 256) or (data[1].toInt() and 0xff)}"
        }

        fun getBroadcastAddresses(): List<InetAddress> {
            val broadcastAddresses = arrayListOf<InetAddress>()
            for (iface in NetworkInterface.getNetworkInterfaces().iterator()) {
                if (!iface.isLoopback && iface.isUp) {
                    logger.debug { "    ${iface.name} ${iface.displayName}" }

                    for (ipAddr in iface.interfaceAddresses) {
                        if (ipAddr.address is Inet4Address) {
                            logger.debug { "        $ipAddr: broadcast=${ipAddr.broadcast}" }
                            broadcastAddresses.add(ipAddr.broadcast)
                        } else {
                            logger.debug { "        $ipAddr (unknown)" }
                        }
                    }
                }
            }
            return broadcastAddresses
        }
    }

    override fun link(name: String): RealLink = link

    inner class RealLink : Network.Link {

        override val udpMtu = MAX_UDP_SIZE

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

        override val mdns by lazy { JvmMdns() }

        inner class JvmUdpSocket(override val serverPort: Int) : Network.UdpSocket {
            internal var udpSocket = DatagramSocket(serverPort)

            private val broadcastAddresses: List<InetAddress>
            init {
                broadcastAddresses = getBroadcastAddresses()

//                println("Trying to set send buffer size to ${4*MAX_UDP_SIZE}")
//                udpSocket.sendBufferSize = 4*MAX_UDP_SIZE;
                logger.info { "UDP socket bound to ${udpSocket.localAddress}" }
                logger.info { "Broadcast addresses:" }
                broadcastAddresses.forEach {
                    logger.info { "  $it" }
                }
                logger.debug { "Send buffer size is ${udpSocket.sendBufferSize}" }
            }

            override fun sendUdp(toAddress: Network.Address, port: Int, bytes: ByteArray) {
                // println("Sending ${bytes.size} bytes to ${toAddress}")
                val packetOut = DatagramPacket(bytes, 0, bytes.size, (toAddress as IpAddress).address, port)
                try {
                    udpSocket.send(packetOut)
                } catch (e: IOException) {
                    throw IOException("sending to $toAddress: ${e.message}", e)
                }
            }

            override fun broadcastUdp(port: Int, bytes: ByteArray) {
                for (broadcastAddress in broadcastAddresses) {
                    val broadcastSocketAddress = InetSocketAddress(broadcastAddress, port)
                    val packetOut = DatagramPacket(bytes, 0, bytes.size, broadcastSocketAddress)
                    try {
                        udpSocket.send(packetOut)
                    } catch (e: Exception) {
                        logger.warn(e) { "Failed to broadcast on $broadcastAddress" }
                    }
                }
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
                            close(CloseReason(
                                CloseReason.Codes.INTERNAL_ERROR, "Internal error: ${e.message}"))
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

            override fun routing(config: Network.HttpServer.HttpRouting.() -> Unit) {
                application.routing {
                    val route = this
                    val routing = object : Network.HttpServer.HttpRouting {
                        override fun get(
                            path: String,
                            handler: (Network.HttpServer.HttpRequest) -> Network.HttpResponse
                        ) {
                            route.get(path) {
                                val response = handler.invoke(object : Network.HttpServer.HttpRequest {
                                    override fun param(name: String): String? = call.parameters[name]
                                })
                                call.respond(
                                    ByteArrayContent(
                                        response.body,
                                        ContentType.parse(response.contentType),
                                        HttpStatusCode.fromValue(response.statusCode)
                                    ))
                            }
                        }
                    }
                    config.invoke(routing)
                }
            }
        }

        override val myAddress = IpAddress.mine()
        override val myHostname = myAddress.address.hostName.replace(Regex("\\.local(domain)?\\.?$"), "")

        inner class JvmMdns() : Network.Mdns {
            private val svc = JmDNS.create(InetAddress.getLocalHost())

            override fun register(hostname: String, type: String, proto: String, port: Int, domain: String, params: MutableMap<String, String>): Network.MdnsRegisteredService? {
                val inst = ServiceInfo.create("$hostname.$type.$proto.${domain.normalizeMdnsDomain()}", hostname, port, 1, 1, params)
                svc.registerService(inst)
                return JvmRegisteredService(inst)
            }

            override fun unregister(inst: Network.MdnsRegisteredService?) { inst?.unregister() }

            override fun listen(type: String, proto: String, domain: String, handler: Network.MdnsListenHandler) {
                val wrapper = object : ServiceListener {
                    override fun serviceResolved(event: ServiceEvent?) {
                        if (event != null) {
                            handler.resolved(JvmMdnsService(event.info))
                        }
                    }

                    override fun serviceRemoved(event: ServiceEvent?) {
                        if (event != null) {
                            handler.removed(JvmMdnsService(event.info))
                        }
                    }

                    override fun serviceAdded(event: ServiceEvent?) { /* noop */ }
                }
                svc.addServiceListener("$type.$proto.${domain.normalizeMdnsDomain()}", wrapper)
            }

            open inner class JvmMdnsService(private val inst: ServiceInfo) : Network.MdnsService {
                override val hostname : String get() = inst.name
                override val type     : String get() = inst.type.removeSuffix(inst.domain).removeSuffix(".").removeSuffix(inst.protocol).removeSuffix(".")
                override val proto    : String get() = inst.protocol
                override val port     : Int    get() = inst.port
                override val domain   : String get() = inst.domain

                override fun getAddress(): Network.Address? {
                    val addresses = inst.inet4Addresses
                    return if (addresses.isNotEmpty()) {
                        IpAddress(addresses[0])
                    } else { null }
                }

                override fun getTXT(key: String): String? = inst.getPropertyString(key)

                override fun getAllTXTs(): MutableMap<String, String> {
                    val map = mutableMapOf<String, String>()
                    val names = inst.propertyNames
                    while (names.hasMoreElements()) {
                        val key = names.nextElement()
                        map[key] = inst.getPropertyString(key)
                    }
                    return map
                }
            }

            inner class JvmRegisteredService(private val inst: ServiceInfo) : JvmMdnsService(inst), Network.MdnsRegisteredService {
                override fun unregister() { svc.unregisterService(inst) }

                override fun updateTXT(txt: MutableMap<String, String>) {
                    val map = getAllTXTs()
                    map.putAll(txt)
                    inst.setText(map)
                }

                override fun updateTXT(key: String, value: String) {
                    updateTXT(mutableMapOf(Pair(key, value)))
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
