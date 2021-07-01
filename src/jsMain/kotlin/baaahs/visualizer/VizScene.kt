package baaahs.visualizer

import three.js.Object3D
import three.js.Scene

actual class VizScene(private val scene: Scene) {
    actual fun add(obj: VizObj) {
        scene.add(obj.obj)
    }

    actual fun remove(obj: VizObj) {
        scene.add(obj.obj)
    }
}

actual class VizObj(val obj: Object3D)

