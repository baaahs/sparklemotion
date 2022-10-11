package baaahs.visualizer

import baaahs.model.ModelUnit

interface IVisualizer {
    var units: ModelUnit

    fun add(itemVisualizer: ItemVisualizer<*>)
    fun clear()
}