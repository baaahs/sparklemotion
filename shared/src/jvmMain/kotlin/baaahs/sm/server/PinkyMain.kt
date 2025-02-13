package baaahs.sm.server

import baaahs.Pinky
import baaahs.Pluggables
import baaahs.di.JvmPinkyModule
import baaahs.di.JvmPlatformModule
import baaahs.di.PluginsModule
import baaahs.gl.GlBase
import baaahs.sm.brain.ProdBrainSimulator
import baaahs.util.KoinLogger
import baaahs.util.Logger
import baaahs.util.SystemClock
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope
import org.koin.dsl.koinApplication
import kotlin.system.exitProcess

@ObsoleteCoroutinesApi
fun main(args: Array<String>) {
    PinkyMain(args).run()
}

@ObsoleteCoroutinesApi
class PinkyMain(private val args: Array<String>) : BasePinkyMain() {
    fun run() {
        GlBase.manager // First thing, we need to wake up OpenGL on the main thread.

        logger.info { "Are you pondering what I'm pondering?" }
        logger.info { "Running JVM ${System.getProperty("java.vendor")} ${System.getProperty("java.version")} from ${System.getProperty("java.home")}." }

        val programName = PinkyMain::class.simpleName ?: "Pinky"

        val clock = SystemClock
        val exceptionReporter = object : ExceptionReporter {
            override fun reportException(context: String, throwable: Throwable) {
                Logger(context).error(throwable) { throwable.message ?: "Unknown error." }
            }
        }
        val serverInjector = koinApplication {
            logger(KoinLogger())

            modules(
                PluginsModule(Pluggables.plugins).getModule(),
                JvmPlatformModule(exceptionReporter, clock).getModule(),
                JvmPinkyModule(programName, args).getModule()
            )
        }

        // TODO: Too much of Pinky still starts up for subcommands.
        // TODO: More granular Koin modules, add OpenPlugin.start() and .stop(), etc.
        val pinkyScope = serverInjector.koin.createScope<Pinky>()
        val pinkyArgs = pinkyScope.get<PinkyArgs>()
        val subcommand = pinkyArgs.subcommand
        if (subcommand == null) {
            runServer(pinkyArgs, pinkyScope)
        } else {
            try {
                runBlocking {
                    with(subcommand) { pinkyScope.execute() }
                }
            } catch (e: Throwable) {
                logger.error(e) { "Failed to run command ${subcommand.name}." }
                exitProcess(1)
            } finally {
                exitProcess(0)
            }
        }
    }

    private fun runServer(pinkyArgs: PinkyArgs, pinkyScope: Scope) {
        val pinky = pinkyScope.get<Pinky>()

        logger.info { "Are you pondering what I'm pondering?" }

        configureKtor(pinky, pinkyScope)
            .start()

        logger.info { responses.random() }

        val pinkyDispatcher: CoroutineDispatcher = pinkyScope.get(named("PinkyMainDispatcher"))
        try {
            val pinkyArgs = pinkyScope.get<PinkyArgs>()
            runBlocking(pinkyScope.get<CoroutineDispatcher>(named("PinkyMainDispatcher")) + CoroutineName("Pinky Launcher")) {
                pinky.startAndRun {
                    if (pinkyArgs.simulateBrains) {
                        pinkyScope.get<ProdBrainSimulator>().enableSimulation()
                    }
                }
            }
        } catch (e: Throwable) {
            logger.error(e) { "Failed to start Pinky." }
            exitProcess(1)
        } finally {
            logger.info { "Exiting." }
            exitProcess(0)
        }
    }
}