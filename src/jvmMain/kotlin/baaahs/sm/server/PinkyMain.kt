package baaahs.sm.server

import baaahs.Pinky
import baaahs.Pluggables
import baaahs.di.JvmPinkyModule
import baaahs.di.JvmPlatformModule
import baaahs.di.PluginsModule
import baaahs.gl.GlBase
import baaahs.io.Fs
import baaahs.io.RealFs
import baaahs.net.JvmNetwork
import baaahs.sm.brain.ProdBrainSimulator
import baaahs.util.KoinLogger
import baaahs.util.Logger
import baaahs.util.SystemClock
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.pipeline.*
import kotlinx.coroutines.CoroutineDispatcher
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

        val programName = PinkyMain::class.simpleName ?: "Pinky"

        val clock = SystemClock
        val serverInjector = koinApplication {
            logger(KoinLogger())

            modules(
                PluginsModule(Pluggables.plugins).getModule(),
                JvmPlatformModule(clock).getModule(),
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

        logger.info { responses.random() }

        val pinkyDispatcher: CoroutineDispatcher = pinkyScope.get(named("PinkyMainDispatcher"))
        try {
            runBlocking(pinkyDispatcher) {
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

    private fun configureKtor(pinky: Pinky, pinkyScope: Scope) {
        val ktor = (pinky.httpServer as JvmNetwork.RealLink.KtorHttpServer)

        ktor.application.install(CallLogging)

        val dataDir = pinkyScope.get<Fs>().resolve(".")
        val firmwareDir = pinkyScope.get<Fs.File>(named("firmwareDir"))

        ktor.application.routing {
            get("") { call.respondRedirect("/ui/") }
            get("monitor") { call.respondRedirect("/monitor/") }
            get("ui") { call.respondRedirect("/ui/") }

            staticResources("", "htdocs")
            get("ui/") { respondWith("ui/index.html", "htdocs") }
            get("monitor/") { respondWith("monitor/index.html", "htdocs") }

            staticFiles("/data/", dataDir.asPath().toFile())
            staticFiles("/fw/", firmwareDir.asPath().toFile())
        }
    }

    private suspend fun PipelineContext<Unit, ApplicationCall>.respondWith(path: String, resourcePackage: String) {
        val file = call.resolveResource(path, resourcePackage)
        if (file is OutgoingContent)
            call.respond(HttpStatusCode.OK, file)
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
