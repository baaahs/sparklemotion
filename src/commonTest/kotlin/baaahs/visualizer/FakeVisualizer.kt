package baaahs.visualizer

import baaahs.model.ModelUnit

class FakeVisualizer(
    override var units: ModelUnit,
    val itemVisualizers: MutableList<ItemVisualizer<*>> = mutableListOf()
) : IVisualizer {
    override fun add(itemVisualizer: ItemVisualizer<*>) {
        itemVisualizers.add(itemVisualizer)
    }

    override fun clear() {
        itemVisualizers.clear()
    }
}