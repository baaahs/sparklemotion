package baaahs.sim

import baaahs.NetworkDisplay
import baaahs.net.Network
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.random.Random

class FakeNetwork(
    private val networkDelay: Int = 1,
    private val display: NetworkDisplay? = null,
    coroutineContext: CoroutineContext = EmptyCoroutineContext
) : Network {
    private val coroutineScope: CoroutineScope = object : CoroutineScope {
        override val coroutineContext: CoroutineContext get() = coroutineContext
    }
    private var nextAddress = 0xb00f
    private val udpListeners: MutableMap<Pair<Network.Address, Int>, Network.UdpListener> = hashMapOf()
    private val udpListenersByPort: MutableMap<Int, MutableList<Network.UdpListener>> = hashMapOf()

    private val httpServersByPort:
            MutableMap<Pair<Network.Address, Int>, FakeLink.FakeHttpServer> = hashMapOf()

    private var services = mutableMapOf<String, FakeLink.FakeMdns.FakeMdnsService>()
    private var listeners = mutableMapOf<String, MutableList<Network.MdnsListenHandler>>()
    private var nextServiceId : Int = 0

    override fun link(): FakeLink {
        val address = FakeAddress(nextAddress++)
        return FakeLink(address)
    }

    private fun sendPacketSuccess() = Random.nextFloat() > packetLossRate() / 2
    private fun receivePacketSuccess() = Random.nextFloat() > packetLossRate() / 2
    private fun packetLossRate() = display?.packetLossRate ?: 0f

    inner class FakeLink(override val myAddress: Network.Address) : Network.Link {
        override val udpMtu = 1500
        override val myHostname = "FakeHost"
        private var nextAvailablePort = 65000
        var webSocketListeners = mutableListOf<Network.WebSocketListener>()
        var tcpConnections = mutableListOf<Network.TcpConnection>()

        override fun listenUdp(port: Int, udpListener: Network.UdpListener): Network.UdpSocket {
            val serverPort = if (port == 0) nextAvailablePort++ else port
            udpListeners.put(Pair(myAddress, serverPort), udpListener)
            val portListeners = udpListenersByPort.getOrPut(serverPort) { mutableListOf() }
            portListeners.add(udpListener)
            return FakeUdpSocket(serverPort)
        }

        override fun mdns(): Network.Mdns {
            return mdns
        }

        override fun startHttpServer(port: Int): Network.HttpServer {
            val fakeHttpServer = FakeHttpServer(port)
            httpServersByPort[myAddress to port] = fakeHttpServer
            return fakeHttpServer
        }

        override fun connectWebSocket(
            toAddress: Network.Address,
            port: Int,
            path: String,
            webSocketListener: Network.WebSocketListener
        ): Network.TcpConnection {
            webSocketListeners.add(webSocketListener)

            val fakeHttpServer = httpServersByPort[toAddress to port]
            val onConnectCallback = fakeHttpServer?.webSocketListeners?.get(path)
            if (onConnectCallback == null) {
                val connection = FakeTcpConnection(myAddress, toAddress, port, null)
                coroutineScope.launch {
                    networkDelay()
                    webSocketListener.reset(connection)
                }
                tcpConnections.add(connection)
                return connection
            }

            lateinit var clientSideConnection: FakeTcpConnection
            val serverSideConnection = FakeTcpConnection(myAddress, toAddress, port, webSocketListener) {
                clientSideConnection
            }

            val serverListener = onConnectCallback(serverSideConnection)

            clientSideConnection = FakeTcpConnection(myAddress, toAddress, port, serverListener) {
                serverSideConnection
            }

            coroutineScope.launch {
                networkDelay()
                webSocketListener.connected(clientSideConnection)
            }

            coroutineScope.launch {
                networkDelay()
                serverListener.connected(serverSideConnection)
            }
            tcpConnections.add(clientSideConnection)
            return clientSideConnection
        }

        inner class FakeTcpConnection(
            override val fromAddress: Network.Address,
            override val toAddress: Network.Address,
            override val port: Int,
            private val webSocketListener: Network.WebSocketListener? = null,
            private val otherListener: (() -> Network.TcpConnection)? = null
        ) : Network.TcpConnection {
            override fun send(bytes: ByteArray) {
                coroutineScope.launch {
                    webSocketListener?.receive(otherListener!!(), bytes)
                }
            }
        }

        private val mdns = FakeMdns()

        inner class FakeMdns() : Network.Mdns {

            override fun register(
                hostname: String,
                type: String,
                proto: String,
                port: Int,
                domain: String,
                params: MutableMap<String, String>
            ): Network.MdnsRegisteredService? {
                val fullname = "$hostname.$type.$proto.${domain.normalizeMdnsDomain()}"
                val inst = FakeRegisteredService(hostname, type, proto, port, domain.normalizeMdnsDomain(), params)
                services[fullname] = inst
                (inst as? FakeRegisteredService)?.announceResolved()
                return inst
            }

            override fun unregister(inst: Network.MdnsRegisteredService?) { inst?.unregister() }

            override fun listen(type: String, proto: String, domain: String, handler: Network.MdnsListenHandler) {
                listeners.getOrPut("$type.$proto.${domain.normalizeMdnsDomain()}") { mutableListOf() }.add(handler)
            }

            open inner class FakeMdnsService(override val hostname: String, override val type: String, override val proto: String, override val port: Int, override val domain: String, val params: MutableMap<String, String>) : Network.MdnsService {
                private var id : Int = nextServiceId++

                override fun getAddress(): Network.Address? = FakeAddress(id)

                override fun getTXT(key: String): String? = params[key]

                override fun getAllTXTs(): MutableMap<String, String> = params
            }

            inner class FakeRegisteredService(hostname: String, type: String, proto: String, port: Int, domain: String, params: MutableMap<String, String>) : FakeMdnsService(hostname, type, proto, port, domain.normalizeMdnsDomain(), params), Network.MdnsRegisteredService {
                override fun unregister() {
                    val fullname = "$hostname.$type.$proto.${domain.normalizeMdnsDomain()}"
                    (services.remove(fullname) as? FakeRegisteredService)?.announceRemoved()
                }

                override fun updateTXT(txt: MutableMap<String, String>) {
                    params.putAll(txt)
                    announceResolved()
                }

                override fun updateTXT(key: String, value: String) {
                    params[key] = value
                    announceResolved()
                }

                internal fun announceResolved() {
                    listeners["$type.$proto.${domain.normalizeMdnsDomain()}"]?.forEach { it.resolved(this) }
                }

                internal fun announceRemoved() {
                    listeners["$type.$proto.${domain.normalizeMdnsDomain()}"]?.forEach { it.removed(this) }
                }
            }
        }

        private inner class FakeUdpSocket(override val serverPort: Int) : Network.UdpSocket {
            override fun sendUdp(toAddress: Network.Address, port: Int, bytes: ByteArray) {
                if (!sendPacketSuccess()) {
                    display?.droppedPacket()
                    return
                }

                val listener = udpListeners[Pair(toAddress, port)]
                if (listener != null) transmitUdp(myAddress, serverPort, listener, bytes)
            }

            override fun broadcastUdp(port: Int, bytes: ByteArray) {
                if (!sendPacketSuccess()) {
                    display?.droppedPacket()
                    return
                }

                udpListenersByPort[port]?.forEach { listener ->
                    transmitUdp(myAddress, serverPort, listener, bytes)
                }
            }

            private fun transmitUdp(
                fromAddress: Network.Address,
                fromPort: Int,
                udpListener: Network.UdpListener,
                bytes: ByteArray
            ) {
                coroutineScope.launch {
                    networkDelay()

                    if (!receivePacketSuccess()) {
                        display?.droppedPacket()
                    } else {
                        display?.receivedPacket()
                        udpListener.receive(fromAddress, fromPort, bytes)
                    }
                }
            }
        }

        internal inner class FakeHttpServer(val port: Int) : Network.HttpServer {
            val webSocketListeners: MutableMap<String, (Network.TcpConnection) -> Network.WebSocketListener> =
                mutableMapOf()

            override fun listenWebSocket(
                path: String,
                onConnect: (incomingConnection: Network.TcpConnection) -> Network.WebSocketListener
            ) {
                webSocketListeners[path] = onConnect
            }
        }
    }

    private suspend fun networkDelay() {
        if (networkDelay != 0) delay(networkDelay.toLong())
    }

    private data class FakeAddress(val id: Int) : Network.Address {
        override fun toString(): String = "x${id.toString(16)}"
    }
}