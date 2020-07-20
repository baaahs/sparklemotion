package baaahs

import baaahs.glshaders.GlslProgram
import baaahs.glshaders.OpenPatch
import baaahs.glshaders.Plugins
import baaahs.glsl.GlslContext
import baaahs.glsl.GlslRenderer
import baaahs.model.ModelInfo
import baaahs.show.Show
import com.soywiz.klock.DateTime

class StageManager(
    plugins: Plugins,
    private val glslRenderer: GlslRenderer,
    val pubSub: PubSub.Server,
    modelInfo: ModelInfo,
    private val surfaceManager: SurfaceManager,
    private val dmxUniverse: Dmx.Universe,
    private val movingHeadManager: MovingHeadManager,
    private val clock: Clock
) : BaseShowResources(plugins, modelInfo) {
    val facade = Facade()
    override val glslContext: GlslContext
        get() = glslRenderer.gl
    private val showStateChannel = pubSub.publish(Topics.showState, null) { showState ->
        if (showState != null) showRunner?.switchTo(showState)
    }
    private var showRunner: ShowRunner? = null
    private val gadgets: MutableMap<String, GadgetManager.GadgetInfo> = mutableMapOf()
    var lastUserInteraction = DateTime.now()

    private val showWithStateChannel: PubSub.Channel<ShowWithState?> =
        pubSub.publish(showWithStateTopic, showRunner?.getShowWithState()) { incomingShowWithState ->
            val newShow = incomingShowWithState?.show
            val newShowState = incomingShowWithState?.showState
            switchTo(newShow, newShowState)
        }

    override fun <T : Gadget> createdGadget(id: String, gadget: T) {
        val topic =
            PubSub.Topic("/gadgets/$id", GadgetDataSerializer)
        val channel = pubSub.publish(topic, gadget.state) { updated ->
            gadget.state.putAll(updated)
            lastUserInteraction = DateTime.now()
        }
        val gadgetChannelListener: (Gadget) -> Unit = { gadget1 ->
            channel.onChange(gadget1.state)
        }
        gadget.listen(gadgetChannelListener)
        val gadgetData = GadgetData(id, gadget, topic.name)
        gadgets[id] = GadgetManager.GadgetInfo(topic, channel, gadgetData, gadgetChannelListener)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : Gadget> useGadget(id: String): T {
        return (gadgets[id]?.gadgetData?.gadget
            ?: error("no such gadget \"$id\" among [${gadgets.keys.sorted()}]")) as T
    }

    fun switchTo(newShow: Show?, newShowState: ShowState? = newShow?.defaultShowState()) {
        val newShowRunner = newShow?.let {
            ShowRunner(newShow, newShowState, openShow(newShow), clock, glslRenderer, surfaceManager)
        }
        switchTo(newShowRunner)
    }

    private fun switchTo(newShowRunner: ShowRunner?) {
        showRunner?.release()
        releaseUnused()
        showRunner = newShowRunner
        val newShowWithState = newShowRunner?.getShowWithState()
        showWithStateChannel.onChange(newShowWithState)
        showStateChannel.onChange(newShowWithState?.showState)
        facade.notifyChanged()
    }

    fun renderAndSendNextFrame(dontProcrastinate: Boolean = true) {
        showRunner?.let { showRunner ->
            // Unless otherwise instructed, = generate and send the next frame right away,
            // then perform any housekeeping tasks immediately afterward, to avoid frame lag.
            if (dontProcrastinate) showRunner.housekeeping()

            if (showRunner.renderNextFrame()) {
                surfaceManager.sendFrame()
                dmxUniverse.sendFrame()
            }

            if (!dontProcrastinate) showRunner.housekeeping()
        }
    }

    fun shutDown() {
        showRunner?.release()
        showStateChannel.unsubscribe()
        showWithStateChannel.unsubscribe()
    }

    inner class Facade : baaahs.ui.Facade() {
        val currentShow: Show?
            get() = this@StageManager.showRunner?.show
    }
}

interface RefCounted {
    fun inUse(): Boolean
    fun use()
    fun release()
    fun onFullRelease()
}

class RefCounter : RefCounted {
    var refCount: Int = 0

    override fun inUse(): Boolean = refCount == 0

    override fun use() {
        refCount++
    }

    override fun release() {
        refCount--

        if (!inUse()) onFullRelease()
    }

    override fun onFullRelease() {
    }
}

class RenderPlan(val programs: List<Pair<OpenPatch, GlslProgram>>) {
    fun render(glslRenderer: GlslRenderer) {
        glslRenderer.draw()
    }
}
