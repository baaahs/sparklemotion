package baaahs.scene

import baaahs.ModelProvider
import baaahs.model.Model
import kotlinx.coroutines.CompletableDeferred

class SceneMonitor : ModelProvider {
    private var scene: Scene? = null
    private val deferredModel: CompletableDeferred<Model> = CompletableDeferred()

    override suspend fun getModel(): Model {
        return deferredModel.await()
    }

    fun onChange(scene: Scene?) {
        this.scene = scene
        println("SceneMonitor: have $scene")
        scene?.model?.open()?.let { deferredModel.complete(it) }
    }
}