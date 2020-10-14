package baaahs.visualizer

import baaahs.Color
import baaahs.geom.Vector3F
import baaahs.io.ByteArrayReader
import baaahs.model.Model
import baaahs.net.Network
import baaahs.proto.Ports
import baaahs.util.Logger
import info.laht.threekt.math.Vector3
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlin.math.min

class VisualizerListenerClient(
    link: Network.Link,
    address: Network.Address,
    private val visualizer: Visualizer,
    model: Model<*>
) :
    Network.WebSocketListener, CoroutineScope by MainScope() {

    private val vizSurfaces = model.allSurfaces.associate { surface ->
        surface.name to visualizer.addSurface(SurfaceGeometry(surface))
    }

    private lateinit var tcpConnection: Network.TcpConnection

    init {
        link.connectWebSocket(address, Ports.PINKY_UI_TCP, "/ws/visualizer", this)

    }

    override fun connected(tcpConnection: Network.TcpConnection) {
        this.tcpConnection = tcpConnection
    }

    override fun receive(tcpConnection: Network.TcpConnection, bytes: ByteArray) {
        val reader = ByteArrayReader(bytes)
        val op = reader.readByte().toInt()
        when (op) {
            0 -> { // Pixel locations.
                val surfaceName = reader.readString()
                val pixelCount = reader.readInt()
                vizSurfaces[surfaceName]?.let { vizSurface ->
                    val pixelLocations = (0 until pixelCount).map {
                        Vector3F.parse(reader).let { Vector3(it.x, it.y, it.z) }
                    }.toTypedArray()
                    vizSurface.vizPixels = VizPixels(vizSurface, pixelLocations)
                }
            }

            1 -> { // Pixel colors.
                val surfaceName = reader.readString()
                val pixelCount = reader.readInt()
                vizSurfaces[surfaceName]?.let { vizSurface ->
                    val vizPixels = vizSurface.vizPixels
                    vizPixels?.let {
                        val minPixCount = min(vizPixels.size, pixelCount)
                        var byteOff = 0
                        for (i in 0 until minPixCount) {
                            vizPixels[i] = Color.parseWithoutAlpha(reader)
                        }
                    }
                }
            }
            else -> throw UnsupportedOperationException("huh?")
        }
    }

    override fun reset(tcpConnection: Network.TcpConnection) {
        logger.info { "Visualizer disconnected from Pinky!" }
    }

    fun close() {
//        tcpConnection.close()
    }

    companion object {
        val logger = Logger("VisualizerListenerClient")
    }
}