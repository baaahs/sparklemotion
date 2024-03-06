package baaahs.rpc

import baaahs.net.Network
import baaahs.ui.Observable
import baaahs.ui.Observer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

// TODO: dedupe this with other globalLaunch
private fun globalLaunch(block: suspend CoroutineScope.() -> Unit) =
    GlobalScope.launch {
        block.invoke(this)
    }

public class WebSocketRpcClient(
    private val link: Network.Link,
    private val serverAddress: Network.Address,
    private val port: Int,
    private val path: String,
    handlerScope: CoroutineScope
) : WebSocketRpcEndpoint(handlerScope, { TODO() }), RpcClient {
    private val observable = Observable()
    private var connection: Connection? = null

    override var state: RpcClient.State = RpcClient.State.Disconnected
        private set

    override fun <T : Any> remoteService(path: String, service: RpcServiceDesc<T>): T {
        TODO("not implemented")
    }

    init {
        globalLaunch {
            connectWebSocket()
        }
    }

    private fun connectWebSocket() {
        if (state != RpcClient.State.Disconnected)
            return

        state = RpcClient.State.Connecting
        observable.notifyChanged()

        val connection = Connection("Client-side connection from ${link.myAddress} to server at $serverAddress")
        this.connection = connection
        link.connectWebSocket(serverAddress, port, path, connection)
    }

    override fun <T, C, R> sendCommand(commandPort: CommandPort<T, C, R>, command: C, commandId: String) {
        connection?.sendCommand(commandPort, command, commandId)
    }

    override fun addObserver(observer: Observer): Observer = observable.addObserver(observer)

    override fun removeObserver(observer: Observer): Unit = observable.removeObserver(observer)
}