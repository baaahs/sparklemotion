package baaahs.sim

import baaahs.Pixels
import baaahs.controller.SacnLink
import baaahs.net.Network
import baaahs.randomDelay
import baaahs.util.Clock
import baaahs.util.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WledsSimulator(
    private val network: Network,
    private var clock: Clock
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

        val sacnLink = SacnLink(link, ByteArray(16) { 0 }, "source")
        link.listenUdp(SacnLink.sAcnPort, object : Network.UdpListener {
            override fun receive(fromAddress: Network.Address, fromPort: Int, bytes: ByteArray) {
                sacnLink.readDataFrame(bytes, pixels)
            }
        })
    }

    fun run() {
        // No-op.
    }
}