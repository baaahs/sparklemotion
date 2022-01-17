package baaahs.mapping

import baaahs.mapper.ControllerId
import baaahs.mapper.FixtureMapping
import baaahs.mapper.SessionMappingResults
import baaahs.mapper.Storage
import baaahs.scene.OpenScene
import baaahs.scene.SceneProvider
import baaahs.ui.IObservable
import baaahs.ui.Observable
import baaahs.ui.addObserver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

interface MappingManager : IObservable {
    val dataHasLoaded: Boolean

    suspend fun start()

    fun findMappings(controllerId: ControllerId): List<FixtureMapping>

    fun getAllControllerMappings(): Map<ControllerId, List<FixtureMapping>>
}

class MappingManagerImpl(
    private val storage: Storage,
    private val sceneProvider: SceneProvider,
    private val coroutineScope: CoroutineScope = GlobalScope
) : Observable(), MappingManager {
    private var sessionMappingResults: SessionMappingResults? = null
    override var dataHasLoaded: Boolean = false

    override suspend fun start() {
        sceneProvider.addObserver(fireImmediately = true) {
            coroutineScope.launch { onSceneChange(sceneProvider.openScene) }
        }
    }

    private suspend fun onSceneChange(openScene: OpenScene?) {
        dataHasLoaded = false
        if (openScene == null) {
            sessionMappingResults = null
        } else {
            sessionMappingResults = storage.loadMappingData(openScene.model)
            dataHasLoaded = true
        }
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