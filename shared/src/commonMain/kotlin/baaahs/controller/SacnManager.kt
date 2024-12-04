package baaahs.controller

import baaahs.controller.SacnManager.SacnState
import baaahs.device.PixelArrayDevice
import baaahs.dmx.Dmx
import baaahs.dmx.Dmx.Companion.channelsPerUniverse
import baaahs.dmx.DmxTransportConfig
import baaahs.dmx.DmxUniverses
import baaahs.dmx.DynamicDmxAllocator
import baaahs.fixtures.*
import baaahs.net.Network
import baaahs.scene.ControllerConfig
import baaahs.scene.MutableControllerConfig
import baaahs.scene.MutableSacnControllerConfig
import baaahs.scene.PreviewBuilder
import baaahs.util.Clock
import baaahs.util.Logger
import kotlinx.coroutines.*
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonPrimitive

class SacnManager(
    private val link: Network.Link,
    private val clock: Clock,
    private val universeListener: Dmx.UniverseListener? = null,
    private val pinkyMainScope: CoroutineScope,
    private val networkScope: CoroutineScope
) : BaseControllerManager<SacnController, SacnControllerConfig, SacnState>(controllerTypeName) {
    private val senderCid = "SparkleMotion000".encodeToByteArray()
    private val sacnLink = SacnLink(link, senderCid, "SparkleMotion", clock)
    private var wledDiscoveryJob: Job? = null

    override fun start() {
        logger.info { "Start WLED discovery..." }
        wledDiscoveryJob = networkScope.launch(CoroutineName("sACN WLED Discovery")) {
            // This might block while the mDNS service warms up.
            listenForWleds(link)
        }
    }

    override fun onChange(
        controllerId: ControllerId,
        oldController: SacnController?,
        controllerConfig: Change<SacnControllerConfig?>,
        controllerState: Change<SacnState?>,
        fixtureMappings: Change<List<FixtureMapping>>
    ): SacnController? {
        val newConfig = controllerConfig.newValue
        val newState = controllerState.newValue

        val address = newState?.address
            ?: newConfig?.address
            ?: return null

        val pixelCount = newState?.pixelCount
        val bytesPerPixel = if (newState?.isRgbw == true) 4 else 3
        val universeCount = DynamicDmxAllocator()
            .allocate(pixelCount ?: 0, bytesPerPixel)
            .calculateEndUniverse(channelsPerUniverse)
        return SacnController(
            controllerId.id, sacnLink, link.createAddress(address),
            newConfig?.defaultFixtureOptions.merge(PixelArrayDevice.Options(pixelCount)),
            newConfig?.defaultTransportConfig,
            newConfig?.universes ?: universeCount,
            universeListener
        )
    }

    override fun reset() {
    }

    override fun stop() {
        wledDiscoveryJob?.cancel()
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
                    networkScope.launch(CoroutineName("sACN Handler for ${wledAddress.asString()}")) {
                        val wledJsonStr = link.httpGetRequest(wledAddress, wledPort, "json")
                        val wledJson = try {
                            json.decodeFromString(WledJson.serializer(), wledJsonStr)
                        } catch (e: Exception) {
                            logger.error(e) { "Failed to deserialize WLED packet: $wledJsonStr" }
                            return@launch
                        }

                        withContext(pinkyMainScope.coroutineContext) {
                            val pixelCount = wledJson.info.leds.count
                            val isRgbw = wledJson.info.leds.rgbw

                            val controllerId = ControllerId(controllerTypeName, id)
                            onStateChange(controllerId) {
                                SacnState(
                                    id, wledAddress.asString(), onlineSince, "WLED ver ${wledJson.info.ver}",
                                    pixelCount = pixelCount, isRgbw = isRgbw
                                )
                            }
                        }
                    }
                }
            }
        })
    }

    @Serializable
    data class SacnState(
        override val title: String,
        override val address: String,
        override val onlineSince: Instant?,
        override val firmwareVersion: String? = null,
        override val lastErrorMessage: String? = null,
        override val lastErrorAt: Instant? = null,
        val pixelCount: Int,
        val isRgbw: Boolean
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
                (state as? SacnState)?.address ?: "",
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