package baaahs.rpc

import baaahs.ui.IObservable
import kotlinx.coroutines.CoroutineScope
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Polymorphic
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.modules.SerializersModule

/**
 * # Protocol:
 *
 * ```json
 * {
 * ```
 */
public class CommandPort<T, C, R>(
    public val name: String,
    private val serializer: KSerializer<C>,
    private val replySerializer: KSerializer<R>,
    private val invoke: suspend T.(C) -> R,
    serialModule: SerializersModule = SerializersModule {}
) {
    private val json = Json { serializersModule = serialModule }

    public fun toJson(command: C): JsonElement = json.encodeToJsonElement(serializer, command)
    public fun fromJson(command: JsonElement): C = json.decodeFromJsonElement(serializer, command)
    public fun replyToJson(command: R): String = json.encodeToString(replySerializer, command)
    public fun replyFromJson(command: String): R = json.decodeFromString(replySerializer, command)

    public suspend fun transmit(endpoint: RpcEndpoint, command: C, commandId: String): R {
        val commandJson = json.encodeToJsonElement(serializer, command)
        endpoint.sendCommand(this, commandJson, commandId)
        val replyJson = endpoint.awaitReply(this, commandId)
        return json.decodeFromJsonElement(replySerializer, replyJson)
    }

    public suspend fun process(receiver: T, commandJson: JsonElement): JsonElement {
        val command = json.decodeFromJsonElement(serializer, commandJson)
        val reply = receiver.invoke(command)
        return json.encodeToJsonElement(replySerializer, reply)
    }
}

@Serializable @Polymorphic
internal sealed interface RpcMessage

internal typealias MessageId = Int

@Serializable @SerialName("invoke")
internal data class RpcInvokeMessage(
    val invocationId: MessageId,
    val service: String,
    val method: String,
    val arguments: JsonElement,
    val contextInvocationId: MessageId? = null
) : RpcMessage

@Serializable @SerialName("success")
internal data class RpcSuccessMessage(
    val invocationId: MessageId,
    val result: JsonElement,
    val keepAlive: Boolean = false
) : RpcMessage

@Serializable @SerialName("error")
internal data class RpcErrorMessage(
    val invocationId: MessageId,
    val result: JsonElement
) : RpcMessage

public interface RpcCommandRecipient {
    public fun <T, C, R> commandSender(
        commandPort: CommandPort<T, C, R>
    ): suspend (command: C) -> R
}

public interface RpcCommandChannel<C, R> {
    public suspend fun send(command: C): R
}

public interface RpcService<T : Any> {
    public val commands: List<CommandPort<T, *, *>>

    public fun createSender(endpoint: RpcEndpoint): T
}

public interface RpcClient : RpcEndpoint, /*RpcCommandRecipient,*/ IObservable {
    public val state: State

    public fun <T : Any> remoteService(path: String, service: RpcService<T>) : T

    public enum class State {
        Disconnected, Connecting, Connected
    }
}

public interface RpcServer {
    public fun <T : Any> registerServiceHandler(path: String, rpcService: RpcService<T>, service: T, scope: CoroutineScope)
}

public interface RpcEndpoint {
    public fun <T: Any, C, R> listenOnCommandChannel(
        commandPort: CommandPort<T, C, R>, callback: suspend (command: C) -> R
    )

    public suspend fun sendCommand(commandPort: CommandPort<*, *, *>, command: JsonElement, commandId: String)
    public suspend fun awaitReply(commandPort: CommandPort<*, *, *>, commandId: String): JsonElement
}

public interface RpcImpl<T> {
    public fun createSender(endpoint: RpcEndpoint): T
    public fun createReceiver(handler: T)
}

public class RpcHandler<T: Any>(
    private val rpcService: RpcService<T>,
    private val handler: T
) {
    private val commands = rpcService.commands
        .associateBy { commandPort -> commandPort.name }

    public suspend fun handleCommand(
        commandName: String,
        commandJson: JsonElement
    ): JsonElement {
        val commandPort = commands[commandName]
            ?: error("Unknown command \"$commandName\" for service \"$rpcService\".")
        return commandPort.process(handler, commandJson)
    }
}