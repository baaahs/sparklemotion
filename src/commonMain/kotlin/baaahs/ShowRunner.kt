package baaahs

import baaahs.OpenShow.OpenScene.OpenPatchSet
import baaahs.dmx.Shenzarpy
import baaahs.glsl.GlslRenderer
import baaahs.glsl.RenderSurface
import baaahs.model.Model
import baaahs.model.MovingHead
import baaahs.show.Show

class ShowRunner(
    private val model: Model<*>,
    private var show: Show,
    private var showState: ShowState,
    private val showManager: ShowManager,
    private val beatSource: BeatSource,
    private val dmxUniverse: Dmx.Universe,
    private val movingHeadManager: MovingHeadManager,
    internal val clock: Clock,
    private val glslRenderer: GlslRenderer,
    pubSub: PubSub.Server
) {
    private var openShow: OpenShow = OpenShow(show, showManager)
    private var nextPatchSet: OpenPatchSet? = showState.findPatchSet(openShow)

    private val showStateChannel = pubSub.publish(Topics.showState, showState) { showState ->
        this.showState = showState
        nextPatchSet = showState.findPatchSet(openShow)
    }

    private var currentPatchSet: OpenPatchSet? = null
    private var currentRenderPlan: RenderPlan? = null
    private val changedSurfaces = mutableListOf<SurfacesChanges>()
    private var totalSurfaceReceivers = 0

    internal val renderSurfaces: MutableMap<Surface, RenderSurface> = hashMapOf()

    private var requestedGadgets: LinkedHashMap<String, Gadget> = linkedMapOf()

    // TODO: Get beat sync working again.
    // Continuous from [0.0 ... 3.0] (0 is first beat in a measure, 3 is last)
    val currentBeat: Float
        get() = beatSource.getBeatData().beatWithinMeasure(clock)

    private fun getDmxBuffer(baseChannel: Int, channelCount: Int): Dmx.Buffer =
        dmxUniverse.writer(baseChannel, channelCount)

    // TODO: Get moving heads working again.
    fun getMovingHeadBuffer(movingHead: MovingHead): MovingHead.Buffer {
        val baseChannel = Config.DMX_DEVICES[movingHead.name] ?: error("no DMX device for ${movingHead.name}")
        val movingHeadBuffer = Shenzarpy(getDmxBuffer(baseChannel, 16))

        movingHeadManager.listen(movingHead) { updated ->
            println("Moving head ${movingHead.name} moved to ${updated.x} ${updated.y}")
            movingHeadBuffer.pan = updated.x / 255f
            movingHeadBuffer.tilt = updated.y / 255f
        }

        return movingHeadBuffer
    }

    fun surfacesChanged(addedSurfaces: Collection<SurfaceReceiver>, removedSurfaces: Collection<SurfaceReceiver>) {
        changedSurfaces.add(SurfacesChanges(ArrayList(addedSurfaces), ArrayList(removedSurfaces)))
    }

    fun nextFrame(dontProcrastinate: Boolean = true) {
        // Unless otherwise instructed, = generate and send the next frame right away,
        // then perform any housekeeping tasks immediately afterward, to avoid frame lag.
        if (dontProcrastinate) housekeeping()

        currentRenderPlan?.let {
            it.render(glslRenderer)
            sendFrame()
        }

        if (!dontProcrastinate) housekeeping()
    }

    private fun housekeeping() {
        var remapToSurfaces = false
        for ((added, removed) in changedSurfaces) {
            logger.info { "ShowRunner surfaces changed! ${added.size} added, ${removed.size} removed" }
            for (receiver in removed) removeReceiver(receiver)
            for (receiver in added) addReceiver(receiver)
            remapToSurfaces = true
        }
        changedSurfaces.clear()

        // Maybe switch to a new show.
        nextPatchSet?.let { startingPatchSet ->
            switchTo(startingPatchSet)

            currentPatchSet = nextPatchSet
            nextPatchSet = null
            remapToSurfaces = true
        }

        if (remapToSurfaces) {
            renderSurfaces.values.forEach { it.useProgram(null) }

            currentRenderPlan?.programs?.forEach { (patchMapping, program) ->
                renderSurfaces.forEach { (surface, renderSurface) ->
                    if (patchMapping.matches(surface)) {
                        renderSurface.useProgram(program)
                    }
                }
            }
        }
    }

    fun switchTo(newShow: Show) {
        openShow = showManager.swapAndRelease(openShow, newShow)
        show = newShow

        nextPatchSet = showState.findPatchSet(openShow)
    }

    private fun switchTo(newPatchSet: OpenPatchSet) {
        renderSurfaces.values.forEach { it.release() }

        currentRenderPlan = prepare(newPatchSet)

        logger.info {
            "New show ${newPatchSet.title} created; " +
                    "${renderSurfaces.size} surfaces " +
                    "and ${requestedGadgets.size} gadgets"
        }

//        TODO gadgetManager.sync(requestedGadgets.toList(), gadgetsState)
        requestedGadgets.clear()
    }

    private fun prepare(newPatchSet: OpenPatchSet): RenderPlan {
        val glslContext = glslRenderer.gl
        return newPatchSet.createRenderPlan(glslContext)
    }

    private fun addReceiver(receiver: SurfaceReceiver) {
        val surface = receiver.surface
        val renderSurface = renderSurfaces.getOrPut(surface) {
            glslRenderer.addSurface(surface)
        }
        renderSurface.receivers.add(receiver)

        totalSurfaceReceivers++
    }

    private fun removeReceiver(receiver: SurfaceReceiver) {
        val surface = receiver.surface
        val renderSurface = renderSurfaces.get(surface)
            ?: throw IllegalStateException("huh? no SurfaceBinder for $surface")

        if (!renderSurface.receivers.remove(receiver)) {
            throw IllegalStateException("huh? receiver not registered for $surface")
        }

        if (renderSurface.receivers.isEmpty()) {
            glslRenderer.removeSurface(renderSurface)
            renderSurface.release()
            renderSurfaces.remove(surface)
        }

        totalSurfaceReceivers--
    }

    fun sendFrame() {
        renderSurfaces.values.forEach { renderSurface ->
//            if (shaderBuffers.size != 1) {
//                throw IllegalStateException("Too many shader buffers for ${surface.describe()}: $shaderBuffers")
//            }

            renderSurface.receivers.forEach { receiver ->
                // TODO: The send might return an error, at which point this receiver should be nuked
                // from the list of receivers for this surface. I'm not quite sure the best way to do
                // that so I'm leaving this note.
                receiver.send(renderSurface.pixels)
            }
        }

        dmxUniverse.sendFrame()
    }

    fun shutDown() {
        // TODO gadgetManager.clear()
    }

    data class SurfacesChanges(val added: Collection<SurfaceReceiver>, val removed: Collection<SurfaceReceiver>)

    interface SurfaceReceiver {
        val surface: Surface
        fun send(pixels: Pixels)
    }

    companion object {
        val logger = Logger("ShowRunner")
    }
}