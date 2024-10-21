package baaahs.fixtures

import baaahs.device.FixtureType
import baaahs.gl.glsl.GlslProgram
import baaahs.gl.patch.LinkedProgram
import baaahs.gl.patch.PortDiagram
import baaahs.gl.render.FixtureRenderTarget
import baaahs.gl.render.RenderManager
import baaahs.gl.render.RenderTarget
import baaahs.plugin.Plugins
import baaahs.show.live.ActivePatchSet
import baaahs.sm.server.FrameListener
import baaahs.timeSync
import baaahs.util.Logger
import baaahs.util.Monitor
import baaahs.visualizer.remote.RemoteVisualizerServer
import baaahs.visualizer.remote.RemoteVisualizers

interface FixtureListener {
    fun fixturesChanged(addedFixtures: Collection<Fixture>, removedFixtures: Collection<Fixture>)
}

interface FixtureManager : FixtureListener {
    val facade: FixtureManagerImpl.Facade

    fun addFrameListener(frameListener: FrameListener)
    fun removeFrameListener(frameListener: FrameListener)
    fun activePatchSetChanged(activePatchSet: ActivePatchSet)
    fun hasActiveRenderPlan(): Boolean
    fun maybeUpdateRenderPlans(): Boolean
    fun sendFrame()
    fun newRemoteVisualizerServer(): RemoteVisualizerServer
    fun addRemoteVisualizerListener(listener: RemoteVisualizerServer.Listener)
    fun removeRemoteVisualizerListener(listener: RemoteVisualizerServer.Listener)
}

