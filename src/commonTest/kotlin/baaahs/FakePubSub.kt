package baaahs

import baaahs.sim.FakeNetwork
import ext.TestCoroutineContext
import kotlinx.coroutines.InternalCoroutinesApi

@InternalCoroutinesApi
class FakePubSub(
    val testCoroutineContext: TestCoroutineContext = TestCoroutineContext("network"),
    val network: FakeNetwork = FakeNetwork(0, testCoroutineContext)
) : PubSub() {
    val serverNetwork = network.link("server")
    val server = listen(serverNetwork.startHttpServer(1234), testCoroutineContext)
    val serverLog = mutableListOf<String>()

    fun client(clientName: String): TestClient = TestClient(clientName)

    inner class TestClient(
        clientName: String
    ) : Client(network.link(clientName), serverNetwork.myAddress, 1234) {
        val log = mutableListOf<String>()
    }
}

class SpyPubSub : PubSub.Endpoint() {
    private val onUpdates = mutableMapOf<String, (Any?) -> Unit>()
    val events = arrayListOf<String>()

    fun receive(topicId: String, value: Any?) {
        onUpdates[topicId]!!.invoke(value)
    }

    override fun <T> openChannel(topic: PubSub.Topic<T>, initialValue: T, onUpdate: (T) -> Unit): PubSub.Channel<T> {
        addEvent("openChannel(${topic.name}) := $initialValue")

        onUpdates[topic.name] = onUpdate as (Any?) -> Unit

        return object : PubSub.Channel<T> {
            override fun onChange(t: T) {
                addEvent("${topic.name}.onChange($t)")
            }

            override fun replaceOnUpdate(onUpdate: (T) -> Unit) {
                addEvent("${topic.name}.replaceOnUpdate()")
            }

            override fun unsubscribe() {
                addEvent("${topic.name}.close()")
            }
        }
    }

    fun clearEvents() {
        events.clear()
        println("Clear events ---")
    }

    private fun addEvent(s: String) {
        events.add(s)
        println("event: $s")
    }

}