package baaahs.fixtures

import baaahs.gl.glsl.GlslProgram
import baaahs.gl.render.FixtureRenderTarget
import baaahs.gl.render.RenderManager
import baaahs.gl.render.RenderTarget
import baaahs.glsl.LinearSurfacePixelStrategy
import baaahs.glsl.SurfacePixelStrategy
import baaahs.model.Model
import baaahs.show.live.ActivePatchSet
import baaahs.timeSync
import baaahs.util.Logger
import baaahs.visualizer.remote.RemoteVisualizerServer
import baaahs.visualizer.remote.RemoteVisualizers

interface FixtureListener {
    fun fixturesChanged(addedFixtures: Collection<Fixture>, removedFixtures: Collection<Fixture>)
}

class FixtureManager(
    private val renderManager: RenderManager,
    private val model: Model,
    private val surfacePixelStrategy: SurfacePixelStrategy = LinearSurfacePixelStrategy(),
    initialRenderTargets: Map<Fixture, FixtureRenderTarget> = emptyMap()
) : FixtureListener {
    val facade = Facade()

    private val renderTargets: MutableMap<Fixture, FixtureRenderTarget> = initialRenderTargets.toMutableMap()
    private val frameListeners: MutableList<() -> Unit> = arrayListOf()
    private val changedFixtures = mutableListOf<FixturesChanges>()

    private var currentActivePatchSet: ActivePatchSet = ActivePatchSet.Empty
    private var activePatchSetChanged = false
    internal var currentRenderPlan: RenderPlan? = null
        private set

    private val remoteVisualizers = RemoteVisualizers()

    fun addFrameListener(callback: () -> Unit) {
        frameListeners.add(callback)
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

    private fun clearRenderTargets() {
        renderTargets.values.forEach { it.release() }
    }

    private fun getFixtureCount(): Int = renderTargets.size

    fun sendFrame() {
        renderTargets.values.forEach { renderTarget ->
            // TODO(tom): The send might return an error, at which point this fixture should be nuked
            // from the list of fixtures. I'm not quite sure the best way to do that so I'm leaving this note.
            renderTarget.sendFrame(remoteVisualizers)
        }
        frameListeners.forEach { it.invoke() }
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
            logger.debug { "Removing fixture ${fixture.title}" }
            renderManager.removeRenderTarget(renderTarget)
            renderTarget.release()
            // TODO: remove from RemoteVisualizers
        } ?: throw IllegalStateException("huh? can't remove unknown fixture $fixture")
    }

    fun activePatchSetChanged(activePatchSet: ActivePatchSet) {
        if (activePatchSet != currentActivePatchSet) {
            currentActivePatchSet = activePatchSet
            activePatchSetChanged = true
        }
    }

    fun maybeUpdateRenderPlans(): Boolean {
        var remapFixtures = incorporateFixtureChanges()

        // Maybe build new shaders.
        // TODO: In the remapFixtures case, this would benefit from reusing cached artifacts.
        if (this.activePatchSetChanged || remapFixtures) {
            val activePatchSet = currentActivePatchSet

            val elapsedMs = timeSync {
                currentRenderPlan = activePatchSet.createRenderPlan(renderManager, renderTargets.values)
            }

            logger.info {
                "New render plan created: ${currentRenderPlan?.describe() ?: "none!"}; took ${elapsedMs}ms"
            }

            remapFixtures = true
            this.activePatchSetChanged = false
        }

        if (remapFixtures) {
            clearRenderTargets()

            currentRenderPlan?.let { renderPlan ->
                renderManager.setRenderPlan(renderPlan)
            }
        }

        return remapFixtures
    }

    fun hasActiveRenderPlan(): Boolean {
        return currentRenderPlan != null
    }

    fun newRemoteVisualizerServer(): RemoteVisualizerServer {
        return RemoteVisualizerServer(this)
    }

    fun addRemoteVisualizerListener(listener: RemoteVisualizerServer.Listener) {
        remoteVisualizers.addListener(listener)
        renderTargets.keys.forEach { fixture -> remoteVisualizers.sendFixtureInfo(fixture) }
    }

    fun removeRemoteVisualizerListener(listener: RemoteVisualizerServer.Listener) {
        remoteVisualizers.removeListener(listener)
    }

    data class FixturesChanges(val added: Collection<Fixture>, val removed: Collection<Fixture>)

    inner class Facade : baaahs.ui.Facade() {
        val fixtureCount: Int
            get() = this@FixtureManager.getFixtureCount()

        val pixelCount: Int
            get() = this@FixtureManager.renderTargets.values.sumOf { it.pixelCount }
    }

    companion object {
        private val logger = Logger<FixtureManager>()
    }
}

class RenderPlan(
    val deviceTypes: Map<DeviceType, DeviceTypeRenderPlan>
) : Map<DeviceType, DeviceTypeRenderPlan> by deviceTypes {
    fun describe() = "${deviceTypes.size} devices, " +
            "${deviceTypes.values.map { it.programs.count() }.sum()} programs, " +
            "${deviceTypes.values.map { it.programs.map { it.renderTargets.count() }.sum() }.sum()} fixtures"
}

class DeviceTypeRenderPlan(
    val programs: List<ProgramRenderPlan>,
) : Iterable<ProgramRenderPlan> by programs

class ProgramRenderPlan(
    val program: GlslProgram?,
    val renderTargets: List<RenderTarget>
)