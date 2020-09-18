package baaahs

import baaahs.gadgets.Slider
import baaahs.net.Network
import baaahs.net.TestNetwork
import ext.TestCoroutineContext
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.serialization.json.*
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.expect

@InternalCoroutinesApi
class GadgetManagerTest {
    lateinit var httpServer: Network.HttpServer
    private lateinit var testCoroutineContext: TestCoroutineContext

    @BeforeTest
    fun setUp() {
        httpServer = TestNetwork().link("test").startHttpServer(1234)
        testCoroutineContext = TestCoroutineContext("network")
    }

    @Test
    fun forInitialGadgets_sync_shouldSyncGadgetsAndState() {
        val pubSub = PubSub.Server(httpServer, testCoroutineContext)
        val gadgetManager = GadgetManager(pubSub)

        val first = Slider("first")
        val second = Slider("second").apply { value = .234f }
        val third = Slider("third").apply { value = .345f }
        gadgetManager.sync(listOf("first" to first, "second" to second, "third" to third))

        val expectedActiveGadgets = buildJsonArray {
            add(buildJsonObject {
                put("name", "first")
                put("gadget", buildJsonObject {
                    put("type", "baaahs.Core:Slider")
                    put("title", "first")
                    put("initialValue", 1.0)
                    put("minValue", 0.0)
                    put("maxValue", 1.0)
                    put("stepValue", JsonNull)
                })
                put("topicName", "/gadgets/first")
            })
            add(buildJsonObject {
                put("name", "second")
                put(
                    "gadget",
                    buildJsonObject {
                        put("type", "baaahs.Core:Slider")
                        put(
                            "title",
                            "second"
                        )
                        put("initialValue", 1.0)
                        put(
                            "minValue",
                            0.0
                        )
                        put("maxValue", 1.0)
                        put(
                            "stepValue",
                            JsonNull
                        )
                    })
                put("topicName", "/gadgets/second")
            })
            add(buildJsonObject {
                put("name", "third")
                put(
                    "gadget",
                    buildJsonObject {
                        put("type", "baaahs.Core:Slider")
                        put(
                            "title",
                            "third"
                        )
                        put("initialValue", 1.0)
                        put(
                            "minValue",
                            0.0
                        )
                        put("maxValue", 1.0)
                        put(
                            "stepValue",
                            JsonNull
                        )
                    })
                put("topicName", "/gadgets/third")
            })
        }
        expect(expectedActiveGadgets) { pubSub.getTopicInfo("activeGadgets")!!.jsonValue }

        expect(buildJsonObject { }) { pubSub.getTopicInfo("/gadgets/first")!!.jsonValue }
        expect(buildJsonObject { put("value", 0.234) }) { pubSub.getTopicInfo("/gadgets/second")!!.jsonValue }
        expect(buildJsonObject { put("value", 0.345) }) { pubSub.getTopicInfo("/gadgets/third")!!.jsonValue }

        expect("{first={}, second={value=0.234}, third={value=0.345}}") {
            gadgetManager.getGadgetsState().toString()
        }
    }

    @Test
    fun forDifferentGadgets_sync_shouldSyncGadgetsAndState() {
        val pubSub = PubSub.Server(httpServer, testCoroutineContext)
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
            buildJsonArray {
                add(buildJsonObject {
                    put("name", "first")
                    put("gadget", buildJsonObject {
                        put("type", "baaahs.Core:Slider")
                        put("title", "uno")
                        put("initialValue", 1.0)
                        put("minValue", 0.0)
                        put("maxValue", 1.0)
                        put("stepValue", JsonNull)
                    })
                    put("topicName", "/gadgets/first")
                })
                add(buildJsonObject {
                    put("name", "second")
                    put("gadget", buildJsonObject {
                        put("type", "baaahs.Core:Slider")
                        put("title", "dos")
                        put("initialValue", 1.0)
                        put("minValue", 0.0)
                        put("maxValue", 1.0)
                        put("stepValue", JsonNull)
                    })
                    put("topicName", "/gadgets/second")
                })
                add(buildJsonObject {
                    put("name", "third")
                    put("gadget", buildJsonObject {
                        put("type", "baaahs.Core:Slider")
                        put("title", "tres")
                        put("initialValue", 1.0)
                        put("minValue", 0.0)
                        put("maxValue", 1.0)
                        put("stepValue", JsonNull)
                    })
                    put("topicName", "/gadgets/third")
                })

            }
        ) { activeGadgetsListener.events.map { json.parseToJsonElement(it) }.only() }
        expect(buildJsonObject {}) { pubSub.getTopicInfo("/gadgets/first")!!.jsonValue }
        expect(buildJsonObject { put("value", 0.123) }) { pubSub.getTopicInfo("/gadgets/second")!!.jsonValue }
        expect(buildJsonObject {}) { pubSub.getTopicInfo("/gadgets/third")!!.jsonValue }
    }

    @Test
    fun forSameGadgetsWithDifferentState_sync_shouldSyncOnlyGadgetState() {
        val pubSub = PubSub.Server(httpServer, testCoroutineContext)
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

        expect(buildJsonObject { put("value", 0.123) }) { pubSub.getTopicInfo("/gadgets/first")!!.jsonValue }
        expect(buildJsonObject { put("value", 0.234) }) { pubSub.getTopicInfo("/gadgets/second")!!.jsonValue }
        expect(buildJsonObject { put("value", 0.345) }) { pubSub.getTopicInfo("/gadgets/third")!!.jsonValue }

        expect("{first={value=0.123}, second={value=0.234}, third={value=0.345}}") {
            gadgetManager.getGadgetsState().toString()
        }

        // New gadget should receive updates from PubSub.
        pubSub.getTopicInfo("/gadgets/third")!!.listeners_TEST_ONLY.first().onUpdate(buildJsonObject {
            put(
                "value",
                .987
            )
        })
        expect(0.987f) { thirdB.value }
    }

    class Listener : PubSub.Listener(PubSub.Origin("test origin")) {
        val events = mutableListOf<String>()

        override fun onUpdate(data: JsonElement) {
            events.add(data.toString())
        }
    }
}