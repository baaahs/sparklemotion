package baaahs.visualizer

import baaahs.geom.Matrix4F
import baaahs.model.Model

actual class VizScene {
    actual fun add(obj: VizObj) {}
    actual fun remove(obj: VizObj) {}
}

actual class VizObj

actual interface EntityVisualizer {
    actual val entity: Model.Entity
    actual val title: String
    actual var mapperIsRunning: Boolean
    actual var selected: Boolean
    actual var transformation: Matrix4F

    actual fun addTo(scene: VizScene)
}