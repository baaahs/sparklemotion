@file:OptIn(InternalCoroutinesApi::class)

package baaahs.net

import baaahs.ImmediateDispatcher
import baaahs.sim.FakeMdns
import kotlinx.coroutines.*

@ExperimentalCoroutinesApi
class TestNetwork(
    var defaultMtu: Int = 1400,
    val dispatcher: CoroutineDispatcher = ImmediateDispatcher
) : Network {
    val links = mutableListOf<Link>()
    private val mdns = FakeMdns()

    override fun link(name: String): Link {
        return Link(defaultMtu).also { links.add(it) }
    }

    inner class Link(mtu: Int) : Network.Link {
        override val myAddress = Address()
        override val myHostname: String get() = "TestHost"
        val packetsToSend = mutableListOf<Packet>()
        val receviedPackets = mutableListOf<Packet>()

        var udpListeners: MutableMap<Int, Network.UdpListener> = mutableMapOf()

        fun sendTo(link: Link) {
            packetsToSend.forEach { packet ->
                link.receiveUdp(packet)
            }
            packetsToSend.clear()
        }

        private fun receiveUdp(packet: Packet) {
            receviedPackets += packet
            val listener = udpListeners[packet.port] ?: error("No listener on port ${packet.port}.")
            CoroutineScope(dispatcher).launch { listener.receive(myAddress, 1234, packet.data) }
        }

        override val udpMtu = mtu

        override fun listenUdp(port: Int, udpListener: Network.UdpListener): Network.UdpSocket {
            this.udpListeners[port] = udpListener
            return TestUdpSocket(port)
        }

        override val mdns = this@TestNetwork.mdns

        inner class TestUdpSocket(override val serverPort: Int) : Network.UdpSocket {
            override fun sendUdp(toAddress: Network.Address, port: Int, bytes: ByteArray) {
                packetsToSend += Packet(toAddress, port, bytes)
            }

            override fun broadcastUdp(port: Int, bytes: ByteArray) {
                packetsToSend += Packet(createAddress("*"), port, bytes)
            }

            override fun close() {}
        }

        override fun startHttpServer(port: Int): Network.HttpServer = object : Network.HttpServer {
            override fun listenWebSocket(
                path: String,
                onConnect: (incomingConnection: Network.TcpConnection) -> Network.WebSocketListener
            ) {
//                TODO("TestNetwork.Link.listenWebSocket not implemented")
            }

            override fun routing(config: Network.HttpServer.HttpRouting.() -> Unit) {
                TODO("not implemented")
            }
        }

        override suspend fun httpGetRequest(address: Network.Address, port: Int, path: String): String {
            TODO("TestNetowrk.Link.httpGetRequest not implemented")
        }

        override fun connectWebSocket(
            toAddress: Network.Address,
            port: Int,
            path: String,
            webSocketListener: Network.WebSocketListener
        ): Network.TcpConnection {
            TODO("Link.connectWebSocket not implemented")
        }

        override fun createAddress(name: String): Network.Address = Address(name)
    }

    class Address(private val name: String = "some address") : Network.Address {
        override fun asString(): String = name
        override fun toString(): String = asString()
    }

    data class Packet(
        val address: Network.Address,
        val port: Int,
        val data: ByteArray
    )
}