package baaahs.sm.brain.sim

import baaahs.controller.ControllerId
import baaahs.controller.sim.ControllerSimulator
import baaahs.net.Network
import baaahs.randomDelay
import baaahs.sm.brain.BrainId
import baaahs.sm.brain.proto.Pixels
import baaahs.util.Clock
import baaahs.util.Logger
import baaahs.util.globalLaunch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class BrainSimulatorManager(
    private val network: Network,
    private var clock: Clock
) {
    private val brainScope = CoroutineScope(Dispatchers.Main)
    internal val brainSimulators: MutableList<BrainSimulator> = mutableListOf()

    fun createBrain(pixels: Pixels?): ControllerSimulator {
        val brainId = BrainId("brain//${brainSimulators.size}")
        logger.debug { "Creating simulated brain $brainId." }

        val brain = BrainSimulator(brainId.uuid, network, pixels, clock, brainScope)
        brainSimulators.add(brain)

        return object : ControllerSimulator {
            override val controllerId: ControllerId
                get() = brainId.asControllerId()

            override fun start() {
                globalLaunch {
                    randomDelay(1000)
                    brain.start()
                }
            }

            override fun stop() {
                brain.stop()
            }
        }
    }

    companion object {
        private val logger = Logger<BrainSimulatorManager>()
    }
}