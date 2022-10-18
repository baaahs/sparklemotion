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
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.koin.core.qualifier.named
import org.koin.dsl.koinApplication
import java.io.FileNotFoundException
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.system.exitProcess

@ObsoleteCoroutinesApi
fun main(args: Array<String>) {
    PinkyMain(args).run()
}

@ObsoleteCoroutinesApi
class PinkyMain(private val args: Array<String>) {
    private val logger = Logger("PinkyMain")

    fun run() {
        logger.info { "Are you pondering what I'm pondering?" }


        GlBase.manager // Need to wake up OpenGL on the main thread.

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

        val ktor = (pinky.httpServer as JvmNetwork.RealLink.KtorHttpServer)
        val resource = Pinky::class.java.classLoader.getResource("baaahs")!!
        val dataDir = pinkyScope.get<Fs>()
        val dataDirFile = dataDir.resolve(".").asPath().toFile()
        if (resource.protocol == "jar") {
            val uri = resource.toURI()!!
            FileSystems.newFileSystem(uri, mapOf("create" to "true"))
            val jsResDir = Paths.get(uri).parent.resolve("htdocs")
            testForIndexDotHtml(jsResDir)
            logger.info { "Serving from jar at $jsResDir." }

            ktor.application.routing {
                static {
                    resources("htdocs")
                    get("monitor") { call.respondRedirect("monitor/") }
                    route("monitor/") { defaultResource("htdocs/monitor/index.html") }
                    get("ui") { call.respondRedirect("ui/") }
                    route("ui/") { defaultResource("htdocs/ui/index.html") }
                    route("data/") { files(dataDirFile) }
                    defaultResource("htdocs/ui-index.html")
                }
            }
        } else {
            val classPathBaseDir = Paths.get(resource.file).parent
            val repoDir = classPathBaseDir.parent.parent.parent.parent.parent
            val jsResDir = repoDir.resolve("build/processedResources/js/main")
            val jsPackageDir = "build/distributions"
            testForIndexDotHtml(jsResDir)
            logger.info { "Serving resources from files at $jsResDir." }
            logger.info { "Serving sparklemotion from files at $jsPackageDir." }

            ktor.application.routing {
                static {
                    staticRootFolder = jsResDir.toFile()

                    file("sparklemotion.js",
                        repoDir.resolve("$jsPackageDir/sparklemotion.js").toFile())
                    file("sparklemotion.js.map",
                        repoDir.resolve("$jsPackageDir/sparklemotion.js.map").toFile())

                    file("vendors.js",
                        repoDir.resolve("$jsPackageDir/vendors.js").toFile())
                    file("vendors.js.map",
                        repoDir.resolve("$jsPackageDir/vendors.js.map").toFile())

                    files(jsResDir.toFile())
                    get("monitor") { call.respondRedirect("monitor/") }
                    route("monitor/") { default("monitor/index.html") }
                    get("ui") { call.respondRedirect("ui/") }
                    route("ui/") { default("ui/index.html") }
                    route("data/") { files(dataDirFile) }
                    default("ui-index.html")
                }
            }
        }

        ktor.application.install(CallLogging)
        ktor.application.routing {
            static("fw") {
                files(pinkyScope.get<Fs.File>(named("firmwareDir")).asPath().toFile())
            }
        }

        val responses = listOf(
            "I think so, Brain, but Lederhosen won't stretch that far.",
            "Yeah, but I thought Madonna already had a steady bloke!",
            "I think so, Brain, but what would goats be doing in red leather turbans?",
            "I think so, Brain... but how would we ever determine Sandra Bullock's shoe size?",
            "Yes, Brain, I think so. But how do we get Twiggy to pose with an electric goose?"
        )
        logger.info { responses.random() }

        try {
            val pinkyArgs = pinkyScope.get<PinkyArgs>()
            runBlocking(pinkyScope.get<CoroutineDispatcher>(named("PinkyMainDispatcher"))) {
                pinky.startAndRun {
                    if (pinkyArgs.simulateBrains) {
                        pinkyScope.get<ProdBrainSimulator>().enableSimulation()
                    }
                }
            }
        } catch(e: Throwable) {
            logger.error(e) { "Failed to start Pinky." }
        } finally {
            logger.info { "Exiting." }
            exitProcess(1)
        }
    }

    private fun testForIndexDotHtml(jsResDir: Path) {
        val indexHtml = jsResDir.resolve("index.html")
        if (!Files.exists(indexHtml)) {
            throw FileNotFoundException("$indexHtml doesn't exist and it really probably should!")
        }
    }
}

private fun Fs.File.asPath(): Path {
    return (fs as RealFs).resolve(this)
}
