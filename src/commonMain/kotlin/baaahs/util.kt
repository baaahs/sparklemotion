package baaahs

import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.random.Random

fun <E> List<E>.random(): E? = if (size > 0) get(Random.nextInt(size)) else null

fun <E> List<E>.random(random: Random): E? = if (size > 0) get(random.nextInt(size)) else null

fun <E> Collection<E>.only(description: String = "item"): E {
    if (size != 1) throw IllegalArgumentException("Expected one $description, found $size: $this")
    else return iterator().next()
}

fun toRadians(degrees: Float) = (degrees * PI / 180).toFloat()

suspend fun randomDelay(timeMs: Int) {
    delay(Random.nextInt(timeMs).toLong())
}

class logger {
    companion object {
        fun debug(message: String) {
            println("DEBUG: $message")
        }

        fun info(message: String) {
            println("INFO: $message")
        }

        fun warn(message: String) {
            println("WARN: $message")
        }

        fun error(message: String) {
            println("ERROR: $message")
        }
    }
}


expect fun getTimeMillis(): Long
expect fun doRunBlocking(block: suspend () -> Unit)

expect fun getResource(name: String): String

internal fun time(function: () -> Unit): Long {
    val now = getTimeMillis()
    function.invoke()
    return getTimeMillis() - now
}
