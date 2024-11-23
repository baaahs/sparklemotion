package baaahs

import baaahs.util.*
import kotlinx.coroutines.*
import kotlin.math.*
import kotlin.random.Random
import kotlin.reflect.KProperty

fun <E> Collection<E>.only(description: String = "item"): E {
    if (size != 1)
        throw IllegalArgumentException("Expected one $description, found $size: $this")
    else return iterator().next()
}

fun <E> Array<E>.only(description: String = "item"): E {
    if (size != 1)
        throw IllegalArgumentException("Expected one $description, found $size: $this")
    else return iterator().next()
}

fun FloatArray.resize(size: Int, init: (Int) -> Float): FloatArray =
    FloatArray(size) { i ->
        if (i < size) get(i) else init(i)
    }

fun <E> Collection<E>.onlyOrNull(): E? {
    return if (size != 1) null else iterator().next()
}

fun <T> List<T>.replacing(index: Int, replacement: T): List<T> {
    return this.mapIndexed { i, t -> if (i == index) replacement else t }
}

fun <T: Any?> MutableList<T>.replaceAll(block: (T) -> T) {
    forEachIndexed { index: Int, item: T -> this[index] = block(item) }
}

fun <T: Any?> MutableList<T>.replace(predicate: (T) -> Boolean, block: (T) -> T) {
    forEachIndexed { index: Int, item: T ->
        if (predicate(item)) {
            this[index] = block(item)
        }
    }
}

fun toRadians(degrees: Float) = (degrees * PI / 180).toFloat()

fun Int.clamp(minValue: Int, maxValue: Int): Int =
    max(min(this, maxValue), minValue)

fun Float.clamp(minValue: Float, maxValue: Float): Float =
    max(min(this, maxValue), minValue)

fun Double.clamp(minValue: Double, maxValue: Double): Double =
    max(min(this, maxValue), minValue)

fun ClosedRange<Float>.scale(value: Float) =
    (endInclusive - start) * value + start

fun ClosedRange<Float>.unscale(value: Float) =
    (value - start) / (endInclusive - start)

fun ClosedRange<Float>.clamp(value: Float) =
    max(min(value, endInclusive), start)

val ClosedRange<Float>.diff
    get() =
        (endInclusive - start).absoluteValue


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


val internalTimerClock: Clock = SystemClock

expect fun <T> doRunBlocking(block: suspend () -> T): T

expect fun decodeBase64(s: String): ByteArray

expect fun encodeBase64(b: ByteArray): String

internal suspend fun time(function: suspend () -> Unit) = internalTimerClock.time(function)
internal suspend fun Clock.time(function: suspend () -> Unit): Int {
    val then = now().asDoubleSeconds
    function.invoke()
    return (now().asDoubleSeconds - then).asMillis().toInt()
}

internal fun timeSync(function: () -> Unit) = internalTimerClock.timeSync(function)
internal fun Clock.timeSync(function: () -> Unit): Int {
    val then = now().asDoubleSeconds
    function.invoke()
    return (now().asDoubleSeconds - then).asMillis().toInt()
}

fun String.camelize(): String =
    replace(Regex("([A-Z]+)"), " $1")
        .split(Regex("[^A-Za-z0-9]+"))
        .joinToString("") { it.toLowerCase().capitalize() }
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

fun <T : Any?> T.listOf() = listOf(this)

// Workaround for https://youtrack.jetbrains.com/issue/KT-38501.
fun debugger(arg: String = "?") {
    println("Debugger breakpoint: $arg")
}

// Workaround for https://youtrack.jetbrains.com/issue/KT-41471.
operator fun <T> Lazy<T>.getValue(thisRef: Any?, property: KProperty<*>) = value

suspend fun throttle(targetRatePerSecond: Float, logger: Logger? = null, block: suspend () -> Unit) {
    val startTime = internalTimerClock.now().asDoubleSeconds

    block()

    val endTime = internalTimerClock.now().asDoubleSeconds
    val elapsed = endTime - startTime
    val target = 1f / targetRatePerSecond
    val delayMs = ((target - elapsed) * 1000).roundToInt()
    if (delayMs <= 0) {
        val elapsedMs = elapsed.asMillis()
        val targetMs = target.asMillis()
        if (elapsedMs > targetMs * 2) {
            logger?.warn { "Throttled block took ${elapsedMs}ms; target is ${targetMs}ms." }
        }
        yield()
    } else {
        delay(delayMs.toLong())
    }
}


fun <T> futureAsync(scope: CoroutineScope = GlobalScope, block: suspend () -> T): Deferred<T> =
    scope.async { block.invoke() }

fun <T> Deferred<T>.onAvailable(callback: (T) -> Unit) {
    this.invokeOnCompletion { callback.invoke(this.getCompleted()) }
}

val Pair<Int, Int>.x get() = first
val Pair<Int, Int>.y get() = second