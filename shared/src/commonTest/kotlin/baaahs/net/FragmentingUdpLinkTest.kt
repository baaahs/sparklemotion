package baaahs.net

import baaahs.describe
import baaahs.kotest.value
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.*
import kotlinx.coroutines.InternalCoroutinesApi
import kotlin.random.Random

@OptIn(InternalCoroutinesApi::class)
class FragmentingUdpSocketSpec : DescribeSpec({
    describe<FragmentingUdpSocket> {
        val port by value { 1234 }
        val mtu by value { 1400 }

        val network by value { TestNetwork(mtu) }
        val receivedPayloads by value { mutableListOf<ByteArray>() }
        val sendLink by value { network.link("send-link") }
        val recvLink by value { network.link("recv-link") }

        fun send(payload: ByteArray) {
            sendLink.listenFragmentingUdp(0, object : Network.UdpListener {
                override suspend fun receive(fromAddress: Network.Address, fromPort: Int, bytes: ByteArray) {
                }
            }).sendUdp(recvLink.myAddress, port, payload)
            sendLink.sendTo(recvLink)
        }

        beforeEach {
            recvLink.listenFragmentingUdp(port, object : Network.UdpListener {
                override suspend fun receive(fromAddress: Network.Address, fromPort: Int, bytes: ByteArray) {
                    receivedPayloads += bytes
                }
            })
        }

        it("shouldSendSmallPayloadsThroughDirectly") {
            val smallPayload = byteArrayOf(0, 1, 2, 3)
            send(smallPayload)

            receivedPayloads.size.shouldBe(1)
            receivedPayloads.first().toList().shouldBe(smallPayload.toList())
            recvLink.receviedPackets.size.shouldBe(1)
        }

        it("shouldFragmentAndReassembleLargerPayloads") {
            val mediumPayload = Random.nextBytes((1400 * 4.5).toInt())
            send(mediumPayload)

            receivedPayloads.size.shouldBe(1)
            receivedPayloads.first().toList().shouldBe(mediumPayload.toList())
            recvLink.receviedPackets.size.shouldBe(5)
        }

        it("shouldFragmentAndReassemblePayloadsLargerThan64k") {
            val mediumPayload = Random.nextBytes(100_000)
            send(mediumPayload)

            receivedPayloads.size.shouldBe(1)
            receivedPayloads.first().toList().shouldBe(mediumPayload.toList())
            recvLink.receviedPackets.size.shouldBe(73)
        }
    }
})