package baaahs

import baaahs.net.Network
import baaahs.sm.brain.ProdBrainSimulator
import baaahs.sm.server.PinkyArgs
import baaahs.util.KoinLogger
import baaahs.util.Logger
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.runBlocking
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope
import org.koin.dsl.koinApplication

abstract class CommonPinkyMain {
    internal abstract val logger: Logger

    fun bootstrap(modules: List<Module>) {
        logger.info { "Are you pondering what I'm pondering?" }
        logger.info { "Running ${systemId()} from ${homeDir()}." }

        val pinkyInjector = koinApplication {
            logger(KoinLogger())
            modules(modules)
        }

        val pinkyScope = pinkyInjector.koin.createScope<Pinky>()
        val httpServer = pinkyScope.get<Network.HttpServer>()
        configureKtorApplication(httpServer, pinkyScope)

        val pinky = pinkyScope.get<Pinky>()
        logger.info { responses.random() }

        try {
            val pinkyArgs = pinkyScope.get<PinkyArgs>()
            runBlocking(pinkyScope.get<CoroutineDispatcher>(named("PinkyMainDispatcher"))) {
                pinky.startAndRun {
                    if (pinkyArgs.simulateBrains) {
                        pinkyScope.get<ProdBrainSimulator>().enableSimulation()
                    }
                }
            }
        } catch (e: Throwable) {
            logger.error(e) { "Failed to start Pinky." }
        } finally {
            logger.info { "Exiting." }
            exitProcess(1)
        }
    }

    internal abstract fun systemId(): String

    internal abstract fun homeDir(): String

    internal abstract fun configureKtorApplication(httpServer: Network.HttpServer, pinkyScope: Scope)

    internal abstract fun exitProcess(code: Int)

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