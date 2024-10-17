package baaahs.app.android

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import baaahs.Pinky
import baaahs.Pluggables
import baaahs.SystemClock
import baaahs.di.AndroidPinkyModule
import baaahs.di.AndroidPlatformModule
import baaahs.di.PluginsModule
import baaahs.io.Fs
import baaahs.io.RealFs
import baaahs.net.AndroidNetwork
import baaahs.net.Network
import baaahs.sm.brain.ProdBrainSimulator
import baaahs.sm.server.PinkyArgs
import baaahs.util.KoinLogger
import baaahs.util.globalLaunch
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.OutgoingContent
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.http.content.resolveResource
import io.ktor.server.http.content.staticFiles
import io.ktor.server.http.content.staticResources
import io.ktor.server.plugins.callloging.CallLogging
import io.ktor.server.response.respond
import io.ktor.server.response.respondRedirect
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.ktor.util.pipeline.PipelineContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.runBlocking
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope
import org.koin.dsl.koinApplication
import java.nio.file.Path
import kotlin.concurrent.thread
import kotlin.system.exitProcess

class SparkleMotionService : Service() {
    private val logger by lazy { baaahs.util.Logger("PinkyMain") }

    override fun onCreate() {
        super.onCreate()
        println("SparkleMotionService started floofy!!!")
        createNotificationChannel()

        // Create a notification for the foreground service
        val notification = NotificationCompat.Builder(this, "ktor_http_channel")
            .setContentTitle("Sparkle Motion")
            .setContentText("Sparkle Motion is running")
//            .setSmallIcon(R.drawable.ic_notification) // Use an appropriate icon
            .build()

        // Start the service in the foreground
        startForeground(1, notification)

        globalLaunch {
            var network: Network? = null
            thread {
                network = AndroidNetwork()
            }.join()
            launch(network!!)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
//        server?.stop(1000, 1000)
        Log.d("KtorHttpService", "Ktor HTTP server stopped")
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "ktor_http_channel",
                "Ktor HTTP Server",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Ktor HTTP Server is running"
            }

            val notificationManager: NotificationManager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun launch(network: Network) {
        val programName = "Pinky"
        val clock = SystemClock
        val pinkyInjector = koinApplication {
            logger(KoinLogger())

            modules(
                PluginsModule(Pluggables.plugins).getModule(),
                AndroidPlatformModule(network, clock).getModule(),
                AndroidPinkyModule(filesDir, programName, emptyArray()).getModule()
            )
        }

        val pinkyScope = pinkyInjector.koin.createScope<Pinky>()
        val pinky = pinkyScope.get<Pinky>()
        configureKtor(pinky, pinkyScope)

        logger.info { responses.random() }

        try {
            val pinkyArgs = pinkyScope.get<PinkyArgs>()
            runBlocking(pinkyScope.get<CoroutineDispatcher>(named("PinkyMainDispatcher"))) {
                pinky.startAndRun {
                    if (pinkyArgs.simulateBrains || true) {
                        pinkyScope.get<ProdBrainSimulator>().enableSimulation()
                    }
                }
            }
        } catch (e: Throwable) {
            logger.error(e) { "Failed to start Pinky." }
            logger.error { e.message?.toString() ?: "No message." }
        } finally {
            logger.info { "Exiting." }
            exitProcess(1)
        }
    }

    private fun configureKtor(pinky: Pinky, pinkyScope: Scope) {
        val ktor = (pinky.httpServer as AndroidNetwork.RealLink.KtorHttpServer)
        ktor.application.install(CallLogging)

        val dataDir = pinkyScope.get<Fs>().resolve(".")
        val firmwareDir = pinkyScope.get<Fs.File>(named("firmwareDir"))

        val assets = assets
        ktor.application.routing {
            get("") {
                println("GET /")
                call.respondRedirect("/ui/")
            }
            get("monitor") { call.respondRedirect("/monitor/") }
            get("midi") { call.respondRedirect("midi/") }
            get("ui") {
                println("GET /ui")
                call.respondRedirect("/ui/")
            }

            staticResources("", "")
            get("ui/") {
                println("GET /ui/")
                assets
                    .list("/").also {
                        println("assets: ${it?.joinToString()}")
                    }
                respondWith("ui/index.html", "" /*, "htdocs"*/)
            }
            get("monitor/") { respondWith("monitor/index.html", "") }

            staticFiles("/data/", dataDir.asPath().toFile())
            staticFiles("/fw/", firmwareDir.asPath().toFile())
        }
        ktor.start()
    }

    private suspend fun PipelineContext<Unit, ApplicationCall>.respondWith(path: String, resourcePackage: String) {
        val file = call.resolveResource(path, resourcePackage)
        if (file is OutgoingContent)
            call.respond(HttpStatusCode.OK, file)
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
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