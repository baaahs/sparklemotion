package baaahs.visualizer

expect interface EntityVisualizer {
    var mapperIsRunning: Boolean

    fun addTo(scene: VizScene)
}