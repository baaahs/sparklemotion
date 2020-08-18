package baaahs

import baaahs.gadgets.Slider
import baaahs.sim.FakeNetwork
import ext.TestCoroutineContext
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.serialization.builtins.serializer
import kotlin.test.Test
import kotlin.test.expect

@InternalCoroutinesApi
class GadgetTest {
    private val testCoroutineContext = TestCoroutineContext("network")
    private val network = FakeNetwork(0, coroutineContext = testCoroutineContext)
    private val serverLink = network.link("test")
    private val clientLink = network.link("test")

    @Test
    fun whenGadgetValuesChange_shouldNotifyListeners() {
        val someGadget = SomeGadget(123)

        val log1 = mutableListOf<String>()
        val listener1: GadgetListener = { _ -> log1.add("changed") }
        someGadget.listen(listener1)

        val log2 = mutableListOf<String>()
        val listener2: GadgetListener = { _ -> log2.add("changed") }
        someGadget.listen(listener2)

        someGadget.value = 321
        log1.assertContents("changed")
        log2.assertContents("changed")

        someGadget.withoutTriggering(listener1) { someGadget.value = 789 }
        log1.assertEmpty()
        log2.assertContents("changed")
    }

    @Test
    fun testClientServerIntegration() {
        val pubSubServer = PubSub.listen(serverLink.startHttpServer(1234))
        val gadgetManager = GadgetManager(pubSubServer)
        val serverSlider = Slider("fader", .1234f)
        gadgetManager.sync(listOf("fader" to serverSlider))

        val pubSubClient = PubSub.connect(clientLink, serverLink.myAddress, 1234)
        val gadgetEvents = mutableListOf<String>()

        var clientAGadgets = listOf<Gadget>()
        GadgetDisplay(pubSubClient) { gadgets -> clientAGadgets = gadgets.map { it.gadget }.toMutableList() }
        gadgetManager.sync(listOf("fader" to serverSlider))
        testCoroutineContext.runAll()

        expect(1) { clientAGadgets.size }
        val clientASlider = clientAGadgets[0] as Slider
        expect(.1234f) { clientASlider.value }

        clientASlider.value = .4321f
        testCoroutineContext.runAll()

        expect(.4321f) { serverSlider.value }

        val client2Link = network.link("test")
        val pubSubClient2 = PubSub.connect(client2Link, serverLink.myAddress, 1234)

        var clientBGadgets = listOf<Gadget>()
        GadgetDisplay(pubSubClient2) { gadgets -> clientBGadgets = gadgets.map { it.gadget }.toMutableList() }
        testCoroutineContext.runAll()

        expect(1) { clientBGadgets.size }
        val clientBSlider = clientBGadgets[0] as Slider
        expect(.4321f) { clientBSlider.value }

        clientASlider.listen { gadget -> gadgetEvents.add("clientASlider updated to ${gadget.state}") }
        clientBSlider.listen { gadget -> gadgetEvents.add("clientBSlider updated to ${gadget.state}") }

        clientASlider.value = .8765f
        testCoroutineContext.runAll()

        expect(.8765f) { clientASlider.value }
        expect(.8765f) { clientBSlider.value }

        // UI must be notified of change.
        expect(
            listOf(
                "clientASlider updated to {value=0.8765}",
                "clientBSlider updated to {value=0.8765}"
            )
        ) { gadgetEvents.sorted() }
    }

    class SomeGadget(initialValue: Int) : Gadget() {
        override val title: String get() = TODO("not implemented")

        var value: Int by updatable("value", initialValue, Int.serializer())
    }
}