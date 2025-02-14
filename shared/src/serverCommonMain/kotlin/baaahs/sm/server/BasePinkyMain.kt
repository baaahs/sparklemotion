package baaahs.sm.server

import baaahs.Pinky
import baaahs.Pluggables
import baaahs.di.PinkyModule
import baaahs.di.PlatformModule
import baaahs.di.PluginsModule
import baaahs.io.Fs
import baaahs.net.Network
import baaahs.util.KoinLogger
import baaahs.util.Logger
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope
import org.koin.dsl.koinApplication

abstract class BasePinkyMain {
    internal val logger by lazy { Logger<BasePinkyMain>() }

    internal lateinit var pinky: Pinky

    fun startUp(
        platformModule: PlatformModule,
        pinkyModule: PinkyModule
    ): Scope {
        val pinkyInjector = koinApplication {
            logger(KoinLogger())

            modules(
                PluginsModule(Pluggables.plugins).getModule(),
                platformModule.getModule(),
                pinkyModule.getModule()
            )
        }

        val pinkyScope = pinkyInjector.koin.createScope<Pinky>()
        pinky = pinkyScope.get<Pinky>()
        configureKtor(pinky, pinkyScope)
            .start()

        logger.info { responses.random() }
        return pinkyScope
    }

    protected fun configureKtor(pinky: Pinky, pinkyScope: Scope): Network.HttpServer {
        val ktor = pinky.httpServer

//        ktor.application.install(CallLogging)

        val dataDir = pinkyScope.get<Fs>().resolve(".")
        val firmwareDir = pinkyScope.get<Fs.File>(named("firmwareDir"))

        ktor.routing {
            get("") { redirect("/ui/") }
            get("monitor") { redirect("/monitor/") }
            get("midi") { redirect("midi/") }
            get("ui") { redirect("/ui/") }

            staticResources("", "htdocs")
            get("ui/") { respondWithResource("ui/index.html", "htdocs") }
            get("monitor/") { respondWithResource("monitor/index.html", "htdocs") }

            staticFiles("/data/", dataDir)
            staticFiles("/fw/", firmwareDir)
        }
        return ktor
    }

    companion object {
        val responses = listOf(
            "I think so, Brain, but Lederhosen won't stretch that far.",
            "Yeah, but I thought Madonna already had a steady bloke!",
            "I think so, Brain, but what would goats be doing in red leather turbans?",
            "I think so, Brain... but how would we ever determine Sandra Bullock's shoe size?",
            "Yes, Brain, I think so. But how do we get Twiggy to pose with an electric goose?"
        )
    }
}