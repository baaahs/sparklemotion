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
            udpListener?.receive(myAddress, xxx, bytes)
        }

        override val udpMtu = mtu

        override fun listenUdp(port: Int, udpListener: Network.UdpListener) {
            this.udpListener = udpListener
        }

        override fun sendUdp(toAddress: Network.Address, port: Int, bytes: ByteArray) {
            packetsToSend += bytes
        }

        override fun broadcastUdp(port: Int, bytes: ByteArray) {
            packetsToSend += bytes
        }

        override fun listenTcp(port: Int, tcpServerSocketListener: Network.TcpServerSocketListener) {
//            TODO("Link.listenTcp not implemented")
        }

        override fun connectTcp(
            toAddress: Network.Address,
            port: Int,
            tcpListener: Network.TcpListener
        ): Network.TcpConnection {
            TODO("Link.connectTcp not implemented")
        }
    }


    class Address(private val name: String = "some address") : Network.Address {
        override fun toString(): String {
            return name
        }
    }
}