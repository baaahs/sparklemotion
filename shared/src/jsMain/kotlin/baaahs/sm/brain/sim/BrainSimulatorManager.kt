package baaahs.sm.brain.sim

import baaahs.net.Network
import baaahs.sm.brain.BrainId
import baaahs.sm.brain.proto.Pixels
import baaahs.util.Clock
import baaahs.util.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class BrainSimulatorManager(
    private val network: Network,
    private var clock: Clock
) {
    val facade = Facade()

    private val brainScope = CoroutineScope(Dispatchers.Main)
    internal val brainSimulators: MutableList<BrainSimulator> = mutableListOf()

    fun createBrain(entityName: String?, pixels: Pixels): BrainSimulator {
        val brainId = BrainId("brain//${brainSimulators.size}")
        if (entityName == null) {
            logger.debug { "Creating anonymous simulated brain: $brainId" }
        } else {
            logger.debug { "Creating simulated brain for $entityName: $brainId" }
        }

        val brain = BrainSimulator(brainId.uuid, network, pixels, clock, brainScope)
        brainSimulators.add(brain)
        facade.notifyChanged()
        return brain
    }

    inner class Facade : baaahs.ui.Facade() {
        val brainSimulators: List<BrainSimulator.Facade>
            get() = this@BrainSimulatorManager.brainSimulators.map { it.facade }
    }

    companion object {
        private val logger = Logger<BrainSimulatorManager>()
    }
}