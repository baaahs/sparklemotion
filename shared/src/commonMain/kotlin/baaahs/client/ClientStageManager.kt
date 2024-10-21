package baaahs.client

import baaahs.*
import baaahs.gl.Toolchain
import baaahs.scene.OpenScene
import baaahs.scene.SceneProvider
import baaahs.show.Feed
import baaahs.show.Show
import baaahs.show.ShowState
import baaahs.show.live.ActivePatchSet
import baaahs.show.live.OpenShow
import kotlinx.serialization.json.JsonElement

class ClientStageManager(
    toolchain: Toolchain,
    private val pubSub: PubSub.Client,
    sceneProvider: SceneProvider
) : BaseShowPlayer(toolchain, SceneProviderWithFallback(sceneProvider)) {
    private val gadgets: MutableMap<String, ClientGadget> = mutableMapOf()
    private val listeners = mutableListOf<Listener>()
    private var openShow: OpenShow? = null
    val activePatchSet: ActivePatchSet
        get() = openShow!!.getSnapshot().activePatchSet

    private fun checkForChanges() {
        listeners.forEach { it.onPatchSetChanged() }
    }

    override fun openShow(show: Show, showState: ShowState?): OpenShow {
        return super.openShow(show, showState)
            .also {
                openShow = it
                checkForChanges()
            }
    }

    override fun <T : Gadget> registerGadget(id: String, gadget: T, controlledFeed: Feed?) {
        gadgets[id] = ClientGadget(id, pubSub, gadget)
        super.registerGadget(id, gadget, controlledFeed)
    }

    override fun <T : Gadget> useGadget(id: String): T {
        @Suppress("UNCHECKED_CAST")
        return gadgets.getBang(id, "gadget").gadget as T
    }

    fun addListener(listener: Listener) {
        listeners.add(listener)
    }

    fun removeListener(listener: Listener) {
        listeners.remove(listener)
    }

    override fun onActivePatchSetMayHaveChanged() {
        checkForChanges()
    }

    private inner class ClientGadget(
        id: String,
        pubSub: PubSub.Client,
        val gadget: Gadget
    ) {
        private val channel: PubSub.Channel<Map<String, JsonElement>>

        init {
            val gadgetListener = this::onGadgetChange
            gadget.listen(gadgetListener)

            val topic =
                PubSub.Topic("/gadgets/$id", GadgetDataSerializer)

            channel = pubSub.subscribe(topic) { json ->
                gadget.withoutTriggering(gadgetListener) {
                    gadget.applyState(json)
                }
            }
        }

        // GadgetListener callback.
        fun onGadgetChange(g: Gadget) {
            channel.onChange(g.state)
        }
    }

    class SceneProviderWithFallback(private val delegate: SceneProvider) : SceneProvider by delegate {
        override val openScene: OpenScene
            get() = openSceneOrFallback
    }

    interface Listener {
        fun onPatchSetChanged()
    }
}