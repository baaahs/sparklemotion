package baaahs

import baaahs.gl.override
import baaahs.sim.FakeNetwork
import ch.tutteli.atrium.api.fluent.en_GB.containsExactly
import ch.tutteli.atrium.api.fluent.en_GB.toBe
import ch.tutteli.atrium.api.verbs.expect
import ext.TestCoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.nullable
import kotlinx.serialization.builtins.serializer
import org.spekframework.spek2.Spek
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.test.assertEquals

@InternalCoroutinesApi
object PubSubSpec : Spek({
    describe<PubSub> {
        val dispatcher by value { TestCoroutineDispatcher() }
        val testCoroutineScope by value { CoroutineScope(EmptyCoroutineContext + dispatcher) }
        val network by value { FakeNetwork(0, coroutineScope = testCoroutineScope) }

        val serverNetwork by value { network.link("server") }
        val server by value { PubSub.listen(serverNetwork.startHttpServer(1234), testCoroutineScope) }
        val serverLog by value { mutableListOf<String>() }

        val client1Network by value { network.link("client1") }
        val client1 by value { PubSub.Client(client1Network, serverNetwork.myAddress, 1234, testCoroutineScope) }
        val client1Log by value { mutableListOf<String>() }

        val client2Network by value { network.link("client2") }
        val client2 by value { PubSub.connect(client2Network, serverNetwork.myAddress, 1234) }
        val client2Log by value { mutableListOf<String>() }

        val topic1 by value { PubSub.Topic("/one", String.serializer()) }

        beforeEachTest {
            // Server needs to come up first, then client1 and client2.
            server.run {}
            client1.run {}
            client2.run {}
        }

//        TODO: This stopped working when we switched commands to use suspend functions. Huh?
//        afterEachTest {
//            expect(testCoroutineContext.exceptions).isEmpty()
//        }

        it("should notify subscribers of changes") {
            val client1TopicObserver = client1.subscribe(topic1) { client1Log.add("topic1 changed: $it") }

            val serverTopicObserver = server.publish(topic1, "value") {
                serverLog.add("topic1 changed: $it")
            }

            val client2TopicObserver = client2.subscribe(topic1) { client2Log.add("topic1 changed: $it") }
            dispatcher.runCurrent()

            serverLog.assertEmpty()
            client1Log.assertContents("topic1 changed: value")
            client2Log.assertContents("topic1 changed: value")

            serverTopicObserver.onChange("new value")
            dispatcher.runCurrent()

            serverLog.assertEmpty()
            client1Log.assertContents("topic1 changed: new value")
            client2Log.assertContents("topic1 changed: new value")

            client2TopicObserver.onChange("from client 2")
            dispatcher.runCurrent()

            serverLog.assertContents("topic1 changed: from client 2")
            client1Log.assertContents("topic1 changed: from client 2")
            client2Log.assertEmpty()
        }

        it("should notify early subscribers of changes too") {
            val serverTopicObserver = server.publish(topic1, "first value") {
                serverLog.add("topic1 changed: $it")
            }

            serverTopicObserver.onChange("second value")

            client1.subscribe(topic1) { client1Log.add("topic1 changed: $it") }
            dispatcher.runCurrent()

            serverLog.assertEmpty()
            client1Log.assertContents("topic1 changed: second value")
        }

        context("when a client unsubscribes") {
            it("should prevent future updates and unregister from server when appropriate") {
                val client1Log2 = mutableListOf<String>()

                val serverTopicObserver = server.publish(topic1, "value") {
                    serverLog.add("topic1 changed: $it")
                }

                val serverTopicInfo = server.getTopicInfo(topic1.name)!!
                assertEquals(1, serverTopicInfo.listeners_TEST_ONLY.size) // assume

                val client1TopicObserver1 = client1.subscribe(topic1) { client1Log.add("topic1 changed1: $it") }
                val client1TopicObserver2 = client1.subscribe(topic1) { client1Log2.add("topic1 changed2: $it") }
                dispatcher.runCurrent()

                client1Log.assertContents("topic1 changed1: value")
                client1Log2.assertContents("topic1 changed2: value")
                assertEquals(2, serverTopicInfo.listeners_TEST_ONLY.size) // sanity check

                client1TopicObserver1.unsubscribe()
                serverTopicObserver.onChange("new value")
                dispatcher.runCurrent()

                client1Log.assertEmpty()
                client1Log2.assertContents("topic1 changed2: new value")
                assertEquals(2, serverTopicInfo.listeners_TEST_ONLY.size)

                client1TopicObserver2.unsubscribe()
                serverTopicObserver.onChange("another new value")
                dispatcher.runCurrent()

                client1Log.assertEmpty()
                client1Log2.assertEmpty()
                assertEquals(1, serverTopicInfo.listeners_TEST_ONLY.size)
            }
        }

        context("before connection is made") {
            it("isConnected should return false") {
                expect(client1.isConnected).toBe(false)
                dispatcher.runCurrent()
                expect(client1.isConnected).toBe(true)
            }
        }

        context("when websocket is connected") {
            it("isConnected should notify listeners") {
                client1.addStateChangeListener { client1Log.add("isConnected was changed to ${client1.isConnected}") }
                dispatcher.runCurrent()
                client1Log.assertContents("isConnected was changed to true")
            }
        }

        context("when connection is reset") {
            it("should notify listener of state change") {
                dispatcher.runCurrent()
                expect(client1.isConnected).toBe(true)

                client1.addStateChangeListener { client1Log.add("isConnected was changed to ${client1.isConnected}") }

                // trigger a connection reset
                client1Network.webSocketListeners[0].reset(client1Network.tcpConnections[0])

                client1Log.assertContents("isConnected was changed to false")
            }

            it("should attempt to reconnect every second") {
                expect(client1Network.tcpConnections.size).toBe(1)

                // trigger a connection reset
                client1Network.webSocketListeners[0].reset(client1Network.tcpConnections[0])
                expect(client1.isConnected).toBe(false)

                expect(client1Network.tcpConnections.size).toBe(1)

                // don't attempt a new connection until a second has passed
                dispatcher.runCurrent()
                expect(client1Network.tcpConnections.size).toBe(1)

                dispatcher.advanceTimeBy(2000)
                dispatcher.runCurrent()

                // assert that there was a new outgoing connection
                expect(client1Network.tcpConnections.size).toBe(2)
                expect(client1.isConnected).toBe(true)
            }

            it("should resubscribe to topics") {
                val client1TopicObserver = client1.subscribe(topic1) { client1Log.add("topic1 changed: $it") }

                val serverTopicObserver = server.publish(topic1, "value") {
                    serverLog.add("topic1 changed: $it")
                }

                expect(client1Network.tcpConnections.size).toBe(1)
                dispatcher.runCurrent()
                client1Log.assertContents("topic1 changed: value")

                // trigger a connection reset
                client1Network.webSocketListeners[0].reset(client1Network.tcpConnections[0])
                expect(client1.isConnected).toBe(false)

                expect(client1Network.tcpConnections.size).toBe(1)

                // don't attempt a new connection until a second has passed
                dispatcher.runCurrent()
                expect(client1Network.tcpConnections.size).toBe(1)

                dispatcher.advanceTimeBy(2000)
                dispatcher.runCurrent()

                // assert that there was a new outgoing connection
                expect(client1Network.tcpConnections.size).toBe(2)
                expect(client1.isConnected).toBe(true)

                serverTopicObserver.onChange("new value")
                dispatcher.runCurrent()
                client1Log.assertContents("topic1 changed: new value")
            }
        }

        context("with nullable class serializer") {
            it("should work") {
                val nullableTopic = PubSub.Topic("/x", Thing.serializer().nullable)

                val serverTopicObserver = server.publish(nullableTopic, Thing("value")) {
                    serverLog.add("topic1 changed: ${it?.string}")
                }

                client1.subscribe(nullableTopic) {
                    client1Log.add("topic1 changed: ${it?.string}")
                }
                dispatcher.runCurrent()
                client1Log.assertContents("topic1 changed: value")

                serverTopicObserver.onChange(null)
                dispatcher.runCurrent()
                client1Log.assertContents("topic1 changed: null")
            }
        }

        context("commands") {
            val commandPort by value { PubSub.CommandPort("command", String.serializer(), String.serializer()) }
            val serverCommandHandler by value {
                val x: suspend (String) -> String = { s: String -> "reply for $s" }; x
            }
            val serverCommandChannel by value {
                server.listenOnCommandChannel(commandPort) { command: String -> serverCommandHandler(command) }
            }
            val clientCommandChannel by value { client1.commandSender(commandPort) }

            beforeEachTest {
                serverCommandChannel.run {}
                clientCommandChannel.run {}
            }

            context("when a client sends a command") {
                val result by value { arrayListOf<String>() }

                beforeEachTest {
                    testCoroutineScope.launch {
                        try {
                            result.add("response: " + clientCommandChannel("the command"))
                        } catch (e: Exception) {
                            result.add("error: ${e.message}")
                        }
                    }
                }

                it("invokes that command on the server and returns the response to the caller") {
                    dispatcher.runCurrent()
                    expect(result).containsExactly("response: reply for the command")
                }

                context("and the server throws an exception") {
                    override(serverCommandHandler) {
                        val x: suspend (String) -> String = { s: String -> throw RuntimeException("error for $s") }; x
                    }

                    it("returns the response to the caller") {
                        dispatcher.runCurrent()
                        expect(result).containsExactly("error: error for the command")
                    }
                }
            }
        }
    }
})

@Serializable
data class Thing(val string: String)
