package baaahs.visualizer

actual class VizScene {
    actual fun add(obj: VizObj) {}
    actual fun remove(obj: VizObj) {}
}

actual class VizObj

actual interface EntityVisualizer {
    actual var mapperIsRunning: Boolean
    actual fun addTo(scene: VizScene)
}