package baaahs.visualizer

class FakeVisualizer(
    val entityVisualizers: MutableList<ItemVisualizer<*>> = mutableListOf()
) : IVisualizer {
    override fun add(entityVisualizer: ItemVisualizer<*>) {
        entityVisualizers.add(entityVisualizer)
    }

    override fun clear() {
        entityVisualizers.clear()
    }
}