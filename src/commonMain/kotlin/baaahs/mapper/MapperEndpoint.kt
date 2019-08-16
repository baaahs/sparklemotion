package baaahs.mapper

import baaahs.decodeBase64
import baaahs.logger
import baaahs.net.Network
import kotlinx.serialization.json.*
import kotlinx.serialization.list
import kotlinx.serialization.serializer

class MapperEndpoint(val storage: Storage) : Network.WebSocketListener {
    companion object {
        val json = Json(JsonConfiguration.Stable)
    }

    override fun connected(tcpConnection: Network.TcpConnection) {
        println("Received connection from ${tcpConnection.fromAddress}")
    }

    @UseExperimental(ExperimentalStdlibApi::class)
    override fun receive(tcpConnection: Network.TcpConnection, bytes: ByteArray) {
        val parts = json.parseJson(bytes.decodeToString()).jsonArray
        val command = parts.first().contentOrNull
        var status = "success"
        var response: JsonElement

        try {
            when (command) {
                "listSessions" -> {
                    response = json.toJson(String.serializer().list, storage.listSessions())
                }
                "saveImage" -> {
                    val name = parts[1].primitive.contentOrNull
                    val imageDataBase64 = parts[2].primitive.contentOrNull
                    val imageData = decodeBase64(imageDataBase64!!)
                    storage.saveImage(name!!, imageData)
                    response = JsonNull
                }
                "saveSession" -> {
                    val mappingSession = json.fromJson(MappingSession.serializer(), parts[1])
                    storage.saveSession(mappingSession)
                    response = JsonNull
                }
                else -> throw UnsupportedOperationException("unknown command \"$command\"")
            }
        } catch (e: Exception) {
            status = "error"
            response = JsonPrimitive(e.toString())
            logger.error("Command failed: $parts")
            logger.error(e.toString())
        }

        println("Command: $parts -> $status $response")
        tcpConnection.send(json.stringify(JsonElementSerializer, json {
            "status" to status
            "response" to response
        }).encodeToByteArray())
    }

    override fun reset(tcpConnection: Network.TcpConnection) {
        println("MapperEndpoint client disconnected from Pinky!")
    }
}
