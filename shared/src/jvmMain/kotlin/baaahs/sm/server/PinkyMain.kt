package baaahs.sm.server

import baaahs.Pinky
import baaahs.Pluggables
import baaahs.di.JvmPinkyModule
import baaahs.di.JvmPlatformModule
import baaahs.di.PluginsModule
import baaahs.gl.GlBase
import baaahs.io.Fs
import baaahs.io.RealFs
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
import java.nio.file.Path
import kotlin.system.exitProcess

@ObsoleteCoroutinesApi
fun main(args: Array<String>) {
    PinkyMain(args).run()
}

@ObsoleteCoroutinesApi
class PinkyMain(private val args: Array<String>) {
    private val logger by lazy { Logger("PinkyMain") }

    fun run() {
        GlBase.manager // First thing, we need to wake up OpenGL on the main thread.

        logger.info { "Are you pondering what I'm pondering?" }
        logger.info { "Running JVM ${System.getProperty("java.vendor")} ${System.getProperty("java.version")} from ${System.getProperty("java.home")}." }

        val programName = PinkyMain::class.simpleName ?: "Pinky"
        val clock = SystemClock
        val pinkyInjector = koinApplication {
            logger(KoinLogger())

            modules(
                PluginsModule(Pluggables.plugins).getModule(),
                JvmPlatformModule(clock).getModule(),
                JvmPinkyModule(programName, args).getModule()
            )
        }

        val pinkyScope = pinkyInjector.koin.createScope<Pinky>()
        val pinky = pinkyScope.get<Pinky>()
        configureKtor(pinky, pinkyScope)

        logger.info { responses.random() }

        try {
            val pinkyArgs = pinkyScope.get<PinkyArgs>()
            Pinky.Companion.logger.info { "PinkyMain before runBlocking..." }
            runBlocking(pinkyScope.get<CoroutineDispatcher>(named("PinkyMainDispatcher")) + CoroutineName("Pinky Launcher")) {
                Pinky.Companion.logger.info { "PinkyMain after runBlocking..." }
                pinky.startAndRun {
                    Pinky.Companion.logger.info { "PinkyMain within startAndRun..." }

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

    private fun configureKtor(pinky: Pinky, pinkyScope: Scope) {
        val ktor = pinky.httpServer

        val dataDir = pinkyScope.get<Fs>().resolve(".")
        val firmwareDir = pinkyScope.get<Fs.File>(named("firmwareDir"))

        ktor.routing {
            get("") { redirect("/ui/") }
            get("monitor") { redirect("/monitor/") }
            get("midi") { redirect("midi/") }
            get("ui") { redirect("/ui/") }

            staticResources("", "htdocs")
            get("ui/") { respondWithResource("ui/index.html", "htdocs") }
            get("monitor/") { respondWithResource("monitor/index.html", "htdocs") }

            staticFiles("/data/", dataDir)
            staticFiles("/fw/", firmwareDir)
        }
        ktor.start()
    }

    companion object {
        private val responses = listOf(
            "I think so, Brain, but Lederhosen won't stretch that far.",
            "Yeah, but I thought Madonna already had a steady bloke!",
            "I think so, Brain, but what would goats be doing in red leather turbans?",
            "I think so, Brain... but how would we ever determine Sandra Bullock's shoe size?",
            "Yes, Brain, I think so. But how do we get Twiggy to pose with an electric goose?"
        )
    }
}

private fun Fs.File.asPath(): Path {
    return (fs as RealFs).resolve(this)
}
