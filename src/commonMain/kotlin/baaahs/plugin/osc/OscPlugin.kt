package baaahs.plugin.osc

import baaahs.plugin.*
import kotlinx.cli.ArgParser

class OscPlugin : OpenServerPlugin {
    private val oscBridge: OscBridge = OscBridge()

    init {
        start()
    }

    fun start() {
        oscBridge.start()
    }

    override val packageName: String = id
    override val title: String = "Sonic Runway OSC"


    companion object : Plugin<Any>, SimulatorPlugin {
        override val id = "baaahs.Osc"
        override fun getArgs(parser: ArgParser): Any = Any()

        override fun openForClient(pluginContext: PluginContext): OpenClientPlugin {
            TODO("Not yet implemented")
        }

        override fun openForServer(pluginContext: PluginContext, args: Any): OpenServerPlugin {
            println("Open OSC plugin for server")
            return OscPlugin()
        }

        override fun openForSimulator(): OpenSimulatorPlugin {
            TODO("Not yet implemented")
        }


//        val soundAnalysisStruct = GlslType.Struct(
//            "SoundAnalysis",
//            "bucketCount" to GlslType.Int,
//            "sampleHistoryCount" to GlslType.Int,
//            "buckets" to GlslType.Sampler2D,
//            "maxMagnitude" to GlslType.Float
//        )
    }

}


expect class OscBridge() {
    fun start()

}