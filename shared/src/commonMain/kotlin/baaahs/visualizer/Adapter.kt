package baaahs.visualizer

import baaahs.visualizer.entity.ItemVisualizer

interface Adapter<T: Any> {
    fun createVisualizer(entity: T): ItemVisualizer<T>

    fun createOrUpdateVisualizer(
        oldVisualizer: ItemVisualizer<T>?,
        entity: T
    ): ItemVisualizer<T> {
        val visualizer =
            if (oldVisualizer != null && oldVisualizer.updateIfApplicable(entity)) {
                oldVisualizer
            } else {
                createVisualizer(entity)
            }
        return visualizer
    }

    fun <T> withinGroup(title: String, block: () -> T): T = block()
}