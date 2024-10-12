package baaahs.fixtures

import baaahs.show.live.ActivePatchSet
import baaahs.sm.server.FrameListener
import baaahs.visualizer.remote.RemoteVisualizerServer

open class StubFixtureManager : FixtureManager {
    override val facade: FixtureManagerImpl.Facade
        get() = TODO("not implemented")

    override fun addFrameListener(frameListener: FrameListener):Unit = TODO("not implemented")
    override fun removeFrameListener(frameListener: FrameListener):Unit = TODO("not implemented")
    override fun activePatchSetChanged(activePatchSet: ActivePatchSet):Unit = TODO("not implemented")
    override fun hasActiveRenderPlan(): Boolean = TODO("not implemented")
    override fun maybeUpdateRenderPlans(): Boolean = TODO("not implemented")
    override fun sendFrame():Unit = TODO("not implemented")
    override fun newRemoteVisualizerServer(): RemoteVisualizerServer = TODO("not implemented")
    override fun addRemoteVisualizerListener(listener: RemoteVisualizerServer.Listener):Unit =
        TODO("not implemented")
    override fun removeRemoteVisualizerListener(listener: RemoteVisualizerServer.Listener):Unit =
        TODO("not implemented")
    override fun fixturesChanged(addedFixtures: Collection<Fixture>, removedFixtures: Collection<Fixture>):Unit =
        TODO("not implemented")
}