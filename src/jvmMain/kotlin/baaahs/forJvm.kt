package baaahs

import kotlinx.coroutines.runBlocking

actual fun doRunBlocking(block: suspend () -> Unit) = runBlocking { block() }

actual fun getResource(name: String): String {
    return Main::class.java.getResource(name).readText()
}

actual fun getDisplay(): Display {
    throw UnsupportedOperationException("not implemented")
}