package baaahs.util

import baaahs.SparkleMotion
import kotlinx.coroutines.CoroutineExceptionHandler
import web.prompts.alert

actual val coroutineExceptionHandler: CoroutineExceptionHandler =
    CoroutineExceptionHandler { _, throwable ->
        SparkleMotion.logger.error(throwable) { throwable.message ?: "Unknown error." }
        alert(throwable.message ?: "Unknown error.")
        throw throwable
    }
