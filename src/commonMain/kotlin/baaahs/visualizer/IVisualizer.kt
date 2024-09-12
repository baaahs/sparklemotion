package baaahs.visualizer

import baaahs.model.ModelUnit
import baaahs.visualizer.entity.ItemVisualizer

interface IVisualizer {
    var units: ModelUnit
    var initialViewingAngle: Float

    fun add(itemVisualizer: ItemVisualizer<*>)
    fun clear()
    fun fitToScene()
}