package baaahs

import baaahs.gadgets.Slider
import baaahs.net.Network
import baaahs.net.TestNetwork
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.json
import kotlinx.serialization.json.jsonArray
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.expect

class GadgetManagerTest {
    lateinit var httpServer: Network.HttpServer

    @BeforeTest
    fun setUp() {
        httpServer = TestNetwork().link().startHttpServer(1234)
    }

    @Test
    fun forInitialGadgets_sync_shouldSyncGadgetsAndState() {
        val pubSub = PubSub.Server(httpServer).apply { install(gadgetModule) }
        val gadgetManager = GadgetManager(pubSub)

        val first = Slider("first")
        val second = Slider("second").apply { value = .234f }
        val third = Slider("third").apply { value = .345f }
        gadgetManager.sync(listOf("first" to first, "second" to second, "third" to third))

        val expectedActiveGadgets = jsonArray {
            +json { "name" to "first"; "gadget" to json { "type" to "baaahs.gadgets.Slider"; "name" to "first"; "initialValue" to 1.0; "minValue" to 0.0; "maxValue" to 1.0; "stepValue" to 0.01 }; "topicName" to "/gadgets/first" }
            +json { "name" to "second"; "gadget" to json { "type" to "baaahs.gadgets.Slider"; "name" to "second"; "initialValue" to 1.0; "minValue" to 0.0; "maxValue" to 1.0; "stepValue" to 0.01 }; "topicName" to "/gadgets/second" }
            +json { "name" to "third"; "gadget" to json { "type" to "baaahs.gadgets.Slider"; "name" to "third"; "initialValue" to 1.0; "minValue" to 0.0; "maxValue" to 1.0; "stepValue" to 0.01 }; "topicName" to "/gadgets/third" }
        }
        expect(expectedActiveGadgets) { pubSub.getTopicInfo("activeGadgets")!!.data }

        expect(json { }) { pubSub.getTopicInfo("/gadgets/first")!!.data }
        expect(json { "value" to 0.234 }) { pubSub.getTopicInfo("/gadgets/second")!!.data }
        expect(json { "value" to 0.345 }) { pubSub.getTopicInfo("/gadgets/third")!!.data }

        expect("{first={}, second={value=0.234}, third={value=0.345}}") {
            gadgetManager.getGadgetsState().toString()
        }
    }

    @Test
    fun forDifferentGadgets_sync_shouldSyncGadgetsAndState() {
        val pubSub = PubSub.Server(httpServer).apply { install(gadgetModule) }
        val gadgetManager = GadgetManager(pubSub)

        val firstA = Slider("first")
        val secondA = Slider("second")
        val thirdA = Slider("third")
        gadgetManager.sync(listOf("first" to firstA, "second" to secondA, "third" to thirdA))

        val activeGadgetsListener = Listener()
        pubSub.getTopicInfo("activeGadgets")!!.listeners.add(activeGadgetsListener)

        val firstB = Slider("uno")
        val secondB = Slider("dos").apply { value = 0.123f }
        val thirdB = Slider("tres")
        gadgetManager.sync(listOf("first" to firstB, "second" to secondB, "third" to thirdB))

        expect(
            listOf(
                "[" +
                        "{\"name\":\"first\",\"gadget\":{\"type\":\"baaahs.gadgets.Slider\",\"name\":\"uno\",\"initialValue\":1.0,\"minValue\":0.0,\"maxValue\":1.0,\"stepValue\":0.01},\"topicName\":\"/gadgets/first\"}," +
                        "{\"name\":\"second\",\"gadget\":{\"type\":\"baaahs.gadgets.Slider\",\"name\":\"dos\",\"initialValue\":1.0,\"minValue\":0.0,\"maxValue\":1.0,\"stepValue\":0.01},\"topicName\":\"/gadgets/second\"}," +
                        "{\"name\":\"third\",\"gadget\":{\"type\":\"baaahs.gadgets.Slider\",\"name\":\"tres\",\"initialValue\":1.0,\"minValue\":0.0,\"maxValue\":1.0,\"stepValue\":0.01},\"topicName\":\"/gadgets/third\"}" +
                        "]"
            )
        ) { activeGadgetsListener.events }
        expect(json {}) { pubSub.getTopicInfo("/gadgets/first")!!.data }
        expect(json { "value" to 0.123 }) { pubSub.getTopicInfo("/gadgets/second")!!.data }
        expect(json {}) { pubSub.getTopicInfo("/gadgets/third")!!.data }
    }

    @Test
    fun forSameGadgetsWithDifferentState_sync_shouldSyncOnlyGadgetState() {
        val pubSub = PubSub.Server(httpServer).apply { install(gadgetModule) }
        val gadgetManager = GadgetManager(pubSub)

        val firstA = Slider("first")
        val secondA = Slider("second")
        val thirdA = Slider("third").apply { value = .345f }
        gadgetManager.sync(listOf("first" to firstA, "second" to secondA, "third" to thirdA))

        val activeGadgetsListener = Listener()
        pubSub.getTopicInfo("activeGadgets")!!.listeners.add(activeGadgetsListener)
        val firstListener = Listener()
        pubSub.getTopicInfo("/gadgets/first")!!.listeners.add(firstListener)
        val secondListener = Listener()
        pubSub.getTopicInfo("/gadgets/second")!!.listeners.add(secondListener)
        val thirdListener = Listener()
        pubSub.getTopicInfo("/gadgets/third")!!.listeners.add(thirdListener)

        val firstB = Slider("first").apply { value = .123f }
        val secondB = Slider("second").apply { value = .234f }
        val thirdB = Slider("third").apply { value = .345f }
        gadgetManager.sync(listOf("first" to firstB, "second" to secondB, "third" to thirdB))

        // No change to active gadgets channel.
        expect(emptyList<String>()) { activeGadgetsListener.events }
        expect(listOf("{\"value\":0.123}")) { firstListener.events }
        expect(listOf("{\"value\":0.234}")) { secondListener.events }
        expect(emptyList<String>()) { thirdListener.events }

        expect(json { "value" to 0.123 }) { pubSub.getTopicInfo("/gadgets/first")!!.data }
        expect(json { "value" to 0.234 }) { pubSub.getTopicInfo("/gadgets/second")!!.data }
        expect(json { "value" to 0.345 }) { pubSub.getTopicInfo("/gadgets/third")!!.data }

        expect("{first={value=0.123}, second={value=0.234}, third={value=0.345}}") {
            gadgetManager.getGadgetsState().toString()
        }

        // New gadget should receive updates from PubSub.
        pubSub.getTopicInfo("/gadgets/third")!!.listeners.first().onUpdate(json { "value" to .987 })
        expect(0.987f) { thirdB.value }
    }

    class Listener : PubSub.Listener(PubSub.Origin()) {
        val events = mutableListOf<String>()

        override fun onUpdate(data: JsonElement) {
            events.add(data.toString())
        }
    }
}