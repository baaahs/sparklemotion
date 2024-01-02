package baaahs.util

import kotlinx.datetime.Clock

public class Logger(id: String) {
    private val nativeLogger = getLogger(id)

    public fun debug(exception: Throwable? = null, message: () -> String) {
        nativeLogger.debug(exception, message)
    }

    public fun info(message: () -> String) {
        nativeLogger.info(null, message)
    }

    public fun warn(message: () -> String) {
        nativeLogger.warn(null, message)
    }

    public fun warn(exception: Throwable, message: () -> String) {
        nativeLogger.warn(exception, message)
    }

    public fun error(message: () -> String) {
        nativeLogger.error(null, message)
    }

    public fun error(exception: Throwable, message: () -> String) {
        nativeLogger.error(exception, message)
    }

    public fun <T> group(message: String, block: () -> T): T {
        return nativeLogger.group(message, block)
    }

    public fun enabled(level: LogLevel): Boolean = nativeLogger.isEnabled(level)

    internal companion object {
        internal fun ts(): String {
            return Clock.System.now().toString()
        }

    }
}

public enum class LogLevel {
    DEBUG,
    INFO,
    WARN,
    ERROR
}

public inline fun <reified T> Logger(): Logger = Logger(T::class.simpleName ?: "unknown")

public interface NativeLogger {
    public fun debug(exception: Throwable? = null, message: () -> String)
    public fun info(exception: Throwable? = null, message: () -> String)
    public fun warn(exception: Throwable? = null, message: () -> String)
    public fun error(exception: Throwable? = null, message: () -> String)

    public fun isEnabled(level: LogLevel): Boolean
    public fun <T> group(message: String, block: () -> T): T
}

internal expect fun getLogger(id: String): NativeLogger
