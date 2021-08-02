package baaahs.util

import com.soywiz.klock.DateFormat
import com.soywiz.klock.DateTime

class Logger(val id: String) {
    fun debug(exception: Throwable? = null, message: () -> String) {
        log(id, LogLevel.DEBUG, message, exception)
    }

    fun info(message: () -> String) {
        log(id, LogLevel.INFO, message)
    }

    fun warn(message: () -> String) {
        log(id, LogLevel.WARN, message)
    }

    fun warn(exception: Throwable, message: () -> String) {
        log(id, LogLevel.WARN, message, exception)
    }

    fun error(message: () -> String) {
        log(id, LogLevel.ERROR, message)
    }

    fun error(exception: Throwable, message: () -> String) {
        log(id, LogLevel.ERROR, message, exception)
    }

    fun <T> group(message: String, block: () -> T): T {
        logGroupBegin(id, message)
        return try {
            block()
        } finally {
            logGroupEnd(id, message)
        }
    }

    fun enabled(level: LogLevel): Boolean = logEnabled(id, level)

    companion object {
        private val FORMAT by lazy { DateFormat("yyyy-MM-dd HH:mm:ss.SSS") }

        fun ts(): String {
            return DateTime.now().format(FORMAT)
        }

    }
}

enum class LogLevel {
    DEBUG,
    INFO,
    WARN,
    ERROR
}

inline fun <reified T> Logger() = Logger(T::class.simpleName ?: "unknown")

expect fun log(id: String, level: LogLevel, message: () -> String, exception: Throwable? = null)
expect fun logEnabled(id: String, level: LogLevel): Boolean
expect fun logGroupBegin(id: String, message: String)
expect fun logGroupEnd(id: String, message: String)
