package baaahs.rpc

import baaahs.io.ByteArrayReader
import baaahs.io.ByteArrayWriter
import baaahs.net.Network
import baaahs.util.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

public abstract class WebSocketRpcEndpoint internal constructor(
    private val handlerScope: CoroutineScope,
    private val findCommandHandler: (String) -> CommandHandler<*, *, *>
): RpcEndpoint {
    private val verbose = false
    private val commandHandlers: MutableMap<String, CommandHandler<*, *, *>> = hashMapOf()
    private val responseHandlers: MutableMap<String, ResponseHandler<*, *, *>> = hashMapOf()

    public fun <T, C, R> commandSender(commandPort: CommandPort<T, C, R>): suspend (command: C) -> R {
        val name = commandPort.name
        if (responseHandlers.containsKey(name))
            error("Command channel $name already exists.")
        val commandChannel = ResponseHandler(commandPort, ::sendCommand).also {
            responseHandlers[name] = it
        }
        return { command: C -> commandChannel.send(command) }
    }

    public abstract fun <T, C, R> sendCommand(commandPort: CommandPort<T, C, R>, command: C, commandId: String)

    override fun <T: Any, C, R> listenOnCommandChannel(
        commandPort: CommandPort<T, C, R>,
        callback: suspend (command: C) -> R
    ) {
        val name = commandPort.name
        if (commandHandlers.containsKey(name))
            error("Command channel $name already exists.")
        commandHandlers[name] = CommandHandler(commandPort) { command -> callback(command) }
    }

    public inner class Connection(
        private val name: String
    ) : Network.WebSocketListener {
        private var tcpConnection: Network.TcpConnection? = null
        private var isConnected: Boolean = false
        private var everConnected: Boolean = false

        override fun connected(tcpConnection: Network.TcpConnection) {
            logger.debug { "$connectionInfo: connection $name established" }
            this.tcpConnection = tcpConnection
            isConnected = true
            everConnected = true
        }

        override suspend fun receive(tcpConnection: Network.TcpConnection, bytes: ByteArray) {
            withContext(handlerScope.coroutineContext) {
                try {
                    doReceive(bytes)
                } catch (e: Exception) {
                    logger.error(e) { "Error processing pubsub command." }
                    throw e
                }
            }
        }

        override fun reset(tcpConnection: Network.TcpConnection) {
            logger.info { "PubSub client $name disconnected." }
            isConnected = false
        }

        private suspend fun doReceive(bytes: ByteArray) {
            val reader = ByteArrayReader(bytes)
            when (val command = reader.readString()) {
                "command" -> {
                    val name = reader.readString()
                    val commandId = reader.readString()
                    logger.debug { "command $name $commandId"}
                    val commandChannel = commandHandlers[name]
                        ?: error("No command channel named $name.")
                    commandChannel.receiveCommand(reader.readString(), commandId, this, handlerScope)
                }

                "commandError" -> {
                    val name = reader.readString()
                    val commandId = reader.readString()
                    logger.debug { "commandError $name $commandId"}
                    val commandChannel = responseHandlers[name]
                        ?: error("No command channel named $name.")
                    commandChannel.receiveError(reader.readString(), commandId)
                }

                "commandReply" -> {
                    val name = reader.readString()
                    val commandId = reader.readString()
                    logger.debug { "commandReply $name $commandId"}
                    val commandChannel = responseHandlers[name]
                        ?: error("No command channel named $name.")
                    commandChannel.receiveReply(reader.readString(), commandId)
                }

                else -> {
                    throw IllegalArgumentException("huh? don't know what to do with $command")
                }
            }
        }

        public fun <T, C, R> sendCommand(commandPort: CommandPort<T, C, R>, command: C, commandId: String) {
            if (isConnected) {
                if (verbose) {
                    logger.debug { "$connectionInfo: command ${commandPort.name} $commandId ${commandPort.toJson(command)}" }
                }

                val writer = ByteArrayWriter()
                writer.writeString("command")
                writer.writeString(commandPort.name)
                writer.writeString(commandId)
                writer.writeString(commandPort.toJson(command))
                sendMessage(writer.toBytes())
            } else {
                logger.warn { "$connectionInfo: not connected; dropping command ${commandPort.name}" }
            }
        }

        public fun <T, C, R> sendReply(commandPort: CommandPort<T, C, R>, reply: R, commandId: String) {
            if (isConnected) {
                if (verbose) {
                    logger.debug {
                        "$connectionInfo: commandReply ${commandPort.name} $commandId ${commandPort.replyToJson(reply)}"
                    }
                }

                val writer = ByteArrayWriter()
                writer.writeString("commandReply")
                writer.writeString(commandPort.name)
                writer.writeString(commandId)
                writer.writeString(commandPort.replyToJson(reply))
                sendMessage(writer.toBytes())
            } else {
                logger.warn { "$connectionInfo: not connected; dropping commandReply ${commandPort.name}" }
            }
        }

        public fun <T, C, R> sendError(commandPort: CommandPort<T, C, R>, message: String, commandId: String) {
            if (isConnected) {
                if (verbose) {
                    logger.debug { "$connectionInfo: commandError ${commandPort.name} $commandId $message" }
                }

                val writer = ByteArrayWriter()
                writer.writeString("commandError")
                writer.writeString(commandPort.name)
                writer.writeString(commandId)
                writer.writeString(message)
                sendMessage(writer.toBytes())
            } else {
                logger.warn { "$connectionInfo: not connected; dropping commandError ${commandPort.name} $commandId \"$message\"" }
            }
        }

        private fun sendMessage(bytes: ByteArray) {
            tcpConnection?.send(bytes)
                ?: error("Not connected!") // TODO something better
        }

        private val connectionInfo get() = "[$name${if (!isConnected) " (not connected)" else ""}]"
    }

    internal companion object {
        internal val logger = Logger<WebSocketRpcEndpoint>()
    }
}

