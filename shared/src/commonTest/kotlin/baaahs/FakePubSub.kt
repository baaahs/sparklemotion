package baaahs

import baaahs.sim.FakeNetwork
import ext.kotlinx_coroutines_test.TestCoroutineDispatcher
import ext.kotlinx_coroutines_test.TestCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi

@InternalCoroutinesApi
class FakePubSub(
    val dispatcher: TestCoroutineDispatcher = TestCoroutineDispatcher(),
    val network: FakeNetwork = FakeNetwork(0, CoroutineScope(dispatcher))
) : PubSub() {
    val serverNetwork = network.link("server")
    val server = listen(serverNetwork.createHttpServer(1234), CoroutineScope(dispatcher))
    val serverLog = mutableListOf<String>()

    fun client(clientName: String): TestClient = TestClient(clientName)

    inner class TestClient(
        clientName: String
    ) : Client(network.link(clientName), serverNetwork.myAddress, 1234) {
        val log = mutableListOf<String>()
    }
}

@ExperimentalCoroutinesApi
@InternalCoroutinesApi
class TestRig {
    val dispatcher = TestCoroutineDispatcher()
    val testCoroutineScope = TestCoroutineScope(dispatcher)
    val network = FakeNetwork(0, coroutineScope = testCoroutineScope)

    val serverNetwork = network.link("server")
    val server = PubSub.listen(serverNetwork.createHttpServer(1234), testCoroutineScope)
    val serverConnections =
        arrayListOf<PubSub.ConnectionFromClient>()
            .also { list ->
                server.listenForConnections { connection -> list.add(connection) }
            }

    val serverLog = mutableListOf<String>()

    val client1Network = network.link("client1")
    val client1 = PubSub.Client(client1Network, serverNetwork.myAddress, 1234, testCoroutineScope)
    val client1Log = mutableListOf<String>()

    val client2Network = network.link("client2")
    val client2 = PubSub.Client(client2Network, serverNetwork.myAddress, 1234, testCoroutineScope)
    val client2Log = mutableListOf<String>()

    fun cleanup() {
        testCoroutineScope.cleanupTestCoroutines()
    }
}

@InternalCoroutinesApi
class SpyPubSub(
    dispatcher: TestCoroutineDispatcher = TestCoroutineDispatcher()
) : PubSub.Endpoint() {
    override val commandChannels: PubSub.CommandChannels = PubSub.CommandChannels()

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

class StubPubSub : PubSub.Endpoint() {
    override val commandChannels: PubSub.CommandChannels
        get() = PubSub.CommandChannels()

    override fun <T> openChannel(topic: PubSub.Topic<T>, initialValue: T, onUpdate: (T) -> Unit): PubSub.Channel<T> {
        return object : PubSub.Channel<T> {
            override fun onChange(t: T) {}
            override fun replaceOnUpdate(onUpdate: (T) -> Unit) {}
            override fun unsubscribe() {}
        }
    }
}