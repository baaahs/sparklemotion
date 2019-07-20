package baaahs.net

class TestNetwork(var defaultMtu: Int = 1400) : Network {
    val links = mutableListOf<Link>()

    override fun link(): Link {
        return Link(defaultMtu).also { links.add(it) }
    }

    class Link(mtu: Int) : Network.Link {
        override val myAddress = Address()
        val packetsToSend = mutableListOf<ByteArray>()
        val receviedPackets = mutableListOf<ByteArray>()

        private var udpListener: Network.UdpListener? = null

        fun sendTo(link: Link) {
            packetsToSend.forEach { bytes ->
                link.receiveUdp(bytes)
            }
            packetsToSend.clear()
        }

        private fun receiveUdp(bytes: ByteArray) {
            receviedPackets += bytes
            udpListener?.receive(myAddress, 1234, bytes)
        }

        override val udpMtu = mtu

        override fun listenUdp(port: Int, udpListener: Network.UdpListener): Network.UdpSocket {
            this.udpListener = udpListener
            return TestUdpSocket(port)
        }

        inner class TestUdpSocket(override val serverPort: Int) : Network.UdpSocket {
            override fun sendUdp(toAddress: Network.Address, port: Int, bytes: ByteArray) {
                packetsToSend += bytes
            }

            override fun broadcastUdp(port: Int, bytes: ByteArray) {
                packetsToSend += bytes
            }

        }

        override fun startHttpServer(port: Int): Network.HttpServer = object : Network.HttpServer {
            override fun listenWebSocket(
                path: String,
                onConnect: (incomingConnection: Network.TcpConnection) -> Network.WebSocketListener
            ) {
//                TODO("TestNetwork.Link.listenWebSocket not implemented")
            }
        }

        override fun connectWebSocket(
            toAddress: Network.Address,
            port: Int,
            path: String,
            webSocketListener: Network.WebSocketListener
        ): Network.TcpConnection {
            TODO("Link.connectWebSocket not implemented")
        }
    }


    class Address(private val name: String = "some address") : Network.Address {
        override fun toString(): String {
            return name
        }
    }
}