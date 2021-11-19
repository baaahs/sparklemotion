package baaahs.sim

import baaahs.Color
import baaahs.controller.SacnLink
import baaahs.controller.SacnManager
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

    fun createFakeWledDevice(name: String, vizPixels: Pixels): FakeWledDevice {
        val id = "wled-X${name}X"
        logger.debug { "Creating simulated WLED device for $name: $id" }

        network as FakeNetwork
        val link = network.link(FakeNetwork.FakeAddress(id))
        val fakeWledDevice = FakeWledDevice(link, id, vizPixels)
        fakeWledDevices.add(fakeWledDevice)

        wledScope.launch { randomDelay(1000); fakeWledDevice.run() }

        return fakeWledDevice
    }

    companion object {
        private val logger = Logger<WledsSimulator>()
    }
}

class FakeWledDevice(link: FakeNetwork.FakeLink, val id: String, val pixels: Pixels) {
    init {
        link.mdns.register(id, "_wled", "_tcp", 80)

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

        link.listenUdp(SacnLink.sAcnPort, object : Network.UdpListener {
            override fun receive(fromAddress: Network.Address, fromPort: Int, bytes: ByteArray) {
                val dataFrame = SacnLink.readDataFrame(bytes)
                val channelOffset = (dataFrame.universe - 1) * SacnManager.channelsPerUniverse
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

    fun run() {
        // No-op.
    }
}