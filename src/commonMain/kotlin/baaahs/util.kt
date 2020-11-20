package baaahs

import baaahs.util.Clock
import baaahs.util.asMillis
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

fun <E> Collection<E>.only(description: String = "item"): E {
    if (size != 1)
        throw IllegalArgumentException("Expected one $description, found $size: $this")
    else return iterator().next()
}

fun <T> List<T>.replacing(index: Int, replacement: T): List<T> {
    return this.mapIndexed { i, t -> if (i == index) replacement else t }
}

fun toRadians(degrees: Float) = (degrees * PI / 180).toFloat()

fun constrain(value: Float, minValue: Float, maxValue: Float): Float {
    return max(min(value, maxValue), minValue)
}

suspend fun randomDelay(timeMs: Int) {
    delay(Random.nextInt(timeMs).toLong())
}

fun <T> unknown(type: String, key: T, among: Collection<T>): String {
    return "unknown $type \"$key\" among [${among.map { it.toString() }.sorted().joinToString(", ")}]"
}

fun <K, V> Map<K, V>?.getBang(key: K, type: String): V {
    if (this == null) {
        error("map for $type is null")
    }

    return get(key)
        ?: error(unknown(type, key, keys))
}

fun Int.boundedBy(range: IntRange): Int {
    return when {
        this < range.first -> range.first
        this > range.last -> range.last
        else -> this
    }
}


expect val internalTimerClock: Clock

expect fun doRunBlocking(block: suspend () -> Unit)

expect fun getResource(name: String): String

expect fun decodeBase64(s: String): ByteArray

internal suspend fun time(function: suspend () -> Unit) = internalTimerClock.time(function)
internal suspend fun Clock.time(function: suspend () -> Unit): Int {
    val then = now()
    function.invoke()
    return (now() - then).asMillis().toInt()
}

internal fun timeSync(function: () -> Unit) = internalTimerClock.timeSync(function)
internal fun Clock.timeSync(function: () -> Unit): Int {
    val then = now()
    function.invoke()
    return (now() - then).asMillis().toInt()
}

internal fun <T> elapsedSync(message: String, function: () -> T) = internalTimerClock.elapsedSync(message, function)
internal fun <T> Clock.elapsedSync(message: String, function: () -> T): T {
    val then = now()
    try {
        return function.invoke()
    } finally {
        println("$message: ${(now() - then).asMillis().toInt()}ms elapsed")
    }
}

fun String.camelize(): String =
    replace(Regex("([A-Z]+)"), " $1")
        .split(Regex("[^A-Za-z0-9]+"))
        .map { it.toLowerCase().capitalize() }
        .joinToString("")
        .decapitalize()

fun String.englishize(): String {
    return Regex("([A-Z](?=[a-z]+)|[A-Z]+(?![a-z]))").replace(this) {
        " " + it.value
    }.capitalize()
}


fun randomId(prefix: String): String {
    return prefix +
            "-" +
            Random.nextInt(0, Int.MAX_VALUE).toString(16) +
            "-" +
            Random.nextInt(0, Int.MAX_VALUE).toString(16)
}

// Workaround for https://youtrack.jetbrains.com/issue/KT-38501.
fun debugger() {
    println("Debugger breakpoint")
}