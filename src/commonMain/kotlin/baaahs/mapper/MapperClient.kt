package baaahs.mapper

import baaahs.Logger
import baaahs.imaging.Bitmap
import baaahs.net.Network
import com.soywiz.klock.DateTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.json.*
import kotlinx.serialization.list
import kotlinx.serialization.serializer

@UseExperimental(ExperimentalStdlibApi::class)
class MapperClient(link: Network.Link, address: Network.Address) : Network.WebSocketListener,
    CoroutineScope by MainScope() {
    private lateinit var tcpConnection: Network.TcpConnection
    private var connected = false
    private lateinit var responses: Channel<ByteArray>

    init {
        link.connectWebSocket(address, 0, "/ws/mapper", this)
    }

    suspend fun listSessions(): List<String> {
        return MapperEndpoint.json.fromJson(String.serializer().list, sendCommand("listSessions"))
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
        sendCommand("saveSession", MapperEndpoint.json.toJson(MappingSession.serializer(), mappingSession))
    }

    private suspend fun sendCommand(command: String, vararg args: JsonElement): JsonElement {
        val content = jsonArray {
            +command
            args.forEach { +it }
        }
        while (!connected) {
            delay(5)
        }
        tcpConnection.send(MapperEndpoint.json.stringify(JsonArraySerializer, content).encodeToByteArray())

        val responseJsonStr = responses.receive().decodeToString()
        try {
            val responseJson = MapperEndpoint.json.parseJson(responseJsonStr)
            val status = responseJson.jsonObject.getPrimitive("status")
            val response = responseJson.jsonObject.getValue("response")
            when (status.contentOrNull) {
                "success" -> return response
                "error" -> throw RuntimeException(response.contentOrNull)
            }
            return responseJson
        } catch (e: JsonParsingException) {
            logger.error { "can't parse response to $command $args: $responseJsonStr" }
            throw e
        }
    }

    override fun connected(tcpConnection: Network.TcpConnection) {
        println("Mapper connected to Pinky!")
        this.tcpConnection = tcpConnection
        responses = Channel(1)
        connected = true
    }

    override fun receive(tcpConnection: Network.TcpConnection, bytes: ByteArray) {
        println("Received ${bytes.decodeToString()}")
        launch { responses.send(bytes) }
    }

    override fun reset(tcpConnection: Network.TcpConnection) {
        responses.close()
        println("Mapper disconnected from Pinky!")
    }

    companion object {
        val logger = Logger("MapperClient")
    }
}
