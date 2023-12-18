package baaahs.dmx

import baaahs.PubSub
import baaahs.controller.BaseControllerManager
import baaahs.controller.ControllerId
import baaahs.controller.ControllerManager
import baaahs.controller.ControllerState
import baaahs.plugin.Plugins
import baaahs.rpc.CommandPort
import baaahs.scene.ControllerConfig
import baaahs.scene.MutableControllerConfig
import baaahs.scene.MutableDirectDmxControllerConfig
import baaahs.sim.FakeDmxUniverse
import baaahs.util.Clock
import baaahs.util.Logger
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule

interface DmxManager {
    fun allOff()

    val dmxUniverse: Dmx.Universe

    companion object : ControllerManager.Meta {
        override val controllerTypeName: String
            get() = "DMX"
        internal val logger = Logger<DmxManager>()

        override fun createMutableControllerConfigFor(
            controllerId: ControllerId?,
            state: ControllerState?
        ): MutableControllerConfig {
            val title = state?.title ?: controllerId?.id ?: "Direct DMX"
            return MutableDirectDmxControllerConfig(DirectDmxControllerConfig(title))
        }
    }
}

class DmxManagerImpl(
    private val dmxDriver: Dmx.Driver,
    private val clock: Clock,
    private val fakeDmxUniverse: FakeDmxUniverse, // For fallback.
    pubSub: PubSub.Server,
    private val universeListener: DmxUniverseListener? = null,
    plugins: Plugins
) : BaseControllerManager(DmxManager.controllerTypeName), DmxManager {
    private val attachedDevices = findAttachedDevices()
    override val dmxUniverse = findDmxUniverse(attachedDevices)

    init {
        pubSub.listenOnCommandChannel(createCommandPort(plugins.serialModule)) { command ->
            ListDmxUniverses.Response(universeListener?.lastFrames ?: emptyMap())
        }
    }

    override fun start() {
        if (attachedDevices.isNotEmpty()) {
            attachedDevices.forEach { device ->
                notifyListeners {
                    onAdd(DirectDmxController(device, clock, universeListener))
                }
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

            return dmxDevices.first().asUniverse(universeListener)
        }

        logger.warn { "No DMX USB devices found, DMX will be disabled." }
        return fakeDmxUniverse
    }

    companion object {
        internal val logger = DmxManager.logger

        fun createCommandPort(serializersModule: SerializersModule) = CommandPort(
            "pinky/dmx/universes",
            ListDmxUniverses.serializer(), ListDmxUniverses.Response.serializer(),
            serializersModule
        )
    }
}

@Serializable
data class DmxInfo(
    val id: String,
    val name: String,
    val type: String,
    val universe: Int?
)

@Serializable
class ListDmxUniverses() {
    @Serializable
    class Response(val universes: Map<String, DmxUniverseListener.LastFrame>)
}
