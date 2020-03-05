package baaahs.api.ws

import baaahs.Logger
import baaahs.net.Network
import kotlinx.serialization.json.*

class WebSocketRouter(handlers: HandlerBuilder.() -> Unit) : Network.WebSocketListener {
    companion object {
        val json = Json(JsonConfiguration.Stable)

        val logger = Logger("WebSocketEndpoint")
    }

    val handlerMap = HandlerBuilder(json).apply { handlers() }.handlerMap.toMap()

    override fun connected(tcpConnection: Network.TcpConnection) {
        logger.info { "Received connection from ${tcpConnection.fromAddress}" }
    }

    override fun receive(tcpConnection: Network.TcpConnection, bytes: ByteArray) {
        val args = json.parseJson(bytes.decodeToString()).jsonArray
        val command = args.first().contentOrNull
        var status = "success"
        var response: JsonElement

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
        tcpConnection.send(json.stringify(JsonElementSerializer, json {
            "status" to status
            "response" to response
        }).encodeToByteArray())
    }

    override fun reset(tcpConnection: Network.TcpConnection) {
        logger.info { "MapperEndpoint client disconnected from Pinky!" }
    }

    class HandlerBuilder(val json: Json) {
        val handlerMap = hashMapOf<String, (List<JsonElement>) -> JsonElement>()
        fun handle(command: String, handler: (List<JsonElement>) -> JsonElement) {
            handlerMap[command] = handler
        }
    }
}
