package baaahs.ui

import external.DroppableProvided
import external.copyFrom
import kotlinx.css.CssBuilder
import kotlinx.css.LinearDimension
import kotlinx.css.RuleSet
import kotlinx.css.StyledElement
import kotlinx.html.DIV
import materialui.components.typography.TypographyElementBuilder
import materialui.components.typography.TypographyProps
import materialui.components.typography.enums.TypographyStyle
import materialui.components.typography.enums.TypographyVariant
import materialui.components.typography.typography
import org.w3c.dom.events.Event
import org.w3c.dom.events.EventTarget
import react.RBuilder
import react.ReactElement
import react.dom.RDOMBuilder
import react.dom.setProp
import styled.StyleSheet
import kotlin.reflect.KProperty

@Suppress("UNCHECKED_CAST")
fun <T> nuffin(): T = null as T

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

val EventTarget?.value: String
        get() = asDynamic()!!.value as String

val RuleSet.name: String
    get() = CssBuilder().apply { +this@name }.classes.joinToString(" ")

val RuleSet.selector: String
    get() = ".$name"

operator fun RuleSet.unaryPlus(): String = name
infix fun String.and(ruleSet: RuleSet): String = this + " " + ruleSet.name
infix fun <T> RuleSet.on(clazz: T): Pair<T, String> = clazz to name
infix fun <T> String.on(clazz: T): Pair<T, String> = clazz to this
infix fun <T> List<RuleSet>.on(clazz: T): Pair<T, String> = clazz to joinToString(" ") { it.name }
infix fun RuleSet.and(that: RuleSet): MutableList<RuleSet> = mutableListOf(this, that)
infix fun String.and(that: String): String = "$this $that"

fun CssBuilder.child(ruleSet: RuleSet, block: RuleSet) = child(ruleSet.selector, block)
fun CssBuilder.descendants(ruleSet: RuleSet, block: RuleSet) = descendants(ruleSet.selector, block)
fun CssBuilder.within(ruleSet: RuleSet, block: RuleSet) = "${ruleSet.selector} &"(block)

fun CssBuilder.mixIn(mixin: CssBuilder) = declarations.putAll(mixin.declarations)

fun keys(jsObj: dynamic) = js("Object").keys(jsObj).unsafeCast<Array<String>>()

fun RDOMBuilder<*>.mixin(jsObj: dynamic) {
    keys(jsObj).forEach { key ->
        setProp(key, jsObj[key])
    }
}

fun StyleSheet.partial(block: CssBuilder.() -> Unit): CssBuilder {
    return CssBuilder().apply { block() }
}

fun <T> StyledElement.important(property: KProperty<T>, value: T) {
    put(property.name, "$value !important")
}

fun RDOMBuilder<*>.install(droppableProvided: DroppableProvided) {
    ref = droppableProvided.innerRef
    copyFrom(droppableProvided.droppableProps)
}

inline fun RBuilder.typographySubtitle1(vararg classMap: Pair<TypographyStyle, String>, crossinline block: TypographyElementBuilder<DIV, TypographyProps>.() -> Unit)
        = typography(*classMap, factory = { DIV(mapOf(), it) }) {
    attrs.variant = TypographyVariant.subtitle1
    block()
}

inline fun RBuilder.typographySubtitle2(vararg classMap: Pair<TypographyStyle, String>, crossinline block: TypographyElementBuilder<DIV, TypographyProps>.() -> Unit)
        = typography(*classMap, factory = { DIV(mapOf(), it) }) {
    attrs.variant = TypographyVariant.subtitle2
    block()
}

inline fun RBuilder.typographyBody1(vararg classMap: Pair<TypographyStyle, String>, crossinline block: TypographyElementBuilder<DIV, TypographyProps>.() -> Unit)
        = typography(*classMap, factory = { DIV(mapOf(), it) }) {
    attrs.variant = TypographyVariant.body1
    block()
}

inline fun RBuilder.typographyBody2(vararg classMap: Pair<TypographyStyle, String>, crossinline block: TypographyElementBuilder<DIV, TypographyProps>.() -> Unit)
        = typography(*classMap, factory = { DIV(mapOf(), it) }) {
    attrs.variant = TypographyVariant.body2
    block()
}

fun LinearDimension.inPixels(): Int {
    return when {
        value == "0" -> 0
        value.endsWith("px") -> value.replace("px", "").toInt()
        else -> error("Not a pixel dimension: \"$value\".")
    }
}

fun renderWrapper(block: RBuilder.() -> Unit): View {
    return object : View {
        override fun RBuilder.render() {
            block()
        }
    }
}

val preventDefault: (Event) -> Unit = { event -> event.preventDefault() }
val disableScroll = {
    baaahs.document.body?.addEventListener("touchmove", preventDefault, js("{ passive: false }"))
}
val enableScroll = {
    baaahs.document.body?.removeEventListener("touchmove", preventDefault)
}

object Events {
    const val primaryButton = 1
}

val Event.buttons: Int get() = asDynamic().buttons as Int
val Event.clientX: Int get() = asDynamic().clientX as Int
val Event.clientY: Int get() = asDynamic().clientY as Int
