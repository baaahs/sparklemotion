package baaahs.net

import kotlin.random.Random
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.expect

class FragmentingUdpLinkTest {
    private val port = 1234
    private val mtu = 1400

    private val receivedPayloads = mutableListOf<ByteArray>()
    private val sendTestLink = TestNetwork.Link(mtu)
    private val sendLink = FragmentingUdpLink(sendTestLink)
    private val recvTestLink = TestNetwork.Link(mtu)
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

    fun defrag(bytes: ByteArray) = bytes.slice(FragmentingUdpLink.headerSize until bytes.size).toByteArray()
}
