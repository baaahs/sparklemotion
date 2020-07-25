package baaahs.ui

import baaahs.Logger
import external.DroppableProvided
import external.copyFrom
import kotlinx.css.CSSBuilder
import kotlinx.css.RuleSet
import org.w3c.dom.events.Event
import react.RMutableRef
import react.RProps
import react.ReactElement
import react.dom.RDOMBuilder
import styled.StyleSheet

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

@Suppress("UNCHECKED_CAST")
fun Function<*>.withEvent(): (Event) -> Unit = this as (Event) -> Unit

private val jsObj = js("Object")
fun RProps.copyInto(dest: RProps) {
    val src = this.asDynamic()
    val keys = jsObj.keys(this).unsafeCast<Array<String>>()
    keys.forEach { key -> dest.asDynamic()[key] = src[key] }
}

val RuleSet.name: String
    get() = CSSBuilder().apply { +this@name }.classes.joinToString(" ")

val RuleSet.selector: String
    get() = ".$name"

operator fun RuleSet.unaryPlus(): String = name
infix fun String.and(ruleSet: RuleSet): String = this + " " + ruleSet.name
infix fun <T> RuleSet.on(clazz: T): Pair<T, String> = clazz to name
infix fun <T> String.on(clazz: T): Pair<T, String> = clazz to this
infix fun <T> List<RuleSet>.on(clazz: T): Pair<T, String> = clazz to joinToString(" ") { it.name }
infix fun RuleSet.and(that: RuleSet): MutableList<RuleSet> = mutableListOf(this, that)
infix fun String.and(that: String): String = "$this $that"

fun CSSBuilder.child(ruleSet: RuleSet, block: RuleSet) = child(ruleSet.selector, block)
fun CSSBuilder.descendants(ruleSet: RuleSet, block: RuleSet) = descendants(ruleSet.selector, block)
fun CSSBuilder.within(ruleSet: RuleSet, block: RuleSet) = "${ruleSet.selector} &"(block)

fun CSSBuilder.mixIn(mixin: CSSBuilder) = declarations.putAll(mixin.declarations)

fun StyleSheet.partial(block: CSSBuilder.() -> Unit): CSSBuilder {
    return CSSBuilder().apply { block() }
}

fun RDOMBuilder<*>.install(droppableProvided: DroppableProvided) {
    ref = droppableProvided.innerRef
    copyFrom(droppableProvided.droppableProps)
}

fun RDOMBuilder<*>.insertPlaceholder(droppableProvided: DroppableProvided) {
    this.childList.add(droppableProvided.placeholder)
}

private val logger = Logger("util.kt")