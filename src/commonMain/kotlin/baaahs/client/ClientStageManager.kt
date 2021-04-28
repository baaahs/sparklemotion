package baaahs.client

import baaahs.*
import baaahs.driverack.DriveRackManager
import baaahs.gl.Toolchain
import baaahs.model.Model
import baaahs.show.DataSource
import baaahs.show.Show
import baaahs.show.live.ActivePatchSet
import baaahs.show.live.OpenShow
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.json.JsonElement

class ClientStageManager(
    toolchain: Toolchain,
    private val pubSub: PubSub.Client,
    model: Model
) : BaseShowPlayer(toolchain, model) {
    private val driveRackManager = DriveRackManager(pubSub, Dispatchers.Default, false)

    private val gadgets: MutableMap<String, ClientGadget> = mutableMapOf()
    private val listeners = mutableListOf<Listener>()
    private var openShow: OpenShow? = null
    val activePatchSet: ActivePatchSet
        get() = openShow!!.activePatchSet()
    private var showContext: ShowContext? = null

    private fun checkForChanges() {
        listeners.forEach { it.onPatchSetChanged() }
    }

    override fun openShow(show: Show, showState: ShowState?): OpenShow {
        return openShow(show, showState, "-1")
    }

    fun openShow(show: Show, showState: ShowState?, driveRackId: String): OpenShow {
        return super.openShow(show, showState)
            .also {
                openShow = it
                showContext = ShowContext(it, driveRackId)
                checkForChanges()
            }
    }

    override fun <T : Gadget> registerGadget(id: String, gadget: T, controlledDataSource: DataSource?) {
        gadgets[id] = ClientGadget(id, pubSub, gadget)
        super.registerGadget(id, gadget, controlledDataSource)
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
                    gadget.state.putAll(json)
                    gadget.changed()
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

    private inner class ShowContext(val openShow: OpenShow, driveRackId: String) {
        private val driveRack = driveRackManager.subscribe(driveRackId, openShow.rackMap, initialBuses = setOf("A", "B"))
        private val driveRackMainBus = driveRack.getBus("A")
        private val driveRackAltBus = driveRack.getBus("B")

        init {
            openShow.channels.forEach { (_, channel) -> channel.attachBus(driveRackMainBus) }
            openShow.altChannels.forEach { (_, channel) -> channel.attachBus(driveRackAltBus) }
        }
    }
}