package baaahs.ui

import baaahs.Logger
import external.DroppableProvided
import external.copyFrom
import kotlinx.css.CSSBuilder
import kotlinx.css.RuleSet
import kotlinx.html.DIV
import react.RMutableRef
import react.ReactElement
import react.dom.RDOMBuilder

@Suppress("UNCHECKED_CAST")
fun <T> nuffin(): T = null as T

@Suppress("UNCHECKED_CAST")
fun <T> useRef(): RMutableRef<T> = react.useRef(null as T)

fun <T : Function<*>> useCallback(vararg dependencies: dynamic, callback: T): T {
    return react.useCallback(callback, dependencies)
}

fun useEffect(vararg dependencies: dynamic, name: String? = "Effect", effect: () -> Unit) {
    return react.useEffect(dependencies.toList()) {
        logger.debug {
            "useEffect $name run due to change: ${dependencies.map {
                it?.toString().truncate(12)
            }}"
        }
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

fun RuleSet.getName(): String {
    return CSSBuilder().apply { +this@getName }.classes.joinToString(" ")
}

operator fun RuleSet.unaryPlus(): String = getName()

infix fun String.and(ruleSet: RuleSet): String = this + " " + ruleSet.getName()

infix fun <T> RuleSet.on(clazz: T): Pair<T, String> {
    return clazz to getName()
}

infix fun RuleSet.and(that: RuleSet): MutableList<RuleSet> {
    return mutableListOf(this, that)
}

infix fun String.and(that: String): String {
    return "$this $that"
}

infix fun <T> List<RuleSet>.on(clazz: T): Pair<T, String> {
    return clazz to joinToString(" ") { it.getName() }
}

fun CSSBuilder.descendants(ruleSet: RuleSet, block: RuleSet) = "& .${ruleSet.getName()}".invoke(block)

fun RDOMBuilder<*>.install(droppableProvided: DroppableProvided) {
    ref = droppableProvided.innerRef
    copyFrom(droppableProvided.droppableProps)
}

fun RDOMBuilder<*>.insertPlaceholder(droppableProvided: DroppableProvided) {
    this.childList.add(droppableProvided.placeholder)
}

private val logger = Logger("util.kt")