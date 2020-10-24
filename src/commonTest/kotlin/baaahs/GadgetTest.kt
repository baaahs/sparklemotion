package baaahs

import baaahs.sim.FakeNetwork
import ext.TestCoroutineContext
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.serialization.builtins.serializer
import kotlin.test.Test

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

    class SomeGadget(initialValue: Int) : Gadget() {
        override val title: String get() = TODO("not implemented")

        var value: Int by updatable("value", initialValue, Int.serializer())
    }
}