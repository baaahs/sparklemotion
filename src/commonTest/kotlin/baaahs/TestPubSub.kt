package baaahs

import baaahs.sim.FakeNetwork
import ext.TestCoroutineContext
import kotlinx.coroutines.InternalCoroutinesApi

@InternalCoroutinesApi
class TestPubSub(
    val testCoroutineContext: TestCoroutineContext = TestCoroutineContext("network"),
    val network: FakeNetwork = FakeNetwork(0, testCoroutineContext)
) : PubSub() {
    val serverNetwork = network.link("server")
    val server = listen(serverNetwork.startHttpServer(1234), testCoroutineContext)
    val serverLog = mutableListOf<String>()

    fun client(clientName: String): TestClient = TestClient(clientName)

    inner class TestClient(
        val clientName: String
    ) : Client(network.link(clientName), serverNetwork.myAddress, 1234) {
        val log = mutableListOf<String>()
    }
}