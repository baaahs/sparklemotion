package baaahs.rpc

import baaahs.net.Network
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonPrimitive
import kotlin.concurrent.Volatile
import kotlin.jvm.Synchronized

public class WebSocketRpcServer(
    httpServer: Network.HttpServer,
    path: String,
    private val networkScope: CoroutineScope
) : RpcServer {
    private val allConnections = arrayListOf<WebSocketRpcServerEndpoint>()
    internal val connections: List<WebSocketRpcServerEndpoint>
        get() = allConnections

    @Volatile
    private var serviceHandlers = mapOf<String, ServiceHandler<*>>()

    internal inner class ServiceHandler<T : Any>(
        serviceDesc: RpcService<T>,
        val handler: T,
        val scope: CoroutineScope
    ) {
        val methods = serviceDesc.commands.associateBy { commandPort -> commandPort.name }

        fun process(rpcMessage: RpcInvokeMessage, connection: WebSocketRpcEndpoint.Connection) {
            val method = methods[rpcMessage.method]
                ?: error("Unknown method \"${rpcMessage.service}.${rpcMessage.method}\"")
            scope.launch {
                val responsne = try {
                    RpcSuccessMessage(
                        rpcMessage.invocationId,
                        method.process(handler, rpcMessage.arguments)
                    )
                } catch (e: Exception) {
                    RpcErrorMessage(
                        rpcMessage.invocationId,
                        JsonPrimitive(e.message)
                    )
                }

                networkScope.launch {
                    connection.send(responsne)
                }
            }
        }
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
    override fun <T : Any> registerServiceHandler(
        path: String,
        rpcService: RpcService<T>,
        service: T,
        scope: CoroutineScope
    ) {
        if (serviceHandlers.containsKey(path))
            error("Service handler for $path already registered.")
        serviceHandlers += path to ServiceHandler(rpcService, service, scope)
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
    ) : WebSocketRpcEndpoint(networkScope, { commandPath -> findHandler(commandPath); TODO() }) {
        val connection =
            object : Connection("Client-side connection from ${tcpConnection.fromAddress} to server at ${tcpConnection.toAddress}") {
                override fun handleInvoke(rpcMessage: RpcInvokeMessage) {
                    val handler = serviceHandlers[rpcMessage.service]
                        ?: error("Unknown service \"${rpcMessage.service}\"")
                    val connection = this
                    handler.process(rpcMessage, this)
                }

            }

//        override fun <T, C, R> sendCommand(commandPort: CommandPort<T, C, R>, command: C, commandId: String) {
//            connection.sendCommand(commandPort, command, commandId)
//        }
    }
}