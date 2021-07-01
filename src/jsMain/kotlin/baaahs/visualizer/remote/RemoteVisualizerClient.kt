package baaahs.visualizer.remote

import baaahs.Color
import baaahs.geom.Vector3F
import baaahs.io.ByteArrayReader
import baaahs.model.Model
import baaahs.net.Network
import baaahs.proto.Ports
import baaahs.util.Logger
import baaahs.visualizer.SurfaceGeometry
import baaahs.visualizer.Visualizer
import baaahs.visualizer.VizPixels
import baaahs.visualizer.remote.RemoteVisualizerListener.Opcode.PixelColors
import baaahs.visualizer.remote.RemoteVisualizerListener.Opcode.PixelLocations
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import three.js.Vector3
import kotlin.math.min

class RemoteVisualizerClient(
    link: Network.Link,
    address: Network.Address,
    private val visualizer: Visualizer,
    model: Model
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
        when (RemoteVisualizerListener.Opcode.get(reader.readByte())) {
            PixelLocations -> { // Pixel locations.
                val surfaceName = reader.readString()
                val pixelCount = reader.readInt()
                vizSurfaces[surfaceName]?.let { vizSurface ->
                    val pixelLocations = (0 until pixelCount).map {
                        Vector3F.parse(reader).let { Vector3(it.x, it.y, it.z) }
                    }.toTypedArray()
                    vizSurface.vizPixels = VizPixels(vizSurface, pixelLocations)
                }
            }

            PixelColors -> { // Pixel colors.
                val surfaceName = reader.readString()
                val pixelCount = reader.readInt()
                vizSurfaces[surfaceName]?.let { vizSurface ->
                    val vizPixels = vizSurface.vizPixels
                    vizPixels?.let {
                        val minPixCount = min(vizPixels.size, pixelCount)
                        for (i in 0 until minPixCount) {
                            vizPixels[i] = Color.parseWithoutAlpha(reader)
                        }
                    }
                }
            }
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