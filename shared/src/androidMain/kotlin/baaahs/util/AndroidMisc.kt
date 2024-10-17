package baaahs.util

import kotlinx.coroutines.CoroutineExceptionHandler

actual val coroutineExceptionHandler: CoroutineExceptionHandler =
    CoroutineExceptionHandler { _, throwable ->
        Logger("Main").error(throwable) { "Unknown error." }
        throw throwable
    }
