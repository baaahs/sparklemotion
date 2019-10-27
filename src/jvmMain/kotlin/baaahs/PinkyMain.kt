package baaahs

import baaahs.dmx.DmxDevice
import baaahs.glsl.GlslBase
import baaahs.net.JvmNetwork
import baaahs.proto.Ports
import baaahs.shows.AllShows
import baaahs.sim.FakeDmxUniverse
import io.ktor.http.content.*
import io.ktor.routing.routing
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileNotFoundException
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

fun main(args: Array<String>) {
    GlslBase.manager // Need to wake up OpenGL on the main thread.

    val sheepModel = SheepModel().apply { load() } as Model<*>

    val resource = Pinky::class.java.classLoader.getResource("baaahs")
    val useResources: Boolean
    val jsResDir = if (resource.protocol == "jar") {
        useResources = true
        val uri = resource.toURI()!!
        FileSystems.newFileSystem(uri, mapOf("create" to "true"))
        Paths.get(uri).parent.resolve("htdocs")
    } else {
        useResources = false
        val classPathBaseDir = Paths.get(resource.file).parent
        classPathBaseDir.parent.parent.parent.parent.parent
            .resolve("build/processedResources/js/main")
    }

    testForIndexDotHtml(jsResDir)

    val network = JvmNetwork()
    val dataDir = File(System.getProperty("user.home")).toPath().resolve("sparklemotion/data")
//    Files.createDirectories(dataDir)

    val fwDir = File(System.getProperty("user.home")).toPath().resolve("sparklemotion/fw")

    val fs = RealFs(dataDir)

    val dmxUniverse = findDmxUniverse()

    val daddy = DirectoryDaddy(RealFs(fwDir), "http://${network.link().myAddress.address.hostAddress}:${Ports.PINKY_UI_TCP}/fw")
    val pinky =
        Pinky(sheepModel, AllShows.allShows, network, dmxUniverse, BeatLinkBeatSource(SystemClock()), SystemClock(),
            fs,
            daddy, object :
                StubPinkyDisplay() {
                override var availableShows: List<Show> = emptyList()
                    set(value) = println("shows = $value")

                override var selectedShow: Show? = null
                    set(value) {
                        field = value; println("selectedShow: ${value}")
                    }

                override var showFrameMs: Int = 0
                    set(value) { field = value; /* println("showFrameMs: ${value}") */ }
            }, JvmSoundAnalyzer(), prerenderPixels = true)

    val ktor = (pinky.httpServer as JvmNetwork.RealLink.KtorHttpServer)
    ktor.application.routing {
        static {
            if (useResources) {
                resources("htdocs")
                defaultResource("htdocs/ui-index.html")
            } else {
                files(jsResDir.toFile())
                default(jsResDir.resolve("ui-index.html").toFile())
            }
        }

        static("fw") {
            files(fwDir.toFile())
        }
    }




    GlobalScope.launch {
        val beatLinkBeatSource = BeatLinkBeatSource(SystemClock())
        beatLinkBeatSource.start()

        pinky.run()
    }

    doRunBlocking {
        delay(200000L)
    }
}

fun testForIndexDotHtml(jsResDir: Path) {
    val indexHtml = jsResDir.resolve("index.html")
    if (!Files.exists(indexHtml)) {
        throw FileNotFoundException("$indexHtml doesn't exist and it really probably should!")
    }
}

private fun findDmxUniverse(): Dmx.Universe {
    val dmxDevices = try {
        DmxDevice.listDevices()
    } catch (e: UnsatisfiedLinkError) {
        logger.warn { "DMX driver not found, DMX will be disabled." }
        e.printStackTrace()
        return FakeDmxUniverse()
    }

    if (dmxDevices.isNotEmpty()) {
        if (dmxDevices.size > 1) {
            logger.warn { "Multiple DMX USB devices found, using ${dmxDevices.first()}." }
        }

        return dmxDevices.first()
    }

    logger.warn { "No DMX USB devices found, DMX will be disabled." }
    return FakeDmxUniverse()
}

val logger = Logger("PinkyMain")
