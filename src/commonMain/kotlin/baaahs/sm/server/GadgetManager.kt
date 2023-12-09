package baaahs.sm.server

import baaahs.*
import baaahs.util.Clock
import kotlinx.serialization.modules.SerializersModule
import kotlin.coroutines.CoroutineContext

class GadgetManager(
    private val pubSub: PubSub.Server,
    private val clock: Clock,
    private val coroutineContext: CoroutineContext
) {
    private val gadgets: MutableMap<String, Gadget> = mutableMapOf()
    var lastUserInteraction = clock.now()

    init {
        val commands = ShowControlCommands(SerializersModule {})
        pubSub.listenOnCommandChannel(commands.setGadgetStateCommand) { command ->
            gadgets.getBang(command.id, "gadget")
                .applyState(command.state)
        }
    }

    fun <T : Gadget> registerGadget(id: String, gadget: T) {
        gadgets[id] = gadget

        val topic = PubSub.Topic("/gadgets/$id", GadgetDataSerializer)
        val channel = pubSub.publish(topic, gadget.state) { updated ->
            lastUserInteraction = clock.now()
            gadget.applyState(updated)
        }
        gadget.listen { channel.onChange(it.state) }
        gadgets[id] = gadget
    }

    fun <T : Gadget> useGadget(id: String): T {
        @Suppress("UNCHECKED_CAST")
        return (gadgets[id]
            ?: error("no such gadget \"$id\" among [${gadgets.keys.sorted()}]")) as T
    }
}