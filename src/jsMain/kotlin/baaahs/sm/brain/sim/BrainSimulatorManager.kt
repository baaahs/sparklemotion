package baaahs.sm.brain.sim

import baaahs.net.Network
import baaahs.randomDelay
import baaahs.sm.brain.BrainId
import baaahs.sm.brain.proto.Pixels
import baaahs.util.Clock
import baaahs.util.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BrainSimulatorManager(
    private val network: Network,
    private var clock: Clock
) {
    private val brainScope = CoroutineScope(Dispatchers.Main)
    internal val brainSimulators: MutableList<BrainSimulator> = mutableListOf()

    fun createBrain(name: String, pixels: Pixels): BrainSimulator {
        val brainId = BrainId("brain//${brainSimulators.size}")
        logger.debug { "Creating simulated brain for $name: $brainId" }

        val brain = BrainSimulator(brainId.uuid, network, pixels, clock)
        brainSimulators.add(brain)

        brainScope.launch { randomDelay(1000); brain.run() }

        return brain
    }

    companion object {
        private val logger = Logger<BrainSimulatorManager>()
    }
}