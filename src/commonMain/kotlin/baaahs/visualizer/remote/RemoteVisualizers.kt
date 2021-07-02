package baaahs.visualizer.remote

import baaahs.fixtures.Fixture
import baaahs.io.ByteArrayWriter

class RemoteVisualizers {
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

    fun sendFrameData(fixture: Fixture, block: (ByteArrayWriter) -> Unit) {
        listeners.forEach { it.sendFrameData(fixture, block) }
    }
}

interface RemoteVisualizable {
    fun addRemoteVisualizer(listener: RemoteVisualizerServer.Listener)
    fun removeRemoteVisualizer(listener: RemoteVisualizerServer.Listener)
}