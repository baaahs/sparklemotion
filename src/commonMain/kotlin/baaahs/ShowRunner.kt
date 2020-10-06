package baaahs

import baaahs.fixtures.Fixture
import baaahs.fixtures.FixtureManager
import baaahs.gl.glsl.CompilationException
import baaahs.gl.glsl.GlslException
import baaahs.gl.patch.AutoWirer
import baaahs.gl.patch.ContentType
import baaahs.gl.render.ModelRenderer
import baaahs.glsl.GuruMeditationError
import baaahs.show.ShaderChannel
import baaahs.show.Show
import baaahs.show.live.ActiveSet
import baaahs.show.live.OpenShow

class ShowRunner(
    val show: Show,
    initialShowState: ShowState? = null,
    private val openShow: OpenShow,
//    private val beatSource: BeatSource,
//    private val dmxUniverse: Dmx.Universe,
//    private val movingHeadManager: MovingHeadManager,
    internal val clock: Clock,
    private val modelRenderer: ModelRenderer,
    private val fixtureManager: FixtureManager,
    private val autoWirer: AutoWirer
) {
    private var showState: ShowState = initialShowState ?: openShow.getShowState()
    private var renderPlanNeedsRefresh: Boolean = true
    internal var currentRenderPlan: RenderPlan? = null

    private var requestedGadgets: LinkedHashMap<String, Gadget> = linkedMapOf()

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
        renderPlanNeedsRefresh = true
    }

    /** @return `true` if a frame was rendered and should be sent to fixtures. */
    fun renderNextFrame(): Boolean {
        return if (currentRenderPlan != null) {
            modelRenderer.draw()
            true
        } else false
    }

    fun onSelectedPatchesChanged() {
        renderPlanNeedsRefresh = true
    }

    fun housekeeping(): Boolean {
        var remapFixtures = fixtureManager.requiresRemap()

        // Maybe switch to a new show.
        if (renderPlanNeedsRefresh) {
            val renderPlanChanged = refreshRenderPlan()
            if (renderPlanChanged) {
                remapFixtures = true
            }

            renderPlanNeedsRefresh = false
        }

        if (remapFixtures) {
            fixtureManager.clearRenderPlans()

            currentRenderPlan?.let {
                fixtureManager.remap(it)
            }
        }

        return remapFixtures
    }

    /** @return `true` if `currentRenderPlan` changed. */
    private fun refreshRenderPlan(): Boolean {
        val activeSet = openShow.activeSet()
        return if (activeSet != currentRenderPlan?.activeSet) {
            currentRenderPlan = prepareRenderPlan(activeSet)

            logger.info {
                "New render plan created; ${currentRenderPlan?.programs?.size ?: 0} programs, " +
                        "${fixtureManager.getFixtureCount()} fixtures " +
                        "and ${requestedGadgets.size} gadgets"
            }
            true
        } else false


//        TODO gadgetManager.sync(requestedGadgets.toList(), gadgetsState)
//        requestedGadgets.clear()
    }

    private fun prepareRenderPlan(activeSet: ActiveSet): RenderPlan {
        try {
            val activePatchHolders = activeSet.getPatchHolders()
            println("active patches = ${activePatchHolders.map { it.title }}")

            val linkedPatches = autoWirer.merge(*activePatchHolders.toTypedArray()).mapValues { (_, portDiagram) ->
                portDiagram.resolvePatch(ShaderChannel.Main, ContentType.ColorStream)
            }
            val glslContext = modelRenderer.gl
            val activeDataSources = mutableSetOf<String>()
            val programs = linkedPatches.mapNotNull { (_, linkedPatch) ->
                linkedPatch?.let { it to it.createProgram(glslContext, openShow.dataFeeds) }
            }
            return RenderPlan(programs, activeSet)
        } catch (e: GlslException) {
            logger.error("Error preparing program", e)
            if (e is CompilationException) {
                e.source?.let { logger.info { it } }
            }
            return GuruMeditationError.createRenderPlan(modelRenderer.gl)
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