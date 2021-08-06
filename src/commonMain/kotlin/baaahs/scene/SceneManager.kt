package baaahs.scene

import baaahs.controller.ControllersManager
import baaahs.mapper.Storage

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