package baaahs.gl.result

import baaahs.visualizer.remote.RemoteVisualizers

abstract class FixtureResults(
    val componentOffset: Int,
    val componentCount: Int
) {
    // TODO: This is pretty janky, having send() call RemoteVisualizers. Find a better way.
    abstract fun send(remoteVisualizers: RemoteVisualizers)
}