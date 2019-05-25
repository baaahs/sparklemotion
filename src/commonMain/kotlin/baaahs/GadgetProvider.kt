package baaahs

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.serializer

class GadgetProvider(private val pubSub: PubSub.Server) {
    val jsonParser = Json(JsonConfiguration.Stable)
    private val activeGadgets = mutableListOf<GadgetData>()
    private val activeGadgetChannel = pubSub.publish(Topics.activeGadgets, activeGadgets) {  }

    private val gadgets = mutableMapOf<Gadget, GadgetChannel>()
    private var nextGadgetId = 1

    fun <G : Gadget> getGadget(gadget: G): G {
        val gadgetId = nextGadgetId++

        val topic =
            PubSub.Topic("/gadgets/${gadget::class.simpleName}/$gadgetId", String.serializer())

        val channel = pubSub.publish(topic, gadget.toJson().toString()) { updated ->
            gadget.setFromJson(jsonParser.parseJson(updated))
        }
        gadgets[gadget] = GadgetChannel(topic, channel)

        activeGadgets.add(GadgetData(gadget, topic.name))

        return gadget
    }

    fun clear() {
        gadgets.values.forEach { gadgetChannel -> gadgetChannel.channel.unsubscribe() }
        gadgets.clear()
        activeGadgets.clear()
    }

    fun sync() {
        activeGadgetChannel.onChange(activeGadgets)
    }

    class GadgetChannel(val topic: PubSub.Topic<String>, val channel: PubSub.Channel<String>)
}