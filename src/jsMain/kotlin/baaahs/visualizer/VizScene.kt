package baaahs.visualizer

import three.js.Object3D

actual class VizObj(
    val obj: Object3D,
    private val listener: SceneListener? = null
) {
    actual fun add(child: VizObj) {
        obj.add(child.obj)
        listener?.add(child.obj)
    }

    actual fun remove(child: VizObj) {
        obj.remove(child.obj)
        listener?.remove(child.obj)
    }
}

interface SceneListener {
    fun add(obj: Object3D)
    fun remove(obj: Object3D)
}