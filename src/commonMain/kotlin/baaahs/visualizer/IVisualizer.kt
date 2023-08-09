package baaahs.visualizer

import baaahs.model.ModelUnit

interface IVisualizer {
    var units: ModelUnit
    var initialViewingAngle: Float

    fun add(itemVisualizer: ItemVisualizer<*>)
    fun clear()
    fun fitToScene()
}