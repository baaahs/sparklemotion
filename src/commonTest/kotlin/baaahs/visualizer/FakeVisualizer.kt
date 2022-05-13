package baaahs.visualizer

class FakeVisualizer(
    val itemVisualizers: MutableList<ItemVisualizer<*>> = mutableListOf()
) : IVisualizer {
    override fun add(itemVisualizer: ItemVisualizer<*>) {
        itemVisualizers.add(itemVisualizer)
    }

    override fun clear() {
        itemVisualizers.clear()
    }
}