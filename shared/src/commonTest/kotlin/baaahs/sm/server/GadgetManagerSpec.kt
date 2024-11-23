package baaahs.sm.server

import baaahs.FakeClock
import baaahs.TestRig
import baaahs.gadgets.Slider
import baaahs.kotest.value
import io.kotest.common.ExperimentalKotest
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.test.testCoroutineScheduler
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive

@ExperimentalKotest
@OptIn(ExperimentalCoroutinesApi::class, InternalCoroutinesApi::class)
class GadgetManagerSpec : DescribeSpec({
//    describe<GadgetManager>.config(coroutineTestScope = true) {

    describe("GadgetManager").config(coroutineTestScope = true) {
        val testRig by value { TestRig() }
        val pinkyMainScope by value { testRig.pinkyScope }
        val clock by value { FakeClock() }
        val gadgetManager by value { GadgetManager(testRig.server, clock, pinkyMainScope) }

        it("should make gadget updates on pinky main scope") {
            val serverSideSlider = Slider("slider", initialValue = 1f)
            val client = testRig.client1
            gadgetManager.registerGadget(serverSideSlider.title, serverSideSlider)
            serverSideSlider.position = .75f
            var clientSlider: MutableMap<String, JsonElement> = mutableMapOf()
            val channel = client.subscribe(gadgetManager.topicFor(serverSideSlider.title)) {
                clientSlider.clear()
                clientSlider.putAll(it)
            }
            testRig.fakePinkyDispatcher.advanceUntilIdle()

            serverSideSlider.position shouldBe .75f
            channel.onChange(mapOf("position" to JsonPrimitive(.5f)))
            serverSideSlider.position shouldBe .75f
            testCoroutineScheduler.advanceUntilIdle()
            serverSideSlider.position shouldBe .75f
            testRig.fakePinkyDispatcher.advanceUntilIdle()
            serverSideSlider.position shouldBe .5f
        }
    }
})