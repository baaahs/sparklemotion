package baaahs.util

import org.koin.core.Koin
import org.koin.core.logger.Level
import org.koin.core.logger.Logger
import org.koin.core.logger.MESSAGE

class KoinLogger : Logger() {
    @Suppress("RemoveRedundantQualifierName")
    private val logger = baaahs.util.Logger<Koin>()

    override fun display(level: Level, msg: MESSAGE) {
        when (level) {
            Level.DEBUG -> logger.debug { msg }
            Level.INFO -> logger.info { msg }
            Level.WARNING -> logger.warn { msg }
            Level.ERROR -> logger.error { msg }
            Level.NONE -> {}
        }
    }
}