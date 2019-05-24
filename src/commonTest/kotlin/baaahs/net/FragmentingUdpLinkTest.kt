package baaahs.net

import kotlin.random.Random
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.expect

class FragmentingUdpLinkTest {
    private val port = 1234
    private val mtu = 1400

    private val receivedPayloads = mutableListOf<ByteArray>()
    private val sendTestLink = TestNetworkLink(mtu)
    private val sendLink = FragmentingUdpLink(sendTestLink)
    private val recvTestLink = TestNetworkLink(mtu)
    private val recvLink = FragmentingUdpLink(recvTestLink)

    @BeforeTest
    fun setUp() {
        recvLink.listenUdp(port, object : Network.UdpListener {
            override fun receive(fromAddress: Network.Address, bytes: ByteArray) {
                receivedPayloads += bytes
            }
        })
    }


    @Test
    fun shouldSendSmallPayloadsThroughDirectly() {
        val smallPayload = byteArrayOf(0, 1, 2, 3)
        send(smallPayload)

        expect(1) { receivedPayloads.size }
        expect(smallPayload.toList()) { receivedPayloads.first().toList() }
        expect(1) { recvTestLink.receviedPackets.size }
    }

    @Test
    fun shouldFragmentAndReassembleLargerPayloads() {
        val mediumPayload = Random.nextBytes((1400 * 4.5).toInt())
        send(mediumPayload)

        expect(1) { receivedPayloads.size }
        expect(mediumPayload.toList()) { receivedPayloads.first().toList() }
        expect(5) { recvTestLink.receviedPackets.size }
    }

    @Test
    fun shouldFragmentAndReassemblePayloadsLargerThan64k() {
        val mediumPayload = Random.nextBytes(100_000)
        send(mediumPayload)

        expect(1) { receivedPayloads.size }
        expect(mediumPayload.toList()) { receivedPayloads.first().toList() }
        expect(73) { recvTestLink.receviedPackets.size }
    }

    /////////////////////////

    private fun send(smallPayload: ByteArray) {
        sendLink.sendUdp(recvLink.myAddress, port, smallPayload)
        sendTestLink.sendTo(recvTestLink)
    }

    class TestNetworkLink(mtu: Int) : Network.Link {
        override val myAddress = someAddress()
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
            TODO("TestNetworkLink.listenTcp not implemented")
        }

        override fun connectTcp(
            toAddress: Network.Address,
            port: Int,
            tcpListener: Network.TcpListener
        ): Network.TcpConnection {
            TODO("TestNetworkLink.connectTcp not implemented")
        }
    }

    companion object {
        fun someAddress() = object : Network.Address {}
    }
}
