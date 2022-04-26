package baaahs.fixtures

import baaahs.io.ByteArrayWriter
import baaahs.model.Model
import baaahs.visualizer.remote.RemoteVisualizerServer

open class FakeFixtureManager : StubFixtureManager(), RemoteVisualizerServer.Listener {
    val remoteVisualizerListeners = mutableListOf<RemoteVisualizerServer.Listener>()

    override fun addRemoteVisualizerListener(listener: RemoteVisualizerServer.Listener) {
        remoteVisualizerListeners.add(listener)
    }
    override fun removeRemoteVisualizerListener(listener: RemoteVisualizerServer.Listener) {
        remoteVisualizerListeners.remove(listener)
    }

    override fun sendFixtureInfo(fixture: Fixture) {
        remoteVisualizerListeners.forEach { it.sendFixtureInfo(fixture) }
    }

    override fun sendFrameData(entity: Model.Entity?, block: (ByteArrayWriter) -> Unit) {
        remoteVisualizerListeners.forEach { it.sendFrameData(entity, block) }
    }
}