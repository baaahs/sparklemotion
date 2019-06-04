package baaahs.net

class TestNetwork(var defaultMtu: Int = 1400) : Network {
    val links = mutableListOf<TestNetworkLink>()

    override fun link(): Network.Link {
        return TestNetworkLink(defaultMtu).also { links.add(it) }
    }

    class TestNetworkLink(mtu: Int) : Network.Link {
        override val myAddress = FragmentingUdpLinkTest.someAddress()
        val packetsToSend = mutableListOf<ByteArray>()
        val receviedPackets = mutableListOf<ByteArray>()

        private var udpListener: Network.UdpListener? = null

        fun sendTo(link: TestNetworkLink) {
            packetsToSend.forEach { bytes ->
                link.receiveUdp(bytes)
            }
            packetsToSend.clear()
        }

        private fun receiveUdp(bytes: ByteArray) {
            receviedPackets += bytes
            udpListener?.receive(myAddress, bytes)
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
//            TODO("TestNetworkLink.listenTcp not implemented")
        }

        override fun connectTcp(
            toAddress: Network.Address,
            port: Int,
            tcpListener: Network.TcpListener
        ): Network.TcpConnection {
            TODO("TestNetworkLink.connectTcp not implemented")
        }
    }

}