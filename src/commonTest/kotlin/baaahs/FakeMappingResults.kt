package baaahs

import baaahs.mapper.ControllerId
import baaahs.mapper.MappingResults

class FakeMappingResults(
    resultsByBrainId: Map<BrainId, MappingResults.Info> = mapOf(),
    resultsBySurfaceName: Map<String, MappingResults.Info> = mapOf()
) : MappingResults {
    private val resultsByControllerId = resultsByBrainId
        .mapKeys { (k, _) -> k.asControllerId() }
        .toMutableMap()

    private val resultsBySurfaceName = resultsBySurfaceName.toMutableMap()

    override fun dataForController(controllerId: ControllerId): MappingResults.Info? =
        resultsByControllerId[controllerId]

    override fun dataForEntity(entityName: String) = resultsBySurfaceName[entityName]
}