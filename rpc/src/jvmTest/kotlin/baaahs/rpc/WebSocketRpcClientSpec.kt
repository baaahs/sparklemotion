package baaahs.rpc

import baaahs.sim.FakeNetwork
import ch.tutteli.atrium.api.fluent.en_GB.containsExactly
import ch.tutteli.atrium.api.fluent.en_GB.toBe
import ch.tutteli.atrium.api.verbs.expect
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.serialization.builtins.serializer
import org.spekframework.spek2.Spek
import org.spekframework.spek2.dsl.GroupBody
import org.spekframework.spek2.dsl.Skip
import org.spekframework.spek2.style.specification.Suite
import org.spekframework.spek2.style.specification.describe
import kotlin.reflect.KFunction
import kotlin.reflect.KProperty

// TODO: Move back to commonTest when ksp handles test stuff better.
object WebSocketRpcClientSpec : Spek({
    describe<WebSocketRpcClient> {
        val dispatcher by value { StandardTestDispatcher() }
        val testCoroutineScope by value { CoroutineScope(dispatcher) }
        val network by value { FakeNetwork(0, coroutineScope = CoroutineScope(dispatcher)) }
        val clientLink by value { network.link("rpcClient") }
        val serverLink by value { network.link("rpcServer") }
        val serverHttpServer by value { serverLink.startHttpServer(1234) }
        val client by value {
            WebSocketRpcClient(
                clientLink,
                serverLink.myAddress,
                1234,
                "/the/path",
                testCoroutineScope
            )
        }
        val server by value { WebSocketRpcServer(serverHttpServer, "/the/path", testCoroutineScope) }

        context("before connection is made") {
            it("state should be disconnected") {
                expect(client.state).toBe(RpcClient.State.Disconnected)
            }

            it("should attempt to connect") {
                dispatcher.scheduler.runCurrent()
                expect(client.state).toBe(RpcClient.State.Connecting)
            }
        }

//        context("when connection is reset") {
//            it("should notify listener of state change") {
//                dispatcher.scheduler.runCurrent()
//                expect(client.isConnected).toBe(true)
//
//                client.addStateChangeListener { client1Log.add("isConnected was changed to ${client.isConnected}") }
//
//                // Trigger a connection reset, and don't try to reconnect (to avoid spec cleanup).
//                val connectionToServer = client1Network.webSocketListeners[0] as PubSub.Client.ConnectionToServer
//                connectionToServer.attemptReconnect = false
//                connectionToServer.reset(client1Network.tcpConnections[0])
//
//                client1Log.assertContents("isConnected was changed to false")
//            }
//
//            it("should attempt to reconnect every second") {
//                expect(client1Network.tcpConnections.size).toBe(1)
//
//                // trigger a connection reset
//                client1Network.webSocketListeners[0].reset(client1Network.tcpConnections[0])
//                expect(client.isConnected).toBe(false)
//
//                expect(client1Network.tcpConnections.size).toBe(1)
//
//                // don't attempt a new connection until a second has passed
//                dispatcher.scheduler.runCurrent()
//                expect(client1Network.tcpConnections.size).toBe(1)
//
//                dispatcher.scheduler.advanceTimeBy(2000)
//                dispatcher.scheduler.runCurrent()
//
//                // assert that there was a new outgoing connection
//                expect(client1Network.tcpConnections.size).toBe(2)
//                expect(client.isConnected).toBe(true)
//            }
//        }

        context("services") {
            val path by value { "testService" }
            val rpcService by value { TestServiceRpc(path) }
            val serviceHandler by value<TestService> {
                object : TestService {
                    override suspend fun testMethod(arg: String): String = "reply for $arg"
                    override suspend fun testMethodReturningService(arg: String): AnotherTestService = TODO()
                }
            }
            val rpcServer by value { server.registerServiceHandler(path, rpcService, serviceHandler,) }
            val rpcClient by value { client.remoteService(path, rpcService) }

            beforeEachTest {
                server.run {}
                client.run {}
                rpcServer.run {}
                rpcClient.run {}
            }

            context("when a client sends a command") {
                val result by value { arrayListOf<String>() }

                beforeEachTest {
                    testCoroutineScope.launch {
                        try {
                            result.add("response: " + rpcClient.testMethod("the command"))
                        } catch (e: Exception) {
                            result.add("error: ${e.message}")
                        }
                    }
                }

                context("with a method returning an ordinary value") {
                    value(serviceHandler) {
                        object : TestService.Stub() {
                            override suspend fun testMethod(arg: String): String = "reply for $arg"
                        }
                    }

                    it("invokes that method on the server and returns the response to the caller") {
                        dispatcher.scheduler.runCurrent()
                        expect(result).containsExactly("response: reply for the command")
                    }

                    context("when the server throws an exception") {
                        value(serviceHandler) {
                            object : TestService.Stub() {
                                override suspend fun testMethod(arg: String): String = error("error for $arg")
                            }
                        }

                        it("returns the response to the caller") {
                            dispatcher.scheduler.runCurrent()
                            expect(result).containsExactly("error: error for the command")
                        }
                    }
                }


//                context("with a method returning a service") {
//                    value(serviceHandler) {
//                        object : TestService.Stub() {
//                            override suspend fun testMethodReturningService(arg: String): AnotherTestService =
//                                object : AnotherTestService {
//                                    override suspend fun anotherTestMethod(arg: String): String =
//                                        "subsequent reply for $arg"
//                                }
//                        }
//                    }
//
//                    it("invokes that method on the server and returns the response to the caller") {
//                        dispatcher.scheduler.runCurrent()
//                        expect(result).containsExactly("response: reply for the command")
//                    }
//
//                    context("when the server throws an exception") {
//                        value(serviceHandler) {
//                            object : TestService.Stub() {
//                                override suspend fun testMethod(arg: String): String = error("error for $arg")
//                            }
//                        }
//
//                        it("returns the response to the caller") {
//                            dispatcher.scheduler.runCurrent()
//                            expect(result).containsExactly("error: error for the command")
//                        }
//                    }
//                }
            }
        }

        context("commands") {
            val commandPort by value {
                CommandPort<Unit, String, String>(
                    "testCommand",
                    String.serializer(),
                    String.serializer(),
                    { TODO() })
            }
            val serverCommandHandler by value {
                val x: suspend (String) -> String = { s: String -> "reply for $s" }; x
            }
            val rpcReceiverChannel by value {
                server.listenOnCommandChannel(commandPort) { command: String -> serverCommandHandler(command) }
            }
            val rpcSenderChannel by value { client.commandSender(commandPort) }

            beforeEachTest {
                server.run {}
                client.run {}
                rpcReceiverChannel.run {}
                rpcSenderChannel.run {}
            }

            context("when a client sends a command") {
                val result by value { arrayListOf<String>() }

                beforeEachTest {
                    testCoroutineScope.launch {
                        try {
                            result.add("response: " + rpcSenderChannel("the command"))
                        } catch (e: Exception) {
                            result.add("error: ${e.message}")
                        }
                    }
                }

                fun Suite.sharedCommandSpecs() {
                    it("invokes that command on the server and returns the response to the caller") {
                        dispatcher.scheduler.runCurrent()
                        expect(result).containsExactly("response: reply for the command")
                    }

                    context("and the server throws an exception") {
                        value(serverCommandHandler) {
                            val x: suspend (String) -> String =
                                { s: String -> throw RuntimeException("error for $s") }; x
                        }

                        it("returns the response to the caller") {
                            dispatcher.scheduler.runCurrent()
                            expect(result).containsExactly("error: error for the command")
                        }
                    }
                }
                sharedCommandSpecs()

                context("when the command is issued from server to client") {
                    value(rpcReceiverChannel) {
                        client.listenOnCommandChannel(commandPort) { command: String ->
                            serverCommandHandler(command)
                        }
                    }
                    value(rpcSenderChannel) { server.connections.first().commandSender(commandPort) }

                    sharedCommandSpecs()
                }
            }

            describe("async command handling") {
                context("when a command suspends") {
                    val otherCommandPort by value {
                        CommandPort<Unit, String, String>(
                            "otherCommand",
                            String.serializer(),
                            String.serializer(),
                            { TODO() }
                        )
                    }
                    val serverOtherCommandChannel by value {
                        server.listenOnCommandChannel(otherCommandPort) { command: String -> "immediate reply for $command" }
                    }
                    val clientOtherCommandChannel by value { client.commandSender(otherCommandPort) }
                    val future by value { CompletableDeferred<String>() }
                    val result by value { arrayListOf<String>() }

                    value(serverCommandHandler) {
                        val x: suspend (String) -> String = { s: String -> "deferred reply for ${future.await()}" }; x
                    }

                    beforeEachTest {
                        serverOtherCommandChannel.run {}

                        testCoroutineScope.launch {
                            result.add("response: " + rpcSenderChannel("the command"))
                        }

                        testCoroutineScope.launch {
                            result.add("response: " + clientOtherCommandChannel("other command"))
                        }
                    }

                    it("can handle additional commands meanwhile") {
                        dispatcher.scheduler.runCurrent()
                        expect(result).containsExactly("response: immediate reply for other command")

                        future.complete("with completion value")
                        dispatcher.scheduler.runCurrent()
                        expect(result).containsExactly(
                            "response: immediate reply for other command",
                            "response: deferred reply for with completion value"
                        )
                    }
                }
            }
        }
    }
})


inline fun <reified T> GroupBody.describe(skip: Skip = Skip.No, noinline body: Suite.() -> Unit) =
    describe(T::class.toString(), skip, body)

fun GroupBody.describe(fn: KFunction<*>, skip: Skip = Skip.No, body: Suite.() -> Unit) =
    describe(fn.toString(), skip, body)

fun GroupBody.describe(prop: KProperty<*>, skip: Skip = Skip.No, body: Suite.() -> Unit) =
    describe(prop.toString(), skip, body)

@Service
interface TestService {
    suspend fun testMethod(arg: String): String

    //    suspend fun testMethodReturningSequence(arg: String): Sequence<String>
    suspend fun testMethodReturningService(arg: String): AnotherTestService

    companion object

Z    open class Stub : TestService {
        override suspend fun testMethod(arg: String): String = TODO()
        override suspend fun testMethodReturningService(arg: String): AnotherTestService = TODO()
    }
}

interface AnotherTestService {
    suspend fun anotherTestMethod(arg: String): String

    open class Stub : AnotherTestService {
        override suspend fun anotherTestMethod(arg: String): String = TODO()
    }
}
