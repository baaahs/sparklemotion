package baaahs.visualizer

actual interface EntityVisualizer {
    actual var mapperIsRunning: Boolean

    actual fun addTo(scene: VizScene)
}