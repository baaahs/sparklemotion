package baaahs.dmx

import baaahs.PubSub
import baaahs.controller.BaseControllerManager
import baaahs.controller.Change
import baaahs.controller.ControllerId
import baaahs.controller.ControllerManager
import baaahs.controller.ControllerState
import baaahs.fixtures.FixtureMapping
import baaahs.plugin.Plugins
import baaahs.scene.MutableControllerConfig
import baaahs.scene.MutableDirectDmxControllerConfig
import baaahs.sim.FakeDmxUniverse
import baaahs.util.Clock
import baaahs.util.Logger
import kotlinx.serialization.modules.SerializersModule

interface DmxManager {
    fun allOff()

    val dmxUniverse: Dmx.Universe

    companion object : ControllerManager.Meta {
        override val controllerTypeName: String
            get() = "DMX"
        override val controllerIcon: String
            get() = "dmx.svg"

        internal val logger = Logger<DmxManager>()

        override fun createMutableControllerConfigFor(
            controllerId: ControllerId?,
            state: ControllerState?
        ): MutableControllerConfig =
            MutableDirectDmxControllerConfig(
                state?.title ?: controllerId?.id ?: "Direct DMX",
                null, null
            )
    }
}

class DmxManagerImpl(
    private val dmxDriver: Dmx.Driver,
    private val clock: Clock,
    private val fakeDmxUniverse: FakeDmxUniverse, // For fallback.
    pubSub: PubSub.Server,
    private val universeListener: DmxUniverseListener? = null,
    plugins: Plugins
) : BaseControllerManager<DirectDmxController, DirectDmxControllerConfig, DirectDmxController.DirectDmxState>(DmxManager.controllerTypeName), DmxManager {
    private val attachedDevices = findAttachedDevices()
    override val dmxUniverse = findDmxUniverse(attachedDevices)

    init {
        createCommandPort(plugins.serialModule).createReceiver(pubSub, object : DmxCommands {
            override suspend fun listDmxUniverses(): Map<String, DmxUniverseListener.LastFrame> =
                universeListener?.lastFrames ?: emptyMap()
        })
    }

    override fun onChange(
        controllerId: ControllerId,
        oldController: DirectDmxController?,
        controllerConfig: Change<DirectDmxControllerConfig?>,
        controllerState: Change<DirectDmxController.DirectDmxState?>,
        fixtureMappings: Change<List<FixtureMapping>>
    ): DirectDmxController? {
        if (controllerConfig.newValue == null && controllerState.newValue == null) return null

        val device = attachedDevices.find { it.id == controllerId.id }
            ?: run {
                logger.error { "No such DMX device $controllerId." }
                return null
            }

        return DirectDmxController(device, universeListener)
    }

    override fun start() {
        if (attachedDevices.isNotEmpty()) {
            attachedDevices.forEach { device ->
                val controllerId = ControllerId(controllerType, device.id)
                onStateChange(controllerId) { fromState ->
                    DirectDmxController.DirectDmxState(
                        "${device.name} (${device.id})",
                        null,
                        clock.now(),
                        null, null
                    )
                }
            }
        }
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

        fun createCommandPort(serializersModule: SerializersModule) =
            DmxCommands.getImpl("pinky/dmx/universes", serializersModule)
    }
}