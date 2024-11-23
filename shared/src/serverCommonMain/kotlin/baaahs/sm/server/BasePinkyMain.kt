package baaahs.sm.server

import baaahs.Pinky
import baaahs.io.Fs
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope

abstract class BasePinkyMain {
    protected fun configureKtor(pinky: Pinky, pinkyScope: Scope) {
        val ktor = pinky.httpServer

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
        ktor.start()
    }

    companion object {
        internal val responses = listOf(
            "I think so, Brain, but Lederhosen won't stretch that far.",
            "Yeah, but I thought Madonna already had a steady bloke!",
            "I think so, Brain, but what would goats be doing in red leather turbans?",
            "I think so, Brain... but how would we ever determine Sandra Bullock's shoe size?",
            "Yes, Brain, I think so. But how do we get Twiggy to pose with an electric goose?"
        )
    }
}