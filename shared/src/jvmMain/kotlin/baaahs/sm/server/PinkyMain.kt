package baaahs.sm.server

import baaahs.CommonPinkyMain
import baaahs.Pluggables
import baaahs.di.JvmPinkyModule
import baaahs.di.JvmPlatformModule
import baaahs.di.PluginsModule
import baaahs.gl.GlBase
import baaahs.io.Fs
import baaahs.io.RealFs
import baaahs.net.KtorHttpServer
import baaahs.net.Network
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
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope
import java.nio.file.Path

fun main(args: Array<String>) {
    PinkyMain(args).run()
}

class PinkyMain(private val args: Array<String>) : CommonPinkyMain() {
    override val logger by lazy { Logger<PinkyMain>() }

    fun run() {
        GlBase.manager // First thing, we need to wake up OpenGL on the main thread.

        val programName = this::class.simpleName ?: "Pinky"
        val clock = SystemClock
        val modules = listOf(
            PluginsModule(Pluggables.plugins).getModule(),
            JvmPlatformModule(clock).getModule(),
            JvmPinkyModule(programName, args).getModule()
        )

        bootstrap(modules)
    }

    override fun systemId(): String =
        "JVM ${System.getProperty("java.vendor")} ${System.getProperty("java.version")}"

    override fun homeDir(): String =
        System.getProperty("java.home")

    override fun exitProcess(code: Int) {
        kotlin.system.exitProcess(code)
    }

    override fun configureKtorApplication(httpServer: Network.HttpServer, pinkyScope: Scope) {
        val application = (httpServer as KtorHttpServer).application
        application.install(CallLogging)

        val dataDir = pinkyScope.get<Fs>().resolve(".")
        val firmwareDir = pinkyScope.get<Fs.File>(named("firmwareDir"))

        application.routing {
            get("") { call.respondRedirect("/ui/") }
            get("monitor") { call.respondRedirect("/monitor/") }
            get("midi") { call.respondRedirect("midi/") }
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
}

private fun Fs.File.asPath(): Path {
    return (fs as RealFs).resolve(this)
}
