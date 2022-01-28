package baaahs

import baaahs.fixtures.FixtureManager
import baaahs.gl.render.RenderManager
import baaahs.show.Show
import baaahs.show.ShowState
import baaahs.show.live.OpenShow
import baaahs.sm.webapi.ShowProblem
import baaahs.util.Clock
import baaahs.util.Logger

class ShowRunner(
    val show: Show,
    initialShowState: ShowState? = null,
    private val openShow: OpenShow,
    internal val clock: Clock,
    private val renderManager: RenderManager,
    private val fixtureManager: FixtureManager,
    updateProblems: (List<ShowProblem>) -> Unit
) {
    private var showState: ShowState = initialShowState ?: openShow.getShowState()
    private var activePatchSetChanged: Boolean = true

    init {
        openShow.use()
        logger.debug { "Running show ${openShow.title}" }
        updateProblems(openShow.allProblems)
    }

    fun getShowState(): ShowState {
        return showState
    }

    fun switchTo(newShowState: ShowState) {
        this.showState = newShowState
        activePatchSetChanged = true
    }

    /** @return `true` if a frame was rendered and should be sent to fixtures. */
    suspend fun renderNextFrame(): Boolean {
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
            fixtureManager.activePatchSetChanged(openShow.buildActivePatchSet())
            activePatchSetChanged = false
        }

        return fixtureManager.maybeUpdateRenderPlans()
    }

    fun release() {
        openShow.disuse()
    }

    companion object {
        val logger = Logger("ShowRunner")
    }
}