class FixtureManagerImpl(
    private val renderManager: RenderManager,
    private val plugins: Plugins,
    private val renderPlanMonitor: Monitor<RenderPlan?> = Monitor(null),
    initialRenderTargets: Map<Fixture, FixtureRenderTarget> = emptyMap()
) : FixtureManager {
    override val facade = Facade()

    private val renderTargets: MutableMap<Fixture, FixtureRenderTarget> = initialRenderTargets.toMutableMap()
    private val frameListeners: MutableList<FrameListener> = arrayListOf()
    private val changedFixtures = mutableListOf<FixturesChanges>()

    private var currentActivePatchSet: ActivePatchSet = ActivePatchSet.Empty
    private var activePatchSetChanged = false
    internal var currentRenderPlan: RenderPlan? = null
        private set

    private val remoteVisualizers = RemoteVisualizers()

    override fun addFrameListener(frameListener: FrameListener) {
        frameListeners.add(frameListener)
    }

    override fun removeFrameListener(frameListener: FrameListener) {
        frameListeners.remove(frameListener)
    }

    fun getRenderTargets_ForTestOnly(): Map<Fixture, RenderTarget> {
        return renderTargets
    }

    override fun fixturesChanged(addedFixtures: Collection<Fixture>, removedFixtures: Collection<Fixture>) {
        changedFixtures.add(FixturesChanges(addedFixtures.toList(), removedFixtures.toList()))
    }

    private fun incorporateFixtureChanges(): Boolean {
        var anyChanges = false

        for ((added, removed) in changedFixtures) {
            logger.info { "fixtures changed! ${added.size} added, ${removed.size} removed" }
            for (fixture in removed) removeFixture(fixture)
            for (fixture in added) addFixture(fixture)
            anyChanges = true
        }
        changedFixtures.clear()
        return anyChanges
    }

    private fun clearRenderPlan() {
        renderTargets.values.forEach { it.clearRenderPlan() }
    }

    private fun getFixtureCount(): Int = renderTargets.size

    override fun sendFrame() {
        frameListeners.forEach { it.beforeFrame() }
        renderTargets.values.forEach { renderTarget ->
            // TODO(tom): The send might return an error, at which point this fixture should be nuked
            // from the list of fixtures. I'm not quite sure the best way to do that so I'm leaving this note.
            renderTarget.sendFrame(remoteVisualizers)
        }
        frameListeners.forEach { it.afterFrame() }
    }

    private fun addFixture(fixture: Fixture) {
        renderTargets.getOrPut(fixture) {
            logger.debug { "Adding fixture ${fixture.title}" }
            renderManager.addFixture(fixture).also {
                remoteVisualizers.sendFixtureInfo(fixture)
            }
        }
    }

    private fun removeFixture(fixture: Fixture) {
        renderTargets.remove(fixture)?.let { renderTarget ->
            logger.debug { "Releasing fixture ${fixture.title}" }
            renderTarget.release()
            // TODO: remove from RemoteVisualizers
        } ?: throw IllegalStateException("huh? can't remove unknown fixture $fixture")
    }

    override fun activePatchSetChanged(activePatchSet: ActivePatchSet) {
        if (activePatchSet != currentActivePatchSet) {
            currentActivePatchSet = activePatchSet
            activePatchSetChanged = true
        }
    }

    override fun maybeUpdateRenderPlans(): Boolean {
        var remapFixtures = incorporateFixtureChanges()

        // Maybe build new shaders.
        // TODO: In the remapFixtures case, this would benefit from reusing cached artifacts.
        if (this.activePatchSetChanged || remapFixtures) {
            val activePatchSet = currentActivePatchSet

            val elapsedMs = timeSync {
                currentRenderPlan = activePatchSet.createRenderPlan(renderManager, renderTargets.values)
            }
            renderPlanMonitor.onChange(currentRenderPlan)

            logger.info {
                "New render plan created: ${currentRenderPlan?.describe() ?: "none!"}; took ${elapsedMs}ms"
            }

            remapFixtures = true
            this.activePatchSetChanged = false
        }

        if (remapFixtures) {
            clearRenderPlan()

            currentRenderPlan?.let { renderPlan ->
                renderManager.setRenderPlan(renderPlan)
            }
        }

        return remapFixtures
    }

    override fun hasActiveRenderPlan(): Boolean {
        return currentRenderPlan != null
    }

    override fun newRemoteVisualizerServer(): RemoteVisualizerServer {
        return RemoteVisualizerServer(this, plugins)
    }

    override fun addRemoteVisualizerListener(listener: RemoteVisualizerServer.Listener) {
        remoteVisualizers.addListener(listener)
        renderTargets.keys.forEach { fixture -> remoteVisualizers.sendFixtureInfo(fixture) }
    }

    override fun removeRemoteVisualizerListener(listener: RemoteVisualizerServer.Listener) {
        remoteVisualizers.removeListener(listener)
    }

    data class FixturesChanges(val added: Collection<Fixture>, val removed: Collection<Fixture>)

    inner class Facade : baaahs.ui.Facade() {
        val fixtureCount: Int
            get() = this@FixtureManagerImpl.getFixtureCount()
        val componentCount: Int
            get() = this@FixtureManagerImpl.renderTargets.values.sumOf { it.componentCount }
        val renderPlanMonitor: Monitor<RenderPlan?>
            get() = this@FixtureManagerImpl.renderPlanMonitor
        val currentRenderPlan: RenderPlan?
            get() = this@FixtureManagerImpl.currentRenderPlan
    }

    companion object {
        private val logger = Logger<FixtureManager>()
    }
}

class RenderPlan(
    val fixtureTypes: Map<FixtureType, FixtureTypeRenderPlan>
) : Map<FixtureType, FixtureTypeRenderPlan> by fixtureTypes {
    fun describe() = "${fixtureTypes.size} devices, " +
            "${fixtureTypes.values.map { it.programs.count() }.sum()} programs, " +
            "${fixtureTypes.values.map { it.programs.map { it.renderTargets.count() }.sum() }.sum()} fixtures"
}

class FixtureTypeRenderPlan(
    val programs: List<ProgramRenderPlan>,
) : Iterable<ProgramRenderPlan> by programs

class ProgramRenderPlan(
    val program: GlslProgram?,
    val renderTargets: List<RenderTarget>,
    val linkedProgram: LinkedProgram? = null,
    val source: String? = null,
    val portDiagram: PortDiagram? = null
)