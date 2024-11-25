package baaahs.sm.server

import baaahs.Gadget
import baaahs.GadgetDataSerializer
import baaahs.PubSub
import baaahs.util.Clock
import baaahs.util.Logger
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.concurrent.Volatile
import kotlin.coroutines.CoroutineContext

class GadgetManager(
    private val pubSub: PubSub.Server,
    private val clock: Clock,
    private val pinkyMainContext: CoroutineContext
) {
    private val gadgets: MutableMap<String, Gadget> = mutableMapOf()

    @Volatile
    var lastUserInteraction = clock.now()

    fun <T : Gadget> registerGadget(id: String, gadget: T) {
        val topic =
            PubSub.Topic("/gadgets/$id", GadgetDataSerializer)
        val channel = pubSub.publish(topic, gadget.state) { updated ->
            CoroutineScope(pinkyMainContext + CoroutineName("Gadget Update Handler")).launch {
                lastUserInteraction = clock.now()

                logger.debug { "Gadget \"$id\" updated to: $updated" }
                gadget.applyState(updated)
            }
        }
        gadget.listen { channel.onChange(it.state) }
        gadgets[id] = gadget
    }

    fun <T : Gadget> useGadget(id: String): T {
        @Suppress("UNCHECKED_CAST")
        return (gadgets[id]
            ?: error("no such gadget \"$id\" among [${gadgets.keys.sorted()}]")) as T
    }

    companion object {
        private val logger = Logger<GadgetManager>()
    }
}