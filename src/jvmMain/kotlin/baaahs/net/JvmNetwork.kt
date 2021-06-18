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
import kotlinx.coroutines.*
import java.io.IOException
import java.net.*
import java.nio.ByteBuffer
import java.time.Duration
import javax.jmdns.JmmDNS
import javax.jmdns.ServiceEvent
import javax.jmdns.ServiceInfo
import javax.jmdns.ServiceListener
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

            private val broadcastAddresses: List<InetAddress> = getBroadcastAddresses()

            init {
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

        override suspend fun httpGetRequest(address: Network.Address, port: Int, path: String): String {
            val url = URLBuilder().apply {
                this.host = address.asString()
                this.path(path)
                this.port = port
            }.buildString()

            return coroutineScope {
                withContext(Dispatchers.IO) {
                    makeRequest(url)
                }
            }
        }

        private fun makeRequest(url: String): String {
            val connection = URL(url).openConnection() as HttpURLConnection
            connection.setRequestProperty("accept", "application/json")
            return connection.inputStream.bufferedReader().use { it.readText() }
        }

        override fun connectWebSocket(
            toAddress: Network.Address,
            port: Int,
            path: String,
            webSocketListener: Network.WebSocketListener
        ): Network.TcpConnection {
            TODO("JvmNetwork.connectWebSocket not implemented")
        }

        override fun createAddress(name: String): Network.Address {
            return IpAddress(InetAddress.getByName(name))
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

        inner class JvmMdns : Network.Mdns {
            private val svc = run {
                logger.debug { "Initilizing JmmDNS." }
                JmmDNS.Factory.getInstance() // Listens on all network interfaces.
            }

            override fun register(
                hostname: String,
                type: String,
                proto: String,
                port: Int,
                domain: String,
                params: Map<String, String>
            ): Network.MdnsRegisteredService {
                val serviceType = "$hostname.$type.$proto.${domain.normalizeMdnsDomain()}"
                logger.info { "Registering mDNS service \"$serviceType}\"." }

                val inst = ServiceInfo.create(serviceType, hostname, port, 1, 1, params)
                svc.registerService(inst)
                return JvmRegisteredService(inst)
            }

            override fun unregister(inst: Network.MdnsRegisteredService) {
                logger.info { "Unregistering mDNS service \"${inst.type}}\"." }
                inst.unregister()
            }

            override fun listen(type: String, proto: String, domain: String, handler: Network.MdnsListenHandler) {
                val serviceType = "$type.$proto.${domain.normalizeMdnsDomain()}"
                logger.info { "Listening for mDNS service \"$serviceType}\"" }

                svc.addServiceListener(serviceType, object : ServiceListener {
                    override fun serviceAdded(event: ServiceEvent) {
                        handler.added(JvmMdnsService(event.info))
                    }

                    override fun serviceRemoved(event: ServiceEvent) {
                        handler.removed(JvmMdnsService(event.info))
                    }

                    override fun serviceResolved(event: ServiceEvent) {
                        handler.resolved(JvmMdnsService(event.info))
                    }
                })
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

                override fun getAllTXTs(): Map<String, String> {
                    val map = mutableMapOf<String, String>()
                    val names = inst.propertyNames
                    while (names.hasMoreElements()) {
                        val key = names.nextElement()
                        map[key] = inst.getPropertyString(key)
                    }
                    return map
                }

                override fun toString(): String {
                    return "JvmMdnsService(inst=$inst, hostname='$hostname', type='$type', proto='$proto', port=$port, domain='$domain')"
                }
            }

            inner class JvmRegisteredService(private val inst: ServiceInfo) : JvmMdnsService(inst), Network.MdnsRegisteredService {
                override fun unregister() { svc.unregisterService(inst) }

                override fun updateTXT(txt: Map<String, String>) {
                    val map = getAllTXTs().toMutableMap()
                    map.putAll(txt)
                    inst.setText(map)
                }

                override fun updateTXT(key: String, value: String) {
                    updateTXT(mapOf(key to value))
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

        override fun asString(): String = address.hostAddress
        override fun toString(): String = asString()
    }
}
