package baaahs.dmx

import baaahs.PubSub
import baaahs.Topics
import baaahs.publishProperty
import baaahs.sim.FakeDmxUniverse
import baaahs.util.Logger
import kotlinx.serialization.Serializable

class DmxManager(
    private val dmxDriver: Dmx.Driver,
    pubSub: PubSub.Server
) {
    private var deviceData by publishProperty(pubSub, Topics.dmxDevices, emptyMap())
    private val listDevices = listDevices()
    val dmxUniverse = findDmxUniverse(listDevices)

    init {
        deviceData = listDevices.map { device ->
            DmxInfo(device.id, device.name, "DMX USB", null)
        }.associateBy { it.id }
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
        return FakeDmxUniverse()
    }

    companion object {
        internal val logger = Logger<DmxManager>()
    }
}

@Serializable
data class DmxInfo(
    val id: String,
    val name: String,
    val type: String,
    val universe: Int?
)