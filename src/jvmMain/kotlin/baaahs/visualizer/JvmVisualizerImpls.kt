package baaahs.visualizer

import baaahs.model.Model
import baaahs.ui.IObservable

actual class VizObj {
    actual fun add(child: VizObj) {}
    actual fun remove(child: VizObj) {}
}

actual interface EntityVisualizer<T : Model.Entity> : IObservable {
    actual val entity: T
    actual val title: String
    actual var isEditing: Boolean
    actual var mapperIsRunning: Boolean
    actual var selected: Boolean
}

actual val visualizerBuilder: VisualizerBuilder get() =
    error("visualizerBuilder unimplemented on JVM")