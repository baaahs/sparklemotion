package baaahs.util

class JsLogger(
    private val id: String
) : NativeLogger {
    override fun debug(exception: Throwable?, message: () -> String) {
        log(id, LogLevel.DEBUG, message, exception)
    }

    override fun info(exception: Throwable?, message: () -> String) {
        log(id, LogLevel.INFO, message, exception)
    }

    override fun warn(exception: Throwable?, message: () -> String) {
        log(id, LogLevel.WARN, message, exception)
    }

    override fun error(exception: Throwable?, message: () -> String) {
        log(id, LogLevel.ERROR, message, exception)
    }

    override fun isEnabled(level: LogLevel): Boolean {
        return level >= LoggerConfig.levelFor(id)
    }

    override fun <T> group(message: String, block: () -> T): T {
        console.asDynamic().group("$id - $message")
        return try {
            block()
        } finally {
            console.asDynamic().groupEnd()
        }
    }

    private fun log(id: String, level: LogLevel, message: () -> String, exception: Throwable?) {
        if (level < LoggerConfig.levelFor(id)) return

        try {
            logMessage(level, "${Logger.ts()} [] $level  $id - ${message()}", exception)
        } catch (t: Throwable) {
            println("!!! Logger bailing: ${t.message}")
        }
    }

    private fun logMessage(level: LogLevel, message: String, exception: Throwable?) {
        when (level) {
            LogLevel.DEBUG -> console.asDynamic().debug(message, exception?.stackTraceToString())
            LogLevel.INFO -> console.info(message, exception?.stackTraceToString())
            LogLevel.WARN -> console.warn(message, exception?.stackTraceToString())
            LogLevel.ERROR -> console.error(message, exception?.stackTraceToString())
        }
    }
}

actual fun getLogger(id: String): NativeLogger = JsLogger(id)
