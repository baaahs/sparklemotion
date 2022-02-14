package baaahs.dmx

import baaahs.controller.BaseControllerManager
import baaahs.controller.ControllerId
import baaahs.scene.ControllerConfig
import baaahs.sim.FakeDmxUniverse
import baaahs.util.Clock
import baaahs.util.Logger
import kotlinx.serialization.Serializable

interface DmxManager {
    fun allOff()

    val dmxUniverse: Dmx.Universe
}

class DmxManagerImpl(
    private val dmxDriver: Dmx.Driver,
    private val clock: Clock,
    private val fakeDmxUniverse: FakeDmxUniverse // For fallback.){}
) : BaseControllerManager("DMX"), DmxManager {
    private val attachedDevices = findAttachedDevices()
    override val dmxUniverse = findDmxUniverse(attachedDevices)

    override fun start() {
        if (attachedDevices.isNotEmpty()) {
            attachedDevices.forEach { device ->
                notifyListeners { onAdd(DirectDmxController(device, clock)) }
            }
        }
    }

    override fun onConfigChange(controllerConfigs: Map<ControllerId, ControllerConfig>) {
    }

    override fun stop() {
        TODO("not implemented")
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