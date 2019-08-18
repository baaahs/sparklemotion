package baaahs

import com.soywiz.klock.DateFormat
import com.soywiz.klock.DateTime
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

fun <E> List<E>.random(): E? = if (size > 0) get(Random.nextInt(size)) else null

fun <E> List<E>.random(random: Random): E? = if (size > 0) get(random.nextInt(size)) else null

fun <E> Collection<E>.only(description: String = "item"): E {
    if (size != 1) throw IllegalArgumentException("Expected one $description, found $size: $this")
    else return iterator().next()
}

fun toRadians(degrees: Float) = (degrees * PI / 180).toFloat()

fun constrain(value: Float, minValue: Float, maxValue: Float): Float {
    return max(min(value, maxValue), minValue)
}

suspend fun randomDelay(timeMs: Int) {
    delay(Random.nextInt(timeMs).toLong())
}

class Logger(val id: String) {
    private fun log(level: String, message: String) {
        println("${ts()} [] $level  $id - $message")
    }

    private fun log(level: String, message: String, exception: Exception) {
        println("${ts()} [] $level  $id - $message: ${exception.message}")
    }

    fun debug(message: () -> String) {
        log("DEBUG", message.invoke())
    }

    fun info(message: () -> String) {
        log("INFO", message.invoke())
    }

    fun warn(message: () -> String) {
        log("WARN", message.invoke())
    }

    fun error(message: () -> String) {
        log("ERROR", message.invoke())
    }

    fun error(message: String, exception: Exception) {
        log("ERROR", message, exception)
    }

    fun error(message: () -> String, exception: Exception) {
        log("ERROR", message.invoke(), exception)
    }

    companion object {
        private val FORMAT by lazy { DateFormat("yyyy-MM-dd HH:mm:ss.SSS") }

        fun ts(): String {
            return DateTime.now().format(FORMAT)
        }

    }
}


expect fun getTimeMillis(): Long
expect fun doRunBlocking(block: suspend () -> Unit)

expect fun getResource(name: String): String

expect fun decodeBase64(s: String): ByteArray

internal suspend fun time(function: suspend () -> Unit): Long {
    val now = getTimeMillis()
    function.invoke()
    return getTimeMillis() - now
}

internal fun timeSync(function: () -> Unit): Long {
    val now = getTimeMillis()
    function.invoke()
    return getTimeMillis() - now
}
