package baaahs.visualizer.remote

import baaahs.fixtures.Fixture
import baaahs.io.ByteArrayWriter
import baaahs.model.Model

open class RemoteVisualizers {
    private val listeners = hashSetOf<RemoteVisualizerServer.Listener>()

    fun addListener(remoteVisualizerListener: RemoteVisualizerServer.Listener) {
        listeners.add(remoteVisualizerListener)
    }

    fun removeListener(remoteVisualizerListener: RemoteVisualizerServer.Listener) {
        listeners.remove(remoteVisualizerListener)
    }

    fun sendFixtureInfo(fixture: Fixture) {
        listeners.forEach { it.sendFixtureInfo(fixture) }
    }

    fun sendFrameData(entity: Model.Entity?, block: (ByteArrayWriter) -> Unit) {
        listeners.forEach { it.sendFrameData(entity, block) }
    }
}
