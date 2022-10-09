package baaahs.controller

import baaahs.camelize
import baaahs.device.PixelArrayDevice
import baaahs.device.PixelFormat
import baaahs.dmx.DmxTransportConfig
import baaahs.dmx.DynamicDmxAllocator
import baaahs.fixtures.*
import baaahs.geom.Vector3F
import baaahs.io.ByteArrayWriter
import baaahs.model.Model
import baaahs.net.Network
import baaahs.scene.*
import baaahs.util.Clock
import baaahs.util.Logger
import baaahs.util.Time
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlin.coroutines.CoroutineContext

class NanoleafManager(
    private val link: Network.Link,
    private val coroutineContext: CoroutineContext,
    private val clock: Clock
) : BaseControllerManager(controllerTypeName) {
    private val nanoleafAdapter = NanoleafAdapter(coroutineContext, clock)
    private var controllerConfigs = emptyMap<ControllerId, NanoleafControllerConfig>()

    override fun onConfigChange(controllerConfigs: Map<ControllerId, ControllerConfig>) {
        val newConfigs = mutableMapOf<ControllerId, NanoleafControllerConfig>()
        controllerConfigs.forEach { (k, v) ->
            if (v is NanoleafControllerConfig) {
                newConfigs[k] = v
            }
        }
        this.controllerConfigs = newConfigs
    }

    override fun start() {
        nanoleafAdapter.start { deviceMetadata ->
            val newControllerId = createControllerId(deviceMetadata)
            val config = controllerConfigs[newControllerId]

            GlobalScope.launch {
                withContext(coroutineContext) {
                    notifyListeners {
                        onAdd(NanoleafController(nanoleafAdapter, deviceMetadata, config, clock))
                    }
                }
            }
        }
    }

    override fun stop() {
        nanoleafAdapter.stop()
    }

    @Serializable
    data class State(
        override val title: String,
        override val address: String,
        override val onlineSince: Time?,
        override val firmwareVersion: String? = null,
        override val lastErrorMessage: String? = null,
        override val lastErrorAt: Time? = null,
        val hostName: String,
        val port: Int,
        val deviceId: String,
        val accessToken: String? = null
    ) : ControllerState()

    companion object : ControllerManager.Meta {
        override val controllerTypeName = "Nanoleaf"

        private val logger = Logger<NanoleafManager>()

        fun createControllerId(deviceMetadata: NanoleafDeviceMetadata) =
            ControllerId(controllerTypeName, deviceMetadata.deviceId)

        override fun createMutableControllerConfigFor(
            controllerId: ControllerId?,
            state: ControllerState?
        ): MutableControllerConfig {
            val nanoleafState = state as? State
            val title = state?.title ?: controllerId?.id ?: "New Nanoleaf Controller"
            return MutableNanoleafControllerConfig(
                NanoleafControllerConfig(
                    nanoleafState?.hostName ?: "??? hostname",
                    nanoleafState?.port ?: -1,
                    nanoleafState?.deviceId ?: controllerId?.id ?: "??? deviceId",
                    nanoleafState?.title ?: controllerId?.id ?: "??? deviceName",
                    nanoleafState?.accessToken
                )
            )
        }
    }
}

class NanoleafController(
    private val nanoleafAdapter: NanoleafAdapter,
    private val deviceMetadata: NanoleafDeviceMetadata,
    private val config: NanoleafControllerConfig?,
    private val clock: Clock
) : Controller {
    private val upSince = clock.now()
    private var accessToken: String? = config?.accessToken
    private var device = accessToken?.let {
        nanoleafAdapter.openDevice(deviceMetadata, it)
    }

    override val controllerId: ControllerId
        get() = ControllerId(NanoleafManager.controllerTypeName, deviceMetadata.deviceId)

    override val state: ControllerState
        get() = NanoleafManager.State(
            deviceMetadata.deviceName,
            deviceMetadata.hostName + ":" + deviceMetadata.port,
            upSince,
            null, null, null,
            deviceMetadata.hostName,
            deviceMetadata.port,
            deviceMetadata.deviceId,
            accessToken
        )
    override val defaultFixtureConfig: FixtureConfig?
        get() = null
    override val defaultTransportConfig: TransportConfig?
        get() = null
    override val transportType: TransportType
        get() = NanoleafTransport

    override fun createTransport(
        entity: Model.Entity?,
        fixtureConfig: FixtureConfig,
        transportConfig: TransportConfig?,
        componentCount: Int,
        bytesPerComponent: Int
    ): Transport = object : Transport {
        override val name: String
            get() = deviceMetadata.deviceName
        override val controller: Controller
            get() = this@NanoleafController
        override val config: TransportConfig?
            get() = transportConfig
        private val pixelArrayConfig = fixtureConfig as? PixelArrayDevice.Config
        private val pixelFormat = pixelArrayConfig?.pixelFormat
            ?: error("No pixel format specified.")

        override fun deliverBytes(byteArray: ByteArray) {
            TODO("not implemented")
        }

        override fun deliverComponents(
            componentCount: Int,
            bytesPerComponent: Int,
            fn: (componentIndex: Int, buf: ByteArrayWriter) -> Unit
        ) {
            val config = transportConfig as NanoleafTransportConfig?
                ?: NanoleafTransportConfig()
            device?.deliverComponents(
                componentCount, bytesPerComponent, pixelFormat, fn
            )
        }
    }

    override fun getAnonymousFixtureMappings(): List<FixtureMapping> {
        if (accessToken == null) return emptyList()
        val panels = device?.panels
            ?: return emptyList()

        val pixelLocations = panels.map { panel ->
            Vector3F(panel.x.toFloat(), panel.y.toFloat(), 0f)
        }
        println("pixelLocations = $pixelLocations")
        val min = pixelLocations.reduce { acc, v -> acc.min(v) }
        val max = pixelLocations.reduce { acc, v -> acc.max(v) }
        val scale = (max - min).let {
            val scaleDimen = maxOf(max.x, max.y)
            Vector3F(scaleDimen, scaleDimen, 1f)
        }
        val scaledPixelLocations = pixelLocations.map { v ->
            (v - min) / scale
        }
        println("scaledPixelLocations = $scaledPixelLocations")

        return listOf(
            FixtureMapping(null,
                PixelArrayDevice.Config(
                    panels.size,
                    PixelFormat.RGB8,
                    pixelLocations = scaledPixelLocations
                )
            )
        )
    }
}

