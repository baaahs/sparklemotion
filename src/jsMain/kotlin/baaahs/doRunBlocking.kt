package baaahs

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.promise

actual fun doRunBlocking(block: suspend () -> Unit): dynamic = GlobalScope.promise { block() }