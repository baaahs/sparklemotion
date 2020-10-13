package baaahs.util

import com.soywiz.klock.DateFormat
import com.soywiz.klock.DateTime

class Logger(val id: String) {
    fun debug(exception: Throwable? = null, message: () -> String) {
        log(id, "DEBUG", message.invoke(), exception)
    }

    fun info(message: () -> String) {
        log(id, "INFO", message.invoke())
    }

    fun warn(message: () -> String) {
        log(id, "WARN", message.invoke())
    }

    fun warn(exception: Throwable, message: () -> String) {
        log(id, "WARN", message.invoke(), exception)
    }

    fun error(message: () -> String) {
        log(id, "ERROR", message.invoke())
    }

    fun error(message: String, exception: Throwable) {
        log(id, "ERROR", message, exception)
    }

    fun error(exception: Throwable, message: () -> String) {
        log(id, "ERROR", message.invoke(), exception)
    }

    fun <T> group(message: String, block: () -> T): T {
        logGroupBegin(id, message)
        return try {
            block()
        } finally {
            logGroupEnd(id, message)
        }
    }

    companion object {
        private val FORMAT by lazy { DateFormat("yyyy-MM-dd HH:mm:ss.SSS") }

        fun ts(): String {
            return DateTime.now().format(FORMAT)
        }

    }
}

inline fun <reified T> Logger() = Logger(T::class.qualifiedName ?: "unknown")

expect fun log(id: String, level: String, message: String, exception: Throwable? = null)
expect fun logGroupBegin(id: String, message: String)
expect fun logGroupEnd(id: String, message: String)
