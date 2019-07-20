package baaahs

import baaahs.db.MongoDB
import baaahs.dmx.DmxDevice
import baaahs.net.JvmNetwork
import baaahs.proto.Ports
import baaahs.shows.AllShows
import baaahs.sim.FakeDmxUniverse
import io.ktor.application.install
import io.ktor.http.content.default
import io.ktor.http.content.files
import io.ktor.http.content.static
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.nio.file.Paths
import java.time.Duration

fun main(args: Array<String>) {
    val db = MongoDB()
    val sheepModel = SheepModel()
    sheepModel.load()

    val classesDir = Paths.get(Pinky::class.java.getResource(".").file)
    val jsResDir = classesDir.parent.parent.parent.parent.parent.parent
        .resolve("build/processedResources/js/main")
    println("jsResDir = ${jsResDir}")

    val network = JvmNetwork()

    val dmxDevices = DmxDevice.listDevices()
    val dmxUniverse = if (dmxDevices.isEmpty()) {
        logger.warn("No DMX USB devices found, DMX will be disabled.")
        FakeDmxUniverse()
    } else {
        if (dmxDevices.size > 1) {
            logger.warn("Multiple DMX USB devices found, using ${dmxDevices.first()}.")
        }

        dmxDevices.first()
    }

    val pinky =
        Pinky(sheepModel, AllShows.allShows, network, dmxUniverse, object : StubPinkyDisplay() {
            override fun listShows(shows: List<Show>) {
                println("shows = ${shows}")
            }
            override var selectedShow: Show? = null
                set(value) { field = value; println("selectedShow: ${value}") }

            override var showFrameMs: Int = 0
                set(value) { field = value; /* println("showFrameMs: ${value}") */ }
        })

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