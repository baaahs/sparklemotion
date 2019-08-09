package baaahs

import baaahs.dmx.DmxDevice
import baaahs.net.JvmNetwork
import baaahs.shows.AllShows
import baaahs.sim.FakeDmxUniverse
import io.ktor.http.content.default
import io.ktor.http.content.files
import io.ktor.http.content.static
import io.ktor.routing.routing
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileNotFoundException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

fun main(args: Array<String>) {
    val sheepModel = SheepModel()
    sheepModel.load()

    val resource = Pinky::class.java.classLoader.getResource("baaahs")
    val classPathBaseDir = Paths.get(resource.file).parent
    val jsResDir = if (resource.protocol == "jar") {
        classPathBaseDir
    } else {
        classPathBaseDir.parent.parent.parent.parent.parent.parent
            .resolve("build/processedResources/js/main")
    }

    testForIndexDotHtml(jsResDir)
    println("jsResDir = ${jsResDir}")

    val network = JvmNetwork()
    val dataDir = File(System.getProperty("user.home")).toPath().resolve("sparklemotion/data")
    Files.createDirectories(dataDir)
    val fs = RealFs(dataDir)

    val dmxUniverse = findDmxUniverse()

    val pinky =
        Pinky(sheepModel, AllShows.allShows, network, dmxUniverse, fs, object : StubPinkyDisplay() {
            override fun listShows(shows: List<Show>) {
                println("shows = ${shows}")
            }
            override var selectedShow: Show? = null
                set(value) { field = value; println("selectedShow: ${value}") }

            override var showFrameMs: Int = 0
                set(value) { field = value; /* println("showFrameMs: ${value}") */ }
        }, prerenderPixels = true)

    (pinky.httpServer as JvmNetwork.RealLink.KtorHttpServer).application.routing {
        static {
            files(jsResDir.toFile())
            default(jsResDir.resolve("ui-index.html").toFile())
        }
    }

    GlobalScope.launch { pinky.run() }

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
        logger.warn("DMX driver not found, DMX will be disabled.")
        e.printStackTrace()
        return FakeDmxUniverse()
    }

    if (dmxDevices.isNotEmpty()) {
        if (dmxDevices.size > 1) {
            logger.warn("Multiple DMX USB devices found, using ${dmxDevices.first()}.")
        }

        return dmxDevices.first()
    }

    logger.warn("No DMX USB devices found, DMX will be disabled.")
    return FakeDmxUniverse()
}