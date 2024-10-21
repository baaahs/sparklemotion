package baaahs.fixtures

import baaahs.visualizer.remote.RemoteVisualizerServer
import baaahs.visualizer.remote.RemoteVisualizers

open class FakeFixtureManager : RemoteVisualizers(), FixtureManager by StubFixtureManager() {
    override fun addRemoteVisualizerListener(listener: RemoteVisualizerServer.Listener) {
        addListener(listener)
    }
}