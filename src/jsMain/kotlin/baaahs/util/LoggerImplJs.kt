package baaahs.util

actual fun log(id: String, level: LogLevel, message: () -> String, exception: Throwable?) {
    if (level < LoggerConfig.levelFor(id)) return

    try {
        logMessage(level, "${Logger.ts()} [] $level  $id - ${message()}", exception)
    } catch (t: Throwable) {
        println("!!! Logger bailing: ${t.message}")
    }
}

actual fun logEnabled(id: String, level: LogLevel): Boolean = level >= LoggerConfig.levelFor(id)

private fun logMessage(level: LogLevel, message: String, exception: Throwable?) {
    when (level) {
        LogLevel.DEBUG -> console.asDynamic().debug(message, exception?.stackTraceToString())
        LogLevel.INFO -> console.info(message, exception?.stackTraceToString())
        LogLevel.WARN -> console.warn(message, exception?.stackTraceToString())
        LogLevel.ERROR -> console.error(message, exception?.stackTraceToString())
    }
}

actual fun logGroupBegin(id: String, message: String) {
    console.asDynamic().group("$id - $message")
}

actual fun logGroupEnd(id: String, message: String) {
    console.asDynamic().groupEnd()
}
