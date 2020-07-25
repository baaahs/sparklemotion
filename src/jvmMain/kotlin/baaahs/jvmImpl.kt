package baaahs

import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import java.util.*

actual fun doRunBlocking(block: suspend () -> Unit) = runBlocking { block() }

actual fun getResource(name: String): String {
    return Pinky::class.java.classLoader.getResource(name).readText()
}

actual fun getTimeMillis(): Long = System.currentTimeMillis()

actual fun decodeBase64(s: String): ByteArray = Base64.getDecoder().decode(s)

actual fun log(id: String, level: String, message: String, exception: Throwable?) {
    val logger = LoggerFactory.getLogger(id)
    when (level) {
        "ERROR" -> logger.error(message, exception)
        "WARN" -> logger.warn(message, exception)
        "INFO" -> logger.info(message, exception)
        "DEBUG" -> logger.debug(message, exception)
        else -> logger.info(message, exception)
    }
}

actual fun logGroupBegin(id: String, message: String) {
    log(id, "INFO", ">> $message")
}

actual fun logGroupEnd(id: String, message: String) {
    log(id, "INFO", "<< $message")
}