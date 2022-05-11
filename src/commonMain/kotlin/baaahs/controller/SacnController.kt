package baaahs.controller

import baaahs.dmx.*
import baaahs.fixtures.*
import baaahs.io.ByteArrayWriter
import baaahs.model.Model
import baaahs.net.Network
import baaahs.util.Logger
import baaahs.util.Time

class SacnController(
    val id: String,
    sacnLink: SacnLink,
    address: Network.Address,
    override val defaultFixtureConfig: FixtureConfig?,
    override val defaultTransportConfig: TransportConfig?,
    private val universeCount: Int,
    onlineSince: Time?
) : Controller {
    override val controllerId: ControllerId = ControllerId(SacnManager.controllerTypeName, id)
    override val state: ControllerState =
        SacnManager.State(controllerId.name(), address.asString(), onlineSince)
    override val transportType: TransportType
        get() = DmxTransport

    private val dmxUniverses = DmxUniverses(universeCount)
    private var dynamicDmxAllocator: DynamicDmxAllocator? = null

    private val node = sacnLink.deviceAt(address)
    val stats get() = node.stats
    private var sequenceNumber = 0

    override fun beforeFixtureResolution() {
        dynamicDmxAllocator = DynamicDmxAllocator(dmxUniverses)
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
        val staticDmxMapping = dynamicDmxAllocator!!.allocate(
            transportConfig as DmxTransportConfig?, componentCount, bytesPerComponent
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
    }

    fun release() {
        logger.debug { "Releasing SacnController $id." }
    }

    companion object {
        private val logger = Logger<SacnController>()
    }
}