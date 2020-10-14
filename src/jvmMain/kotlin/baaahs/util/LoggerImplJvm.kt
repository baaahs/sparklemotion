package baaahs.util

import org.slf4j.LoggerFactory

actual fun log(id: String, level: String, message: String, exception: Throwable?) {
    try {
        val logger = LoggerFactory.getLogger(id)
        when (level) {
            "ERROR" -> logger.error(message, exception)
            "WARN" -> logger.warn(message, exception)
            "INFO" -> logger.info(message, exception)
            "DEBUG" -> logger.debug(message, exception)
            else -> logger.info(message, exception)
        }
    } catch (t: Throwable) {
        println("!!! Logger bailing")
    }
}

actual fun logGroupBegin(id: String, message: String) {
    log(id, "INFO", ">> $message")
}

actual fun logGroupEnd(id: String, message: String) {
    log(id, "INFO", "<< $message")
}