package baaahs.gl.result

import baaahs.model.Model
import baaahs.visualizer.remote.RemoteVisualizers

abstract class FixtureResults(
    val pixelOffset: Int,
    val pixelCount: Int
) {
    // TODO: This is pretty janky, having send() call RemoteVisualizers. Find a better way.
    abstract fun send(entity: Model.Entity?, remoteVisualizers: RemoteVisualizers)
}