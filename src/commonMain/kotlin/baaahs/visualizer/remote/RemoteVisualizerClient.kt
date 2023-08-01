package baaahs.visualizer.remote

import baaahs.client.document.SceneManager
import baaahs.io.ByteArrayReader
import baaahs.model.Model
import baaahs.model.ModelUnit
import baaahs.net.Network
import baaahs.plugin.Plugins
import baaahs.scene.SceneMonitor
import baaahs.sim.FixtureSimulation
import baaahs.sim.SimulationEnv
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
    sceneManager: SceneManager,
    sceneMonitor: SceneMonitor,
    simulationEnv: SimulationEnv,
    private val plugins: Plugins
) : Network.WebSocketListener, CoroutineScope by MainScope() {
    private lateinit var fixtureSimulations: Map<String, FixtureSimulation>
    private lateinit var tcpConnection: Network.TcpConnection

    init {
        // Make sure SceneManager is initialized.
        sceneManager.facade

        sceneMonitor.addObserver(fireImmediately = true) {
            val openScene = sceneMonitor.openScene
            visualizer.clear()
            visualizer.units = openScene?.model?.units ?: ModelUnit.Centimeters

            fixtureSimulations = buildMap {
                openScene?.let { scene ->
                    val entityAdapter = EntityAdapter(simulationEnv, scene.model.units)

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
                ).fixtureConfig
                val fixtureSimulation = fixtureSimulations[entityName]
                fixtureSimulation?.itemVisualizer?.receiveFixtureConfig(remoteConfig)
            }

            FrameData -> {
                val entityName = reader.readString()
                val fixtureSimulation = fixtureSimulations[entityName]
                fixtureSimulation?.itemVisualizer?.receiveRemoteFrameData(reader)
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