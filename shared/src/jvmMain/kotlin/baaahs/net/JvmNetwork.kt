package baaahs.net

import baaahs.util.Logger
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.websocket.*
import kotlinx.coroutines.*
import java.io.IOException
import java.net.*
import java.time.Duration
import javax.jmdns.JmmDNS
import javax.jmdns.ServiceEvent
import javax.jmdns.ServiceInfo
import javax.jmdns.ServiceListener
import kotlin.collections.set
import kotlin.concurrent.thread


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
            thread(isDaemon = true) {
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
                            logger.error(e) { "Error handling UDP packet" }
                        }
                    }
                }
            }
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
                    throw IOException("sending to $toAddress: ${e.message}")
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

            override fun close() {
                udpSocket.close()
            }
        }

        override fun createHttpServer(port: Int): KtorHttpServer {
            val httpServer = embeddedServer(Netty, port, configure = {
                // Let's give brains lots of time for OTA download:
                responseWriteTimeoutSeconds = 3000
            }) {
                install(WebSockets) {
                    pingPeriod = Duration.ofSeconds(15)
                    timeout = Duration.ofSeconds(15)
                    maxFrameSize = Long.MAX_VALUE
                    masking = false
                }
            }

            return KtorHttpServer(httpServer, this, port)
                .also { httpServer.start(false) }
        }

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
