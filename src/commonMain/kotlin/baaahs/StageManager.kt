package baaahs

import baaahs.glshaders.GlslProgram
import baaahs.glshaders.OpenPatch
import baaahs.glshaders.Plugins
import baaahs.glsl.GlslContext
import baaahs.glsl.GlslRenderer
import baaahs.model.ModelInfo
import com.soywiz.klock.DateTime

class StageManager(
    plugins: Plugins,
    override val glslContext: GlslContext,
    val pubSub: PubSub.Server,
    modelInfo: ModelInfo
) : BaseShowResources(plugins, modelInfo) {
    private var showRunner: ShowRunner? = null
    private val gadgets: MutableMap<String, GadgetManager.GadgetInfo> = mutableMapOf()
    var lastUserInteraction = DateTime.now()

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

    fun switchTo(newShowRunner: ShowRunner?) {
        showRunner?.release()
        releaseUnused()
        showRunner = newShowRunner
    }

    fun renderAndSendNextFrame() {
        showRunner?.renderAndSendNextFrame()
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
