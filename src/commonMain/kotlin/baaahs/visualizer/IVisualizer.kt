package baaahs.visualizer

interface IVisualizer {
    fun add(entityVisualizer: ItemVisualizer<*>)
    fun clear()
}