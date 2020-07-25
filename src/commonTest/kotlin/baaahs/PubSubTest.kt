package baaahs

import baaahs.sim.FakeNetwork
import ext.Second
import ext.TestCoroutineContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.nullable
import kotlinx.serialization.builtins.serializer
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.expect

@InternalCoroutinesApi
class PubSubTest {
    val testCoroutineContext = TestCoroutineContext("network")
    val network = FakeNetwork(0, coroutineContext = testCoroutineContext)

    val serverNetwork = network.link("server")
    val server = PubSub.listen(serverNetwork.startHttpServer(1234))
    val serverLog = mutableListOf<String>()

    val client1Network = network.link("client1")
    val client1 = PubSub.Client(client1Network, serverNetwork.myAddress, 1234, CoroutineScope(testCoroutineContext))
    val client1Log = mutableListOf<String>()

    val client2Network = network.link("client2")
    val client2 = PubSub.connect(client2Network, serverNetwork.myAddress, 1234)
    val client2Log = mutableListOf<String>()

    val topic1 = PubSub.Topic("/one", String.serializer())

    @AfterTest
    fun tearDown() {
        assertEquals(emptyList(), testCoroutineContext.exceptions)
    }

    @Test
    fun subscribersShouldBeNotifiedOfChanges() {
        val client1TopicObserver = client1.subscribe(topic1) { client1Log.add("topic1 changed: $it") }

        val serverTopicObserver = server.publish(topic1, "value") {
            serverLog.add("topic1 changed: $it")
        }

        val client2TopicObserver = client2.subscribe(topic1) { client2Log.add("topic1 changed: $it") }
        testCoroutineContext.runAll()

        serverLog.assertEmpty()
        client1Log.assertContents("topic1 changed: value")
        client2Log.assertContents("topic1 changed: value")

        serverTopicObserver.onChange("new value")
        testCoroutineContext.runAll()

        serverLog.assertEmpty()
        client1Log.assertContents("topic1 changed: new value")
        client2Log.assertContents("topic1 changed: new value")

        client2TopicObserver.onChange("from client 2")
        testCoroutineContext.runAll()

        serverLog.assertContents("topic1 changed: from client 2")
        client1Log.assertContents("topic1 changed: from client 2")
        client2Log.assertEmpty()
    }

    @Test
    fun earlySubscribersShouldBeNotifiedOfChangesToo() {
        val serverTopicObserver = server.publish(topic1, "first value") {
            serverLog.add("topic1 changed: $it")
        }

        serverTopicObserver.onChange("second value")

        client1.subscribe(topic1) { client1Log.add("topic1 changed: $it") }
        testCoroutineContext.runAll()

        serverLog.assertEmpty()
        client1Log.assertContents("topic1 changed: second value")
    }

    @Test
    fun clientUnsubscribeShouldPreventFutureUpdatesAndUnregisterFromServerWhenAppropriate() {
        val client1Log2 = mutableListOf<String>()

        val serverTopicObserver = server.publish(topic1, "value") {
            serverLog.add("topic1 changed: $it")
        }

        val serverTopicInfo = server.getTopicInfo(topic1.name)!!
        assertEquals(1, serverTopicInfo.listeners_TEST_ONLY.size) // assume

        val client1TopicObserver1 = client1.subscribe(topic1) { client1Log.add("topic1 changed1: $it") }
        val client1TopicObserver2 = client1.subscribe(topic1) { client1Log2.add("topic1 changed2: $it") }
        testCoroutineContext.runAll()

        client1Log.assertContents("topic1 changed1: value")
        client1Log2.assertContents("topic1 changed2: value")
        assertEquals(2, serverTopicInfo.listeners_TEST_ONLY.size) // sanity check

        client1TopicObserver1.unsubscribe()
        serverTopicObserver.onChange("new value")
        testCoroutineContext.runAll()

        client1Log.assertEmpty()
        client1Log2.assertContents("topic1 changed2: new value")
        assertEquals(2, serverTopicInfo.listeners_TEST_ONLY.size)

        client1TopicObserver2.unsubscribe()
        serverTopicObserver.onChange("another new value")
        testCoroutineContext.runAll()

        client1Log.assertEmpty()
        client1Log2.assertEmpty()
        assertEquals(1, serverTopicInfo.listeners_TEST_ONLY.size)
    }

