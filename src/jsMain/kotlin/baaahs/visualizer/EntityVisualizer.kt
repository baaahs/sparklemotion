package baaahs.visualizer

actual interface EntityVisualizer {
    actual val title: String
    actual var mapperIsRunning: Boolean
    actual var selected: Boolean

    actual fun addTo(scene: VizScene)
}