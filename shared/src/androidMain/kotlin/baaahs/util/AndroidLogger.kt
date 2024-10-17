package baaahs.util

import android.util.Log

class AndroidLogger(private val id: String) : NativeLogger {

    override fun debug(exception: Throwable?, message: () -> String) {
        if (isEnabled(LogLevel.DEBUG))
            Log.d(id, message(), exception)
    }

    override fun info(exception: Throwable?, message: () -> String) {
        if (isEnabled(LogLevel.INFO))
            Log.i(id, message(), exception)
    }

    override fun warn(exception: Throwable?, message: () -> String) {
        if (isEnabled(LogLevel.WARN))
            Log.w(id, message(), exception)
    }

    override fun error(exception: Throwable?, message: () -> String) {
        if (isEnabled(LogLevel.ERROR))
            Log.e(id, message(), exception)
    }

    override fun isEnabled(level: LogLevel): Boolean {
        return when (level) {
            LogLevel.DEBUG -> Log.isLoggable(id, Log.DEBUG)
            LogLevel.INFO -> Log.isLoggable(id, Log.INFO)
            LogLevel.WARN -> Log.isLoggable(id, Log.WARN)
            LogLevel.ERROR -> Log.isLoggable(id, Log.ERROR)
        }
    }

    override fun <T> group(message: String, block: () -> T): T {
        info { ">> $message" }
        return try {
            block()
        } finally {
            info { "<< $message" }
        }
    }
}

actual fun getLogger(id: String): NativeLogger = AndroidLogger(id)