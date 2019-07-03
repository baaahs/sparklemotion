package baaahs.mapper

import baaahs.decodeBase64
import baaahs.net.Network
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.json.contentOrNull

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
        println("Command: $parts")
        when (command) {
            "saveImage" -> {
                val name = parts[1].primitive.contentOrNull
                val imageDataBase64 = parts[2].primitive.contentOrNull
                val imageData = decodeBase64(imageDataBase64!!)
                storage.saveImage(name!!, imageData)
            }
            "saveSession" -> {
                val mappingSession = json.fromJson(MappingSession.serializer(), parts[1])
                storage.saveSession(mappingSession)
            }
        }
    }

    override fun reset(tcpConnection: Network.TcpConnection) {
    }
}
