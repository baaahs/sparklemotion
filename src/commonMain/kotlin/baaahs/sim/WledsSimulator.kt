package baaahs.sim

import baaahs.Color
import baaahs.controller.ControllerId
import baaahs.controller.SacnLink
import baaahs.controller.sim.ControllerSimulator
import baaahs.dmx.Dmx
import baaahs.net.Network
import baaahs.randomDelay
import baaahs.sm.brain.proto.Pixels
import baaahs.util.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WledsSimulator(
    private val network: Network
) {
    private val wledScope = CoroutineScope(Dispatchers.Main)
    internal val fakeWledDevices: MutableList<FakeWledDevice> = mutableListOf()

    fun createFakeWledDevice(controllerId: ControllerId, vizPixels: Pixels?): FakeWledDevice {
        val id = controllerId.name()
        logger.debug { "Creating simulated WLED device: $id" }

        network as FakeNetwork
        val link = network.link(FakeNetwork.FakeAddress(id))
        val fakeWledDevice = FakeWledDevice(link, controllerId, vizPixels)
        fakeWledDevices.add(fakeWledDevice)

        wledScope.launch { randomDelay(1000); fakeWledDevice.start() }

        return fakeWledDevice
    }

    companion object {
        private val logger = Logger<WledsSimulator>()
    }
}

class FakeWledDevice(
    private val link: FakeNetwork.FakeLink,
    override val controllerId: ControllerId,
    val pixels: Pixels?
) : ControllerSimulator {
    private lateinit var mdnsService: Network.MdnsRegisteredService
    private lateinit var udpSocket: Network.UdpSocket

    override fun start() {
        mdnsService = link.mdns.register(controllerId.id, "_wled", "_tcp", 80)

        val fakeHttpServer = link.startHttpServer(80) as FakeNetwork.FakeLink.FakeHttpServer

        fakeHttpServer.httpGetResponses["json"] =
                /** language=json*/
            """
              {
                "state": {
                  "on": true,
                  "bri": 255,
                  "mainseg": 1
                },
                "info": {
                  "ver": "fake",
                  "leds": {
                    "count": ${pixels?.size ?: 0},
                    "rgbw": false,
                    "wv": false,
                    "fps": 30,
                    "pwr": 255,
                    "maxpwr": 255
                  },
                  "name": "fake name",
                  "udpport": 4321
                }
              }
            """.trimIndent().encodeToByteArray()

        udpSocket = link.listenUdp(SacnLink.sAcnPort, object : Network.UdpListener {
            override fun receive(fromAddress: Network.Address, fromPort: Int, bytes: ByteArray) {
                if (pixels == null) return

                val dataFrame = SacnLink.readDataFrame(bytes)
                val channelOffset = (dataFrame.universe - 1) * Dmx.channelsPerUniverse
                val channels = dataFrame.channels

                for (i in 0 until channels.size / 3) {
                    val pixelOffset = (channelOffset + i * 3) / 3
                    val colorOffset = i * 3
                    pixels[pixelOffset] = Color(
                        channels[colorOffset],
                        channels[colorOffset + 1],
                        channels[colorOffset + 2]
                    )
                }
            }
        })
    }

    override fun stop() {
//        link.close(udpSocket)
        link.mdns.unregister(mdnsService)
    }
}