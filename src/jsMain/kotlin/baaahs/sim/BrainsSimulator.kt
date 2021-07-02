package baaahs.sim

import baaahs.Brain
import baaahs.BrainId
import baaahs.Pixels
import baaahs.net.Network
import baaahs.randomDelay
import baaahs.util.Clock
import baaahs.util.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BrainsSimulator(
    private val network: Network,
    private var clock: Clock
) {
    private val brainScope = CoroutineScope(Dispatchers.Main)
    internal val brains: MutableList<Brain> = mutableListOf()

    fun createBrain(name: String, pixels: Pixels): Brain {
        val brainId = BrainId("brain//${brains.size}")
        logger.debug { "Creating simulated brain for $name: $brainId" }

        val brain = Brain(brainId.uuid, network, pixels, clock)
        brains.add(brain)

        brainScope.launch { randomDelay(1000); brain.run() }

        return brain
    }

    companion object {
        private val logger = Logger<BrainsSimulator>()
    }
}