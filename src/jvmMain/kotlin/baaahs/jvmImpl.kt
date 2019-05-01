package baaahs

import kotlinx.coroutines.runBlocking

actual fun doRunBlocking(block: suspend () -> Unit) = runBlocking { block() }

actual fun getResource(name: String): String {
    return Visualizer::class.java.classLoader.getResource(name).readText()
}

actual fun getDisplay(): Display {
    throw UnsupportedOperationException("not implemented")
}

actual fun getTimeMillis(): Long = System.currentTimeMillis()

actual fun createUiApp(elementId: String, uiContext: UiContext): Any = throw Throwable("not supported")