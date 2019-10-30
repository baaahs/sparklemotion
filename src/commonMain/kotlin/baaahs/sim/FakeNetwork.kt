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

    override fun link(): FakeLink {
        val address = FakeAddress(nextAddress++)
        return FakeLink(address)
    }

    private fun sendPacketSuccess() = Random.nextFloat() > packetLossRate() / 2
    private fun receivePacketSuccess() = Random.nextFloat() > packetLossRate() / 2
    private fun packetLossRate() = display?.packetLossRate ?: 0f

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

        private inner class FakeUdpSocket(override val serverPort: Int) : Network.UdpSocket {
            override fun sendUdp(toAddress: Network.Address, port: Int, bytes: ByteArray) {
                if (!sendPacketSuccess()) {
                    display?.apply { packetsDropped++ }
                    return
                }

                val listener = udpListeners[Pair(toAddress, port)]
                if (listener != null) transmitUdp(myAddress, serverPort, listener, bytes)
            }

            override fun broadcastUdp(port: Int, bytes: ByteArray) {
                if (!sendPacketSuccess()) {
                    display?.apply { packetsDropped++ }
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
                        display?.apply { packetsDropped++ }
                    } else {
                        display?.apply { packetsReceived++ }
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