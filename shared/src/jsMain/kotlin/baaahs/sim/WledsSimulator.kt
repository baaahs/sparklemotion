package baaahs.sim

import baaahs.Color
import baaahs.controller.SacnLink
import baaahs.net.Network
import baaahs.sm.brain.proto.Pixels
import baaahs.util.Logger

class WledsSimulator(
    private val network: Network
) {
    internal val fakeWledDevices: MutableList<FakeWledDevice> = mutableListOf()

    fun createFakeWledDevice(name: String, vizPixels: Pixels): FakeWledDevice {
        val id = "wled-X${name}X"
        logger.debug { "Creating simulated WLED device for $name: $id" }

        network as FakeNetwork
        val link = network.link(FakeNetwork.FakeAddress(id))
        val fakeWledDevice = FakeWledDevice(link, id, vizPixels)
        fakeWledDevices.add(fakeWledDevice)

        return fakeWledDevice
    }

    companion object {
        private val logger = Logger<WledsSimulator>()
    }
}

class FakeWledDevice(
    private val link: FakeNetwork.FakeLink,
    val id: String,
    val pixels: Pixels
) {
    private lateinit var mdnsService: Network.MdnsRegisteredService
    private lateinit var udpSocket: Network.UdpSocket

    fun start() {
        mdnsService = link.mdns.register(id, "_wled", "_tcp", 80)

        val fakeHttpServer = link.createHttpServer(80) as FakeNetwork.FakeLink.FakeHttpServer

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
                    "count": ${pixels.size},
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
                val dataFrame = SacnLink.readDataFrame(bytes)
//                val usedChannelsPerUniverse = Dmx.channelsPerUniverse
                val usedChannelsPerUniverse = 170 * 3 // Whole pixels only.
                val channelOffset = (dataFrame.universe - 1) * usedChannelsPerUniverse
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

    fun stop() {
//        link.close(udpSocket)
        link.mdns.unregister(mdnsService)
    }
}