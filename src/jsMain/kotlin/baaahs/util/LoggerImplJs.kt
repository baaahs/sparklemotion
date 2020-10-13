package baaahs.util

actual fun log(id: String, level: String, message: String, exception: Throwable?) {
    try {
        logMessage(level, "${Logger.ts()} [] $level  $id - $message", exception)
    } catch (t: Throwable) {
        println("!!! Logger bailing")
    }
}


private fun logMessage(level: String, message: String, exception: Throwable?) {
    when (level) {
        "ERROR" -> console.error(message, exception)
        "WARN" -> console.warn(message, exception)
        "INFO" -> console.info(message, exception)
        "DEBUG" -> console.asDynamic().debug(message, exception)
        else -> console.log(message, exception)
    }
}

actual fun logGroupBegin(id: String, message: String) {
    console.asDynamic().group("$id - $message")
}

actual fun logGroupEnd(id: String, message: String) {
    console.asDynamic().groupEnd()
}
