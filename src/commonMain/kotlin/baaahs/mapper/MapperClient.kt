package baaahs.mapper

import baaahs.imaging.Bitmap
import baaahs.net.Network
import com.soywiz.klock.DateTime
import kotlinx.serialization.json.JsonArraySerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonArray

@UseExperimental(ExperimentalStdlibApi::class)
class MapperClient(link: Network.Link, address: Network.Address) : Network.WebSocketListener {
    val tcpConnection = link.connectWebSocket(address, 0, "/ws/mapper", this)

    fun saveImage(sessionStartTime: DateTime, name: String, bitmap: Bitmap): String {
        val filename = "${Storage.formatDateTime(sessionStartTime)}/$name.webp"
        val dataUrl = bitmap.toDataUrl()
        val startOfData = ";base64,"
        val i = dataUrl.indexOf(startOfData)
        if (i > -1) {
            sendCommand(
                "saveImage",
                JsonPrimitive(filename),
                JsonPrimitive(dataUrl.substring(i + startOfData.length))
            )
        } else {
            throw IllegalArgumentException("failed to save image $dataUrl")
        }
        return filename
    }

    fun saveSession(mappingSession: MappingSession) {
        sendCommand("saveSession", MapperEndpoint.json.toJson(MappingSession.serializer(), mappingSession))
    }

    private fun sendCommand(command: String, vararg args: JsonElement) {
        val content = jsonArray {
            +command
            args.forEach { +it }
        }
        tcpConnection.send(MapperEndpoint.json.stringify(JsonArraySerializer, content).encodeToByteArray())
    }

    override fun connected(tcpConnection: Network.TcpConnection) {
        println("Mapper connected to Pinky!")
    }

    override fun receive(tcpConnection: Network.TcpConnection, bytes: ByteArray) {
        println("Received ${bytes.decodeToString()}")
    }

    override fun reset(tcpConnection: Network.TcpConnection) {
        TODO("MapperClient.reset not implemented")
    }
}
