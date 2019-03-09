package baaahs

import kotlinx.coroutines.runBlocking

actual fun doRunBlocking(block: suspend () -> Unit) = runBlocking { block() }