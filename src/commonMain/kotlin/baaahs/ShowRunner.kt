package baaahs

import baaahs.dmx.Shenzarpy
import baaahs.glsl.GlslRenderer
import baaahs.shaders.GlslShader
import baaahs.shaders.IGlslShader

class ShowRunner(
    private val model: Model<*>,
    initialShow: Show,
    private val gadgetManager: GadgetManager,
    private val beatSource: BeatSource,
    private val dmxUniverse: Dmx.Universe,
    private val movingHeadManager: MovingHeadManager,
    internal val clock: Clock,
    private val glslRenderer: GlslRenderer
) : ShowContext {
    var nextShow: Show? = initialShow
    private var currentShow: Show? = null
    private var currentShowRenderer: Show.Renderer? = null
    private val changedSurfaces = mutableListOf<SurfacesChanges>()
    private var totalSurfaceReceivers = 0
    private var performedHousekeeping: Boolean = false

    override val allSurfaces: List<Surface> get() = surfaceBinders.keys.toList()
    override val allUnusedSurfaces: List<Surface> get() = allSurfaces.minus(
        surfaceBinders.values.filter { !it.hasBuffer() }.map { it.surface })

    override val allMovingHeads: List<MovingHead> get() = model.movingHeads

    private val surfaceBinders: MutableMap<Surface, SurfaceBinder> = hashMapOf()

    private var requestedGadgets: LinkedHashMap<String, Gadget> = linkedMapOf()

    private var shadersLocked = true
    private var gadgetsLocked = true

    // Continuous from [0.0 ... 3.0] (0 is first beat in a measure, 3 is last)
    override val currentBeat: Float
        get() = beatSource.getBeatData().beatWithinMeasure(clock)

    override fun getBeatSource(): BeatSource = beatSource

    private fun recordShader(surface: Surface, shaderBuffer: GlslShader.Buffer) {
        val surfaceBinder = surfaceBinders[surface] ?: throw IllegalStateException("unknown surface $surface")
        surfaceBinder.setBuffer(shaderBuffer)
    }

    /**
     * Obtain a shader buffer which can be used to control the illumination of a surface.
     *
     * @param surface The surface we're shading.
     * @param shader The type of shader.
     * @return A shader buffer of the appropriate type.
     */
    override fun getShaderBuffer(surface: Surface, shader: IGlslShader): GlslShader.Buffer {
        if (shadersLocked) throw IllegalStateException("Shaders can't be obtained during #nextFrame()")
        val buffer = shader.createBuffer(surface)
        recordShader(surface, buffer)
        return buffer
    }

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

    fun nextFrame() {
        if (!performedHousekeeping) housekeeping() else performedHousekeeping = false

        // Always generate and send the next frame right away, then perform any housekeeping tasks immediately
        // afterward, to avoid frame lag.
        currentShowRenderer?.let {
            it.nextFrame()
            glslRenderer.draw()
            sendFrame()
        }

        housekeeping()
        performedHousekeeping = true
    }

    private fun housekeeping() {
        for ((added, removed) in changedSurfaces) {
            println("ShowRunner surfaces changed! ${added.size} added, ${removed.size} removed")
            for (receiver in removed) removeReceiver(receiver)
            for (receiver in added) addReceiver(receiver)

            if (nextShow == null) {
                shadersLocked = false
                try {
                    currentShowRenderer?.surfacesChanged(added.map { it.surface }, removed.map { it.surface })

                    logger.info {
                        "Show ${currentShow!!.name} updated; " +
                                "${surfaceBinders.size} surfaces"
                    }
                } catch (e: Show.RestartShowException) {
                    // Show doesn't support changing surfaces, just restart it cold.
                    nextShow = currentShow ?: nextShow
                }
                shadersLocked = true
            }
        }
        changedSurfaces.clear()

        // Maybe switch to a new show.
        nextShow?.let { startingShow ->
            createShowRenderer(startingShow)

            currentShow = nextShow
            nextShow = null
        }
    }

    private fun createShowRenderer(startingShow: Show) {
        surfaceBinders.values.forEach { it.releaseBuffer() }

        val restartingSameShow = nextShow == currentShow
        val gadgetsState = if (restartingSameShow) gadgetManager.getGadgetsState() else emptyMap()

        unlockShadersAndGadgets {
            currentShowRenderer = startingShow.createRenderer(model, this)
        }

        logger.info {
            "New show ${startingShow.name} created; " +
                    "${surfaceBinders.size} surfaces " +
                    "and ${requestedGadgets.size} gadgets"
        }

        gadgetManager.sync(requestedGadgets.toList(), gadgetsState)
        requestedGadgets.clear()
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
        val binder = surfaceBinders.getOrPut(surface) {
            SurfaceBinder(surface, glslRenderer.addSurface(surface))
        }
        binder.receivers.add(receiver)

        totalSurfaceReceivers++
    }

    private fun removeReceiver(receiver: SurfaceReceiver) {
        val surface = receiver.surface
        val surfaceBinder = surfaceBinders.get(surface)
            ?: throw IllegalStateException("huh? no SurfaceBinder for $surface")

        if (!surfaceBinder.receivers.remove(receiver)) {
            throw IllegalStateException("huh? receiver not registered for $surface")
        }

        if (surfaceBinder.receivers.isEmpty()) {
            glslRenderer.removeSurface(surfaceBinder.renderSurface)
            surfaceBinder.release()
            surfaceBinders.remove(surface)
        }

        totalSurfaceReceivers--
    }

    fun sendFrame() {
        surfaceBinders.values.forEach { surfaceBinder ->
//            if (shaderBuffers.size != 1) {
//                throw IllegalStateException("Too many shader buffers for ${surface.describe()}: $shaderBuffers")
//            }

            surfaceBinder.receivers.forEach { receiver ->
                // TODO: The send might return an error, at which point this receiver should be nuked
                // from the list of receivers for this surface. I'm not quite sure the best way to do
                // that so I'm leaving this note.
                receiver.send(surfaceBinder.renderSurface.pixels)
            }
        }

        dmxUniverse.sendFrame()
    }

    fun shutDown() {
        gadgetManager.clear()
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