package baaahs.client

import baaahs.*
import baaahs.gl.Toolchain
import baaahs.midi.MidiDevices
import baaahs.scene.SceneProvider
import baaahs.show.Feed
import baaahs.show.Show
import baaahs.show.ShowState
import baaahs.show.live.ActivePatchSet
import baaahs.show.live.OpenShow
import baaahs.util.globalLaunch
import kotlinx.serialization.json.JsonElement

class ClientStageManager(
    toolchain: Toolchain,
    private val pubSub: PubSub.Client,
    sceneProvider: SceneProvider,
    private val midiDevices: MidiDevices
) : BaseShowPlayer(toolchain, sceneProvider) {
    private val gadgets: MutableMap<String, ClientGadget> = mutableMapOf()
    private val listeners = mutableListOf<Listener>()
    private var openShow: OpenShow? = null
    val activePatchSet: ActivePatchSet
        get() = openShow!!.buildActivePatchSet()

    init {
        globalLaunch {
            midiDevices.listTransmitters().forEach { midiTransmitter ->
                midiTransmitter.listen { midiMessage ->
                    println("MIDI from ${midiTransmitter.id}: $midiMessage")
                }
            }
        }
    }
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
                    checkForChanges()
                }
            }
        }

        // GadgetListener callback.
        fun onGadgetChange(g: Gadget) {
            channel.onChange(g.state)
            checkForChanges()
        }
    }

    interface Listener {
        fun onPatchSetChanged()
    }
}