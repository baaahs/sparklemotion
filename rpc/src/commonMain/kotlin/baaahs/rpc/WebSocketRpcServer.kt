package baaahs.rpc

import baaahs.net.Network
import kotlinx.coroutines.CoroutineScope
import kotlin.concurrent.Volatile
import kotlin.jvm.Synchronized

public class WebSocketRpcServer(
    httpServer: Network.HttpServer,
    path: String,
    private val handlerScope: CoroutineScope
) : RpcServer {
    private val allConnections = arrayListOf<WebSocketRpcServerEndpoint>()
    internal val connections: List<WebSocketRpcServerEndpoint>
        get() = allConnections

    @Volatile
    private var serviceHandlers = mapOf<String, ServiceHandler<*>>()

    internal class ServiceHandler<T : Any>(
        serviceDesc: RpcServiceDesc<T>,
        val handler: T
    ) {
        val methods = serviceDesc.commands.associateBy { commandPort -> commandPort.name }
    }

    init {
        httpServer.listenWebSocket(path) { incomingConnection ->
            WebSocketRpcServerEndpoint(incomingConnection)
                .also { allConnections.add(it) }
                .connection
        }
    }

    public fun <T, C, R> listenOnCommandChannel(commandPort: CommandPort<T, C, R>, callback: suspend (C) -> R) {
        TODO("not implemented")
    }

    @Synchronized
    override fun <T : Any> registerServiceHandler(path: String, service: RpcServiceDesc<T>, testService: T) {
        if (serviceHandlers.containsKey(path))
            error("Service handler for $path already registered.")
        serviceHandlers += path to ServiceHandler(service, testService)
    }

    private fun findHandler(path: String): CommandPort<*, *, *> {
        val parts = path.split("/")
        val servicePath = parts[0]
        val serviceHandler = serviceHandlers[servicePath]
            ?: error("Unknown service \"$servicePath\".")
        val methodName = parts[1]
        return serviceHandler.methods[methodName]
            ?: error("Unknown command \"$methodName\" for service \"$servicePath\".")
    }

    internal inner class WebSocketRpcServerEndpoint(
        tcpConnection: Network.TcpConnection
    ) : WebSocketRpcEndpoint(handlerScope, { commandPath -> findHandler(commandPath); TODO() }) {
        val connection =
            Connection("Client-side connection from ${tcpConnection.fromAddress} to server at ${tcpConnection.toAddress}")

        override fun <T, C, R> sendCommand(commandPort: CommandPort<T, C, R>, command: C, commandId: String) {
            connection.sendCommand(commandPort, command, commandId)
        }
    }
}