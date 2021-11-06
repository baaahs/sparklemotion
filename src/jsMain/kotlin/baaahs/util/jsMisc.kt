package baaahs.util

import baaahs.window
import kotlinx.coroutines.CoroutineExceptionHandler

actual val coroutineExceptionHandler: CoroutineExceptionHandler =
    CoroutineExceptionHandler { _, throwable ->
        window.alert(throwable.message ?: "Unknown error.")
        throw throwable
    }
