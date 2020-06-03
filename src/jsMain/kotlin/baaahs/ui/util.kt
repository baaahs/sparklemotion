package baaahs.ui

import org.w3c.dom.svg.SVGAnimatedLength
import react.RMutableRef
import react.ReactElement

@Suppress("UNCHECKED_CAST")
fun <T> nuffin(): T = null as T

@Suppress("UNCHECKED_CAST")
fun <T> useRef(): RMutableRef<T> = react.useRef(null as T)

fun <T : Function<*>> useCallback(vararg dependencies: dynamic, callback: T): T {
    return react.useCallback(callback, dependencies)
}

fun useEffect(vararg dependencies: dynamic, name: String? = "Effect", effect: () -> Unit) {
    return react.useEffect(dependencies.toList()) {
        println("useEffect $name run due to change: ${dependencies.map {
            it?.toString().truncate(12)
        }}")
        effect()
    }
}

fun <T> List<T>.replace(index: Int, newValueBlock: (T) -> T): List<T> =
    mapIndexed { i, item -> if (i == index) newValueBlock(item) else item }

fun String.asTextNode(): ReactElement = asDynamic()

fun Array<*>.truncateStrings(length: Int): List<String?> {
    return map { it?.toString()?.truncate(length) }
}
fun String?.truncate(length: Int): String? {
    return if (this != null && this.length >= length) {
        substring(0, length - 1) + "…"
    } else {
        this
    }
}