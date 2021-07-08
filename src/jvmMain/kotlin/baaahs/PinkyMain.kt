package baaahs

import baaahs.di.JvmBeatLinkPluginModule
import baaahs.di.JvmPinkyModule
import baaahs.di.JvmPlatformModule
import baaahs.di.JvmSoundAnalysisPluginModule
import baaahs.gl.GlBase
import baaahs.io.Fs
import baaahs.io.RealFs
import baaahs.net.JvmNetwork
import baaahs.util.KoinLogger
import baaahs.util.Logger
import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.default
import com.xenomachina.argparser.mainBody
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.content.*
import io.ktor.routing.*
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

@ObsoleteCoroutinesApi
fun main(args: Array<String>) {
    mainBody(PinkyMain::class.simpleName) {
        PinkyMain(ArgParser(args).parseInto(PinkyMain::Args)).run()
    }
}

@ObsoleteCoroutinesApi
class PinkyMain(private val args: Args) {
    private val logger = Logger("PinkyMain")

    fun run() {
        logger.info { "Are you pondering what I'm pondering?" }


        GlBase.manager // Need to wake up OpenGL on the main thread.

        val pinkyInjector = koinApplication {
            logger(KoinLogger())

            modules(
                JvmPlatformModule(args).getModule(),
                JvmPinkyModule().getModule(),
                JvmBeatLinkPluginModule(args).getModule(),
                JvmSoundAnalysisPluginModule(args).getModule()
            )
        }

        val pinkyScope = pinkyInjector.koin.createScope<Pinky>()
        val pinky = pinkyScope.get<Pinky>()

        val ktor = (pinky.httpServer as JvmNetwork.RealLink.KtorHttpServer)
        val resource = Pinky::class.java.classLoader.getResource("baaahs")!!
        if (resource.protocol == "jar") {
            val uri = resource.toURI()!!
            FileSystems.newFileSystem(uri, mapOf("create" to "true"))
            val jsResDir = Paths.get(uri).parent.resolve("htdocs")
            testForIndexDotHtml(jsResDir)
            logger.info { "Serving from jar at $jsResDir." }

            ktor.application.routing {
                static {
                    resources("htdocs")
                    route("mapper") { default("htdocs/mapper/index.html") }
                    route("monitor") { default("htdocs/monitor/index.html") }
                    route("ui") { default("htdocs/ui/index.html") }
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
                    route("mapper") { default("mapper/index.html") }
                    route("monitor") { default("monitor/index.html") }
                    route("ui") { default("ui/index.html") }
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

        runBlocking(pinkyScope.get<CoroutineDispatcher>(named("PinkyMainDispatcher"))) {
            pinky.startAndRun(simulateBrains = args.simulateBrains)
        }
    }

    private fun testForIndexDotHtml(jsResDir: Path) {
        val indexHtml = jsResDir.resolve("index.html")
        if (!Files.exists(indexHtml)) {
            throw FileNotFoundException("$indexHtml doesn't exist and it really probably should!")
        }
    }

    class Args(parser: ArgParser) {
        val model by parser.storing("model").default(Pluggables.defaultModel)

        val showName by parser.storing("show").default<String?>(null)

        val switchShowAfter by parser.storing(
            "Switch show after no input for x seconds",
            transform = { if (isNullOrEmpty()) null else toInt() })
            .default<Int?>(600)

        val adjustShowAfter by parser.storing(
            "Start adjusting show inputs after no input for x seconds",
            transform = { if (isNullOrEmpty()) null else toInt() })
            .default<Int?>(null)

        val enableBeatLink by parser.flagging("Enable beat detection").default(true)

        val simulateBrains by parser.flagging("Simulate connected brains").default(false)
    }
}

private fun Fs.File.asPath(): Path {
    return (fs as RealFs).resolve(this)
}
