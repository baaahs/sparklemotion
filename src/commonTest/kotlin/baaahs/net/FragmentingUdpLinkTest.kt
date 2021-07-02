package baaahs.net

import baaahs.describe
import ch.tutteli.atrium.api.fluent.en_GB.toBe
import ch.tutteli.atrium.api.verbs.expect
import org.spekframework.spek2.Spek
import kotlin.random.Random

object FragmentingUdpSocketSpec : Spek({
    describe<FragmentingUdpSocket> {
        val port by value { 1234 }
        val mtu by value { 1400 }

        val network by value { TestNetwork(mtu) }
        val receivedPayloads by value { mutableListOf<ByteArray>() }
        val sendLink by value { network.link("send-link") }
        val recvLink by value { network.link("recv-link") }

        fun send(payload: ByteArray) {
            sendLink.listenFragmentingUdp(0, object : Network.UdpListener {
                override fun receive(fromAddress: Network.Address, fromPort: Int, bytes: ByteArray) {
                }
            }).sendUdp(recvLink.myAddress, port, payload)
            sendLink.sendTo(recvLink)
        }

        beforeEachTest {
            recvLink.listenFragmentingUdp(port, object : Network.UdpListener {
                override fun receive(fromAddress: Network.Address, fromPort: Int, bytes: ByteArray) {
                    receivedPayloads += bytes
                }
            })
        }

        it("shouldSendSmallPayloadsThroughDirectly") {
            val smallPayload = byteArrayOf(0, 1, 2, 3)
            send(smallPayload)

            expect(receivedPayloads.size).toBe(1)
            expect(receivedPayloads.first().toList()).toBe(smallPayload.toList())
            expect(recvLink.receviedPackets.size).toBe(1)
        }

        it("shouldFragmentAndReassembleLargerPayloads") {
            val mediumPayload = Random.nextBytes((1400 * 4.5).toInt())
            send(mediumPayload)

            expect(receivedPayloads.size).toBe(1)
            expect(receivedPayloads.first().toList()).toBe(mediumPayload.toList())
            expect(recvLink.receviedPackets.size).toBe(5)
        }

        it("shouldFragmentAndReassemblePayloadsLargerThan64k") {
            val mediumPayload = Random.nextBytes(100_000)
            send(mediumPayload)

            expect(receivedPayloads.size).toBe(1)
            expect(receivedPayloads.first().toList()).toBe(mediumPayload.toList())
            expect(recvLink.receviedPackets.size).toBe(73)
        }
    }
})