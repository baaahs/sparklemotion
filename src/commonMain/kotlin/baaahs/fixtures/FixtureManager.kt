package baaahs.fixtures

import baaahs.RenderPlan
import baaahs.gl.glsl.GlslProgram
import baaahs.gl.glsl.Resolver
import baaahs.gl.patch.LinkedPatch
import baaahs.gl.patch.PatchResolver
import baaahs.gl.render.FixtureRenderPlan
import baaahs.gl.render.RenderManager
import baaahs.show.live.ActiveSet
import baaahs.timeSync
import baaahs.util.Logger

class FixtureManager(
    private val renderManager: RenderManager
) {
    private val changedFixtures = mutableListOf<FixturesChanges>()
    private val fixtureRenderPlans: MutableMap<Fixture, FixtureRenderPlan> = hashMapOf()
    private var totalFixtures = 0

    private var currentActiveSet: ActiveSet = ActiveSet(emptyList())
    private var activeSetChanged = false
    internal var currentRenderPlan: RenderPlan? = null

    fun getFixtureRenderPlans_ForTestOnly(): Map<Fixture, FixtureRenderPlan> {
        return fixtureRenderPlans
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
        fixtureRenderPlans.forEach { (fixture, fixtureRenderPlan) ->
            renderPlan.programs.forEach { (patch: LinkedPatch, program: GlslProgram) ->
                if (patch.matches(fixture)) {
                    fixtureRenderPlan.useProgram(program)
                }
            }
        }
    }

    fun clearRenderPlans() {
        fixtureRenderPlans.values.forEach { it.release() }
    }

    fun getFixtureCount(): Int {
        return fixtureRenderPlans.size
    }

    fun sendFrame() {
        fixtureRenderPlans.values.forEach { fixtureRenderPlan ->
            // TODO(tom): The send might return an error, at which point this fixture should be nuked
            // from the list of fixtures. I'm not quite sure the best way to do that so I'm leaving this note.
            fixtureRenderPlan.sendFrame()
        }
    }

    private fun addFixture(fixture: Fixture) {
        fixtureRenderPlans.getOrPut(fixture) {
            logger.debug { "Adding fixture ${fixture.title}" }
            renderManager.addFixture(fixture)
                .also { totalFixtures++ }
        }
    }

    private fun removeFixture(fixture: Fixture) {
        fixtureRenderPlans.remove(fixture)?.let { fixtureRenderPlan ->
            logger.debug { "Removing fixture ${fixture.title}" }
            renderManager.removeFixture(fixtureRenderPlan)
            fixtureRenderPlan.release()
            totalFixtures--
        } ?: throw IllegalStateException("huh? can't remove unknown fixture $fixture")
    }

    fun activeSetChanged(activeSet: ActiveSet) {
        if (activeSet != currentActiveSet) {
            currentActiveSet = activeSet
            activeSetChanged = true
        }
    }

    fun maybeUpdateRenderPlans(resolver: Resolver): Boolean {
        var remapFixtures = incorporateFixtureChanges()

        // Maybe build new shaders.
        if (this.activeSetChanged) {
            val activeSet = currentActiveSet

            val elapsedMs = timeSync {
                currentRenderPlan = prepareRenderPlan(activeSet, resolver)
            }

            logger.info {
                "New render plan created; ${currentRenderPlan?.programs?.size ?: 0} programs, " +
                        "${getFixtureCount()} fixtures; took ${elapsedMs}ms"
            }

            remapFixtures = true
            this.activeSetChanged = false
        }

        if (remapFixtures) {
            clearRenderPlans()

            currentRenderPlan?.let {
                remap(it)
            }
        }

        return remapFixtures
    }

    private fun prepareRenderPlan(activeSet: ActiveSet, resolver: Resolver): RenderPlan {
        val patchResolution = PatchResolver(fixtureRenderPlans.values, activeSet)
        return patchResolution.createRenderPlan(renderManager, resolver)
    }

    fun hasActiveRenderPlan(): Boolean {
        return currentRenderPlan != null
    }

    data class FixturesChanges(val added: Collection<Fixture>, val removed: Collection<Fixture>)

    companion object {
        private val logger = Logger<FixtureManager>()
    }
}