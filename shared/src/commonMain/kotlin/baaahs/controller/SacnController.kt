package baaahs.controller

import baaahs.dmx.*
import baaahs.fixtures.*
import baaahs.io.ByteArrayWriter
import baaahs.model.Model
import baaahs.net.Network
import baaahs.util.Logger
import kotlinx.datetime.Instant

class SacnController(
    val id: String,
    private val sacnLink: SacnLink,
    private val address: Network.Address,
    override val defaultFixtureOptions: FixtureOptions?,
    override val defaultTransportConfig: TransportConfig?,
    private val universeCount: Int,
    private val onlineSince: Instant?,
    private val universeListener: Dmx.UniverseListener? = null
) : Controller {
    override val controllerId: ControllerId = ControllerId(SacnManager.controllerTypeName, id)
    override val state: ControllerState get() = SacnManager.State(
        controllerId.name(), address.asString(), onlineSince, null,
        sacnLink.lastError?.message,
        sacnLink.lastErrorAt
    )
    override val transportType: TransportType
        get() = DmxTransportType

    private val dmxUniverses = DmxUniverses(universeCount)
    private var dynamicDmxAllocator: DynamicDmxAllocator? = null

    private val node = sacnLink.deviceAt(address)
    val stats get() = node.stats
    private var sequenceNumber = 0

    override fun beforeFixtureResolution() {
        dynamicDmxAllocator = DynamicDmxAllocator(dmxUniverses.channelsPerUniverse)
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
            transportConfig as DmxTransportConfig?
        )
        return SacnTransport(transportConfig, staticDmxMapping)
            .also { dmxUniverses.validate(staticDmxMapping) }
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
                    universeIndex * Dmx.channelsPerUniverse,
                    maxChannel,
                    sequenceNumber
                )
            }
            dmxUniverses.universeMaxChannel[universeIndex] = 0
        }

        universeListener?.onSend(controllerId.name(), dmxUniverses.channels)
    }

    fun release() {
        logger.debug { "Releasing SacnController $id." }
    }

    companion object {
        private val logger = Logger<SacnController>()
    }
}