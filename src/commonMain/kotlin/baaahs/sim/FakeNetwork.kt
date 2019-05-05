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

    private val tcpServerSocketsByPort: MutableMap<Pair<Network.Address, Int>, Network.TcpServerSocketListener> =
        hashMapOf()

    override fun link(): Network.Link {
        val address = FakeAddress(nextAddress++)
        return FakeLink(address)
    }

    private fun listenUdp(address: Network.Address, port: Int, udpListener: Network.UdpListener) {
        udpListeners.put(Pair(address, port), udpListener)
        val portListeners = udpListenersByPort.getOrPut(port) { mutableListOf() }
        portListeners.add(udpListener)
    }

    private fun sendUdp(fromAddress: Network.Address, toAddress: Network.Address, port: Int, bytes: ByteArray) {
        if (!sendPacketSuccess()) {
            display?.droppedPacket()
            return
        }

        val listener = udpListeners[Pair(toAddress, port)]
        if (listener != null) transmitUdp(fromAddress, listener, bytes)
    }

    private fun broadcastUdp(fromAddress: Network.Address, port: Int, bytes: ByteArray) {
        if (!sendPacketSuccess()) {
            display?.droppedPacket()
            return
        }

        udpListenersByPort[port]?.forEach { listener ->
            transmitUdp(fromAddress, listener, bytes)
        }
    }

    private fun transmitUdp(fromAddress: Network.Address, udpListener: Network.UdpListener, bytes: ByteArray) {
        coroutineScope.launch {
            networkDelay()

            if (!receivePacketSuccess()) {
                display?.droppedPacket()
            } else {
                display?.receivedPacket()
                udpListener.receive(fromAddress, bytes)
            }
        }
    }

    private fun listenTcp(
        myAddress: Network.Address,
        port: Int,
        tcpServerSocketListener: Network.TcpServerSocketListener
    ) {
        tcpServerSocketsByPort[Pair(myAddress, port)] = tcpServerSocketListener
    }

    private fun connectTcp(
        clientAddress: Network.Address,
        serverAddress: Network.Address,
        serverPort: Int,
        clientListener: Network.TcpListener
    ): Network.TcpConnection {
        val serverSocketListener = tcpServerSocketsByPort[Pair(serverAddress, serverPort)]
        if (serverSocketListener == null) {
            val connection = FakeTcpConnection(clientAddress, serverAddress, serverPort, null)
            coroutineScope.launch {
                networkDelay()
                clientListener.reset(connection);
            }
            return connection
        }

        lateinit var clientSideConnection: FakeTcpConnection
        val serverSideConnection = FakeTcpConnection(clientAddress, serverAddress, serverPort, clientListener) {
            clientSideConnection
        }

        val serverListener = serverSocketListener.incomingConnection(serverSideConnection)

        clientSideConnection = FakeTcpConnection(clientAddress, serverAddress, serverPort, serverListener) {
            serverSideConnection
        }

        coroutineScope.launch {
            networkDelay();
            clientListener.connected(clientSideConnection)
        }

        coroutineScope.launch {
            networkDelay();
            serverListener.connected(serverSideConnection)
        }

        return clientSideConnection
    }

    inner class FakeTcpConnection(
        override val fromAddress: Network.Address,
        override val toAddress: Network.Address,
        override val port: Int,
        private val tcpListener: Network.TcpListener? = null,
        private val otherListener: (() -> Network.TcpConnection)? = null
    ) : Network.TcpConnection {
        override fun send(bytes: ByteArray) {
            coroutineScope.launch {
                tcpListener?.receive(otherListener!!(), bytes)
            }
        }
    }

    private fun sendPacketSuccess() = Random.nextFloat() > packetLossRate() / 2
    private fun receivePacketSuccess() = Random.nextFloat() > packetLossRate() / 2
    private fun packetLossRate() = display?.packetLossRate ?: 0f

    private inner class FakeLink(override val myAddress: Network.Address) : Network.Link {
        override val udpMtu = 1500

        override fun listenUdp(port: Int, udpListener: Network.UdpListener) {
            this@FakeNetwork.listenUdp(myAddress, port, udpListener)
        }

        override fun sendUdp(toAddress: Network.Address, port: Int, bytes: ByteArray) {
            this@FakeNetwork.sendUdp(myAddress, toAddress, port, bytes)
        }

        override fun broadcastUdp(port: Int, bytes: ByteArray) {
            this@FakeNetwork.broadcastUdp(myAddress, port, bytes)
        }

        override fun listenTcp(port: Int, tcpServerSocketListener: Network.TcpServerSocketListener) {
            this@FakeNetwork.listenTcp(myAddress, port, tcpServerSocketListener)
        }

        override fun connectTcp(
            toAddress: Network.Address,
            port: Int,
            tcpListener: Network.TcpListener
        ): Network.TcpConnection = this@FakeNetwork.connectTcp(myAddress, toAddress, port, tcpListener)
    }

    private suspend fun networkDelay() {
        if (networkDelay != 0) delay(networkDelay.toLong())
    }

    private data class FakeAddress(val id: Int) : Network.Address {
        override fun toString(): String = "x${id.toString(16)}"
    }
}