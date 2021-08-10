package baaahs.api.ws

import baaahs.imaging.Bitmap
import baaahs.mapper.MappingSession
import baaahs.mapper.Storage
import baaahs.net.Network
import baaahs.proto.Ports
import baaahs.util.Logger
import com.soywiz.klock.DateTime
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.*

class WebSocketClient(link: Network.Link, address: Network.Address) : Network.WebSocketListener {
    private lateinit var tcpConnection: Network.TcpConnection
    private var connected = false
    private lateinit var responses: Channel<ByteArray>

    init {
        link.connectWebSocket(address, Ports.PINKY_UI_TCP, "/ws/api", this)
    }

    suspend fun listSessions(): List<String> {
        return WebSocketRouter.json.decodeFromJsonElement(ListSerializer(String.serializer()), sendCommand("listSessions"))
    }

    suspend fun saveImage(sessionStartTime: DateTime, name: String, bitmap: Bitmap): String {
        val filename = "${Storage.formatDateTime(sessionStartTime)}/$name.webp"
        val dataUrl = bitmap.toDataUrl()
        val startOfData = ";base64,"
        val i = dataUrl.indexOf(startOfData)
        if (i == -1) {
            throw IllegalArgumentException("failed to save image $dataUrl")
        }

        sendCommand(
            "saveImage",
            JsonPrimitive(filename),
            JsonPrimitive(dataUrl.substring(i + startOfData.length))
        )

        return filename
    }

    suspend fun saveSession(mappingSession: MappingSession) {
        sendCommand("saveSession",
            WebSocketRouter.json.encodeToJsonElement(MappingSession.serializer(), mappingSession)
        )
    }

    suspend fun loadSession(name: String): MappingSession {
        val response = sendCommand(
            "loadSession",
            WebSocketRouter.json.encodeToJsonElement(String.serializer(), name)
        )
        return WebSocketRouter.json.decodeFromJsonElement(MappingSession.serializer(), response)
    }

    private suspend fun sendCommand(command: String, vararg args: JsonElement): JsonElement {
        val content = buildJsonArray {
            add(command)
            args.forEach { add(it) }
        }
        while (!connected) {
            logger.warn { "Mapper not connected to Pinky…" }
            delay(50)
        }
        tcpConnection.send(WebSocketRouter.json.encodeToString(JsonArray.serializer(), content).encodeToByteArray())

        val responseJsonStr = responses.receive().decodeToString()
        try {
            val responseJson = WebSocketRouter.json.parseToJsonElement(responseJsonStr)
            val status = responseJson.jsonObject.getValue("status").jsonPrimitive
            val response = responseJson.jsonObject.getValue("response")
            when (status.contentOrNull) {
                "success" -> return response
                "error" -> throw RuntimeException(response.jsonPrimitive.contentOrNull)
            }
            return responseJson
        } catch (e: SerializationException) {
            logger.error { "can't parse response to $command $args: $responseJsonStr" }
            throw e
        }
    }

    override fun connected(tcpConnection: Network.TcpConnection) {
        logger.info { "Mapper connected to Pinky!" }
        this.tcpConnection = tcpConnection
        responses = Channel(1)
        connected = true
    }

    override suspend fun receive(tcpConnection: Network.TcpConnection, bytes: ByteArray) {
        logger.debug { "Received ${bytes.decodeToString()}" }
        responses.send(bytes)
    }

    override fun reset(tcpConnection: Network.TcpConnection) {
        if (::responses.isInitialized) responses.close()
        logger.info { "Mapper disconnected from Pinky!" }
    }

    companion object {
        val logger = Logger("MapperClient")
    }
}
