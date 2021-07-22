package baaahs

import baaahs.mapper.ControllerId
import baaahs.mapper.FixtureMapping
import baaahs.mapper.MappingResults

class FakeMappingResults(
    resultsByBrainId: Map<BrainId, FixtureMapping> = mapOf(),
    resultsBySurfaceName: Map<String, FixtureMapping> = mapOf()
) : MappingResults {
    private val resultsByControllerId = resultsByBrainId
        .mapKeys { (k, _) -> k.asControllerId() }
        .toMutableMap()

    private val resultsBySurfaceName = resultsBySurfaceName.toMutableMap()

    override fun dataForController(controllerId: ControllerId): FixtureMapping? =
        resultsByControllerId[controllerId]

    override fun dataForEntity(entityName: String) = resultsBySurfaceName[entityName]
}