object NanoleafTransport : TransportType {
    override val id: String
        get() = "Nanoleaf"
    override val title: String
        get() = "Nanoleaf"
    override val emptyConfig: TransportConfig
        get() = NanoleafTransportConfig()
}

@Serializable
class NanoleafTransportConfig() : TransportConfig {
    override val transportType: TransportType
        get() = NanoleafTransport

    override fun edit(): MutableTransportConfig {
        TODO("not implemented")
    }

    override fun plus(other: TransportConfig?): TransportConfig =
        this

    override fun preview(): ConfigPreview {
        TODO("not implemented")
    }
}

expect class NanoleafAdapter(coroutineContext: CoroutineContext, clock: Clock) {
    fun start(callback: (NanoleafDeviceMetadata) -> Unit)
    fun stop()
    fun openDevice(deviceMetadata: NanoleafDeviceMetadata, accessToken: String): NanoleafDevice
}

interface NanoleafDevice : NanoleafDeviceMetadata {
    val panels: List<NanoleafPanel>

    fun deliverComponents(
        componentCount: Int,
        bytesPerComponent: Int,
        pixelFormat: PixelFormat,
        fn: (componentIndex: Int, buf: ByteArrayWriter) -> Unit
    )
}

data class NanoleafPanel(
    val id: Int,
    val x: Int,
    val y: Int,
    val orientation: Int,
    val shape: String
)


interface NanoleafDeviceMetadata {
    val hostName: String
    val port: Int
    val deviceId: String
    val deviceName: String
}

data class NanoleafDeviceMetadataData(
    override val hostName: String,
    override val port: Int,
    override val deviceId: String,
    override val deviceName: String
) : NanoleafDeviceMetadata

@Serializable
@SerialName("Nanoleaf")
data class NanoleafControllerConfig(
    override val hostName: String,
    override val port: Int,
    override val deviceId: String,
    override val deviceName: String,
    val accessToken: String? = null,
    override val fixtures: List<FixtureMappingData> = emptyList(),
    override val defaultFixtureConfig: FixtureConfig? = null,
    override val defaultTransportConfig: TransportConfig? = null
) : ControllerConfig, NanoleafDeviceMetadata {
    override val title: String get() = deviceName

    override val controllerType: String get() = NanoleafManager.controllerTypeName
    override val emptyTransportConfig: TransportConfig
        get() = DmxTransportConfig()

    @Transient
    private var dmxAllocator: DynamicDmxAllocator? = null

    override fun edit(): MutableControllerConfig =
        MutableNanoleafControllerConfig(this)

    // TODO: This is pretty dumb, find a better way to do this.
    override fun buildFixturePreviews(tempModel: Model): List<FixturePreview> {
        dmxAllocator = DynamicDmxAllocator()
        try {
            return super.buildFixturePreviews(tempModel)
        } finally {
            dmxAllocator = null
        }
    }

    override fun createFixturePreview(fixtureConfig: FixtureConfig, transportConfig: TransportConfig): FixturePreview {
        val staticDmxMapping = dmxAllocator!!.allocate(
            fixtureConfig.componentCount!!,
            fixtureConfig.bytesPerComponent,
            transportConfig as DmxTransportConfig
        )
        val dmxPreview = error("foo")

        return object : FixturePreview {
            override val fixtureConfig: ConfigPreview
                get() = fixtureConfig.preview()
            override val transportConfig: ConfigPreview
                get() = dmxPreview
        }
    }
}

class MutableNanoleafControllerConfig(config: NanoleafControllerConfig) : MutableControllerConfig {
    override var title: String = config.title
    var hostName: String = config.hostName
    var port: Int = config.port
    var deviceId: String = config.deviceId
    var deviceName: String = config.deviceName
    var accessToken: String? = config.accessToken

    override val controllerMeta: ControllerManager.Meta
        get() = NanoleafManager

    override val fixtures: MutableList<MutableFixtureMapping> =
        config.fixtures.map { it.edit() }.toMutableList()
    override var defaultFixtureConfig: MutableFixtureConfig? =
        config.defaultFixtureConfig?.edit()
    override var defaultTransportConfig: MutableTransportConfig? =
        config.defaultTransportConfig?.edit()

    override fun build(): ControllerConfig =
        NanoleafControllerConfig(
            hostName, port, deviceId, deviceName,
            accessToken,
            fixtures.map { it.build() },
            defaultFixtureConfig?.build(),
            defaultTransportConfig?.build()
        )

    override fun suggestId(): String = title.camelize()

    override fun matches(controllerMatcher: ControllerMatcher): Boolean =
        controllerMatcher.matches(title, NanoleafManager.controllerTypeName)

    override fun getEditorPanels(editingController: EditingController<*>): List<ControllerEditorPanel<*>> =
        listOf(NanoleafControllerEditorPanel)
}