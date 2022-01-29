package baaahs.util

import baaahs.SparkleMotion
import baaahs.window
import kotlinx.coroutines.CoroutineExceptionHandler

actual val coroutineExceptionHandler: CoroutineExceptionHandler =
    CoroutineExceptionHandler { _, throwable ->
        SparkleMotion.logger.error(throwable) { throwable.message ?: "Unknown error." }
        window.alert(throwable.message ?: "Unknown error.")
        throw throwable
    }
