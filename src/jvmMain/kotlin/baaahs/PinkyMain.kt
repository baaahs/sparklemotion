package baaahs

import baaahs.dmx.DmxDevice
import baaahs.glsl.GlslBase
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
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.time.Duration

fun main(args: Array<String>) {
    GlslBase.manager // Need to wake up OpenGL on the main thread.

    val sheepModel = SheepModel()
    sheepModel.load()

    val classesDir = Paths.get(Pinky::class.java.getResource(".").file)
    val jsResDir = classesDir.parent.parent.parent.parent.parent.parent
        .resolve("build/processedResources/js/main")
    println("jsResDir = ${jsResDir}")

    val httpServer = embeddedServer(Netty, Ports.PINKY_UI_TCP) {
        install(io.ktor.websocket.WebSockets) {
            pingPeriod = Duration.ofSeconds(15)
            timeout = Duration.ofSeconds(15)
            maxFrameSize = Long.MAX_VALUE
            masking = false
        }

        routing {
            static {
                files(jsResDir.toFile())
                default(jsResDir.resolve("ui-index.html").toFile())
            }
        }
    }.start(false)

    val network = JvmNetwork()
    val dataDir = File(System.getProperty("user.home")).toPath().resolve("sparklemotion/data")
    Files.createDirectories(dataDir)
    val fs = RealFs(dataDir)

    val dmxUniverse = findDmxUniverse()

    val pinky =
        Pinky(sheepModel, AllShows.allShows, network, dmxUniverse, BeatLinkBeatSource(SystemClock()), SystemClock(),
            fs,
            object :
            StubPinkyDisplay() {
            override fun listShows(shows: List<Show>) {
                println("shows = ${shows}")
            }

            override var selectedShow: Show? = null
                set(value) {
                    field = value; println("selectedShow: ${value}")
                }

            override var showFrameMs: Int = 0
                set(value) { field = value; /* println("showFrameMs: ${value}") */ }
        }, prerenderPixels = true)

    (pinky.httpServer as JvmNetwork.RealLink.KtorHttpServer).application.routing {
        static {
            files(jsResDir.toFile())
            default(jsResDir.resolve("ui-index.html").toFile())
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