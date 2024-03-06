package baaahs.rpc

import baaahs.ui.IObservable
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule

public class CommandPort<T, C, R>(
    public val name: String,
    private val serializer: KSerializer<C>,
    private val replySerializer: KSerializer<R>,
    private val invoke: suspend T.(C) -> R,
    serialModule: SerializersModule = SerializersModule {}
) {
    private val json = Json { serializersModule = serialModule }

    public fun toJson(command: C): String = json.encodeToString(serializer, command)
    public fun fromJson(command: String): C = json.decodeFromString(serializer, command)
    public fun replyToJson(command: R): String = json.encodeToString(replySerializer, command)
    public fun replyFromJson(command: String): R = json.decodeFromString(replySerializer, command)
}

public interface RpcCommandRecipient {
    public fun <T, C, R> commandSender(
        commandPort: CommandPort<T, C, R>
    ): suspend (command: C) -> R
}

public interface RpcCommandChannel<C, R> {
    public suspend fun send(command: C): R
}

public interface RpcServiceDesc<T : Any> {
    public val commands: List<CommandPort<*, *, *>>
}

public interface RpcClient : RpcEndpoint, RpcCommandRecipient, IObservable {
    public val state: State

    public fun <T : Any> remoteService(path: String, service: RpcServiceDesc<T>) : T

    public enum class State {
        Disconnected, Connecting, Connected
    }
}

public interface RpcServer {
    public fun <T : Any> registerServiceHandler(path: String, service: RpcServiceDesc<T>, testService: T)
}

public interface RpcEndpoint {
    public fun <T: Any, C, R> listenOnCommandChannel(
        commandPort: CommandPort<T, C, R>, callback: suspend (command: C) -> R
    )
}

public interface RpcImpl<T> {
    public fun createSender(endpoint: RpcEndpoint): T
    public fun createReceiver(endpoint: RpcEndpoint, handler: T)
}