package baaahs.fixtures

import baaahs.RenderPlan
import baaahs.gl.glsl.FeedResolver
import baaahs.gl.glsl.GlslProgram
import baaahs.gl.patch.ContentType
import baaahs.gl.patch.LinkedPatch
import baaahs.gl.patch.PatchResolver
import baaahs.gl.render.RenderManager
import baaahs.gl.render.RenderTarget
import baaahs.show.live.ActiveSet
import baaahs.timeSync
import baaahs.util.Logger

class FixtureManager(
    private val renderManager: RenderManager
) {
    private val frameListeners: MutableList<() -> Unit> = arrayListOf()
    private val changedFixtures = mutableListOf<FixturesChanges>()
    private val renderTargets: MutableMap<Fixture, RenderTarget> = hashMapOf()
    private var totalFixtures = 0

    private var currentActiveSet: ActiveSet = ActiveSet(emptyList())
    private var activeSetChanged = false
    internal var currentRenderPlan: RenderPlan? = null

    fun addFrameListener(callback: () -> Unit) {
        frameListeners.add(callback)
    }

    fun getRenderTargets_ForTestOnly(): Map<Fixture, RenderTarget> {
        return renderTargets
    }

    fun fixturesChanged(addedFixtures: Collection<Fixture>, removedFixtures: Collection<Fixture>) {
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

    fun remap(renderPlan: RenderPlan) {
        renderTargets.forEach { (fixture, renderTarget) ->
            renderPlan.programs.forEach { (deviceType, patches) ->
                if (fixture.deviceType == deviceType) {
                    patches.forEach { (patch, program) ->
                        if (patch.matches(fixture)) {
                            renderTarget.useProgram(program)
                        }

                    }
                }
            }
        }
    }

    fun clearRenderTargets() {
        renderTargets.values.forEach { it.release() }
    }

    fun getFixtureCount(): Int {
        return renderTargets.size
    }

    fun sendFrame() {
        renderTargets.values.forEach { renderTarget ->
            // TODO(tom): The send might return an error, at which point this fixture should be nuked
            // from the list of fixtures. I'm not quite sure the best way to do that so I'm leaving this note.
            renderTarget.sendFrame()
        }
        frameListeners.forEach { it.invoke() }
    }

    private fun addFixture(fixture: Fixture) {
        renderTargets.getOrPut(fixture) {
            logger.debug { "Adding fixture ${fixture.title}" }
            renderManager.addFixture(fixture)
                .also { totalFixtures++ }
        }
    }

    private fun removeFixture(fixture: Fixture) {
        renderTargets.remove(fixture)?.let { renderTarget ->
            logger.debug { "Removing fixture ${fixture.title}" }
            renderManager.removeRenderTarget(renderTarget)
            renderTarget.release()
            totalFixtures--
        } ?: throw IllegalStateException("huh? can't remove unknown fixture $fixture")
    }

    fun activeSetChanged(activeSet: ActiveSet) {
        if (activeSet != currentActiveSet) {
            currentActiveSet = activeSet
            activeSetChanged = true
        }
    }

    fun maybeUpdateRenderPlans(feedResolver: FeedResolver): Boolean {
        var remapFixtures = incorporateFixtureChanges()

        // Maybe build new shaders.
        if (this.activeSetChanged) {
            val activeSet = currentActiveSet

            val elapsedMs = timeSync {
                val patchResolution = PatchResolver(renderTargets.values, activeSet)
                currentRenderPlan = patchResolution.createRenderPlan(renderManager, feedResolver)
            }

            logger.info {
                "New render plan created; ${currentRenderPlan?.programs?.size ?: 0} programs, " +
                        "${getFixtureCount()} fixtures; took ${elapsedMs}ms"
            }

            remapFixtures = true
            this.activeSetChanged = false
        }

        if (remapFixtures) {
            clearRenderTargets()

            currentRenderPlan?.let {
                remap(it)
            }
        }

        return remapFixtures
    }

    fun hasActiveRenderPlan(): Boolean {
        return currentRenderPlan != null
    }

    data class FixturesChanges(val added: Collection<Fixture>, val removed: Collection<Fixture>)

    companion object {
        private val logger = Logger<FixtureManager>()
    }
}