package baaahs

import com.soywiz.klock.DateFormat
import com.soywiz.klock.DateTime
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

fun <K, V> Map<K, V>.getBang(key: K, type: String): V {
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

expect fun log(id: String, level: String, message: String, exception: Throwable? = null)
expect fun logGroupBegin(id: String, message: String)
expect fun logGroupEnd(id: String, message: String)

class Logger(val id: String) {
    fun debug(message: () -> String) {
        log(id, "DEBUG", message.invoke())
    }

    fun info(message: () -> String) {
        log(id, "INFO", message.invoke())
    }

    fun warn(message: () -> String) {
        log(id, "WARN", message.invoke())
    }

    fun warn(exception: Throwable, message: () -> String) {
        log(id, "WARN", message.invoke(), exception)
    }

    fun error(message: () -> String) {
        log(id, "ERROR", message.invoke())
    }

    fun error(message: String, exception: Throwable) {
        log(id, "ERROR", message, exception)
    }

    fun error(exception: Throwable, message: () -> String) {
        log(id, "ERROR", message.invoke(), exception)
    }

    fun <T> group(message: String, block: () -> T): T {
        logGroupBegin(id, message)
        return try {
            block()
        } finally {
            logGroupEnd(id, message)
        }
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

internal fun timeSync(function: () -> Unit): Int {
    val now = getTimeMillis()
    function.invoke()
    return (getTimeMillis() - now).toInt()
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
