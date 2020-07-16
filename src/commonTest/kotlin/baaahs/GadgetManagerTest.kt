package baaahs

import baaahs.gadgets.Slider
import baaahs.net.Network
import baaahs.net.TestNetwork
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.json
import kotlinx.serialization.json.jsonArray
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.expect

class GadgetManagerTest {
    lateinit var httpServer: Network.HttpServer

    @BeforeTest
    fun setUp() {
        httpServer = TestNetwork().link("test").startHttpServer(1234)
    }

    @Test
    fun forInitialGadgets_sync_shouldSyncGadgetsAndState() {
        val pubSub = PubSub.Server(httpServer)
        val gadgetManager = GadgetManager(pubSub)

        val first = Slider("first")
        val second = Slider("second").apply { value = .234f }
        val third = Slider("third").apply { value = .345f }
        gadgetManager.sync(listOf("first" to first, "second" to second, "third" to third))

        val expectedActiveGadgets = jsonArray {
            +json { "name" to "first"; "gadget" to json { "type" to "baaahs.gadgets.Slider"; "title" to "first"; "initialValue" to 1.0; "minValue" to 0.0; "maxValue" to 1.0; "stepValue" to JsonNull }; "topicName" to "/gadgets/first" }
            +json { "name" to "second"; "gadget" to json { "type" to "baaahs.gadgets.Slider"; "title" to "second"; "initialValue" to 1.0; "minValue" to 0.0; "maxValue" to 1.0; "stepValue" to JsonNull }; "topicName" to "/gadgets/second" }
            +json { "name" to "third"; "gadget" to json { "type" to "baaahs.gadgets.Slider"; "title" to "third"; "initialValue" to 1.0; "minValue" to 0.0; "maxValue" to 1.0; "stepValue" to JsonNull }; "topicName" to "/gadgets/third" }
        }
        expect(expectedActiveGadgets) { pubSub.getTopicInfo("activeGadgets")!!.jsonValue }

        expect(json { }) { pubSub.getTopicInfo("/gadgets/first")!!.jsonValue }
        expect(json { "value" to 0.234 }) { pubSub.getTopicInfo("/gadgets/second")!!.jsonValue }
        expect(json { "value" to 0.345 }) { pubSub.getTopicInfo("/gadgets/third")!!.jsonValue }

        expect("{first={}, second={value=0.234}, third={value=0.345}}") {
            gadgetManager.getGadgetsState().toString()
        }
    }

    @Test
    fun forDifferentGadgets_sync_shouldSyncGadgetsAndState() {
        val pubSub = PubSub.Server(httpServer)
        val gadgetManager = GadgetManager(pubSub)

        val firstA = Slider("first")
        val secondA = Slider("second")
        val thirdA = Slider("third")
        gadgetManager.sync(listOf("first" to firstA, "second" to secondA, "third" to thirdA))

        val activeGadgetsListener = Listener()
        pubSub.getTopicInfo("activeGadgets")!!.addListener(activeGadgetsListener)
        expect(1) { activeGadgetsListener.events.size }
        activeGadgetsListener.events.clear()

        val firstB = Slider("uno")
        val secondB = Slider("dos").apply { value = 0.123f }
        val thirdB = Slider("tres")
        gadgetManager.sync(listOf("first" to firstB, "second" to secondB, "third" to thirdB))

        expect(
            jsonArray {
                +json { "name" to "first"; "gadget" to json { "type" to "baaahs.gadgets.Slider"; "title" to "uno"; "initialValue" to 1.0; "minValue" to 0.0; "maxValue" to 1.0; "stepValue" to JsonNull}; "topicName" to "/gadgets/first"}
                +json { "name" to "second"; "gadget" to json { "type" to "baaahs.gadgets.Slider"; "title" to "dos"; "initialValue" to 1.0; "minValue" to 0.0; "maxValue" to 1.0; "stepValue" to JsonNull}; "topicName" to "/gadgets/second"}
                +json { "name" to "third"; "gadget" to json { "type" to "baaahs.gadgets.Slider"; "title" to "tres"; "initialValue" to 1.0; "minValue" to 0.0; "maxValue" to 1.0; "stepValue" to JsonNull}; "topicName" to "/gadgets/third"}

            }
        ) { activeGadgetsListener.events.map { json.parseJson(it) }.only() }
        expect(json {}) { pubSub.getTopicInfo("/gadgets/first")!!.jsonValue }
        expect(json { "value" to 0.123 }) { pubSub.getTopicInfo("/gadgets/second")!!.jsonValue }
        expect(json {}) { pubSub.getTopicInfo("/gadgets/third")!!.jsonValue }
    }

    @Test
    fun forSameGadgetsWithDifferentState_sync_shouldSyncOnlyGadgetState() {
        val pubSub = PubSub.Server(httpServer)
        val gadgetManager = GadgetManager(pubSub)

        val firstA = Slider("first")
        val secondA = Slider("second")
        val thirdA = Slider("third").apply { value = .345f }
        gadgetManager.sync(listOf("first" to firstA, "second" to secondA, "third" to thirdA))

        val activeGadgetsListener = Listener()
        pubSub.getTopicInfo("activeGadgets")!!.addListener(activeGadgetsListener)
        expect(1) { activeGadgetsListener.events.size }
        activeGadgetsListener.events.clear()

        val firstListener = Listener()
        pubSub.getTopicInfo("/gadgets/first")!!.addListener(firstListener)
        firstListener.events.clear()

        val secondListener = Listener()
        pubSub.getTopicInfo("/gadgets/second")!!.addListener(secondListener)
        secondListener.events.clear()

        val thirdListener = Listener()
        pubSub.getTopicInfo("/gadgets/third")!!.addListener(thirdListener)
        thirdListener.events.clear()

        val firstB = Slider("first").apply { value = .123f }
        val secondB = Slider("second").apply { value = .234f }
        val thirdB = Slider("third").apply { value = .345f }
        gadgetManager.sync(listOf("first" to firstB, "second" to secondB, "third" to thirdB))

        // No change to active gadgets channel.
        expect(emptyList<String>()) { activeGadgetsListener.events }
        expect(listOf("{\"value\":0.123}")) { firstListener.events }
        expect(listOf("{\"value\":0.234}")) { secondListener.events }
        expect(emptyList<String>()) { thirdListener.events }

        expect(json { "value" to 0.123 }) { pubSub.getTopicInfo("/gadgets/first")!!.jsonValue }
        expect(json { "value" to 0.234 }) { pubSub.getTopicInfo("/gadgets/second")!!.jsonValue }
        expect(json { "value" to 0.345 }) { pubSub.getTopicInfo("/gadgets/third")!!.jsonValue }

        expect("{first={value=0.123}, second={value=0.234}, third={value=0.345}}") {
            gadgetManager.getGadgetsState().toString()
        }

        // New gadget should receive updates from PubSub.
        pubSub.getTopicInfo("/gadgets/third")!!.listeners_TEST_ONLY.first().onUpdate(json { "value" to .987 })
        expect(0.987f) { thirdB.value }
    }

    class Listener : PubSub.Listener(PubSub.Origin("test origin")) {
        val events = mutableListOf<String>()

        override fun onUpdate(data: JsonElement) {
            events.add(data.toString())
        }
    }
}