package baaahs.visualizer

import baaahs.model.ModelUnit
import baaahs.visualizer.entity.ItemVisualizer

class FakeVisualizer(
    override var units: ModelUnit,
    override var initialViewingAngle: Float = 0f,
    val itemVisualizers: MutableList<ItemVisualizer<*>> = mutableListOf()
) : IVisualizer {
    override fun add(itemVisualizer: ItemVisualizer<*>) {
        itemVisualizers.add(itemVisualizer)
    }

    override fun clear() {
        itemVisualizers.clear()
    }

    override fun fitToScene() {}
}