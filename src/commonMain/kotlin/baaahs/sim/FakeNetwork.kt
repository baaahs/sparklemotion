package baaahs.sim

import baaahs.Logger
import baaahs.net.Network
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.random.Random

class FakeNetwork(
    private val networkDelay: Int = 1,
    coroutineContext: CoroutineContext = EmptyCoroutineContext
) : Network {
    val facade = Facade()

    private val coroutineScope: CoroutineScope = object : CoroutineScope {
        override val coroutineContext: CoroutineContext get() = coroutineContext
    }
    private var nextAddress = 0xb00f
    private val udpListeners: MutableMap<Pair<Network.Address, Int>, Network.UdpListener> = hashMapOf()
    private val udpListenersByPort: MutableMap<Int, MutableList<Network.UdpListener>> = hashMapOf()

    private val httpServersByPort:
            MutableMap<Pair<Network.Address, Int>, FakeLink.FakeHttpServer> = hashMapOf()

    override fun link(): FakeLink {
        val address = FakeAddress(nextAddress++)
        return FakeLink(address)
    }

    var packetLossRate: Float = .05f
    var packetsReceived: Int = 0
    var packetsDropped: Int = 0

    private fun sendPacketShouldSucceed() = Random.nextFloat() > packetLossRate / 2
    private fun receivePacketShouldSucceed() = Random.nextFloat() > packetLossRate / 2

    inner class FakeLink(override val myAddress: Network.Address) : Network.Link {
        override val udpMtu = 1500
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
            if (fakeHttpServer == null) {
                logger.warn { "No HTTP server at $toAddress:$port for $path" }
            }

            val onConnectCallback = fakeHttpServer?.webSocketListeners?.get(path)
            if (onConnectCallback == null) {
                val connection = FakeTcpConnection(myAddress, toAddress, port, null)
                coroutineScope.launch {
                    networkDelay()
                    webSocketListener.reset(connection)
                }
                tcpConnections.add(connection)
                return connection
            } else {
                logger.warn { "No WebSocket listener at $toAddress:$port$path" }
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

        private inner class FakeUdpSocket(override val serverPort: Int) : Network.UdpSocket {
            override fun sendUdp(toAddress: Network.Address, port: Int, bytes: ByteArray) {
                if (!sendPacketShouldSucceed()) {
                    packetsDropped++.updates(facade)
                    return
                }

                val listener = udpListeners[Pair(toAddress, port)]
                if (listener != null) transmitUdp(myAddress, serverPort, listener, bytes)
            }

            override fun broadcastUdp(port: Int, bytes: ByteArray) {
                if (!sendPacketShouldSucceed()) {
                    packetsDropped++.updates(facade)
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

                    if (!receivePacketShouldSucceed()) {
                        packetsDropped++.updates(facade)
                    } else {
                        packetsReceived++.updates(facade)

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

    companion object {
        val logger = Logger("FakeNetwork")
    }

    inner class Facade : baaahs.ui.Facade() {
        var packetLossRate: Float
            get() = this@FakeNetwork.packetLossRate
            set(value) { this@FakeNetwork.packetLossRate = value }
        val packetsReceived: Int get() = this@FakeNetwork.packetsReceived
        val packetsDropped: Int get() = this@FakeNetwork.packetsDropped
    }

    @Suppress("unused")
    fun Any?.updates(facade: Facade) = facade.notifyChanged()
}