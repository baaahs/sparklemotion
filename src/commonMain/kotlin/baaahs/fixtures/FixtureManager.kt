package baaahs.fixtures

import baaahs.RenderPlan
import baaahs.ShowRunner
import baaahs.gl.glsl.GlslProgram
import baaahs.gl.glsl.Resolver
import baaahs.gl.patch.LinkedPatch
import baaahs.gl.patch.PatchResolver
import baaahs.gl.render.FixtureRenderPlan
import baaahs.gl.render.RenderManager
import baaahs.show.live.ActiveSet
import baaahs.timeSync

class FixtureManager(
    private val renderManager: RenderManager
) {
    private val changedFixtures = mutableListOf<ShowRunner.FixturesChanges>()
    private val fixtureRenderPlans: MutableMap<Fixture, FixtureRenderPlan> = hashMapOf()
    private var totalFixtureReceivers = 0

    private var currentActiveSet: ActiveSet = ActiveSet(emptyList())
    private var activeSetChanged = false
    internal var currentRenderPlan: RenderPlan? = null

    fun getFixtureRenderPlans_ForTestOnly(): Map<Fixture, FixtureRenderPlan> {
        return fixtureRenderPlans
    }

    fun fixturesChanged(addedFixtures: Collection<ShowRunner.FixtureReceiver>, removedFixtures: Collection<ShowRunner.FixtureReceiver>) {
        changedFixtures.add(ShowRunner.FixturesChanges(addedFixtures.toList(), removedFixtures.toList()))
    }

    fun requiresRemap(): Boolean {
        var anyChanges = false

        for ((added, removed) in changedFixtures) {
            ShowRunner.logger.info { "ShowRunner surfaces changed! ${added.size} added, ${removed.size} removed" }
            for (receiver in removed) removeReceiver(receiver)
            for (receiver in added) addReceiver(receiver)
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
            fixtureRenderPlan.receivers.forEach { receiver ->
                // TODO: The send might return an error, at which point this receiver should be nuked
                // from the list of receivers for this fixture. I'm not quite sure the best way to do
                // that so I'm leaving this note.
                val pixels = PixelArrayDevice.getPixels(fixtureRenderPlan)
                receiver.send(pixels)
            }
        }
    }

    private fun addReceiver(receiver: ShowRunner.FixtureReceiver) {
        val fixture = receiver.fixture
        val fixtureRenderPlan = fixtureRenderPlans.getOrPut(fixture) {
            renderManager.addFixture(fixture)
        }
        fixtureRenderPlan.receivers.add(receiver)

        totalFixtureReceivers++
    }

    private fun removeReceiver(receiver: ShowRunner.FixtureReceiver) {
        val fixture = receiver.fixture
        val fixtureRenderPlan = fixtureRenderPlans[fixture]
            ?: throw IllegalStateException("huh? no SurfaceBinder for $fixture")

        if (!fixtureRenderPlan.receivers.remove(receiver)) {
            throw IllegalStateException("huh? receiver not registered for $fixture")
        }

        if (fixtureRenderPlan.receivers.isEmpty()) {
            renderManager.removeFixture(fixtureRenderPlan)
            fixtureRenderPlan.release()
            fixtureRenderPlans.remove(fixture)
        }

        totalFixtureReceivers--
    }

    fun activeSetChanged(activeSet: ActiveSet) {
        if (activeSet != currentActiveSet) {
            currentActiveSet = activeSet
            activeSetChanged = true
        }
    }

    fun maybeUpdateRenderPlans(resolver: Resolver): Boolean {
        var remapFixtures = requiresRemap()

        // Maybe build new shaders.
        if (this.activeSetChanged) {
            val activeSet = currentActiveSet

            val elapsedMs = timeSync {
                currentRenderPlan = prepareRenderPlan(activeSet, resolver)
            }

            ShowRunner.logger.info {
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
}