package baaahs.visualizer.remote

import baaahs.io.ByteArrayReader
import baaahs.model.Model
import baaahs.net.Network
import baaahs.plugin.Plugins
import baaahs.sim.FakeDmxUniverse
import baaahs.sim.SimulationEnv
import baaahs.sm.brain.proto.Ports
import baaahs.util.Clock
import baaahs.util.Logger
import baaahs.visualizer.PixelArranger
import baaahs.visualizer.SwirlyPixelArranger
import baaahs.visualizer.Visualizer
import baaahs.visualizer.remote.RemoteVisualizerServer.Opcode.FixtureInfo
import baaahs.visualizer.remote.RemoteVisualizerServer.Opcode.FrameData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope

class RemoteVisualizerClient(
    link: Network.Link,
    address: Network.Address,
    private val visualizer: Visualizer,
    model: Model,
    clock: Clock,
    private val plugins: Plugins
) :
    Network.WebSocketListener, CoroutineScope by MainScope() {

    private val simulationEnv = SimulationEnv {
        component(clock)
        component(FakeDmxUniverse())
        component<PixelArranger>(SwirlyPixelArranger(0.2f, 3f))
        component(visualizer)
    }

    private val fixtureSimulations = model.allEntities.associate { entity ->
        val simulation = entity.createFixtureSimulation(simulationEnv)
        val entityVisualizer = simulation.entityVisualizer
        visualizer.addEntityVisualizer(entityVisualizer)
        entity.name to simulation
    }

    private lateinit var tcpConnection: Network.TcpConnection

    init {
        link.connectWebSocket(address, Ports.PINKY_UI_TCP, "/ws/visualizer", this)
    }

    override fun connected(tcpConnection: Network.TcpConnection) {
        this.tcpConnection = tcpConnection
    }

    override suspend fun receive(tcpConnection: Network.TcpConnection, bytes: ByteArray) {
        val reader = ByteArrayReader(bytes)
        when (RemoteVisualizerServer.Opcode.get(reader.readByte())) {
            FixtureInfo -> {
                val entityName = reader.readString()
                val fixtureConfig = plugins.json.decodeFromString(
                    FixtureConfigWrapper.serializer(),
                    reader.readString()
                ).fixtureConfig
                val fixtureSimulation = fixtureSimulations[entityName]
                fixtureSimulation?.let {
                    fixtureConfig.receiveRemoteVisualizationFixtureInfo(reader, it)
                }
            }

            FrameData -> {
                val entityName = reader.readString()
                val fixtureSimulation = fixtureSimulations[entityName]
                fixtureSimulation?.receiveRemoteVisualizationFrameData(reader)
            }
        }
    }

    override fun reset(tcpConnection: Network.TcpConnection) {
        logger.info { "Visualizer disconnected from Pinky!" }
    }

    fun close() {
        tcpConnection.close()
    }

    companion object {
        val logger = Logger("VisualizerListenerClient")
    }
}