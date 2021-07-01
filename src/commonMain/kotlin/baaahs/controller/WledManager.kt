package baaahs.controller

import baaahs.PubSub
import baaahs.Topics
import baaahs.fixtures.*
import baaahs.geom.Vector3F
import baaahs.glsl.LinearSurfacePixelStrategy
import baaahs.glsl.SurfacePixelStrategy
import baaahs.mapper.ControllerId
import baaahs.model.LightBar
import baaahs.model.Model
import baaahs.net.Network
import baaahs.publishProperty
import baaahs.util.Clock
import baaahs.util.Logger
import baaahs.util.Time
import kotlinx.coroutines.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class WledManager(
    private val fixtureManager: FixtureManager,
    private val model: Model,
    link: Network.Link,
    pubSub: PubSub.Server,
    private val mainDispatcher: CoroutineDispatcher,
    private val clock: Clock,
    private val surfacePixelStrategy: SurfacePixelStrategy = LinearSurfacePixelStrategy()
) {
    private val senderCid = "SparkleMotion000".encodeToByteArray()
    private val sacnLink = SacnLink(link, senderCid, "SparkleMotion")
    private var wledTransports = mutableMapOf<String, WledTransport>()
    private var wledDevices by publishProperty(pubSub, Topics.sacnDevices, emptyMap())

    init {
        CoroutineScope(Dispatchers.Default).launch {
            listenForWleds(link)
        }

        CoroutineScope(Dispatchers.Default).launch {
            launch {
                while (true) {
                    logger.info { "Sending pixels to ${wledTransports.size} WLED devices." }
                    wledTransports.forEach { (id, wledTransport) ->
                        logger.debug { "$id: ${wledTransport.stats.sendDataPacket.summarize()}" }
                    }
                    delay(10000)
                }
            }
        }
    }

    private fun listenForWleds(link: Network.Link) {
        logger.debug { "Listening for _wled._tcp" }
        link.mdns.listen("_wled", "_tcp", "local.", object : Network.MdnsListenHandler {
            override fun added(service: Network.MdnsService) {
                logger.debug { "mDns: added $service" }
            }

            override fun removed(service: Network.MdnsService) {
                logger.debug { "mDns: removed $service" }
            }

            override fun resolved(service: Network.MdnsService) {
                val id = service.hostname
                val onlineSince = clock.now()

                val wledAddress = service.getAddress()
                val wledPort = service.port
                logger.debug { "Resolved ${service.type} at $id — $wledAddress:$wledPort" }

                if (wledAddress != null) {
                    CoroutineScope(Dispatchers.Default).launch {
                        logger.warn { "httpGetRequest ->" }
                        val wledJsonStr = link.httpGetRequest(wledAddress, wledPort, "json")
                        logger.warn { "               -> $wledJsonStr" }
                        val wledJson = json.decodeFromString(WledJson.serializer(), wledJsonStr)
                        logger.warn { "               -> $wledJson" }
                        println("wledJson = $wledJson")

                        withContext(mainDispatcher) {
                            val pixelCount = wledJson.info.leds.count

                            val newWledDevice = WledDevice(id, wledAddress.asString(), pixelCount, onlineSince)
                            val controllerId = ControllerId(controllerTypeName, id)
                            val provisionalFixture = fixtureManager.createFixtureFor(controllerId, null, NullTransport)
                            val entity = provisionalFixture.modelEntity
                            println("WledManager: entity = $entity")
                            val pixelLocations = if (provisionalFixture.pixelLocations.isNotEmpty()) {
                                provisionalFixture.pixelLocations
                            } else if (entity is LightBar) {
                                entity.getPixelLocations(pixelCount)
                            } else {
                                surfacePixelStrategy.forFixture(pixelCount, entity, model)
                            }

                            val wledTransport =
                                WledTransport(newWledDevice, sacnLink.deviceAt(wledAddress), entity, pixelLocations)

                            val existingWledTransport = wledTransports[id]
                            if (existingWledTransport == null) {
                                logger.info { "Add $id ($pixelCount pixels) as fixture..." }

                                wledTransports[id] = wledTransport
                                fixtureManager.fixturesChanged(listOf(wledTransport.fixture), emptyList())
                                updateWledDevices(id, newWledDevice)
                            } else {
                                // TODO: check to see if any attributes of the device have changed.
                                logger.info { "Ignoring duplicate $id ($pixelCount pixels)." }
                            }
                        }
                    }
                }
            }
        })
    }

    private fun updateWledDevices(id: String, newWledDevice: WledDevice) {
        val newWledDevices = wledDevices.toMutableMap()
        newWledDevices[id] = newWledDevice
        wledDevices = newWledDevices
    }

    companion object {
        val controllerTypeName = "WLED"
        private val logger = Logger<WledManager>()
        private val json = Json {
            ignoreUnknownKeys = true
        }
    }
}

class WledTransport(
    private val wledDevice: WledDevice,
    private val sacnDevice: SacnLink.SacnDevice,
    entity: Model.Entity?,
    pixelLocations: List<Vector3F>
) : Transport {
    override val name: String get() = wledDevice.id
    val stats get() = sacnDevice.stats

    val fixture: Fixture = Fixture(
        entity, wledDevice.pixelCount, pixelLocations,
        PixelArrayDevice, wledDevice.id, this
    )


    override fun send(fixture: Fixture, resultViews: List<ResultView>) {
        val resultColors = PixelArrayDevice.getColorResults(resultViews)
        sacnDevice.sendDataPacket(resultColors)
    }
}

@Serializable
data class WledDevice(
    val id: String,
    val address: String?,
    val pixelCount: Int,
    val onlineSince: Time
)

@Serializable
data class WledJson(
    val state: State,
    val info: Info
) {

    @Serializable
    data class State(
        var on: Boolean,
        var bri: Int,
        var mainseg: Int
    )

    @Serializable
    data class Info(
        val ver: String,
        val leds: Leds,
        val name: String,
        val udpport: Int
    ) {
        @Serializable
        data class Leds(
            val count: Int,
            val rgbw: Boolean,
            val wv: Boolean,
            val fps: Int,
            val pwr: Int,
            val maxpwr: Int
        )
    }
}