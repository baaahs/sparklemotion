package baaahs.sm.server

import baaahs.Gadget
import baaahs.GadgetDataSerializer
import baaahs.PubSub
import baaahs.ui.Observable
import baaahs.util.Clock
import kotlin.coroutines.CoroutineContext

class GadgetManager(
    private val pubSub: PubSub.Server,
    private val clock: Clock,
    private val coroutineContext: CoroutineContext
) : Observable() {
    private val gadgets: MutableMap<String, Gadget> = mutableMapOf()
    var lastUserInteraction = clock.now()

    fun <T : Gadget> registerGadget(id: String, gadget: T, ) {
        val topic =
            PubSub.Topic("/gadgets/$id", GadgetDataSerializer)
        val channel = pubSub.publish(topic, gadget.state) { updated ->
            lastUserInteraction = clock.now()

            gadget.applyState(updated)
            notifyChanged()
        }
        val gadgetChannelListener: (Gadget) -> Unit = { channel.onChange(it.state) }
        gadget.listen(gadgetChannelListener)
        gadgets[id] = gadget
    }

    fun <T : Gadget> useGadget(id: String): T {
        @Suppress("UNCHECKED_CAST")
        return (gadgets[id]
            ?: error("no such gadget \"$id\" among [${gadgets.keys.sorted()}]")) as T
    }
}