    @Test
    fun beforeConnectionIsMade_isConnectedShouldBeFalse() {
        expect(false) { client1.isConnected }
        testCoroutineContext.runAll()
        expect(true) { client1.isConnected }
    }

    @Test
    fun whenWebsocketIsConnected_isConnectedShouldNotifyListeners() {
        client1.addStateChangeListener { client1Log.add("isConnected was changed to ${client1.isConnected}") }
        testCoroutineContext.runAll()
        client1Log.assertContents("isConnected was changed to true")
    }

    @Test
    fun whenConnectionIsReset_ShouldNotifyListenerOfStateChange() {
        testCoroutineContext.runAll()
        expect(true) { client1.isConnected }

        client1.addStateChangeListener { client1Log.add("isConnected was changed to ${client1.isConnected}") }

        // trigger a connection reset
        client1Network.webSocketListeners[0].reset(client1Network.tcpConnections[0])

        client1Log.assertContents("isConnected was changed to false")
    }

    @Test
    fun whenConnectionIsReset_attemptToReconnectEverySecond() {
        expect(1) { client1Network.tcpConnections.size }

        // trigger a connection reset
        client1Network.webSocketListeners[0].reset(client1Network.tcpConnections[0])
        expect(false) { client1.isConnected }

        expect(1) { client1Network.tcpConnections.size }

        // don't attempt a new connection until a second has passed
        testCoroutineContext.triggerActions()
        expect(1) { client1Network.tcpConnections.size }

        testCoroutineContext.advanceTimeBy(2, Second())
        testCoroutineContext.triggerActions()

        // assert that there was a new outgoing connection
        expect(2) { client1Network.tcpConnections.size }
        expect(true) { client1.isConnected }
    }

    @Test
    fun whenConnectionIsReset_resubscribeToTopics() {
        val client1TopicObserver = client1.subscribe(topic1) { client1Log.add("topic1 changed: $it") }

        val serverTopicObserver = server.publish(topic1, "value") {
            serverLog.add("topic1 changed: $it")
        }

        expect(1) { client1Network.tcpConnections.size }
        testCoroutineContext.triggerActions()
        client1Log.assertContents("topic1 changed: value")

        // trigger a connection reset
        client1Network.webSocketListeners[0].reset(client1Network.tcpConnections[0])
        expect(false) { client1.isConnected }

        expect(1) { client1Network.tcpConnections.size }

        // don't attempt a new connection until a second has passed
        testCoroutineContext.triggerActions()
        expect(1) { client1Network.tcpConnections.size }

        testCoroutineContext.advanceTimeBy(2, Second())
        testCoroutineContext.triggerActions()

        // assert that there was a new outgoing connection
        expect(2) { client1Network.tcpConnections.size }
        expect(true) { client1.isConnected }

        serverTopicObserver.onChange("new value")
        testCoroutineContext.triggerActions()
        client1Log.assertContents("topic1 changed: new value")
    }

    @Test
    fun withNullableClassSerializer_shouldWork() {
        val nullableTopic = PubSub.Topic("/x", Thing.serializer().nullable)

        val serverTopicObserver = server.publish(nullableTopic, Thing("value")) {
            serverLog.add("topic1 changed: ${it?.string}")
        }

        client1.subscribe(nullableTopic) {
            client1Log.add("topic1 changed: ${it?.string}")
        }
        testCoroutineContext.runAll()
        client1Log.assertContents("topic1 changed: value")

        serverTopicObserver.onChange(null)
        testCoroutineContext.runAll()
        client1Log.assertContents("topic1 changed: null")
    }

    @Serializable
    data class Thing(val string: String)
}
