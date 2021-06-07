package baaahs.net

import ch.tutteli.atrium.api.fluent.en_GB.toBe
import ch.tutteli.atrium.api.verbs.expect
import kotlin.random.Random
import kotlin.test.BeforeTest
import kotlin.test.Test

class FragmentingUdpLinkTest {
    private val port = 1234
    private val mtu = 1400

    private val network = TestNetwork(mtu)
    private val receivedPayloads = mutableListOf<ByteArray>()
    private val sendTestLink = network.link()
    private val sendLink = FragmentingUdpLink(sendTestLink)
    private val recvTestLink = network.link()
    private val recvLink = FragmentingUdpLink(recvTestLink)

    @BeforeTest
    fun setUp() {
        recvLink.listenUdp(port, object : Network.UdpListener {
            override fun receive(fromAddress: Network.Address, fromPort: Int, bytes: ByteArray) {
                receivedPayloads += bytes
            }
        })
    }


    @Test
    fun shouldSendSmallPayloadsThroughDirectly() {
        val smallPayload = byteArrayOf(0, 1, 2, 3)
        send(smallPayload)

        expect(receivedPayloads.size).toBe(1)
        expect(receivedPayloads.first().toList()).toBe(smallPayload.toList())
        expect(recvTestLink.receviedPackets.size).toBe(1)
    }

    @Test
    fun shouldFragmentAndReassembleLargerPayloads() {
        val mediumPayload = Random.nextBytes((1400 * 4.5).toInt())
        send(mediumPayload)

        expect(receivedPayloads.size).toBe(1)
        expect(receivedPayloads.first().toList()).toBe(mediumPayload.toList())
        expect(recvTestLink.receviedPackets.size).toBe(5)
    }

    @Test
    fun shouldFragmentAndReassemblePayloadsLargerThan64k() {
        val mediumPayload = Random.nextBytes(100_000)
        send(mediumPayload)

        expect(receivedPayloads.size).toBe(1)
        expect(receivedPayloads.first().toList()).toBe(mediumPayload.toList())
        expect(recvTestLink.receviedPackets.size).toBe(73)
    }

    /////////////////////////

    private fun send(smallPayload: ByteArray) {
        sendLink.listenUdp(0, object : Network.UdpListener {
            override fun receive(fromAddress: Network.Address, fromPort: Int, bytes: ByteArray) {
            }
        }).sendUdp(recvLink.myAddress, port, smallPayload)
        sendTestLink.sendTo(recvTestLink)
    }

    fun defrag(bytes: ByteArray) = bytes.slice(FragmentingUdpLink.headerSize until bytes.size).toByteArray()
}
