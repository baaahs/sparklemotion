package baaahs.rpc

import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule

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

public interface RpcClient {
    public fun <C, R> commandSender(commandPort: CommandPort<C, R>): suspend (command: C) -> R
}

public interface RpcServer {
    public fun <C, R> listenOnCommandChannel(
        commandPort: CommandPort<C, R>, callback: suspend (command: C) -> R
    )
}

public interface RpcImpl<T> {
    public fun createSender(client: RpcClient): T
    public fun createReceiver(server: RpcServer, handler: T)
}