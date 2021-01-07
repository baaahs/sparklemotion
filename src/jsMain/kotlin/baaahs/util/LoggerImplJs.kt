package baaahs.util

actual fun log(id: String, level: LogLevel, message: () -> String, exception: Throwable?) {
    if (level < LoggerConfig.levelFor(id)) return

    try {
        logMessage(level, "${Logger.ts()} [] $level  $id - ${message()}", exception)
    } catch (t: Throwable) {
        println("!!! Logger bailing: ${t.message}")
    }
}


private fun logMessage(level: LogLevel, message: String, exception: Throwable?) {
    when (level) {
        LogLevel.DEBUG -> console.asDynamic().debug(message, exception)
        LogLevel.INFO -> console.info(message, exception)
        LogLevel.WARN -> console.warn(message, exception)
        LogLevel.ERROR -> console.error(message, exception)
    }
}

actual fun logGroupBegin(id: String, message: String) {
    console.asDynamic().group("$id - $message")
}

actual fun logGroupEnd(id: String, message: String) {
    console.asDynamic().groupEnd()
}
