package baaahs.util

import org.koin.core.Koin
import org.koin.core.logger.Level
import org.koin.core.logger.Logger
import org.koin.core.logger.MESSAGE

class KoinLogger : Logger(
    Level.ERROR // TODO: Koin 3.1.3 and Kotlin 1.6.0 fail on JVM unless duration logging is turned off.
) {
    @Suppress("RemoveRedundantQualifierName")
    private val logger = baaahs.util.Logger<Koin>()

    override fun log(level: Level, msg: MESSAGE) {
        when (level) {
            Level.DEBUG -> logger.debug { msg }
            Level.INFO -> logger.info { msg }
            Level.ERROR -> logger.error { msg }
            Level.NONE -> {}
        }
    }
}