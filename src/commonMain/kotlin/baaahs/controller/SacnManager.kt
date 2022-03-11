package baaahs.controller

import baaahs.device.PixelArrayDevice
import baaahs.dmx.*
import baaahs.dmx.Dmx.Companion.channelsPerUniverse
import baaahs.fixtures.*
import baaahs.io.ByteArrayWriter
import baaahs.model.Model
import baaahs.net.Network
import baaahs.scene.ControllerConfig
import baaahs.scene.FixtureMappingData
import baaahs.scene.MutableControllerConfig
import baaahs.scene.MutableSacnControllerConfig
import baaahs.util.Clock
import baaahs.util.Delta
import baaahs.util.Logger
import baaahs.util.Time
import kotlinx.coroutines.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class SacnManager(
    private val link: Network.Link,
    private val mainDispatcher: CoroutineDispatcher,
    private val clock: Clock
) : BaseControllerManager(controllerTypeName) {
    private val senderCid = "SparkleMotion000".encodeToByteArray()
    private val sacnLink = SacnLink(link, senderCid, "SparkleMotion")
    private var lastConfig: Map<ControllerId, SacnControllerConfig> = emptyMap()
    private var controllers: Map<ControllerId, SacnController> = emptyMap()

    override fun start() {
        startWledDiscovery()
    }

    override fun onConfigChange(controllerConfigs: Map<ControllerId, ControllerConfig>) {
        handleConfigs(controllerConfigs.filterByType())
    }

    inline fun <reified T : ControllerConfig> Map<ControllerId, ControllerConfig>.filterByType(): Map<ControllerId, T> =
        buildMap {
            this@filterByType.forEach { (k, v) ->
                if (v is T) put(k, v)
            }
        }

    override fun stop() {
        TODO("not implemented")
    }

    private fun handleConfigs(configs: Map<ControllerId, SacnControllerConfig>) {
        controllers = buildMap {
            Delta.diff(lastConfig, configs, object : Delta.MapChangeListener<ControllerId, SacnControllerConfig> {
                override fun onAdd(key: ControllerId, value: SacnControllerConfig) {
                    val controller = SacnController(
                        key.id, value.address,
                        value.defaultFixtureConfig, value.defaultTransportConfig,
                        value.universes, clock.now()
                    )
                    put(controller.controllerId, controller)
                    notifyListeners { onAdd(controller) }
                }

                override fun onRemove(key: ControllerId, value: SacnControllerConfig) {
                    val oldController = controllers[key]!!
                    oldController.release()
                    notifyListeners { onRemove(oldController) }
                }
            })
        }
        lastConfig = configs
    }

    fun startWledDiscovery() {
        CoroutineScope(Dispatchers.Default).launch {
            listenForWleds(link)
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
                        val wledJsonStr = link.httpGetRequest(wledAddress, wledPort, "json")
                        val wledJson = json.decodeFromString(WledJson.serializer(), wledJsonStr)

                        withContext(mainDispatcher) {
                            val pixelCount = wledJson.info.leds.count

                            val sacnController = SacnController(
                                id,
                                wledAddress.asString(),
                                PixelArrayDevice.Config(pixelCount),
                                null,
                                pixelCount  * 3 / channelsPerUniverse + 1,
                                onlineSince
                            )
                            notifyListeners { onAdd(sacnController) }
                        }
                    }
                }
            }
        })
    }

    @Serializable
    data class State(
        override val title: String,
        override val address: String,
        override val onlineSince: Time?
    ) : ControllerState()

    inner class SacnController(
        val id: String,
        val address: String,
        override val defaultFixtureConfig: FixtureConfig?,
        override val defaultTransportConfig: TransportConfig?,
        val universeCount: Int,
        val onlineSince: Time?
    ) : Controller {
        override val controllerId: ControllerId = ControllerId(controllerTypeName, id)
        override val state: ControllerState =
            State(controllerId.name(), address, onlineSince)
        override val transportType: TransportType
            get() = DmxTransport

        private val dmxUniverses = DmxUniverses(universeCount)
        private var dynamicDmxAllocator: DynamicDmxAllocator? = null

        private val node = sacnLink.deviceAt(link.createAddress(address))
        val stats get() = node.stats
        private var sequenceNumber = 0

        override fun beforeFixtureResolution() {
            dynamicDmxAllocator = DynamicDmxAllocator(1)
        }

        override fun afterFixtureResolution() {
            dynamicDmxAllocator = null
        }

        override fun createTransport(
            entity: Model.Entity?,
            fixtureConfig: FixtureConfig,
            transportConfig: TransportConfig?,
            componentCount: Int,
            bytesPerComponent: Int
        ): Transport {
            val staticDmxMapping = dynamicDmxAllocator!!.allocate(transportConfig as DmxTransportConfig?, 1, 3)
            return SacnTransport(transportConfig, staticDmxMapping)
                .also { it.validate() }
        }

        override fun getAnonymousFixtureMappings(): List<FixtureMapping> = emptyList()

        inner class SacnTransport(
            transportConfig: DmxTransportConfig?,
            private val staticDmxMapping: StaticDmxMapping
        ) : Transport {
            override val name: String get() = id
            override val controller: Controller
                get() = this@SacnController
            override val config: TransportConfig? = transportConfig
            private val startChannel = staticDmxMapping.startChannel
            private val channelCount = staticDmxMapping.channelCount - 1

            fun validate() {
                if (startChannel >= dmxUniverses.channels.size)
                    error("For $name, start channel $startChannel won't fit in $universeCount universes")
                if (startChannel + channelCount - 1 >= dmxUniverses.channels.size)
                    error("For $name, end channel $channelCount won't fit in $universeCount universes")
            }

            override fun deliverBytes(byteArray: ByteArray) {
                staticDmxMapping.writeBytes(byteArray, dmxUniverses)
            }

            override fun deliverComponents(
                componentCount: Int,
                bytesPerComponent: Int,
                fn: (componentIndex: Int, buf: ByteArrayWriter) -> Unit
            ) {
                staticDmxMapping.writeComponents(componentCount, bytesPerComponent, dmxUniverses, fn)
            }
        }

        override fun afterFrame() {
            sequenceNumber++
            for (universeIndex in 0 until universeCount) {
                val maxChannel = dmxUniverses.universeMaxChannel[universeIndex]
                if (maxChannel > 0) {
                    node.sendDataPacket(
                        dmxUniverses.channels,
                        universeIndex + 1,
                        universeIndex * channelsPerUniverse,
                        maxChannel,
                        sequenceNumber
                    )
                }
                dmxUniverses.universeMaxChannel[universeIndex] = 0
            }
        }

        fun release() {
            logger.debug { "Releasing SacnController $id." }
        }
    }

    companion object : ControllerManager.Meta {
        override val controllerTypeName = "SACN"

        private val logger = Logger<SacnManager>()
        private val json = Json {
            ignoreUnknownKeys = true
        }

        override fun createMutableControllerConfigFor(
            controllerId: ControllerId?,
            state: ControllerState?
        ): MutableControllerConfig {
            val sacnState = state as? State
            val title = state?.title ?: controllerId?.id ?: "New sACN Controller"
            return MutableSacnControllerConfig(SacnControllerConfig(
                title,
                sacnState?.address ?: "",
                1
            ))
        }
    }
}

@Serializable @SerialName("SACN")
data class SacnControllerConfig(
    override val title: String,
    val address: String,
    val universes: Int,
    override val fixtures: List<FixtureMappingData> = emptyList(),
    override val defaultFixtureConfig: FixtureConfig? = null,
    override val defaultTransportConfig: TransportConfig? = null
) : ControllerConfig {
    override val controllerType: String get() = SacnManager.controllerTypeName
    override val emptyTransportConfig: TransportConfig
        get() = DmxTransportConfig()

    override fun edit(): MutableControllerConfig =
        MutableSacnControllerConfig(this)

    override fun createFixturePreview(fixtureConfig: FixtureConfig, transportConfig: TransportConfig): FixturePreview = object : FixturePreview {
        override val fixtureConfig: ConfigPreview
            get() = fixtureConfig.preview()
        override val transportConfig: ConfigPreview
            get() = transportConfig.preview()
    }
}

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