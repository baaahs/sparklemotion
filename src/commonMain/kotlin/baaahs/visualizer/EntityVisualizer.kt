package baaahs.visualizer

import baaahs.geom.Matrix4F
import baaahs.model.Model

expect interface EntityVisualizer {
    val entity: Model.Entity
    val title: String
    var mapperIsRunning: Boolean
    var selected: Boolean
    var transformation: Matrix4F

    fun addTo(scene: VizScene)
}