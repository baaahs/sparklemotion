package baaahs.util

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

fun Float?.percent() = this?.let { "${(it * 100f).roundToInt()}%" } ?: "—%"

fun globalLaunch(block: suspend CoroutineScope.() -> Unit) =
    GlobalScope.launch(coroutineExceptionHandler) { block.invoke(this) }

expect val coroutineExceptionHandler: CoroutineExceptionHandler