package baaahs.visualizer

interface Adapter<T: Any> {
    fun createVisualizer(entity: T): ItemVisualizer<T>
}

