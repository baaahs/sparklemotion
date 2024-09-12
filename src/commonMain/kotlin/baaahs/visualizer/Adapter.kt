package baaahs.visualizer

import baaahs.visualizer.entity.ItemVisualizer

interface Adapter<T: Any> {
    fun createVisualizer(entity: T): ItemVisualizer<T>
}

