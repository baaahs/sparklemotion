package baaahs.util

import org.slf4j.LoggerFactory

actual fun log(id: String, level: LogLevel, message: () -> String, exception: Throwable?) {
    try {
        val logger = LoggerFactory.getLogger(id)
        when (level) {
            LogLevel.DEBUG -> if (logger.isDebugEnabled) logger.debug(message(), exception)
            LogLevel.INFO -> if (logger.isInfoEnabled) logger.info(message(), exception)
            LogLevel.WARN -> if (logger.isWarnEnabled) logger.warn(message(), exception)
            LogLevel.ERROR -> if (logger.isErrorEnabled) logger.error(message(), exception)
        }
    } catch (t: Throwable) {
        println("!!! Logger bailing, ${t.message}")
    }
}

actual fun logEnabled(id: String, level: LogLevel): Boolean {
    val logger = LoggerFactory.getLogger(id)

    return when (level) {
        LogLevel.DEBUG -> logger.isDebugEnabled
        LogLevel.INFO -> logger.isInfoEnabled
        LogLevel.WARN -> logger.isWarnEnabled
        LogLevel.ERROR -> logger.isErrorEnabled
    }
}

actual fun logGroupBegin(id: String, message: String) {
    log(id, LogLevel.INFO, { ">> $message" })
}

actual fun logGroupEnd(id: String, message: String) {
    log(id, LogLevel.INFO, { "<< $message" })
}