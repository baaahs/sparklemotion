package baaahs

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

interface Network {
    fun link(): Link

    interface Link {
        val myAddress: Address

        fun listenUdp(port: Int, udpListener: UdpListener)
        fun sendUdp(toAddress: Address, port: Int, bytes: ByteArray)
        fun broadcastUdp(port: Int, bytes: ByteArray)

        fun sendUdp(toAddress: Address, port: Int, message: Message) {
            sendUdp(toAddress, port, message.toBytes())
        }

        fun broadcastUdp(port: Int, message: Message) {
            broadcastUdp(port, message.toBytes())
        }

        fun listenTcp(port: Int, tcpListener: TcpListener)
        fun connectTcp(toAddress: Address, port: Int, tcpListener: TcpListener): TcpConnection
    }

    interface Address

    interface UdpListener {
        fun receive(fromAddress: Address, bytes: ByteArray)
    }

    interface TcpConnection {
        val fromAddress: Address
        val toAddress: Address
        val port: Int

        fun send(bytes: ByteArray)

        fun send(message: Message) {
            send(message.toBytes())
        }
    }

    interface TcpListener {
        fun connected(tcpConnection: TcpConnection)
        fun receive(tcpConnection: TcpConnection, bytes: ByteArray)
        fun reset(tcpConnection: TcpConnection)
    }
}


class FakeNetwork(
    private val networkDelay: Long = 1L,
    private val display: NetworkDisplay
) : Network {
    private var nextAddress = 0xb00f
    private val udpListeners: MutableMap<Pair<Network.Address, Int>, Network.UdpListener> = hashMapOf()
    private val udpListenersByPort: MutableMap<Int, MutableList<Network.UdpListener>> = hashMapOf()

    private val tcpServerSocketsByPort: MutableMap<Pair<Network.Address, Int>, Network.TcpListener> =
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
            display.droppedPacket()
            return
        }

        val listener = udpListeners[Pair(toAddress, port)]
        if (listener != null) transmitUdp(fromAddress, listener, bytes)
    }

    private fun broadcastUdp(fromAddress: Network.Address, port: Int, bytes: ByteArray) {
        if (!sendPacketSuccess()) {
            display.droppedPacket()
            return
        }

        udpListenersByPort[port]?.forEach { listener ->
            transmitUdp(fromAddress, listener, bytes)
        }
    }

    private fun transmitUdp(fromAddress: Network.Address, udpListener: Network.UdpListener, bytes: ByteArray) {
        GlobalScope.launch {
            delay(networkDelay)
            if (!receivePacketSuccess()) {
                display.droppedPacket()
            } else {
                display.receivedPacket()
                udpListener.receive(fromAddress, bytes)
            }
        }
    }

    private fun listenTcp(myAddress: Network.Address, port: Int, tcpListener: Network.TcpListener) {
        tcpServerSocketsByPort[Pair(myAddress, port)] = tcpListener
    }

    private fun connectTcp(
        clientAddress: Network.Address,
        serverAddress: Network.Address,
        serverPort: Int,
        clientListener: Network.TcpListener
    ): Network.TcpConnection {
        val serverListener = tcpServerSocketsByPort[Pair(serverAddress, serverPort)]
        if (serverListener == null) {
            val connection = FakeTcpConnection(clientAddress, serverAddress, serverPort, null)
            GlobalScope.launch {
                delay(1);
                clientListener.reset(connection);
            }
            return connection
        }

        lateinit var clientSideConnection: FakeTcpConnection
        val serverSideConnection = FakeTcpConnection(clientAddress, serverAddress, serverPort, clientListener) {
            clientSideConnection
        }

        clientSideConnection = FakeTcpConnection(clientAddress, serverAddress, serverPort, serverListener) {
            serverSideConnection
        }

        GlobalScope.launch {
            delay(1);
            clientListener.connected(clientSideConnection)
        }

        GlobalScope.launch {
            delay(1);
            serverListener.connected(serverSideConnection)
        }

        return clientSideConnection
    }

    class FakeTcpConnection(
        override val fromAddress: Network.Address,
        override val toAddress: Network.Address,
        override val port: Int,
        private val tcpListener: Network.TcpListener? = null,
        private val otherListener: (() -> Network.TcpConnection)? = null
    ) : Network.TcpConnection {
        override fun send(bytes: ByteArray) {
            tcpListener?.receive(otherListener!!(), bytes)
        }
    }

    private fun sendPacketSuccess() = Random.nextFloat() > display.packetLossRate / 2
    private fun receivePacketSuccess() = Random.nextFloat() > display.packetLossRate / 2

    private inner class FakeLink(override val myAddress: Network.Address) : Network.Link {
        override fun listenUdp(port: Int, udpListener: Network.UdpListener) {
            this@FakeNetwork.listenUdp(myAddress, port, udpListener)
        }

        override fun sendUdp(toAddress: Network.Address, port: Int, bytes: ByteArray) {
            this@FakeNetwork.sendUdp(myAddress, toAddress, port, bytes)
        }

        override fun broadcastUdp(port: Int, bytes: ByteArray) {
            this@FakeNetwork.broadcastUdp(myAddress, port, bytes)
        }

        override fun listenTcp(port: Int, tcpListener: Network.TcpListener) {
            this@FakeNetwork.listenTcp(myAddress, port, tcpListener)
        }

        override fun connectTcp(
            toAddress: Network.Address,
            port: Int,
            tcpListener: Network.TcpListener
        ): Network.TcpConnection = this@FakeNetwork.connectTcp(myAddress, toAddress, port, tcpListener)
    }
}

private data class FakeAddress(val id: Int) : Network.Address {
    override fun toString(): String = "x${id.toString(16)}"
}

