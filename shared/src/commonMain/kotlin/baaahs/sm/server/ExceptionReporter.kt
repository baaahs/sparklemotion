package baaahs.sm.server

import kotlinx.coroutines.CoroutineName
import kotlin.coroutines.CoroutineContext

interface ExceptionReporter {
    fun reportException(context: String, throwable: Throwable)

    companion object {
        val RETHROW = object : ExceptionReporter {
            override fun reportException(context: String, throwable: Throwable) {
                throw throwable
            }
        }
    }
}

fun ExceptionReporter.reportException(context: CoroutineContext, throwable: Throwable) =
    reportException(context[CoroutineName]?.name ?: "Unknown context", throwable)