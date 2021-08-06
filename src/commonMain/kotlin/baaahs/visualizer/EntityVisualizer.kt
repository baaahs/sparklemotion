package baaahs.visualizer

expect interface EntityVisualizer {
    val title: String
    var mapperIsRunning: Boolean
    var selected: Boolean

    fun addTo(scene: VizScene)
}