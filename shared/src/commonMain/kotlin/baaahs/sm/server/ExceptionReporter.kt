package baaahs.sm.server

import kotlinx.coroutines.CoroutineName
import kotlin.coroutines.CoroutineContext

interface ExceptionReporter {
    fun reportException(context: String, throwable: Throwable)
}

fun ExceptionReporter.reportException(context: CoroutineContext, throwable: Throwable) =
    reportException(context[CoroutineName]?.name ?: "Unknown context", throwable)