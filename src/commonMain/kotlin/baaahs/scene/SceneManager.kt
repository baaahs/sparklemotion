package baaahs.scene

import baaahs.controller.ControllersManager
import baaahs.mapper.Storage
import baaahs.model.ModelData
import kotlinx.serialization.Serializable

class SceneManager(
    private val storage: Storage,
    private val controllersManager: ControllersManager
) {
    private var sceneConfig: SceneConfig? = null

    suspend fun onStart() {
        sceneConfig = storage.loadSceneConfig()
        controllersManager.onSceneChange(sceneConfig)
    }
}

@Serializable
data class Scene(
    val model: ModelData
)