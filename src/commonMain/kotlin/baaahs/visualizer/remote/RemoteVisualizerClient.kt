package baaahs.visualizer.remote

import baaahs.io.ByteArrayReader
import baaahs.model.Model
import baaahs.net.Network
import baaahs.plugin.Plugins
import baaahs.scene.SceneMonitor
import baaahs.sim.FixtureSimulation
import baaahs.sm.brain.proto.Ports
import baaahs.ui.addObserver
import baaahs.util.Logger
import baaahs.visualizer.EntityAdapter
import baaahs.visualizer.IVisualizer
import baaahs.visualizer.remote.RemoteVisualizerServer.Opcode.FixtureInfo
import baaahs.visualizer.remote.RemoteVisualizerServer.Opcode.FrameData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope

class RemoteVisualizerClient(
    link: Network.Link,
    address: Network.Address,
    private val visualizer: IVisualizer,
    sceneMonitor: SceneMonitor,
    entityAdapter: EntityAdapter,
    private val plugins: Plugins
) : Network.WebSocketListener, CoroutineScope by MainScope() {
    private lateinit var fixtureSimulations: Map<String, FixtureSimulation>
    private lateinit var tcpConnection: Network.TcpConnection

    init {
        sceneMonitor.addObserver(fireImmediately = true) {
            visualizer.clear()

            val openScene = sceneMonitor.openScene
            fixtureSimulations = buildMap {
                openScene?.let { scene ->
                    scene.model.visit { entity ->
                        createFixtureSimulation(entity, entityAdapter)?.let { simulation ->
                            val entityVisualizer = simulation.itemVisualizer
                            visualizer.add(entityVisualizer)
                            put(entity.name, simulation)
                        }
                    }
                }
            }
        }

        link.connectWebSocket(address, Ports.PINKY_UI_TCP, "/ws/visualizer", this)
    }

    private fun createFixtureSimulation(
        entity: Model.Entity,
        entityAdapter: EntityAdapter
    ) = entity.createFixtureSimulation(entityAdapter)

    override fun connected(tcpConnection: Network.TcpConnection) {
        this.tcpConnection = tcpConnection
    }

    override suspend fun receive(tcpConnection: Network.TcpConnection, bytes: ByteArray) {
        if (!::fixtureSimulations.isInitialized) return

        val reader = ByteArrayReader(bytes)
        when (RemoteVisualizerServer.Opcode.get(reader.readByte())) {
            FixtureInfo -> {
                val entityName = reader.readString()
                val remoteConfig = plugins.json.decodeFromString(
                    RemoteConfigWrapper.serializer(),
                    reader.readString()
                ).remoteConfig
                val fixtureSimulation = fixtureSimulations[entityName]
                fixtureSimulation?.updateVisualizerWith(remoteConfig)
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