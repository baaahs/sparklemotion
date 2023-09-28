package baaahs.util

import org.slf4j.LoggerFactory

class JvmLogger(id: String) : NativeLogger {
    private val logger = LoggerFactory.getLogger(id)

    override fun debug(exception: Throwable?, message: () -> String) {
        if (logger.isDebugEnabled) logger.debug(message(), exception)
    }

    override fun info(exception: Throwable?, message: () -> String) {
        if (logger.isInfoEnabled) logger.info(message(), exception)
    }

    override fun warn(exception: Throwable?, message: () -> String) {
        if (logger.isWarnEnabled) logger.warn(message(), exception)
    }

    override fun error(exception: Throwable?, message: () -> String) {
        if (logger.isErrorEnabled) logger.error(message(), exception)
    }

    override fun isEnabled(level: LogLevel): Boolean {
        return when (level) {
            LogLevel.DEBUG -> logger.isDebugEnabled
            LogLevel.INFO -> logger.isInfoEnabled
            LogLevel.WARN -> logger.isWarnEnabled
            LogLevel.ERROR -> logger.isErrorEnabled
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

actual fun getLogger(id: String): NativeLogger = JvmLogger(id)