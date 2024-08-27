package baaahs.plugin.osc

import baaahs.plugin.*
import baaahs.util.Logger
import com.illposed.osc.OSCMessageEvent
import com.illposed.osc.OSCMessageListener
import com.illposed.osc.transport.OSCPort
import com.illposed.osc.transport.OSCPortIn
import io.ktor.server.application.*
import io.ktor.server.application.Plugin
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

import io.ktor.server.application.*
import io.ktor.util.*

class OscPlugin(
    private val pluginContext: PluginContext,
    private val oscPort: Int,
    private val wsPort: Int
) : OpenServerPlugin, OpenClientPlugin {
    override val packageName: String = id
    override val title: String = "OSC Plugin"

    private lateinit var receiver: OSCPortIn
    private val messageChannel = Channel<String>(Channel.UNLIMITED)

    fun initialize() {
        receiver = OSCPortIn(oscPort)

        val listener = OSCMessageListener { event ->
            val jsonMessage = event.toJson()
            logger.debug { "Message received: $jsonMessage" }
            pluginContext.scope.launch {
                messageChannel.send(jsonMessage)
            }
        }

        receiver.dispatcher.addListener(null, listener) // null means listen to all messages
        receiver.startListening()

        startWebSocketServer()

        logger.info { "OSC receiver started on port $oscPort" }
        logger.info { "WebSocket server started on port $wsPort" }
    }

    private fun startWebSocketServer() {
        embeddedServer(Netty, port = wsPort) {
            install(WebSockets)
            routing {
                webSocket("/") {
                    try {
                        for (message in messageChannel) {
                            outgoing.send(Frame.Text(message))
                        }
                    } finally {
                        messageChannel.cancel()
                    }
                }
            }
        }.start(wait = false)
    }

    private fun OSCMessageEvent.toJson(): String {
        val messageMap = mapOf(
            "address" to message.address,
            "arguments" to message.arguments
        )
        return Json.encodeToString(messageMap)
    }

    override fun release() {
        if (::receiver.isInitialized) {
            receiver.stopListening()
            receiver.close()
        }
        messageChannel.close()
    }

    companion object : Plugin<Application, OscPlugin.Configuration, OscPlugin> {
        override val key = AttributeKey<OscPlugin>("OscPlugin")
        override val id = "baaahs.Osc"
        private val logger = Logger<OscPlugin>()

        override fun getArgs(parser: ArgParser) = Args(parser)

        override fun openForServer(pluginContext: PluginContext, args: Args): OpenServerPlugin =
            OscPlugin(pluginContext, args.oscPort, args.wsPort).apply { initialize() }

        override fun openForClient(pluginContext: PluginContext): OpenClientPlugin =
            OscPlugin(pluginContext, OSCPort.DEFAULT_SC_OSC_PORT, 8080)

        override fun install(pipeline: Application, configure: Configuration.() -> Unit): OscPlugin {
            val config = Configuration().apply(configure)
            return OscPlugin(PluginContext(pipeline.clock, pipeline.pubSub), config.oscPort, config.wsPort)
                .apply { initialize() }
        }
    }

    class Configuration {
        var oscPort: Int = OSCPort.DEFAULT_SC_OSC_PORT
        var wsPort: Int = 8080
    }

    class Args(parser: ArgParser) {
        val oscPort by parser.option(
            ArgType.Int,
            shortName = "p",
            description = "Port to listen for OSC messages"
        ).default(OSCPort.DEFAULT_SC_OSC_PORT)

        val wsPort by parser.option(
            ArgType.Int,
            shortName = "w",
            description = "Port for WebSocket server"
        ).default(8080)
    }
}
