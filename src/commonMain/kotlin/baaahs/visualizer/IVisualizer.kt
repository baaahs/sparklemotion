package baaahs.visualizer

interface IVisualizer {
    fun add(itemVisualizer: ItemVisualizer<*>)
    fun clear()
}