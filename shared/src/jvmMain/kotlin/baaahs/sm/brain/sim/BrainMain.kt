package baaahs.sm.brain.sim

import baaahs.io.RealFs
import baaahs.net.JvmNetwork
import baaahs.plugin.Plugins
import baaahs.sm.brain.sim.JvmPixelsDisplay.PixelLayout
import baaahs.sm.server.ExceptionReporter
import baaahs.util.SystemClock
import baaahs.util.globalLaunch
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.io.File

suspend fun main(args: Array<String>) {
    val argParser = ArgParser(BrainMain::class.simpleName ?: "Brain")
    val brainArgs = BrainMain.Args(argParser)
    argParser.parse(args)
    BrainMain(brainArgs).run().join()
}

class BrainMain(private val args: Args) {
    fun run() = globalLaunch {
        val plugins = Plugins.safe(Plugins.dummyContext)
        val fs = RealFs("Files", File(".").toPath())
//        val sceneFile = fs.resolve(args.scene ?: error("No scene specified."))
//        val model = plugins.sceneStore.load(sceneFile)
//            ?.open()?.model
//            ?: error("No such scene file: \"$sceneFile\"")

        val network = JvmNetwork(CoroutineScope(Dispatchers.IO), ExceptionReporter.RETHROW)
        val brainId = args.brainId ?: JvmNetwork.myAddress.toString()
        val startingPixelLayout = PixelLayout(20, 20)
        val brainSimulator = BrainSimulator(
            brainId, network, JvmPixelsDisplay(startingPixelLayout).pixels, SystemClock, CoroutineScope(Dispatchers.Default)
        )

//        val mySurface = if (args.anonymous) {
//            null
//        } else if (args.entityName == null) {
//            if (Random.nextBoolean())
//                model.allEntities.filterIsInstance<Model.Surface>().random()
//            else null
//        } else {
//            args.entityName?.let { model.findEntityByName(it) }
//        }
//        println("I'll be ${mySurface ?: "anonymous"}!")
//        mySurface?.let { brainSimulator.forcedFixtureName(mySurface.name) }

        brainSimulator.start()
    }

    class Args(parser: ArgParser) {
        val scene by parser.option(ArgType.String)

        val brainId by parser.option(ArgType.String, description = "brain ID")

        val entityName by parser.option(ArgType.String, description = "entity name")

        val anonymous by parser.option(ArgType.Boolean, description = "anonymous surface")
            .default(false)
    }
}

