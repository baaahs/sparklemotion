package baaahs.dmx

import baaahs.PubSub
import baaahs.Topics
import baaahs.controller.Controller
import baaahs.controller.ControllerListener
import baaahs.controller.ControllerManager
import baaahs.fixtures.FixtureConfig
import baaahs.fixtures.Transport
import baaahs.mapper.ControllerId
import baaahs.mapper.FixtureMapping
import baaahs.model.Model
import baaahs.publishProperty
import baaahs.sim.FakeDmxUniverse
import baaahs.util.Logger
import kotlinx.serialization.Serializable

interface DmxManager {
    val dmxUniverse: Dmx.Universe
}

class DmxManagerImpl(
    private val dmxDriver: Dmx.Driver,
    pubSub: PubSub.Server,
    private val fakeDmxUniverse: FakeDmxUniverse // For fallback.
) : DmxManager, ControllerManager {
    private var deviceData by publishProperty(pubSub, Topics.dmxDevices, emptyMap())
    private val localDevices = listDevices()
    override val dmxUniverse = findDmxUniverse(localDevices)

    init {
        deviceData = localDevices.map { device ->
            DmxInfo(device.id, device.name, "DMX USB", null)
        }.associateBy { it.id }
    }

    override fun start(controllerListener: ControllerListener) {
        if (localDevices.isNotEmpty()) {
            val added = localDevices.map { device -> DmxController(device) }
            controllerListener.onChange(added, emptyList())
        }
    }

    override fun stop() {
        TODO("not implemented")
    }

    private fun listDevices(): List<Dmx.Device> = dmxDriver.findDmxDevices()

    private fun findDmxUniverse(dmxDevices: List<Dmx.Device>): Dmx.Universe {
        if (dmxDevices.isNotEmpty()) {
            if (dmxDevices.size > 1) {
                logger.warn { "Multiple DMX USB devices found, using ${dmxDevices.first()}." }
            }

            return dmxDevices.first().asUniverse()
        }

        logger.warn { "No DMX USB devices found, DMX will be disabled." }
        return fakeDmxUniverse
    }

    companion object {
        internal val logger = Logger<DmxManager>()
    }
}

class DmxController(private val device: Dmx.Device) : Controller {
    override val controllerId: ControllerId
        get() = ControllerId("DMX", device.id)
    override val fixtureMapping: FixtureMapping?
        get() = null
    private val universe = device.asUniverse()

    override fun createTransport(
        entity: Model.Entity?,
        fixtureConfig: FixtureConfig,
        deviceOffset: Int,
        pixelCount: Int
    ): Transport = object : Transport {
        val buffer = universe.writer(deviceOffset, fixtureConfig.bufferSize(entity, pixelCount))

        override val name: String
            get() = "DMX Transport"

        override fun deliverBytes(byteArray: ByteArray) {
//            if (byteArray.size > buffer.)
            byteArray.forEachIndexed { i, byte -> buffer[i] = byte }
        }
    }

    override fun getAnonymousFixtureMappings(): List<FixtureMapping> = emptyList()
}

@Serializable
data class DmxInfo(
    val id: String,
    val name: String,
    val type: String,
    val universe: Int?
)