package baaahs.controller

import baaahs.PubSub
import baaahs.fixtures.FixtureConfig
import baaahs.fixtures.Transport
import baaahs.io.ByteArrayWriter
import baaahs.mapper.FixtureMapping
import baaahs.mapper.SacnTransportConfig
import baaahs.mapper.TransportConfig
import baaahs.model.Model
import baaahs.net.Network
import baaahs.publishProperty
import baaahs.scene.ControllerConfig
import baaahs.sm.webapi.Topics
import baaahs.util.Clock
import baaahs.util.Logger
import baaahs.util.Time
import kotlinx.coroutines.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.math.max
import kotlin.math.min

object Delta {
    fun <K, V> diff(oldMap: Map<K, V>, newMap: Map<K, V>, listener: MapChangeListener<K, V>) {
        val toRemove = oldMap.keys - newMap.keys
        val toChange = oldMap.keys.intersect(newMap.keys)
        val toAdd = newMap.keys - oldMap.keys

        for (k in toRemove) {
            listener.onRemove(k, oldMap[k]!!)
        }

        for (k in toChange) {
            val oldValue = oldMap[k]!!
            val newValue = newMap[k]!!
            if (oldValue != newValue) {
                listener.onChange(k, oldValue, newValue)
            }
        }

        for (k in toAdd) {
            listener.onAdd(k, newMap[k]!!)
        }
    }

    interface MapChangeListener<K, V> {
        fun onAdd(key: K, value: V)

        fun onChange(key: K, oldValue: V, newValue: V) {
            onRemove(key, oldValue)
            onAdd(key, newValue)
        }

        fun onRemove(key: K, value: V)
    }
}

private class SacnConfigAndController(
    var config: SacnControllerConfig,
    var controller: SacnManager.SacnController
)

