package baaahs.sm.brain

import baaahs.Pinky
import baaahs.mapping.MappingManager
import baaahs.sim.FakeNetwork
import baaahs.sm.brain.proto.BrainHelloMessage
import baaahs.ui.addObserver

class ProdBrainSimulator(
    private val mappingManager: MappingManager,
    private val brainManager: BrainManager
) {
    private var enabled = false
    private val simulatedBrainIds = arrayListOf<BrainId>()

    init {
        mappingManager.addObserver { updateSimBrains() }
    }

    fun enableSimulation() {
        val wasEnabled = enabled
        enabled = true

        if (!wasEnabled) updateSimBrains()
    }

    private fun updateSimBrains() {
        simulatedBrainIds.removeAll { brainId ->
            brainManager.removeBrain(brainId)
            true
        }

        if (!enabled) return

        val mappingInfos = mappingManager.getAllControllerMappings()
        mappingInfos.forEach { (controllerId, mappings) ->
            when (controllerId.controllerType) {
                BrainManager.controllerTypeName -> {
                    mappings.forEach { mapping ->
                        brainManager.foundBrain(
                            FakeNetwork.FakeAddress("Simulated Brain ${controllerId.id}"),
                            BrainHelloMessage(controllerId.id, mapping.entity?.name, null, null),
                            isSimulatedBrain = true
                        )
                        simulatedBrainIds.add(BrainId(controllerId.id))
                    }
                }
                else -> {
                    Pinky.logger.error { "Unknown controller type for $controllerId." }
                }
            }
        }
    }
}