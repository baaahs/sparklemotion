package baaahs.mapping

import baaahs.ModelProvider
import baaahs.mapper.ControllerId
import baaahs.mapper.FixtureMapping
import baaahs.mapper.SessionMappingResults
import baaahs.mapper.Storage
import baaahs.ui.IObservable
import baaahs.ui.Observable

interface MappingManager : IObservable {
    val dataHasLoaded: Boolean

    suspend fun start()

    fun findMappings(controllerId: ControllerId): List<FixtureMapping>

    fun getAllControllerMappings(): Map<ControllerId, List<FixtureMapping>>
}

class MappingManagerImpl(
    private val storage: Storage,
    private val modelProvider: ModelProvider,
) : Observable(), MappingManager {
    private var sessionMappingResults: SessionMappingResults? = null
    override val dataHasLoaded: Boolean
        get() = sessionMappingResults != null

    override suspend fun start() {
        sessionMappingResults = storage.loadMappingData(modelProvider.getModel())
        notifyChanged()
    }

    override fun findMappings(controllerId: ControllerId): List<FixtureMapping> {
        val results = sessionMappingResults
            ?: error("Mapping results requested before available.")

        return results.dataForController(controllerId)
    }

    override fun getAllControllerMappings(): Map<ControllerId, List<FixtureMapping>> {
        val results = sessionMappingResults
            ?: error("Mapping results requested before available.")

        return results.controllerData
    }
}