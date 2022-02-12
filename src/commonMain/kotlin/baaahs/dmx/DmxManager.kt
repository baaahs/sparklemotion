package baaahs.dmx

import baaahs.PubSub
import baaahs.controller.BaseControllerManager
import baaahs.publishProperty
import baaahs.scene.ControllerConfig
import baaahs.sim.FakeDmxUniverse
import baaahs.sm.webapi.Topics
import baaahs.util.Logger
import kotlinx.serialization.Serializable

interface DmxManager {
    fun allOff()

    val dmxUniverse: Dmx.Universe
}

class DmxManagerImpl(
    private val dmxDriver: Dmx.Driver,
    pubSub: PubSub.Server,
    private val fakeDmxUniverse: FakeDmxUniverse // For fallback.
) : BaseControllerManager("DMX"), DmxManager {
    private var deviceData by publishProperty(pubSub, Topics.dmxDevices, emptyMap())
    private val attachedDevices = findAttachedDevices()
    override val dmxUniverse = findDmxUniverse(attachedDevices)

    init {
        deviceData = attachedDevices.map { device ->
            DmxInfo(device.id, device.name, "DMX USB", null)
        }.associateBy { it.id }
    }

    override fun start() {
        if (attachedDevices.isNotEmpty()) {
            attachedDevices.forEach { device ->
                notifyListeners { onAdd(DirectDmxController(device)) }
            }
        }
    }

    override fun onConfigChange(controllerConfigs: Map<String, ControllerConfig>) {
    }

    override fun stop() {
        TODO("not implemented")
    }

    override fun logStatus() {
        logger.info { "Sending to ${attachedDevices.size} attached DMX controllers." }
    }

    override fun allOff() {
        dmxUniverse.allOff()
    }

    private fun findAttachedDevices(): List<Dmx.Device> = dmxDriver.findDmxDevices()

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

@Serializable
data class DmxInfo(
    val id: String,
    val name: String,
    val type: String,
    val universe: Int?
)