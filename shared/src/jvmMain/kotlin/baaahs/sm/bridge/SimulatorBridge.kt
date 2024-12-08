package baaahs.sm.bridge

import baaahs.Pluggables
import baaahs.PubSub
import baaahs.net.JvmNetwork
import baaahs.plugin.PluginContext
import baaahs.plugin.SimulatorPlugin
import baaahs.sm.brain.proto.Ports
import baaahs.sm.server.ExceptionReporter
import baaahs.util.SystemClock
import io.ktor.server.websocket.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.newSingleThreadContext

/**
 * The Simulator Bridge is a daemon that can perform work in a JVM on behalf of the simulator running in a browser.
 */
class SimulatorBridge {
    private val pubSub: PubSub.Server

    init {
        val context = newSingleThreadContext("Bridge Main")
        val network = JvmNetwork(CoroutineScope(Dispatchers.IO), ExceptionReporter.RETHROW)
        val link = network.link("bridge")
        val httpServer = link.createHttpServer(Ports.SIMULATOR_BRIDGE_TCP)
        val handlerScope = CoroutineScope(context)
        pubSub = PubSub.Server(httpServer, handlerScope)
    }

    private val plugins = Pluggables.plugins.filterIsInstance<SimulatorPlugin>().mapNotNull {
        val simulatorPlugin = it.openForSimulator()
        simulatorPlugin.getBridgePlugin(PluginContext(SystemClock, pubSub))
    }

    init {
        pubSub.listenForConnections { connectionFromClient ->
            plugins.forEach { bridgePlugin ->
                bridgePlugin.onConnectionOpen(connectionFromClient)
            }
        }
    }
//    private val soundAnalyzer = JvmSoundAnalyzer()

//    private val httpServerz = embeddedServer(Netty, Ports.SIMULATOR_BRIDGE_TCP) {
//        install(WebSockets) {
//            pingPeriod = Duration.ofSeconds(15)
//            timeout = Duration.ofSeconds(15)
//            maxFrameSize = Long.MAX_VALUE
//            masking = false
//        }
//
//        routing {
//            webSocket("/bridge") {
//                println("Connection from ${this.call.request.host()}…")
//                webSocketConnections.add(this)
//
//                val tcpConnection = object : Network.TcpConnection {
//                    override val fromAddress: Network.Address get() = TODO("not implemented")
//                    override val toAddress: Network.Address get() = TODO("not implemented")
//                    override val port: Int get() = TODO("not implemented")
//
//                    override fun send(bytes: ByteArray) {
//                        val frame = Binary(true, ByteBuffer.wrap(bytes.clone()))
//                        JvmNetwork.networkScope.launch {
//                            this@webSocket.send(frame)
//                            this@webSocket.flush()
//                        }
//                    }
//
//                    override fun close() {
//                        JvmNetwork.networkScope.launch {
//                            this@webSocket.close()
//                        }
//                    }
//                }
//
//                plugins.forEach { bridgePlugin ->
//                    bridgePlugin.onConnectionOpen(tcpConnection)
//                }
//
//                sendFrequencies(this)
//
//                for (frame in incoming) {
//                    when (frame) {
//                        is Text -> {
//                            val text = frame.readText()
//    "listAudioInputs" -> {
//        val inputs = soundAnalysisPlatform.listAudioInputs().map { audioInput ->
//            BridgeAudioInput(audioInput.id, audioInput.title)
//        }
//        val inputsJson = json.encodeToString(
//            ListSerializer(BridgeAudioInput.serializer()),
//            inputs
//        )
//        send(toWsMessage("onAudioInputs", buildJsonObject {  }))
//    }
//                            if (text.equals("bye", ignoreCase = true)) {
//                                close(CloseReason(NORMAL, "Client said BYE"))
//                                webSocketConnections.remove(this)
//                            }
//                        }
//                        else -> error("Unsupported frame type ${frame::class}")
//                    }
//                }
//
//                plugins.forEach { bridgePlugin ->
//                    bridgePlugin.onConnectionClose(tcpConnection)
//                }
//
//                webSocketConnections.remove(this)
//            }
//        }
//    }

    private fun sendFrequencies(connection: WebSocketServerSession) {
//        connection.outgoing.trySend(
//            Text(
//                toWsMessage(
//                    "soundFrequencies",
//                    OpenBridgePlugin.json.encodeToJsonElement(
//                        ListSerializer(Float.serializer()),
//                        soundAnalyzer.frequencies.toList()
//                    )
//                )
//            )
//        ).isSuccess
    }

    fun run() {
//        soundAnalyzer.listen(object : SoundAnalyzer.AnalysisListener {
//            override fun onSample(analysis: SoundAnalyzer.Analysis) {
//                // todo: don't send more frequently than framerate
////                sendToClients(
////                    "soundMagnitudes",
////                    json.encodeToJsonElement(ListSerializer(Float.serializer()), analysis.magnitudes.toList())
////                )
//            }
//        })

//        httpServer.start(true)
    }
}

fun main() {
    SimulatorBridge().run()
}