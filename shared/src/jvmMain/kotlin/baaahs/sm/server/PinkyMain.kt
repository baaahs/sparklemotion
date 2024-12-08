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
        val pinkyInjector = koinApplication {
            logger(KoinLogger())

            modules(
                PluginsModule(Pluggables.plugins).getModule(),
                JvmPlatformModule(exceptionReporter, clock).getModule(),
                JvmPinkyModule(programName, args).getModule()
            )
        }

        val pinkyScope = pinkyInjector.koin.createScope<Pinky>()
        val pinky = pinkyScope.get<Pinky>()
        configureKtor(pinky, pinkyScope)
            .start()

        logger.info { responses.random() }

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
        } finally {
            logger.info { "Exiting." }
            exitProcess(1)
        }
    }
}