package baaahs.mapper

import baaahs.api.ws.WebSocketClient
import baaahs.imaging.Bitmap
import baaahs.net.Network
import baaahs.plugin.Plugins
import baaahs.util.Clock
import kotlinx.datetime.Instant

class MapperBackend(
    plugins: Plugins,
    clock: Clock,
    link: Network.Link,
    pinkyAddress: Network.Address,
    private val udpSockets: UdpSockets
) {
    private val webSocketClient = WebSocketClient(plugins, clock, link, pinkyAddress)

    fun adviseMapperStatus(isRunning: Boolean) {
        udpSockets.adviseMapperStatus(isRunning)
    }

    suspend fun listSessions() =
        webSocketClient.listSessions()

    suspend fun loadSession(name: String) =
        webSocketClient.loadSession(name)

    suspend fun listImages(sessionName: String?) =
        webSocketClient.listImages(sessionName)

    suspend fun saveImage(sessionStartTime: Instant, name: String, bitmap: Bitmap): String =
        webSocketClient.saveImage(sessionStartTime, name, bitmap)

    suspend fun saveSession(mappingSession: MappingSession): String =
        webSocketClient.saveSession(mappingSession)

    suspend fun getImageUrl(filename: String) =
        webSocketClient.getImageUrl(filename)
}