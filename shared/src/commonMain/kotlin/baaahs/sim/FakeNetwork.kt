package baaahs.sim

import baaahs.net.Network
import baaahs.util.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.random.Random

class FakeNetwork(
    private val networkDelay: Int = 1,
    private val coroutineScope: CoroutineScope = CoroutineScope(EmptyCoroutineContext)
) : Network {
    private val addressCounters = hashMapOf<String, Int>()
    val facade = Facade()

    private val udpListeners: MutableMap<Pair<Network.Address, Int>, Network.UdpListener> = hashMapOf()
    private val udpListenersByPort: MutableMap<Int, MutableList<Network.UdpListener>> = hashMapOf()

    private val httpServersByPort:
            MutableMap<Pair<Network.Address, Int>, FakeLink.FakeHttpServer> = hashMapOf()

    private val mdns = FakeMdns()

    override fun link(name: String): FakeLink {
        val next = addressCounters.getOrElse(name) { 1 }
        addressCounters[name] = next + 1
        val address = FakeAddress("$name$next")
        return FakeLink(address)
    }

    fun link(address: FakeAddress): FakeLink {
        return FakeLink(address)
    }

    var packetLossRate: Float = .005f
    var packetsReceived: Int = 0
    var packetsDropped: Int = 0
    var packetsQueued: Int = 0

    private fun sendPacketShouldSucceed() = Random.nextFloat() > packetLossRate / 2
    private fun receivePacketShouldSucceed() = Random.nextFloat() > packetLossRate / 2

    inner class FakeLink(override val myAddress: FakeAddress) : Network.Link {
        override val udpMtu = 1500
        override val myHostname = "FakeHost"
        private var nextAvailablePort = 65000
        var webSocketListeners = mutableListOf<Network.WebSocketListener>()
        var tcpConnections = mutableListOf<Network.TcpConnection>()

        override fun listenUdp(port: Int, udpListener: Network.UdpListener): Network.UdpSocket {
            val serverPort = if (port == 0) nextAvailablePort++ else port
            udpListeners[myAddress to serverPort] = udpListener
            val portListeners = udpListenersByPort.getOrPut(serverPort) { mutableListOf() }
            portListeners.add(udpListener)
            return FakeUdpSocket(serverPort)
        }

        override val mdns = this@FakeNetwork.mdns

        override fun createHttpServer(port: Int): Network.HttpServer {
            val fakeHttpServer = FakeHttpServer(port)
            httpServersByPort[myAddress to port] = fakeHttpServer
            logger.info { "HTTP server listening on $myAddress:$port." }
            return fakeHttpServer
        }

        override suspend fun httpGetRequest(address: Network.Address, port: Int, path: String): String {
            val fakeHttpServer = httpServersByPort[address to port]
                ?: error ("No HTTP server at $address:$port.")
            val response = fakeHttpServer.httpGetResponses[path]
                ?: error("No response registered for http://$address:$port/$path.")
            return response.decodeToString()
        }

        override fun connectWebSocket(
            toAddress: Network.Address,
            port: Int,
            path: String,
            webSocketListener: Network.WebSocketListener
        ): Network.TcpConnection {
            webSocketListeners.add(webSocketListener)

            val fakeHttpServer = httpServersByPort[toAddress to port]
            if (fakeHttpServer == null) {
                logger.warn { "No HTTP server at $toAddress:$port for $path" }
            }

            val onConnectCallback = fakeHttpServer?.webSocketListeners?.get(path)
            if (onConnectCallback == null) {
                logger.warn { "No WebSocket listener at $toAddress:$port$path" }
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
                serverListener.connected(serverSideConnection)
                webSocketListener.connected(clientSideConnection)
            }

            tcpConnections.add(clientSideConnection)
            return clientSideConnection
        }

        override fun createAddress(name: String): Network.Address = FakeAddress(name)

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

            override fun close() {
                coroutineScope.launch {
                    webSocketListener?.reset(this@FakeTcpConnection)
                }
            }
        }

        private inner class FakeUdpSocket(override val serverPort: Int) : Network.UdpSocket {
            private var closed = false

            override fun sendUdp(toAddress: Network.Address, port: Int, bytes: ByteArray) {
                if (closed) error("Socket is closed.")
                if (!sendPacketShouldSucceed()) {
                    logger.debug { "Dropped UDP packet to $toAddress:$port" }
                    packetsDropped++.updates(facade)
                    return
                }

                val listener = udpListeners[toAddress to port]
                if (listener != null) transmitUdp(myAddress, serverPort, listener, bytes)
            }

            override fun broadcastUdp(port: Int, bytes: ByteArray) {
                if (closed) error("Socket is closed.")
                if (!sendPacketShouldSucceed()) {
                    logger.debug { "Dropped UDP packet to *:$port" }
                    packetsDropped++.updates(facade)
                    return
                }

                udpListenersByPort[port]?.forEach { listener ->
                    transmitUdp(myAddress, serverPort, listener, bytes)
                }
            }

            override fun close() {
                println("FakeUdpSocket.close $myAddress")
                udpListeners.remove(myAddress to serverPort)
                closed = true
            }

            private fun transmitUdp(
                fromAddress: Network.Address,
                fromPort: Int,
                udpListener: Network.UdpListener,
                bytes: ByteArray
            ) {
                packetsQueued++
                coroutineScope.launch {
                    networkDelay()

                    if (!receivePacketShouldSucceed()) {
                        packetsDropped++
                    } else {
                        packetsReceived++

                        udpListener.receive(fromAddress, fromPort, bytes)
                    }
                    packetsQueued--.updates(facade)
                }
            }
        }

        internal inner class FakeHttpServer(val port: Int) : Network.HttpServer {
            val httpGetResponses: MutableMap<String, ByteArray> = mutableMapOf()

            val webSocketListeners: MutableMap<String, (Network.TcpConnection) -> Network.WebSocketListener> =
                mutableMapOf()

            override fun listenWebSocket(
                path: String,
                onConnect: (incomingConnection: Network.TcpConnection) -> Network.WebSocketListener
            ) {
                webSocketListeners[path] = onConnect
            }

            override fun routing(config: Network.HttpServer.HttpRouting.() -> Unit) {
                TODO("not implemented")
            }

            override fun start() {
                TODO("not implemented")
            }
        }
    }

    private suspend fun networkDelay() {
        if (networkDelay != 0) delay(networkDelay.toLong())
    }

    data class FakeAddress(val name: String) : Network.Address {
        override fun asString(): String = name
        override fun toString(): String = asString()
    }

    companion object {
        val logger = Logger<FakeNetwork>()
    }

    inner class Facade : baaahs.ui.Facade() {
        var packetLossRate: Float
            get() = this@FakeNetwork.packetLossRate
            set(value) { this@FakeNetwork.packetLossRate = value }
        val packetsReceived: Int get() = this@FakeNetwork.packetsReceived
        val packetsDropped: Int get() = this@FakeNetwork.packetsDropped
        val packetsQueued: Int get() = this@FakeNetwork.packetsQueued
    }

    @Suppress("unused")
    fun Any?.updates(facade: Facade) = facade.notifyChanged()
}