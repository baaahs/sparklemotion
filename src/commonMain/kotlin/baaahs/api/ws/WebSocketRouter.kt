package baaahs.api.ws

import baaahs.Logger
import baaahs.net.Network
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.serialization.json.*
import kotlin.coroutines.CoroutineContext

class WebSocketRouter(
    val coroutineContext: CoroutineContext,
    handlers: HandlerBuilder.() -> Unit
) : Network.WebSocketListener {
    private val handlerScope = CoroutineScope(coroutineContext)

    companion object {
        val json = Json
        val logger = Logger("WebSocketEndpoint")
    }

    private val handlerMap = HandlerBuilder(json).apply { handlers() }.handlerMap.toMap()

    override fun connected(tcpConnection: Network.TcpConnection) {
        logger.info { "Received connection from ${tcpConnection.fromAddress}" }
    }

    override fun receive(tcpConnection: Network.TcpConnection, bytes: ByteArray) {
        val args = json.parseToJsonElement(bytes.decodeToString()).jsonArray
        val command = args.first().jsonPrimitive.contentOrNull
        var status = "success"
        var response: JsonElement

        handlerScope.launch {
            try {
                val handler = handlerMap[command]
                    ?: throw UnsupportedOperationException("unknown command \"$command\"")

                response = handler(args.toList())
            } catch (e: Exception) {
                status = "error"
                response = JsonPrimitive(e.toString())
                logger.error { "Command failed: $args" }
                logger.error { e.toString() }
            }

            logger.debug { "Command: $args -> $status $response" }
            tcpConnection.send(json.encodeToString(JsonElement.serializer(), buildJsonObject {
                put("status", status)
                put("response", response)
            }).encodeToByteArray())
        }
    }

    override fun reset(tcpConnection: Network.TcpConnection) {
        logger.info { "MapperEndpoint client disconnected from Pinky!" }
    }

    class HandlerBuilder(val json: Json) {
        val handlerMap = hashMapOf<String, suspend (List<JsonElement>) -> JsonElement>()
        fun handle(command: String, handler: suspend (List<JsonElement>) -> JsonElement) {
            handlerMap[command] = handler
        }
    }
}
