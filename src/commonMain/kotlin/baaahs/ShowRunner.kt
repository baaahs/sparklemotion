package baaahs

import baaahs.fixtures.Fixture
import baaahs.fixtures.FixtureManager
import baaahs.gl.glsl.GlslProgram
import baaahs.gl.patch.AutoWirer
import baaahs.gl.patch.LinkedPatch
import baaahs.gl.render.RenderManager
import baaahs.show.Show
import baaahs.show.live.ActiveSet
import baaahs.show.live.OpenShow
import baaahs.util.Logger

class ShowRunner(
    val show: Show,
    initialShowState: ShowState? = null,
    private val openShow: OpenShow,
//    private val beatSource: BeatSource,
//    private val dmxUniverse: Dmx.Universe,
//    private val movingHeadManager: MovingHeadManager,
    internal val clock: Clock,
    private val renderManager: RenderManager,
    private val fixtureManager: FixtureManager,
    private val autoWirer: AutoWirer
) {
    private var showState: ShowState = initialShowState ?: openShow.getShowState()
    private var activeSetChanged: Boolean = true

    // TODO: Get beat sync working again.
//    // Continuous from [0.0 ... 3.0] (0 is first beat in a measure, 3 is last)
//    val currentBeat: Float
//        get() = beatSource.getBeatData().beatWithinMeasure(clock)

//    private fun getDmxBuffer(baseChannel: Int, channelCount: Int): Dmx.Buffer =
//        dmxUniverse.writer(baseChannel, channelCount)
//
//    // TODO: Get moving heads working again.
//    fun getMovingHeadBuffer(movingHead: MovingHead): MovingHead.Buffer {
//        val baseChannel = Config.DMX_DEVICES[movingHead.name] ?: error("no DMX device for ${movingHead.name}")
//        val movingHeadBuffer = Shenzarpy(getDmxBuffer(baseChannel, 16))
//
//        movingHeadManager.listen(movingHead) { updated ->
//            println("Moving head ${movingHead.name} moved to ${updated.x} ${updated.y}")
//            movingHeadBuffer.pan = updated.x / 255f
//            movingHeadBuffer.tilt = updated.y / 255f
//        }
//
//        return movingHeadBuffer
//    }

    fun getShowState(): ShowState {
        return showState
    }

    fun switchTo(newShowState: ShowState) {
        this.showState = newShowState
        activeSetChanged = true
    }

    /** @return `true` if a frame was rendered and should be sent to fixtures. */
    fun renderNextFrame(): Boolean {
        return if (fixtureManager.hasActiveRenderPlan()) {
            renderManager.draw()
            true
        } else false
    }

    fun onSelectedPatchesChanged() {
        activeSetChanged = true
    }

    fun housekeeping(): Boolean {
        if (activeSetChanged) {
            fixtureManager.activeSetChanged(openShow.activeSet())
        }

        return fixtureManager.maybeUpdateRenderPlans { id, dataSource ->
            openShow.dataFeeds.getBang(dataSource, "data feed")
        }
    }

    fun shutDown() {
        // TODO gadgetManager.clear()
    }

    fun release() {
        openShow.release()
    }

    data class FixturesChanges(val added: Collection<FixtureReceiver>, val removed: Collection<FixtureReceiver>)

    interface FixtureReceiver {
        val fixture: Fixture
        fun send(pixels: Pixels)
    }

    companion object {
        val logger = Logger("ShowRunner")
    }
}

class RenderPlan(val programs: List<Pair<LinkedPatch, GlslProgram>>, val activeSet: ActiveSet)