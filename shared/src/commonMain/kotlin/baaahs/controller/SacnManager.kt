package baaahs.controller

import baaahs.device.PixelArrayDevice
import baaahs.dmx.Dmx
import baaahs.dmx.Dmx.Companion.channelsPerUniverse
import baaahs.dmx.DmxTransportConfig
import baaahs.dmx.DmxUniverses
import baaahs.dmx.DynamicDmxAllocator
import baaahs.fixtures.ConfigPreview
import baaahs.fixtures.FixtureOptions
import baaahs.fixtures.FixturePreview
import baaahs.fixtures.TransportConfig
import baaahs.net.Network
import baaahs.scene.*
import baaahs.util.Clock
import baaahs.util.Delta
import baaahs.util.Logger
import baaahs.util.coroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonPrimitive
import kotlin.coroutines.CoroutineContext

class SacnManager(
    private val link: Network.Link,
    private val coroutineContext: CoroutineContext,
    private val clock: Clock,
    private val universeListener: Dmx.UniverseListener? = null
) : BaseControllerManager(controllerTypeName) {
    private val senderCid = "SparkleMotion000".encodeToByteArray()
    private val sacnLink = SacnLink(link, senderCid, "SparkleMotion", clock)
    private var lastConfig: Map<ControllerId, OpenControllerConfig<SacnControllerConfig>> = emptyMap()
    private var controllers: Map<ControllerId, SacnController> = emptyMap()
    private var discoveredControllers: MutableMap<ControllerId, SacnController> = hashMapOf()

    override fun start() {
        startWledDiscovery()
    }

    override fun onConfigChange(controllerConfigs: Map<ControllerId, OpenControllerConfig<*>>) {
        handleConfigs(controllerConfigs.values
            .filterIsInstance<OpenControllerConfig<SacnControllerConfig>>()
        )
    }

    inline fun <reified T : ControllerConfig> Map<ControllerId, ControllerConfig>.filterByType(): Map<ControllerId, T> =
        buildMap {
            this@filterByType.forEach { (k, v) ->
                if (v is T) put(k, v)
            }
        }

    override fun reset() {
        discoveredControllers.forEach { (_, controller) ->
            controller.release()
            notifyListeners { onRemove(controller) }
        }
        discoveredControllers.clear()
    }

    override fun stop() {
        TODO("not implemented")
    }

    private fun handleConfigs(configs: List<OpenControllerConfig<SacnControllerConfig>>) {
        val configMap = configs.associateBy { it.id }
        controllers = buildMap {
            Delta.diff(
                lastConfig,
                configMap,
                object : Delta.MapChangeListener<ControllerId, OpenControllerConfig<SacnControllerConfig>> {
                    override fun onAdd(key: ControllerId, value: OpenControllerConfig<SacnControllerConfig>) {
                        val controllerConfig = value.controllerConfig
                        val controller = SacnController(
                            key.id, sacnLink, link.createAddress(controllerConfig.address),
                            value.defaultFixtureOptions, value.defaultTransportConfig,
                            controllerConfig.universes, clock.now(),
                            universeListener
                        )
                        put(controller.controllerId, controller)
                        notifyListeners { onAdd(controller) }
                    }

                    override fun onRemove(key: ControllerId, value: OpenControllerConfig<SacnControllerConfig>) {
                        val oldController = controllers[key]
                        if (oldController == null) {
                            logger.warn { "Unknown controller \"$key\" removed." }
                        } else {
                            oldController.release()
                            notifyListeners { onRemove(oldController) }
                        }
                    }
                }
            )
        }
        lastConfig = configMap
    }

    private fun startWledDiscovery() {
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
                    CoroutineScope(Dispatchers.Default + coroutineExceptionHandler).launch {
                        val wledJsonStr = link.httpGetRequest(wledAddress, wledPort, "json")
                        val wledJson = try {
                            json.decodeFromString(WledJson.serializer(), wledJsonStr)
                        } catch (e: Exception) {
                            logger.error(e) { "Failed to deserialize WLED packet: $wledJsonStr" }
                            return@launch
                        }

                        withContext(this@SacnManager.coroutineContext) {
                            val pixelCount = wledJson.info.leds.count
                            val bytesPerPixel = if (wledJson.info.leds.rgbw) 4 else 3

                            val universeCount = DynamicDmxAllocator()
                                .allocate(pixelCount, bytesPerPixel)
                                .calculateEndUniverse(channelsPerUniverse)
                            val sacnController = SacnController(
                                id, sacnLink, wledAddress,
                                PixelArrayDevice.Options(pixelCount),
                                null, universeCount, onlineSince,
                                universeListener
                            )
                            discoveredControllers[sacnController.controllerId] = sacnController
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
        override val onlineSince: Instant?,
        override val firmwareVersion: String? = null,
        override val lastErrorMessage: String? = null,
        override val lastErrorAt: Instant? = null
    ) : ControllerState()

    companion object : ControllerManager.Meta {
        override val controllerTypeName = "SACN"
        override val controllerIcon: String
            get() = "sacn.svg"

        private val logger = Logger<SacnManager>()
        private val json = Json {
            ignoreUnknownKeys = true
        }

        override fun createMutableControllerConfigFor(
            controllerId: ControllerId?,
            state: ControllerState?
        ): MutableControllerConfig =
            MutableSacnControllerConfig(
                state?.title ?: controllerId?.id ?: "New sACN Controller",
                (state as? State)?.address ?: "",
                1, null, null
            )
    }
}

@Serializable @SerialName("SACN")
data class SacnControllerConfig(
    override val title: String,
    val address: String, // TODO: Should be optional.
    val universes: Int, // TODO: Should be optional.
    override val defaultFixtureOptions: FixtureOptions? = null,
    override val defaultTransportConfig: TransportConfig? = null
) : ControllerConfig {
    override val controllerType: String get() = SacnManager.controllerTypeName
    override val emptyTransportConfig: TransportConfig
        get() = DmxTransportConfig()

    override fun edit(): MutableControllerConfig =
        MutableSacnControllerConfig(
            title, address, universes, defaultFixtureOptions?.edit(), defaultTransportConfig?.edit()
        )

    override fun createPreviewBuilder(): PreviewBuilder = object : PreviewBuilder {
        val dmxAllocator = DynamicDmxAllocator()

        override fun createFixturePreview(fixtureOptions: FixtureOptions, transportConfig: TransportConfig): FixturePreview {
            val staticDmxMapping = dmxAllocator.allocate(
                fixtureOptions.componentCount ?: error("No component count."),
                fixtureOptions.bytesPerComponent,
                transportConfig as DmxTransportConfig
            )
            val dmxUniverses = DmxUniverses(universes)
            val dmxPreview = staticDmxMapping.preview(dmxUniverses)

            return object : FixturePreview {
                override val fixtureOptions: ConfigPreview
                    get() = fixtureOptions.preview()
                override val transportConfig: ConfigPreview
                    get() = dmxPreview
            }
        }
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
            val wv: JsonPrimitive, // Protocol claims this is a boolean, but WLED sends 0.
            val fps: Int,
            val pwr: Int,
            val maxpwr: Int
        )
    }
}