package baaahs

import baaahs.gl.override
import baaahs.kotest.value
import baaahs.rpc.CommandPort
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.spec.style.scopes.DescribeSpecContainerScope
import io.kotest.matchers.*
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldContainExactly
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.nullable
import kotlinx.serialization.builtins.serializer
import kotlin.test.assertEquals

@ExperimentalCoroutinesApi
@InternalCoroutinesApi
class PubSubSpec : DescribeSpec({
    describe<PubSub> {
        val testRig by value { TestRig() }
        val topic1 by value { PubSub.Topic("/one", String.serializer()) }

        beforeEach {
            // Server needs to come up first, then client1 and client2.
            testRig.server.run {}
            testRig.serverConnections.run {}
            testRig.client1.run {}
            testRig.client2.run {}
        }

//      TODO: This stopped working when we switched commands to use suspend functions. Huh?
//        afterEachTest { testRig.cleanup() }

        it("should notify subscribers of changes") {
            val client1TopicObserver =
                testRig.client1.subscribe(topic1) { testRig.client1Log.add("topic1 changed: $it") }

            val serverTopicObserver = testRig.server.publish(topic1, "value") {
                testRig.serverLog.add("topic1 changed: $it")
            }

            val client2TopicObserver =
                testRig.client2.subscribe(topic1) { testRig.client2Log.add("topic1 changed: $it") }
            testRig.dispatcher.runCurrent()

            testRig.serverLog.assertEmpty()
            testRig.client1Log.assertContents("topic1 changed: value")
            testRig.client2Log.assertContents("topic1 changed: value")

            serverTopicObserver.onChange("new value")
            testRig.dispatcher.runCurrent()

            testRig.serverLog.assertEmpty()
            testRig.client1Log.assertContents("topic1 changed: new value")
            testRig.client2Log.assertContents("topic1 changed: new value")

            client2TopicObserver.onChange("from client 2")
            testRig.dispatcher.runCurrent()

            testRig.serverLog.assertContents("topic1 changed: from client 2")
            testRig.client1Log.assertContents("topic1 changed: from client 2")
            testRig.client2Log.assertEmpty()
        }

        it("should notify early subscribers of changes too") {
            val serverTopicObserver = testRig.server.publish(topic1, "first value") {
                testRig.serverLog.add("topic1 changed: $it")
            }

            serverTopicObserver.onChange("second value")

            testRig.client1.subscribe(topic1) { testRig.client1Log.add("topic1 changed: $it") }
            testRig.dispatcher.runCurrent()

            testRig.serverLog.assertEmpty()
            testRig.client1Log.assertContents("topic1 changed: second value")
        }

        context("when a client unsubscribes") {
            it("should prevent future updates and unregister from server when appropriate") {
                val client1Log2 = mutableListOf<String>()

                val serverTopicObserver = testRig.server.publish(topic1, "value") {
                    testRig.serverLog.add("topic1 changed: $it")
                }

                val serverTopicInfo = testRig.server.getTopicInfo(topic1.name)!!
                assertEquals(1, serverTopicInfo.listeners_TEST_ONLY.size) // assume

                val client1TopicObserver1 =
                    testRig.client1.subscribe(topic1) { testRig.client1Log.add("topic1 changed1: $it") }
                val client1TopicObserver2 =
                    testRig.client1.subscribe(topic1) { client1Log2.add("topic1 changed2: $it") }
                testRig.dispatcher.runCurrent()

                testRig.client1Log.assertContents("topic1 changed1: value")
                client1Log2.assertContents("topic1 changed2: value")
                assertEquals(2, serverTopicInfo.listeners_TEST_ONLY.size) // sanity check

                client1TopicObserver1.unsubscribe()
                serverTopicObserver.onChange("new value")
                testRig.dispatcher.runCurrent()

                testRig.client1Log.assertEmpty()
                client1Log2.assertContents("topic1 changed2: new value")
                assertEquals(2, serverTopicInfo.listeners_TEST_ONLY.size)

                client1TopicObserver2.unsubscribe()
                serverTopicObserver.onChange("another new value")
                testRig.dispatcher.runCurrent()

                testRig.client1Log.assertEmpty()
                client1Log2.assertEmpty()
                assertEquals(1, serverTopicInfo.listeners_TEST_ONLY.size)
            }
        }

        context("before connection is made") {
            it("isConnected should return false") {
                testRig.client1.isConnected.shouldBeFalse()
                testRig.dispatcher.runCurrent()
                testRig.client1.isConnected.shouldBeTrue()
            }
        }

        context("when websocket is connected") {
            it("isConnected should notify listeners") {
                testRig.client1.addStateChangeListener { testRig.client1Log.add("isConnected was changed to ${testRig.client1.isConnected}") }
                testRig.dispatcher.runCurrent()
                testRig.client1Log.assertContents("isConnected was changed to true")
            }
        }

        context("when connection is reset") {
            it("should notify listener of state change") {
                testRig.dispatcher.runCurrent()
                testRig.client1.isConnected.shouldBeTrue()

                testRig.client1.addStateChangeListener { testRig.client1Log.add("isConnected was changed to ${testRig.client1.isConnected}") }

                // Trigger a connection reset, and don't try to reconnect (to avoid spec cleanup).
                val connectionToServer = testRig.client1Network.webSocketListeners[0] as PubSub.Client.ConnectionToServer
                connectionToServer.attemptReconnect = false
                connectionToServer.reset(testRig.client1Network.tcpConnections[0])

                testRig.client1Log.assertContents("isConnected was changed to false")
            }

            it("should attempt to reconnect every second") {
                testRig.client1Network.tcpConnections.size.shouldBe(1)

                // trigger a connection reset
                testRig.client1Network.webSocketListeners[0].reset(testRig.client1Network.tcpConnections[0])
                testRig.client1.isConnected.shouldBeFalse()

                testRig.client1Network.tcpConnections.size.shouldBe(1)

                // don't attempt a new connection until a second has passed
                testRig.dispatcher.runCurrent()
                testRig.client1Network.tcpConnections.size.shouldBe(1)

                testRig.dispatcher.advanceTimeBy(2000)
                testRig.dispatcher.runCurrent()

                // assert that there was a new outgoing connection
                testRig.client1Network.tcpConnections.size.shouldBe(2)
                testRig.client1.isConnected.shouldBeTrue()
            }

            it("should resubscribe to topics") {
                val client1TopicObserver =
                    testRig.client1.subscribe(topic1) { testRig.client1Log.add("topic1 changed: $it") }

                val serverTopicObserver = testRig.server.publish(topic1, "value") {
                    testRig.serverLog.add("topic1 changed: $it")
                }

                testRig.client1Network.tcpConnections.size.shouldBe(1)
                testRig.dispatcher.runCurrent()
                testRig.client1Log.assertContents("topic1 changed: value")

                // trigger a connection reset
                testRig.client1Network.webSocketListeners[0].reset(testRig.client1Network.tcpConnections[0])
                testRig.client1.isConnected.shouldBeFalse()

                testRig.client1Network.tcpConnections.size.shouldBe(1)

                // don't attempt a new connection until a second has passed
                testRig.dispatcher.runCurrent()
                testRig.client1Network.tcpConnections.size.shouldBe(1)

                testRig.dispatcher.advanceTimeBy(2000)
                testRig.dispatcher.runCurrent()

                // assert that there was a new outgoing connection
                testRig.client1Network.tcpConnections.size.shouldBe(2)
                testRig.client1.isConnected.shouldBeTrue()

                serverTopicObserver.onChange("new value")
                testRig.dispatcher.runCurrent()
                testRig.client1Log.assertContents("topic1 changed: new value")
            }
        }

        context("with nullable class serializer") {
            it("should work") {
                val nullableTopic = PubSub.Topic("/x", Thing.serializer().nullable)

                val serverTopicObserver = testRig.server.publish(nullableTopic, Thing("value")) {
                    testRig.serverLog.add("topic1 changed: ${it?.string}")
                }

                testRig.client1.subscribe(nullableTopic) {
                    testRig.client1Log.add("topic1 changed: ${it?.string}")
                }
                testRig.dispatcher.runCurrent()
                testRig.client1Log.assertContents("topic1 changed: value")

                serverTopicObserver.onChange(null)
                testRig.dispatcher.runCurrent()
                testRig.client1Log.assertContents("topic1 changed: null")
            }
        }

        context("commands") {
            val commandPort by value { CommandPort("command", String.serializer(), String.serializer()) }
            val serverCommandHandler by value {
                val x: suspend (String) -> String = { s: String -> "reply for $s" }; x
            }
            val serverCommandChannel by value {
                testRig.server.listenOnCommandChannel(commandPort) { command: String -> serverCommandHandler(command) }
            }
            val clientCommandChannel by value { testRig.client1.commandSender(commandPort) }

            beforeEach {
                serverCommandChannel.run {}
                clientCommandChannel.run {}
            }

            describe("async command handling") {
                context("when a command suspends") {
                    val otherCommandPort by value { CommandPort("otherCommand", String.serializer(), String.serializer()) }
                    val serverOtherCommandChannel by value {
                        testRig.server.listenOnCommandChannel(otherCommandPort) { command: String -> "immediate reply for $command" }
                    }
                    val clientOtherCommandChannel by value { testRig.client1.commandSender(otherCommandPort) }
                    val future by value { CompletableDeferred<String>() }
                    val result by value { arrayListOf<String>() }

                    override(serverCommandHandler) {
                        val x: suspend (String) -> String = { s: String -> "deferred reply for ${future.await()}" }; x
                    }

                    beforeEach {
                        serverOtherCommandChannel.run {}

                        testRig.testCoroutineScope.launch {
                            result.add("response: " + clientCommandChannel("the command"))
                        }

                        testRig.testCoroutineScope.launch {
                            result.add("response: " + clientOtherCommandChannel("other command"))
                        }
                    }

                    it("can handle additional commands meanwhile") {
                        testRig.dispatcher.runCurrent()
                        result.shouldContainExactly("response: immediate reply for other command")

                        future.complete("with completion value")
                        testRig.dispatcher.runCurrent()
                        result.shouldContainExactly(
                            "response: immediate reply for other command",
                            "response: deferred reply for with completion value"
                        )
                    }
                }
            }

            context("when a client sends a command") {
                val result by value { arrayListOf<String>() }

                beforeEach {
                    testRig.testCoroutineScope.launch {
                        try {
                            result.add("response: " + clientCommandChannel("the command"))
                        } catch (e: Exception) {
                            result.add("error: ${e.message}")
                        }
                    }
                }

                suspend fun DescribeSpecContainerScope.sharedCommandSpecs() {
                    it("invokes that command on the server and returns the response to the caller") {
                        testRig.dispatcher.runCurrent()
                        result.shouldContainExactly("response: reply for the command")
                    }

                    context("and the server throws an exception") {
                        override(serverCommandHandler) {
                            val x: suspend (String) -> String = { s: String -> throw RuntimeException("error for $s") }; x
                        }

                        it("returns the response to the caller") {
                            testRig.dispatcher.runCurrent()
                            result.shouldContainExactly("error: error for the command")
                        }
                    }
                }
                sharedCommandSpecs()

                context("when the command client is a PubSub.Server") {
                    override(serverCommandChannel) {
                        testRig.client1.listenOnCommandChannel(commandPort) { command: String ->
                            serverCommandHandler(command)
                        }
                    }
                    override(clientCommandChannel) { testRig.serverConnections.first().commandSender(commandPort) }

                    sharedCommandSpecs()
                }
            }
        }
    }
})

@Serializable
data class Thing(val string: String)
