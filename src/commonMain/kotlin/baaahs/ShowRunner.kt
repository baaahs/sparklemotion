package baaahs

import baaahs.shaders.CompositingMode
import baaahs.shaders.CompositorShader

class ShowRunner(
    private val model: SheepModel,
    initialShow: Show,
    private val gadgetManager: GadgetManager,
    private val beatSource: BeatSource,
    private val dmxUniverse: Dmx.Universe,
    private val clock: Clock
) {
    var nextShow: Show? = initialShow
    private var currentShow: Show? = null
    private var currentShowRenderer: Show.Renderer? = null
    private val changedSurfaces = mutableListOf<SurfacesChanges>()
    private var totalSurfaceReceivers = 0

    val allSurfaces: List<Surface> get() = surfaceReceivers.keys.toList()
    val allUnusedSurfaces: List<Surface> get() = allSurfaces.minus(shaderBuffers.keys)

    private val shaderBuffers: MutableMap<Surface, MutableList<Shader.Buffer>> = hashMapOf()

    private var requestedGadgets: LinkedHashMap<String, Gadget> = linkedMapOf()

    private var shadersLocked = true
    private var gadgetsLocked = true

    val currentBeat: Float
        get() = beatSource.getBeatData().beatWithinMeasure(clock)

    fun getBeatSource(): BeatSource = beatSource

    private fun recordShader(surface: Surface, shaderBuffer: Shader.Buffer) {
        val buffersForSurface = shaderBuffers.getOrPut(surface) { mutableListOf() }

        if (shaderBuffer is CompositorShader.Buffer) {
            if (!buffersForSurface.remove(shaderBuffer.bufferA)
                || !buffersForSurface.remove(shaderBuffer.bufferB)
            ) {
                throw IllegalStateException("Composite of unknown shader buffers!")
            }
        }

        buffersForSurface += shaderBuffer
    }

    /**
     * Obtain a shader buffer which can be used to control the illumination of a surface.
     *
     * @param surface The surface we're shading.
     * @param shader The type of shader.
     * @return A shader buffer of the appropriate type.
     */
    fun <B : Shader.Buffer> getShaderBuffer(surface: Surface, shader: Shader<B>): B {
        if (shadersLocked) throw IllegalStateException("Shaders can't be obtained during #nextFrame()")
        val buffer = shader.createBuffer(surface)
        recordShader(surface, buffer)
        return buffer
    }

    /**
     * Obtain a compositing shader buffer which can be used to blend two other shaders together.
     *
     * The shaders must already have been obtained using [getShaderBuffer].
     */
    fun getCompositorBuffer(
        surface: Surface,
        bufferA: Shader.Buffer,
        bufferB: Shader.Buffer,
        mode: CompositingMode = CompositingMode.NORMAL,
        fade: Float = 0.5f
    ): CompositorShader.Buffer {
        if (shadersLocked) throw IllegalStateException("Shaders can't be obtained during #nextFrame()")
        return CompositorShader(bufferA.shader, bufferB.shader)
            .createBuffer(bufferA, bufferB)
            .also {
                it.mode = mode
                it.fade = fade
                recordShader(surface, it)
            }
    }

    private fun getDmxBuffer(baseChannel: Int, channelCount: Int): Dmx.Buffer =
        dmxUniverse.writer(baseChannel, channelCount)

    fun getMovingHead(movingHead: SheepModel.MovingHead): Shenzarpy {
        if (shadersLocked) throw IllegalStateException("Moving heads can't be obtained during #nextFrame()")
        val baseChannel = Config.DMX_DEVICES[movingHead.name]!!
        return Shenzarpy(getDmxBuffer(baseChannel, 16))
    }

    /**
     * Obtain a gadget that can be used to receive input from a user. The gadget will be displayed in the show's UI.
     *
     * @param name Symbolic name for this gadget; must be unique within the show.
     * @param gadget The gadget to display.
     */
    fun <T : Gadget> getGadget(name: String, gadget: T): T {
        if (gadgetsLocked) throw IllegalStateException("Gadgets can't be obtained during #nextFrame()")
        val oldValue = requestedGadgets.put(name, gadget)
        if (oldValue != null) throw IllegalStateException("Gadget names must be unique ($name)")
        return gadget
    }

    fun surfacesChanged(addedSurfaces: Collection<SurfaceReceiver>, removedSurfaces: Collection<SurfaceReceiver>) {
        changedSurfaces.add(SurfacesChanges(ArrayList(addedSurfaces), ArrayList(removedSurfaces)))
    }

    fun nextFrame() {
        // Always generate and send the next frame right away, then perform any housekeeping tasks immediately
        // afterward, to avoid frame lag.
        currentShowRenderer?.let {
            it.nextFrame()
            send()
        }

        housekeeping()
    }

    private val surfaceReceivers = mutableMapOf<Surface, MutableList<SurfaceReceiver>>()

    private fun housekeeping() {
        for ((added, removed) in changedSurfaces) {
            println("ShowRunner surfaces changed! ${added.size} added, ${removed.size} removed")
            for (receiver in removed) removeReceiver(receiver)
            for (receiver in added) addReceiver(receiver)

            if (nextShow == null) {
                shadersLocked = false
                try {
                    currentShowRenderer?.surfacesChanged(added.map { it.surface }, removed.map { it.surface })

                    logger.info("Show ${currentShow!!.name} updated; " +
                        "${shaderBuffers.size} surfaces")
                } catch (e: Show.RestartShowException) {
                    // Show doesn't support changing surfaces, just restart it cold.
                    nextShow = currentShow ?: nextShow
                }
                shadersLocked = true
            }
        }
        changedSurfaces.clear()

        if (totalSurfaceReceivers > 0) {
            nextShow?.let { startingShow ->
                createShowRenderer(startingShow)

                currentShow = nextShow
                nextShow = null
            }
        }
    }

    private fun createShowRenderer(startingShow: Show) {
        shaderBuffers.clear()

        val restartingSameShow = nextShow == currentShow
        val gadgetsState = if (restartingSameShow) gadgetManager.getGadgetsState() else emptyMap()

        unlockShadersAndGadgets {
            currentShowRenderer = startingShow.createRenderer(model, this)
        }

        logger.info(
            "New show ${startingShow.name} created; " +
                "${shaderBuffers.size} surfaces " +
                "and ${requestedGadgets.size} gadgets"
        )

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
        receiversFor(receiver.surface).add(receiver)
        totalSurfaceReceivers++
    }

    private fun removeReceiver(receiver: SurfaceReceiver) {
        receiversFor(receiver.surface).remove(receiver)
        shaderBuffers.remove(receiver.surface)
        totalSurfaceReceivers--
    }

    private fun receiversFor(surface: Surface): MutableList<SurfaceReceiver> {
        return surfaceReceivers.getOrPut(surface) { mutableListOf() }
    }

    fun send() {
        shaderBuffers.forEach { (surface, shaderBuffers) ->
            if (shaderBuffers.size != 1) {
                throw IllegalStateException("Too many shader buffers for ${surface.describe()}: $shaderBuffers")
            }
            val shaderBuffer = shaderBuffers.first()

            receiversFor(surface).forEach { receiver ->
                receiver.sendFn(shaderBuffer)
            }
        }

        dmxUniverse.sendFrame()
    }

    fun shutDown() {
        gadgetManager.clear()
    }

    data class SurfacesChanges(val added: Collection<SurfaceReceiver>, val removed: Collection<SurfaceReceiver>)
    class SurfaceReceiver(val surface: Surface, val sendFn: (Shader.Buffer) -> Unit)
}