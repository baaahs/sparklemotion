package baaahs.visualizer.remote

import baaahs.Color
import baaahs.geom.Vector3F
import baaahs.io.ByteArrayReader
import baaahs.model.Model
import baaahs.net.Network
import baaahs.proto.Ports
import baaahs.sim.FakeDmxUniverse
import baaahs.sim.SimulationEnv
import baaahs.util.Clock
import baaahs.util.Logger
import baaahs.visualizer.*
import baaahs.visualizer.remote.RemoteVisualizerListener.Opcode.PixelColors
import baaahs.visualizer.remote.RemoteVisualizerListener.Opcode.PixelLocations
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlin.math.min

class RemoteVisualizerClient(
    link: Network.Link,
    address: Network.Address,
    private val visualizer: Visualizer,
    model: Model,
    clock: Clock
) :
    Network.WebSocketListener, CoroutineScope by MainScope() {

    private val simulationEnv = SimulationEnv {
        component(clock)
        component(FakeDmxUniverse())
        component<PixelArranger>(SwirlyPixelArranger(0.2f, 3f))
        component(visualizer)
    }

    private val entityVisualizers = model.allEntities.associate { entity ->
        val simulation = entity.createFixtureSimulation(simulationEnv)
        val entityVisualizer = simulation.entityVisualizer
        visualizer.addEntityVisualizer(entityVisualizer)
        entity.name to entityVisualizer
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
                val entityName = reader.readString()
                val pixelCount = reader.readInt()
                entityVisualizers[entityName]?.let { entityVisualizer ->
                    if (entityVisualizer is SurfaceVisualizer) {
                        val pixelLocations = (0 until pixelCount).map {
                            Vector3F.parse(reader).toVector3()
                        }.toTypedArray()
                        entityVisualizer.vizPixels = VizPixels(pixelLocations, entityVisualizer.surfaceGeometry.panelNormal)
                    }
                }
            }

            PixelColors -> { // Pixel colors.
                val entityName = reader.readString()
                val pixelCount = reader.readInt()
                entityVisualizers[entityName]?.let { entityVisualizer ->
                    if (entityVisualizer is SurfaceVisualizer) {
                        val vizPixels = entityVisualizer.vizPixels
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