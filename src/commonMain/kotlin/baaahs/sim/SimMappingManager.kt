package baaahs.sim

import baaahs.controller.ControllerId
import baaahs.fixtures.FixtureMapping
import baaahs.mapper.SessionMappingResults
import baaahs.mapping.MappingManager
import baaahs.ui.Observable

class SimMappingManager : Observable(), MappingManager {
    var mappingData: SessionMappingResults? = null

    override val dataHasLoaded: Boolean
        get() = true

    override suspend fun start() {
    }

    override fun findMappings(controllerId: ControllerId): List<FixtureMapping> =
        mappingData?.dataForController(controllerId)
            ?: emptyList()

    override fun getAllControllerMappings(): Map<ControllerId, List<FixtureMapping>> =
        emptyMap()
}