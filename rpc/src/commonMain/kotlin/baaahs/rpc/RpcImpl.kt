package baaahs.rpc

import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlin.coroutines.coroutineContext

public class CommandPort<C, R>(
    public val name: String,
    private val serializer: KSerializer<C>,
    private val replySerializer: KSerializer<R>,
    serialModule: SerializersModule = SerializersModule {}
) {
    private val json = Json { serializersModule = serialModule }

    public fun toJson(command: C): String = json.encodeToString(serializer, command)
    public fun fromJson(command: String): C = json.decodeFromString(serializer, command)
    public fun replyToJson(command: R): String = json.encodeToString(replySerializer, command)
    public fun replyFromJson(command: String): R = json.decodeFromString(replySerializer, command)
}

public interface RpcCommandRecipient {
    public fun <C, R> sendCommand(commandPort: CommandPort<C, R>, command: C, commandId: String)

    public fun <C, R> openCommandChannel(
        commandPort: CommandPort<C, R>
    ): RpcCommandChannel<C, R>

    public fun <C, R> commandSender(
        commandPort: CommandPort<C, R>
    ): suspend (command: C) -> R
}

public interface RpcCommandChannel<C, R> {
    public suspend fun send(command: C): R
}

public interface RpcClient : RpcEndpoint, RpcCommandRecipient {
    public companion object {
        public suspend fun id(): String =
            coroutineContext[RpcClientId]?.id ?: error("not in RPC call")
    }
}

public interface RpcServer : RpcEndpoint

public interface RpcEndpoint {
    public fun <C, R> listenOnCommandChannel(
        commandPort: CommandPort<C, R>, callback: suspend (command: C) -> R
    )
}

public interface RpcImpl<T> {
    public fun createSender(endpoint: RpcEndpoint): T
    public fun createReceiver(endpoint: RpcEndpoint, handler: T)
}