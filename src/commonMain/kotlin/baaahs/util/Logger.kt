package baaahs.util

import com.soywiz.klock.DateFormat
import com.soywiz.klock.DateTime

class Logger(val id: String) {
    private val nativeLogger = getLogger(id)

    fun debug(exception: Throwable? = null, message: () -> String) {
        nativeLogger.debug(exception, message)
    }

    fun info(message: () -> String) {
        nativeLogger.info(null, message)
    }

    fun warn(message: () -> String) {
        nativeLogger.warn(null, message)
    }

    fun warn(exception: Throwable, message: () -> String) {
        nativeLogger.warn(exception, message)
    }

    fun error(message: () -> String) {
        nativeLogger.error(null, message)
    }

    fun error(exception: Throwable, message: () -> String) {
        nativeLogger.error(exception, message)
    }

    fun <T> group(message: String, block: () -> T): T {
        return nativeLogger.group(message, block)
    }

    fun enabled(level: LogLevel): Boolean = nativeLogger.isEnabled(level)

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

interface NativeLogger {
    fun debug(exception: Throwable? = null, message: () -> String)
    fun info(exception: Throwable? = null, message: () -> String)
    fun warn(exception: Throwable? = null, message: () -> String)
    fun error(exception: Throwable? = null, message: () -> String)

    fun isEnabled(level: LogLevel): Boolean
    fun <T> group(message: String, block: () -> T): T
}

expect fun getLogger(id: String): NativeLogger
