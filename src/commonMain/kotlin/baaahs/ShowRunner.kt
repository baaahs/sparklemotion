package baaahs

import baaahs.dmx.Shenzarpy
import baaahs.glshaders.GlslProgram
import baaahs.glshaders.Patch
import baaahs.glsl.GlslRenderer
import baaahs.glsl.RenderSurface
import baaahs.model.Model
import baaahs.model.MovingHead
import baaahs.show.DataSource
import baaahs.show.PatchMapping
import baaahs.show.PatchSet

class ShowRunner(
    private val model: Model<*>,
    initialPatchSet: PatchSet,
    private val show: baaahs.show.Show,
    private val showResources: ShowResources,
    private val beatSource: BeatSource,
    private val dmxUniverse: Dmx.Universe,
    private val movingHeadManager: MovingHeadManager,
    internal val clock: Clock,
    private val glslRenderer: GlslRenderer,
    pubSub: PubSub.Server
) : ShowContext {
    private var nextPatchSet: PatchSet? = initialPatchSet

    var showState = ShowState(0, show.scenes.map { 0 })
    private val showStateChannel = pubSub.publish(Topics.showState, showState) { showState ->
        this.showState = showState
        nextPatchSet = showState.findPatchSet(show)
    }

    private var currentPatchSet: PatchSet? = null
    private var currentRenderPlan: RenderPlan? = null
    private val changedSurfaces = mutableListOf<SurfacesChanges>()
    private var totalSurfaceReceivers = 0

    override val allSurfaces: List<Surface> get() = renderSurfaces.keys.toList()
    override val allMovingHeads: List<MovingHead> get() = model.movingHeads

    internal val renderSurfaces: MutableMap<Surface, RenderSurface> = hashMapOf()

    private var requestedGadgets: LinkedHashMap<String, Gadget> = linkedMapOf()

    private var shadersLocked = true
    private var gadgetsLocked = true

    // Continuous from [0.0 ... 3.0] (0 is first beat in a measure, 3 is last)
    override val currentBeat: Float
        get() = beatSource.getBeatData().beatWithinMeasure(clock)

    override fun getBeatSource(): BeatSource = beatSource

    private fun getDmxBuffer(baseChannel: Int, channelCount: Int): Dmx.Buffer =
        dmxUniverse.writer(baseChannel, channelCount)

    override fun getMovingHeadBuffer(movingHead: MovingHead): MovingHead.Buffer {
        if (shadersLocked) throw IllegalStateException("Moving heads can't be obtained during #nextFrame()")
        val baseChannel = Config.DMX_DEVICES[movingHead.name] ?: error("no DMX device for ${movingHead.name}")
        val movingHeadBuffer = Shenzarpy(getDmxBuffer(baseChannel, 16))

        movingHeadManager.listen(movingHead) { updated ->
            println("Moving head ${movingHead.name} moved to ${updated.x} ${updated.y}")
            movingHeadBuffer.pan = updated.x / 255f
            movingHeadBuffer.tilt = updated.y / 255f
        }

        return movingHeadBuffer
    }

    /**
     * Obtain a gadget that can be used to receive input from a user. The gadget will be displayed in the show's UI.
     *
     * @param name Symbolic name for this gadget; must be unique within the show.
     * @param gadget The gadget to display.
     */
    override fun <T : Gadget> getGadget(name: String, gadget: T): T {
        if (gadgetsLocked) throw IllegalStateException("Gadgets can't be obtained during #nextFrame()")
        val oldValue = requestedGadgets.put(name, gadget)
        if (oldValue != null) throw IllegalStateException("Gadget names must be unique ($name)")
        return gadget
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
            println("ShowRunner surfaces changed! ${added.size} added, ${removed.size} removed")
            for (receiver in removed) removeReceiver(receiver)
            for (receiver in added) addReceiver(receiver)

            if (nextPatchSet == null) {
                shadersLocked = false
                try {
// TODO                   currentShowRenderer?.surfacesChanged(added.map { it.surface }, removed.map { it.surface })

                    logger.info {
                        "Show ${currentPatchSet!!.title} updated; " +
                                "${renderSurfaces.size} surfaces"
                    }
                } catch (e: Show.RestartShowException) {
                    // Show doesn't support changing surfaces, just restart it cold.
                    nextPatchSet = currentPatchSet ?: nextPatchSet
                }
                shadersLocked = true
            }
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

    fun switchTo(scene: Int, patchSet: Int) {
        nextPatchSet = show.scenes[scene].patchSets[patchSet]
    }

    private fun switchTo(newPatchSet: PatchSet) {
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

    private fun prepare(newPatchSet: PatchSet): RenderPlan {
        val glslContext = glslRenderer.gl

        val activeDataSources = mutableSetOf<String>()
        val programs = newPatchSet.patchMappings.map { patchMapping ->
            val patch = Patch(showResources.shaders, show.dataSources, patchMapping.links)
            val program = patch.compile(glslContext) { dataSource: DataSource ->
                val dataSourceFeed = showResources.dataSources[dataSource.id]
                    ?: error(unknown("datasource", dataSource.id, showResources.dataSources.keys))
                activeDataSources.add(dataSource.id)
                dataSourceFeed
            }
            patchMapping to program
        }
        return RenderPlan(programs)
    }

    class RenderPlan(val programs: List<Pair<PatchMapping, GlslProgram>>) {
        fun render(glslRenderer: GlslRenderer) {
            glslRenderer.draw()
        }
    }

    private fun unlockShadersAndGadgets(fn: () -> Unit) {
        shadersLocked = false
        gadgetsLocked = false

        try {
            fn()
        } finally {
            shadersLocked = true
            gadgetsLocked = true
        }
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