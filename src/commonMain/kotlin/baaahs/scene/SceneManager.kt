package baaahs.scene

import baaahs.controller.ControllersManager
import baaahs.mapper.Storage
import baaahs.model.Model
import baaahs.models.sheepModelMetadata

class SceneManager(
    private val storage: Storage,
    private val controllersManager: ControllersManager
) {
    private var scene: Scene? = null
    private var model: Model? = null

    suspend fun onStart() {
        scene = storage.loadScene(storage.oldSceneJsonFile)
        controllersManager.onSceneChange(scene)

        val model = scene?.model?.open(sheepModelMetadata)
        this.model = model
    }
}