public class RemoteException(message: String) : Exception(message)

internal class CommandHandler<T : Any, C, R>(
    private val commandPort: CommandPort<T, C, R>,
    private val callback: suspend (command: C) -> R
) {
    suspend fun receiveCommand(
        commandJson: String,
        commandId: String,
        fromConnection: WebSocketRpcEndpoint.Connection,
        handlerScope: CoroutineScope
    ) {
        handlerScope.launch {
            try {
                val command = commandPort.fromJson(commandJson)
                val reply = callback.invoke(command)
                fromConnection.sendReply(commandPort, reply, commandId)
            } catch (e: Exception) {
                WebSocketRpcEndpoint.logger.warn(e) { "Error in remote command invocation (${commandPort.name} $commandJson $commandId)." }
                fromConnection.sendError(commandPort, e.message ?: "unknown error", commandId)
            }
        }
    }
}

internal class ResponseHandler<T, C, R>(
    private val commandPort: CommandPort<T, C, R>,
    private val sendCommand: (CommandPort<T, C, R>, C, String) -> Unit
) : RpcCommandChannel<C, R> {
    private val mutex = Mutex()
    private val handlers = mutableMapOf<String, CommandHandler<R>>()
    private var nextCommandId = 0

    override suspend fun send(command: C): R {
        val handler = mutex.withLock {
            val commandId = nextCommandId++.toString(16)
            CommandHandler<R>(commandId)
                .also { handlers[commandId] = it }
        }
        sendCommand(commandPort, command, handler.commandId)
        return handler.receive()
    }

    suspend fun receiveReply(replyJson: String, commandId: String) {
        mutex.withLock { handlers.remove(commandId) }
            ?.let { handler ->
                val reply = commandPort.replyFromJson(replyJson)
                handler.onReply(reply)
            }
    }

    suspend fun receiveError(message: String, commandId: String) {
        mutex.withLock { handlers.remove(commandId) }
            ?.onError(message)
    }

    private class CommandHandler<R>(val commandId: String) {
        private val coroutineChannel = kotlinx.coroutines.channels.Channel<Pair<R?, String?>>()

        suspend fun receive(): R {
            val (reply, error) = coroutineChannel.receive()
            return reply
                ?: throw RemoteException(error ?: "Unknown error; command=$commandId")
        }

        suspend fun onReply(reply: R) {
            coroutineChannel.send(reply to null)
        }

        suspend fun onError(message: String) {
            coroutineChannel.send(null to message)
        }
    }
}

