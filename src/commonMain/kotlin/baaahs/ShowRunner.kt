package baaahs

import baaahs.driverack.DriveRackManager
import baaahs.fixtures.FixtureManager
import baaahs.gl.render.RenderManager
import baaahs.show.Show
import baaahs.show.live.OpenShow
import baaahs.util.Clock
import baaahs.util.Logger

class ShowRunner(
    val show: Show,
    initialShowState: ShowState? = null,
    private val openShow: OpenShow,
    internal val clock: Clock,
    private val renderManager: RenderManager,
    private val fixtureManager: FixtureManager,
    driveRackManager: DriveRackManager,
    private val updateProblems: (List<ShowProblem>) -> Unit
) {
    private var showState: ShowState = initialShowState ?: openShow.getShowState()
    private var activePatchSetChanged: Boolean = true
    private val driveRack = driveRackManager.publish(openShow.rackMap, initialBuses = setOf("A", "B"))
    private val driveRackMainBus = driveRack.getBus("A")
    private val driveRackAltBus = driveRack.getBus("B")

    val driveRackId: String get() = driveRack.id

    init {
        logger.debug { "Running show ${openShow.title}" }
        updateProblems(openShow.allProblems)

        openShow.channels.forEach { (_, channel) -> channel.attachBus(driveRackMainBus) }
        openShow.altChannels.forEach { (_, channel) -> channel.attachBus(driveRackAltBus) }
    }

    fun getShowState(): ShowState {
        return showState
    }

    fun switchTo(newShowState: ShowState) {
        this.showState = newShowState
        activePatchSetChanged = true
    }

    /** @return `true` if a frame was rendered and should be sent to fixtures. */
    fun renderNextFrame(): Boolean {
        return if (fixtureManager.hasActiveRenderPlan()) {
            renderManager.draw()
            true
        } else false
    }

    fun onSelectedPatchesChanged() {
        activePatchSetChanged = true
    }

    fun housekeeping(): Boolean {
        if (activePatchSetChanged) {
            fixtureManager.activePatchSetChanged(openShow.activePatchSet())
        }

        return fixtureManager.maybeUpdateRenderPlans()
    }

    fun release() {
        openShow.release()
    }

    companion object {
        val logger = Logger("ShowRunner")
    }
}