class SacnManager(
    private val link: Network.Link,
    pubSub: PubSub.Server,
    private val mainDispatcher: CoroutineDispatcher,
    private val clock: Clock
) : BaseControllerManager(controllerTypeName) {
    private val senderCid = "SparkleMotion000".encodeToByteArray()
    private val sacnLink = SacnLink(link, senderCid, "SparkleMotion")
    private var sacnDevices by publishProperty(pubSub, Topics.sacnDevices, emptyMap())
    private var lastConfig: Map<String, SacnControllerConfig> = emptyMap()
    private var controllers: Map<String, SacnController> = emptyMap()

    override fun start() {
        startWledDiscovery()
    }

    override fun onConfigChange(controllerConfigs: Map<String, ControllerConfig>) {
        handleConfigs(controllerConfigs.filterByType())
    }

    inline fun <reified T : ControllerConfig> Map<String, ControllerConfig>.filterByType(): Map<String, T> =
        buildMap {
            this@filterByType.forEach { (k, v) ->
                if (v is T) put(k, v)
            }
        }

    override fun stop() {
        TODO("not implemented")
    }

    override fun logStatus() {
        logger.info { "Sending to ${sacnDevices.size} SACN controllers." }

    }

    private fun handleConfigs(configs: Map<String, SacnControllerConfig>) {
        controllers = buildMap {
            Delta.diff(lastConfig, configs, object : Delta.MapChangeListener<String, SacnControllerConfig> {
                override fun onAdd(key: String, value: SacnControllerConfig) {
                    val controller = SacnController(key, value.address, null, value.universes, clock.now())
                    put(key, controller)
                    notifyListeners { onAdd(controller) }
                    addSacnDevice(controller)
                }

                override fun onRemove(key: String, value: SacnControllerConfig) {
                    val oldController = controllers[key]!!
                    removeSacnDevice(oldController)
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
                                FixtureMapping(null, pixelCount, null),
                                pixelCount  * 3 / channelsPerUniverse + 1,
                                onlineSince
                            )
                            notifyListeners { onAdd(sacnController) }
                            addSacnDevice(sacnController)
                        }
                    }
                }
            }
        })
    }

    private fun addSacnDevice(sacnController: SacnController) {
        val newWledDevices = sacnDevices.toMutableMap()
        newWledDevices[sacnController.id] = with (sacnController) {
            SacnDevice(id, address, fixtureMapping?.pixelCount, onlineSince)
        }
        sacnDevices = newWledDevices
    }

    private fun removeSacnDevice(sacnController: SacnController) {
        val newWledDevices = sacnDevices.toMutableMap()
        newWledDevices.remove(sacnController.id)
        sacnDevices = newWledDevices
    }

    inner class SacnController(
        val id: String,
        val address: String,
        override val fixtureMapping: FixtureMapping?,
        val universeCount: Int,
        val onlineSince: Time?
    ) : Controller {
        override val controllerId: ControllerId = ControllerId(controllerTypeName, id)
        private val channels = ByteArray(channelsPerUniverse * universeCount)
        private val universeMaxChannel = IntArray(universeCount)
        private val node = sacnLink.deviceAt(link.createAddress(address))
        val stats get() = node.stats
        private var sequenceNumber = 0

        override fun createTransport(
            entity: Model.Entity?,
            fixtureConfig: FixtureConfig,
            transportConfig: TransportConfig?,
            pixelCount: Int
        ): Transport = SacnTransport(transportConfig as SacnTransportConfig?)
            .also { it.validate() }

        override fun getAnonymousFixtureMappings(): List<FixtureMapping> = emptyList()

        inner class SacnTransport(transportConfig: SacnTransportConfig?) : Transport {
            override val name: String get() = id
            override val controllerId: ControllerId
                get() = this@SacnController.controllerId
            override val config: TransportConfig? = transportConfig
            private val startChannel = transportConfig?.startChannel ?: 0
            private val endChannel = transportConfig?.endChannel
            private val componentsStartAtUniverseBoundaries =
                transportConfig?.componentsStartAtUniverseBoundaries ?: true

            private val writer = ByteArrayWriter(channels)

            fun validate() {
                if (startChannel >= channels.size)
                    error("For $name, start channel $startChannel won't fit in $universeCount universes")
                if (endChannel != null && endChannel >= channels.size)
                    error("For $name, end channel $endChannel won't fit in $universeCount universes")
            }

            override fun deliverBytes(byteArray: ByteArray) {
                val channelCount = min(byteArray.size, endChannel ?: Int.MAX_VALUE)
                byteArray.copyInto(channels, startChannel, 0, channelCount)
            }

            override fun deliverComponents(
                componentCount: Int,
                bytesPerComponent: Int,
                fn: (componentIndex: Int, buf: ByteArrayWriter) -> Unit
            ) {
                fun bumpUniverseMax(universeIndex: Int, channelIndex: Int) {
                    universeMaxChannel[universeIndex] =
                        max(channelIndex, universeMaxChannel[universeIndex])
                }

                if (componentsStartAtUniverseBoundaries) {
                    val componentsPerUniverse = channelsPerUniverse / bytesPerComponent
                    val effectiveChannelsPerUniverse = componentsPerUniverse * bytesPerComponent
                    val startUniverseIndex = startChannel / channelsPerUniverse
                    val startChannelIndex = startChannel % channelsPerUniverse

                    for (componentIndex in 0 until componentCount) {
                        val componentByteOffset = startChannelIndex + componentIndex * bytesPerComponent
                        val universeOffset = componentByteOffset / effectiveChannelsPerUniverse
                        val channelIndex = componentByteOffset % effectiveChannelsPerUniverse
                        val universeIndex = startUniverseIndex + universeOffset
                        writer.offset = universeIndex * channelsPerUniverse + channelIndex
                        fn(componentIndex, writer)
                        bumpUniverseMax(universeIndex, channelIndex + bytesPerComponent)
                    }
                } else {
                    for (componentIndex in 0 until componentCount) {
                        val channelIndex = startChannel + componentIndex * bytesPerComponent
                        writer.offset = channelIndex
                        for (i in channelIndex until channelIndex + bytesPerComponent) {
                            val universeIndex = i / channelsPerUniverse
                            val bIndex = i % channelsPerUniverse
                            bumpUniverseMax(universeIndex, bIndex + 1)
                        }
                        fn(componentIndex, writer)
                    }

                }
            }
        }

        override fun afterFrame() {
            sequenceNumber++
            for (universeIndex in 0 until universeCount) {
                val maxChannel = universeMaxChannel[universeIndex]
                if (maxChannel > 0) {
                    node.sendDataPacket(
                        channels,
                        universeIndex + 1,
                        universeIndex * channelsPerUniverse,
                        maxChannel,
                        sequenceNumber
                    )
                }
                universeMaxChannel[universeIndex] = 0
            }
        }

        fun release() {
            logger.debug { "Releasing SacnController $id." }
        }
    }

    companion object {
        val controllerTypeName = "SACN"

        val channelsPerUniverse = 512

        private val logger = Logger<SacnManager>()
        private val json = Json {
            ignoreUnknownKeys = true
        }
    }
}

@Serializable @SerialName("SACN")
data class SacnControllerConfig(
    override val title: String,
    val address: String,
    val universes: Int
) : ControllerConfig {
    override val controllerType: String get() = "SACN"
}

@Serializable
data class SacnDevice(
    val id: String,
    val address: String?,
    val pixelCount: Int?,
    val onlineSince: Time?
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