package baaahs

import com.soywiz.klock.DateTime
import kotlinx.serialization.json.JsonElement
import kotlin.random.Random

class GadgetManager(private val pubSub: PubSub.Server) {
    private val activeGadgets = mutableListOf<GadgetData>()
    private val activeGadgetChannel = pubSub.publish(Topics.activeGadgets, activeGadgets) { }

    private val gadgets = mutableMapOf<String, GadgetInfo>()
    private val priorRequestedGadgets = mutableListOf<Pair<String, Gadget>>()
    var lastUserInteraction = DateTime.now()

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
                gadgetInfo.gadgetData.gadget.unlisten(gadgetInfo.gadgetChannelListener)
                gadgetInfo.gadgetData.gadget = newGadget
                newGadget.listen(gadgetInfo.gadgetChannelListener)

                if (oldGadget.state != newGadget.state) {
                    gadgetInfo.channel.onChange(newGadget.state)
                }
            }
        } else {
            logger.debug {
                "Gadgets don't match!\n" +
                        "old: ${priorRequestedGadgets}\n" +
                        "new: ${requestedGadgets}"
            }
            activeGadgets.clear()
            requestedGadgets.forEach { (name, gadget) ->
                val topic =
                    PubSub.Topic("/gadgets/$name", GadgetDataSerializer)

                val channel = pubSub.publish(topic, gadget.state) { updated -> gadget.state.putAll(updated) }
                val gadgetData = GadgetData(name, gadget, topic.name)

                activeGadgets.add(gadgetData)
                val gadgetChannelListener: (Gadget) -> Unit = { gadget1 ->
                    lastUserInteraction = DateTime.now()
                    channel.onChange(gadget1.state)
                }
                gadgets[name] = GadgetInfo(topic, channel, gadgetData, gadgetChannelListener)
                gadget.listen(gadgetChannelListener)
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

    fun adjustSomething() {
        val priorLastUserInteraction = lastUserInteraction
        activeGadgets.forEach { gadgetData ->
            if (Random.nextFloat() < .1) {
                gadgetData.gadget.adjustALittleBit()
                gadgetData.gadget.changed()
            }
        }
        lastUserInteraction = priorLastUserInteraction
    }

    class GadgetInfo(
        val topic: PubSub.Topic<Map<String, JsonElement>>,
        val channel: PubSub.Channel<Map<String, JsonElement>>,
        val gadgetData: GadgetData,
        val gadgetChannelListener: (Gadget) -> Unit
    )

    companion object {
        val logger = Logger("GadgetManager")
    }
}