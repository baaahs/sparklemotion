package baaahs.dmx

import baaahs.controller.Controller
import baaahs.controller.ControllerId
import baaahs.controller.ControllerState
import baaahs.fixtures.*
import baaahs.io.ByteArrayWriter
import baaahs.model.Model
import baaahs.scene.ControllerConfig
import baaahs.scene.FixtureMappingData
import baaahs.scene.MutableControllerConfig
import baaahs.scene.MutableDirectDmxControllerConfig
import baaahs.util.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

class DirectDmxController(
    private val device: Dmx.Device,
    clock: Clock,
    private val universeListener: Dmx.UniverseListener? = null
) : Controller {
    override val controllerId: ControllerId
        get() = ControllerId(controllerType, device.id)
    private val startedAt = clock.now()
    override val state: ControllerState =
        State(device.name, "N/A", startedAt)
    override val defaultFixtureOptions: FixtureOptions?
        get() = null
    override val transportType: TransportType
        get() = DmxTransportType
    override val defaultTransportConfig: TransportConfig?
        get() = null
    private val universe = device.asUniverse(universeListener)
    private var dynamicDmxAllocator: DynamicDmxAllocator? = null

    override fun beforeFixtureResolution() {
        dynamicDmxAllocator = DynamicDmxAllocator()
    }

    override fun afterFixtureResolution() {
        dynamicDmxAllocator = null
    }

    override fun createTransport(
        entity: Model.Entity?,
        fixtureConfig: FixtureConfig,
        transportConfig: TransportConfig?
    ): Transport {
        val staticDmxMapping = dynamicDmxAllocator!!.allocate(
            fixtureConfig.componentCount, fixtureConfig.bytesPerComponent,
            transportConfig as DmxTransportConfig
        )
        return DirectDmxTransport(transportConfig, staticDmxMapping)
    }


    @Serializable
    data class State(
        override val title: String,
        override val address: String?,
        override val onlineSince: Instant?,
        override val firmwareVersion: String? = null,
        override val lastErrorMessage: String? = null,
        override val lastErrorAt: Instant? = null
    ) : ControllerState()

    inner class DirectDmxTransport(
        override val config: DmxTransportConfig,
        staticDmxMapping: StaticDmxMapping
    ) : Transport {
        val startChannel = staticDmxMapping.startChannel
        val endChannel = staticDmxMapping.calculateEndChannel()

        private val buffer = run {
            universe.writer(startChannel, endChannel - startChannel + 1)
        }

        override val name: String
            get() = "DMX Transport"
        override val controller: Controller
            get() = this@DirectDmxController

        override fun deliverBytes(byteArray: ByteArray) {
            byteArray.forEachIndexed { i, byte -> buffer[i] = byte }
        }

        override fun deliverComponents(
            componentCount: Int,
            bytesPerComponent: Int,
            fn: (componentIndex: Int, buf: ByteArrayWriter) -> Unit
        ) {
            val buf = ByteArrayWriter(bytesPerComponent)
            for (componentIndex in 0 until componentCount) {
                buf.offset = 0
                fn(componentIndex, buf)

                val bytes = buf.toBytes()
                val startChannel = componentIndex * bytesPerComponent
                for (i in 0 until bytesPerComponent) {
                    buffer[startChannel + i] = bytes[i]
                }
            }
        }
    }

    override fun afterFrame() {
        universe.sendFrame()
    }

    override fun getAnonymousFixtureMappings(): List<FixtureMapping> = emptyList()

    companion object {
        const val controllerType = "DMX"
    }
}

object DmxTransportType : TransportType {
    override val id: String
        get() = "DMX"
    override val title: String
        get() = "DMX"
    override val emptyConfig: TransportConfig
        get() = DmxTransportConfig()
    override val isConfigurable: Boolean
        get() = true
}

@Serializable
@SerialName("DirectDMX")
data class DirectDmxControllerConfig(
    override val title: String = "Direct DMX",
    override val fixtures: List<FixtureMappingData> = emptyList(),
    @SerialName("defaultFixtureConfig")
    override val defaultFixtureOptions: FixtureOptions? = null,
    override val defaultTransportConfig: TransportConfig? = null
) : ControllerConfig {
    override val controllerType: String
        get() = DirectDmxController.controllerType
    override val emptyTransportConfig: TransportConfig
        get() = DmxTransportConfig()

    @Transient
    private var dmxAllocator: DynamicDmxAllocator? = null

    override fun edit(): MutableControllerConfig =
        MutableDirectDmxControllerConfig(this)

    // TODO: This is pretty dumb, find a better way to do this.
    override fun buildFixturePreviews(tempModel: Model): List<FixturePreview> {
        dmxAllocator = DynamicDmxAllocator()
        try {
            return super.buildFixturePreviews(tempModel)
        } finally {
            dmxAllocator = null
        }
    }

    override fun createFixturePreview(fixtureOptions: FixtureOptions, transportConfig: TransportConfig): FixturePreview {
        val staticDmxMapping = dmxAllocator!!.allocate(
            fixtureOptions.componentCount ?: 1,
            fixtureOptions.bytesPerComponent,
            transportConfig as DmxTransportConfig
        )
        val dmxPreview = staticDmxMapping.preview(DmxUniverses(1))

        return object : FixturePreview {
            override val fixtureOptions: ConfigPreview
                get() = fixtureOptions.preview()
            override val transportConfig: ConfigPreview
                get() = dmxPreview
        }
    }
}