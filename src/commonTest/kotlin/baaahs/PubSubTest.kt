package baaahs

import ext.TestCoroutineContext
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals

@InternalCoroutinesApi
class PubSubTest {
    @Serializable
    data class Data(val value: String)

    val testCoroutineContext = TestCoroutineContext("network")
    val network = FakeNetwork(0, coroutineContext = testCoroutineContext)

    val serverNetwork = network.link()
    val server = PubSub.listen(serverNetwork, 1234)
    val serverLog = mutableListOf<String>()

    val client1Network = network.link()
    val client1 = PubSub.connect(client1Network, serverNetwork.myAddress, 1234)
    val client1Log = mutableListOf<String>()

    val client2Network = network.link()
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
}

fun MutableList<String>.assertEmpty() {
    assertEquals(emptyList<String>(), this)
    this.clear()
}

fun MutableList<String>.assertContents(vararg s: String) {
    assertEquals(s.toList(), this)
    this.clear()
}
