package baaahs.visualizer.remote

import baaahs.client.document.SceneManager
import baaahs.controller.ControllerId
import baaahs.controller.sim.ControllerSimulator
import baaahs.io.ByteArrayReader
import baaahs.net.Network
import baaahs.plugin.Plugins
import baaahs.sim.FakeDmxUniverse
import baaahs.sim.FixtureSimulation
import baaahs.sim.SimulationEnv
import baaahs.sm.brain.proto.Ports
import baaahs.ui.addObserver
import baaahs.util.Clock
import baaahs.util.Logger
import baaahs.visualizer.EntityAdapter
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
    sceneManager: SceneManager,
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
    private val entityAdapter = EntityAdapter(simulationEnv)

    private lateinit var fixtureSimulations: Map<String, FixtureSimulation>
    private lateinit var tcpConnection: Network.TcpConnection

    init {
        sceneManager.facade.addObserver(fireImmediately = true) {
            visualizer.facade.clear()

            val openScene = sceneManager.facade.openScene
            fixtureSimulations = buildMap {
                openScene?.let { scene ->
                    scene.model.visit { entity ->
                        entity.createFixtureVisualizer(
                            simulationEnv,
                            entityAdapter,
                            object : ControllerSimulator {
                                override val controllerId: ControllerId
                                    get() = TODO("not implemented")

                                override fun start() {
                                    TODO("not implemented")
                                }

                                override fun stop() {
                                    TODO("not implemented")
                                }
                            }
                        )?.let { simulation ->
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
                fixtureSimulation?.let {
                    remoteConfig.receiveRemoteVisualizationFixtureInfo(reader, it)
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