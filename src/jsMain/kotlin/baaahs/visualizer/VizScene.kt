package baaahs.visualizer

import three.js.Object3D

actual class VizScene(private val sceneListener: SceneListener) {
    actual fun add(obj: VizObj) {
        sceneListener.add(obj.obj)
    }

    actual fun remove(obj: VizObj) {
        sceneListener.add(obj.obj)
    }
}

actual class VizObj(val obj: Object3D)

interface SceneListener {
    fun add(obj: Object3D)
    fun remove(obj: Object3D)
}