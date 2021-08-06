package baaahs

import baaahs.mapper.ControllerId
import baaahs.mapper.FixtureMapping
import baaahs.mapping.MappingManager
import baaahs.ui.Observable

class FakeMappingManager(
    resultsByBrainId: Map<BrainId, FixtureMapping> = mapOf(),
    resultsBySurfaceName: Map<String, FixtureMapping> = mapOf()
) : Observable(), MappingManager {
    private val resultsByControllerId = resultsByBrainId
        .mapKeys { (k, _) -> k.asControllerId() }
        .toMutableMap()

    override val dataHasLoaded: Boolean get() = true

    private val resultsBySurfaceName = resultsBySurfaceName.toMutableMap()

    override suspend fun start() {
    }

    override fun findMappings(controllerId: ControllerId): List<FixtureMapping> {
        return listOfNotNull(resultsByControllerId[controllerId])
    }

    override fun getAllControllerMappings(): Map<ControllerId, FixtureMapping> {
        return resultsByControllerId
    }
}