package baaahs.controller

import baaahs.dmx.*
import baaahs.fixtures.*
import baaahs.io.ByteArrayWriter
import baaahs.model.Model
import baaahs.net.Network
import baaahs.util.Logger

class SacnController(
    val id: String,
    sacnLink: SacnLink,
    address: Network.Address,
    override val defaultFixtureOptions: FixtureOptions?,
    override val defaultTransportConfig: TransportConfig?,
    private val universeCount: Int,
    private val universeListener: Dmx.UniverseListener? = null
) : Controller {
    override val controllerId: ControllerId = ControllerId(SacnManager.controllerTypeName, id)
    override val transportType: TransportType
        get() = DmxTransportType

    private val dmxUniverses = DmxUniverses(universeCount)

    private val node = sacnLink.deviceAt(address)
    val stats get() = node.stats
    private var sequenceNumber = 0

    override fun createFixtureResolver(): FixtureResolver = object : FixtureResolver {
        val dynamicDmxAllocator = DynamicDmxAllocator(dmxUniverses.channelsPerUniverse)

        override fun createTransport(
            entity: Model.Entity?,
            fixtureConfig: FixtureConfig,
            transportConfig: TransportConfig?
        ): Transport {
            val staticDmxMapping = dynamicDmxAllocator.allocate(
                fixtureConfig.componentCount, fixtureConfig.bytesPerComponent,
                transportConfig as DmxTransportConfig?
            )
        return try {
            dmxUniverses.validate(staticDmxMapping)
            SacnTransport(transportConfig, staticDmxMapping)
        } catch (e: Exception) {
            logger.error(e) { "Failed to allocate DMX for $entity." }
            NullTransport
        }
        }
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

    override fun release() {
        logger.debug { "Releasing SacnController $id." }
    }

    companion object {
        private val logger = Logger<SacnController>()
    }
}