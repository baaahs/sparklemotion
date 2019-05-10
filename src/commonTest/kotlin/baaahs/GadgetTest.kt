package baaahs

import baaahs.gadgets.Slider
import baaahs.sim.FakeDmxUniverse
import baaahs.sim.FakeNetwork
import ext.TestCoroutineContext
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.serializer
import kotlin.test.Test
import kotlin.test.expect

@InternalCoroutinesApi
class GadgetTest {
    private val testCoroutineContext = TestCoroutineContext("network")
    private val network = FakeNetwork(0, coroutineContext = testCoroutineContext)
    private val serverLink = network.link()

    @Test
    fun whenGadgetValuesChange_shouldNotifyListeners() {
        val someGadget = SomeGadget(123)

        val log1 = mutableListOf<String>()
        val listener1 = object: GadgetListener { override fun onChanged(gadget: Gadget) { log1.add("changed") } }
        someGadget.listen(listener1)

        val log2 = mutableListOf<String>()
        val listener2 = object: GadgetListener { override fun onChanged(gadget: Gadget) { log2.add("changed") } }
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
        val pubSubServer = PubSub.listen(serverLink, 1234)
        pubSubServer.install(gadgetModule)

        val gadgetProvider = GadgetProvider(pubSubServer)
        val showRunner = ShowRunner(gadgetProvider, FakePinkyDisplay(), listOf(), object : Pinky.BeatProvider {
            override val beat: Float = 1.0f
            override var bpm: Float = 1.0f
        }, FakeDmxUniverse())

        val serverSlider = showRunner.getGadget(Slider("fader", .1234f))

        val pubSubClient = PubSub.connect(serverLink, serverLink.myAddress, 1234)
        pubSubClient.install(gadgetModule)

        var clientGadgets = listOf<Gadget>()
        GadgetDisplay(pubSubClient) { gadgets -> clientGadgets = gadgets.map { it.gadget }.toMutableList() }
        testCoroutineContext.runAll()

        expect(1) { clientGadgets.size }
        val clientSlider = clientGadgets[0] as Slider
        expect(.1234f) { clientSlider.value }

        clientSlider.value = .4321f
        testCoroutineContext.runAll()

        expect(.4321f) { serverSlider.value }
    }

    class SomeGadget(initialValue: Int) : Gadget() {
        var value: Int by watchForChanges(initialValue)

        override fun toJson(): JsonElement = TODO("SomeGadget.toJson not implemented")

        override fun setFromJson(jsonElement: JsonElement): Unit = TODO("SomeGadget.setFromJson not implemented")
    }

    class FakePinkyDisplay : PinkyDisplay {
        override fun listShows(showMetas: List<Show.MetaData>) {
        }

        override var brainCount = 0
        override var beat = 0
        override var color: Color? = Color.ORANGE
        override var onPrimaryColorChange: (() -> Unit)? = { }
        override var selectedShow: Show.MetaData? = null
    }
}