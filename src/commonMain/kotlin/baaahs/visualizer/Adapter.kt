package baaahs.visualizer

import baaahs.model.Model

interface Adapter<T : Model.Entity /*TODO undo*/> {
    fun createVisualizer(entity: T): EntityVisualizer<T>
}

