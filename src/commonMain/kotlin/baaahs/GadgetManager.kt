package baaahs

import kotlinx.serialization.json.JsonElement

class GadgetManager(private val pubSub: PubSub.Server) {
    private val activeGadgets = mutableListOf<GadgetData>()
    private val activeGadgetChannel = pubSub.publish(Topics.activeGadgets, activeGadgets) { }

    private val gadgets = mutableMapOf<String, GadgetInfo>()
    private val priorRequestedGadgets = mutableListOf<Pair<String, Gadget>>()
    private var nextGadgetId = 1

    fun clear() {
        gadgets.values.forEach { gadgetChannel -> gadgetChannel.channel.unsubscribe() }
        gadgets.clear()
        activeGadgets.clear()
    }

    fun sync(
        requestedGadgets: List<Pair<String, Gadget>>,
        restoreState: Map<String, Map<String, JsonElement>> = emptyMap()
    ) {
        // First, update state on new gadgets.
        requestedGadgets.forEach { (name, gadget) ->
            restoreState[name]?.let { state -> gadget.state.putAll(state) }
        }

        if (priorRequestedGadgets == requestedGadgets) {
            requestedGadgets.zip(priorRequestedGadgets).forEach { (new, old) ->
                val (name, oldGadget) = old
                val newGadget = new.second

                val gadgetInfo = gadgets[name]!!
                gadgetInfo.channel.replaceOnUpdate { updated -> newGadget.state.putAll(updated) }
                gadgetInfo.gadgetData.gadget = newGadget

                if (oldGadget.state != newGadget.state) {
                    gadgetInfo.channel.onChange(newGadget.state)
                }
            }
        } else {
            println("Gadgets don't match!")
            println("old: ${priorRequestedGadgets}")
            println("new: ${requestedGadgets}")
            activeGadgets.clear()
            requestedGadgets.forEach { (name, gadget) ->
                val gadgetId = nextGadgetId++

                val topic =
                    PubSub.Topic("/gadgets/${gadget::class.simpleName}/$gadgetId", GadgetDataSerializer)

                val channel = pubSub.publish(topic, gadget.state) { updated -> gadget.state.putAll(updated) }
                val gadgetData = GadgetData(name, gadget, topic.name)
                activeGadgets.add(gadgetData)
                gadgets[name] = GadgetInfo(topic, channel, gadgetData)
            }
            activeGadgetChannel.onChange(activeGadgets)
        }

        priorRequestedGadgets.clear()
        priorRequestedGadgets.addAll(requestedGadgets)
    }

    fun getGadgetsState(): Map<String, Map<String, JsonElement>> {
        return activeGadgets.associate { gadgetData ->
            gadgetData.name to gadgetData.gadget.state
        }
    }

    internal fun findGadget(name: String) = gadgets[name]?.gadgetData?.gadget
    internal fun findGadgetInfo(name: String) = gadgets[name]

    class GadgetInfo(
        val topic: PubSub.Topic<Map<String, JsonElement>>,
        val channel: PubSub.Channel<Map<String, JsonElement>>,
        val gadgetData: GadgetData
    )
}