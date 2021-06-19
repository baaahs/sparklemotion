package baaahs

import baaahs.mapper.ControllerId
import baaahs.mapper.MappingResults
import mockk
import org.spekframework.spek2.Spek

object BrainManagerSpec : Spek({
    describe<BrainManager> {
        val brainManager by value {
            BrainManager(
                mockk(), PermissiveFirmwareDaddy(), FakeMappingResults(), mockk(),
                Pinky.NetworkStats(), FakeClock(), StubPubSubServer()
            )
        }

        // TODO this!
    }
})

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