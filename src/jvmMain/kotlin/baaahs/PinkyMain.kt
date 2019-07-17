package baaahs

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
    val sheepModel = SheepModel()
    sheepModel.load()

    val classesDir = Paths.get(Pinky::class.java.getResource(".").file)
    val jsResDir = classesDir.parent.parent.parent.parent.parent.parent
        .resolve("build/processedResources/js/main")
    println("jsResDir = ${jsResDir}")
    val httpServer = embeddedServer(Netty, Ports.PINKY_UI_TCP) {
        routing {
            static {
                files(jsResDir.toFile())
                default(jsResDir.resolve("ui-index.html").toFile())
            }
        }
    }.start(false)

    httpServer.application.install(io.ktor.websocket.WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }

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
        Pinky(sheepModel, AllShows.allShows, JvmNetwork(httpServer), dmxUniverse, object : StubPinkyDisplay() {
            override fun listShows(shows: List<Show>) {
                println("shows = ${shows}")
            }
            override var selectedShow: Show? = null
                set(value) { field = value; println("selectedShow: ${value}") }

            override var showFrameMs: Int = 0
                set(value) { field = value; /* println("showFrameMs: ${value}") */ }
        })

    GlobalScope.launch { pinky.run() }

    doRunBlocking {
        delay(200000L)
    }
}