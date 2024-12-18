package baaahs.util

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

fun Float?.percent() = this?.let { "${(it * 100).roundToInt()}%" } ?: "—%"
fun Double?.percent() = this?.let { "${(it * 100).roundToInt()}%" } ?: "—%"

fun globalLaunch(block: suspend CoroutineScope.() -> Unit) =
    GlobalScope.launch(coroutineExceptionHandler) {
        block.invoke(this)
    }

expect val coroutineExceptionHandler: CoroutineExceptionHandler

fun FloatArray.toDoubleArray(): DoubleArray {
    return DoubleArray(size) { i -> get(i).toDouble() }
}

fun DoubleArray.toFloatArray(): FloatArray {
    return FloatArray(size) { i -> get(i).toFloat() }
}