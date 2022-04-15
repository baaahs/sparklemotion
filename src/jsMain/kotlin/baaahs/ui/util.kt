package baaahs.ui

import csstype.ClassName
import external.DroppableProvided
import external.copyFrom
import kotlinext.js.getOwnPropertyNames
import kotlinx.css.*
import mui.material.Typography
import mui.material.TypographyProps
import org.w3c.dom.Element
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.events.Event
import org.w3c.dom.events.EventTarget
import react.RBuilder
import react.RElementBuilder
import react.ReactNode
import react.dom.RDOMBuilder
import react.dom.events.*
import react.dom.setProp
import styled.StyleSheet
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty0

@Suppress("UNCHECKED_CAST")
fun <T> nuffin(): T = null as T

fun <T> List<T>.replace(index: Int, newValueBlock: (T) -> T): List<T> =
    mapIndexed { i, item -> if (i == index) newValueBlock(item) else item }

fun String.asTextNode(): ReactNode = ReactNode(this)

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

@Suppress("UNCHECKED_CAST")
fun Function<*>.withMouseEvent(): MouseEventHandler<*> =
    this as MouseEventHandler<*>

@Suppress("UNCHECKED_CAST")
fun <T : Element> Function<*>.withTMouseEvent(): ((MouseEvent<T, *>, dynamic) -> Unit)? =
    this as ((MouseEvent<T, *>, dynamic) -> Unit)?

@Suppress("UNCHECKED_CAST")
fun Function<*>.withChangeEvent(): ChangeEventHandler<*> =
    this as ChangeEventHandler<*>

@Suppress("UNCHECKED_CAST")
fun Function<*>.withFormEvent(): FormEventHandler<*> =
    this as FormEventHandler<*>

@Suppress("UNCHECKED_CAST")
fun <T : Element> Function<*>.withTChangeEvent(): (event: ChangeEvent<T>, checked: Boolean) -> Unit =
    this as (event: ChangeEvent<T>, checked: Boolean) -> Unit

@Suppress("UNCHECKED_CAST")
fun <T : Element> Function<*>.withSelectEvent(): (event: ChangeEvent<T>, child: ReactNode) -> Unit =
    this as (event: ChangeEvent<T>, child: ReactNode) -> Unit

val EventTarget?.value: String
        get() = asDynamic().value as String

val EventTarget?.checked: Boolean
        get() = (this as HTMLInputElement).checked

val RuleSet.name: String
    get() = CssBuilder().apply { +this@name }.classes.joinToString(" ")

val RuleSet.selector: String
    get() = ".$name"

operator fun RuleSet.unaryPlus(): String = name
operator fun RuleSet.unaryMinus(): ClassName = ClassName(name)
val String.className: ClassName get() = ClassName(this)
infix fun String.and(ruleSet: RuleSet): String = this + " " + ruleSet.name
infix fun ClassName.and(ruleSet: RuleSet): ClassName = ClassName(this.unsafeCast<String>() + " " + ruleSet.name)
infix fun <T> RuleSet.on(clazz: T): Pair<T, String> = clazz to name
infix fun <T> String.on(clazz: T): Pair<T, String> = clazz to this
infix fun <T> List<RuleSet>.on(clazz: T): Pair<T, String> = clazz to joinToString(" ") { it.name }
infix fun RuleSet.and(that: RuleSet): MutableList<RuleSet> = mutableListOf(this, that)
infix fun String.and(that: String): String = "$this $that"


fun CssBuilder.child(styleSheet: StyleSheet, rule: KProperty0<RuleSet>, block: RuleSet) =
    child(".${styleSheet.name}-${rule.name}") { block() }

fun CssBuilder.descendants(styleSheet: StyleSheet, rule: KProperty0<RuleSet>, block: RuleSet) =
    descendants(".${styleSheet.name}-${rule.name}") { block() }

fun CssBuilder.within(ruleSet: RuleSet, block: RuleSet) = "${ruleSet.selector} &"(block)

fun CssBuilder.mixIn(mixin: Any) =
    when (mixin) {
        is CssBuilder -> declarations.putAll(mixin.declarations)
        else -> {
            for (key in mixin.getOwnPropertyNames()) {
                declarations[key] = mixin.asDynamic()[key]
            }
        }
    }

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

fun RElementBuilder<*>.install(droppableProvided: DroppableProvided) {
    ref = droppableProvided.innerRef
    copyFrom(droppableProvided.droppableProps)
}

inline fun RBuilder.typographyH1(crossinline block: RElementBuilder<TypographyProps>.() -> Unit) =
    Typography {
        attrs.variant = "h1"
        block()
    }

inline fun RBuilder.typographyH2(crossinline block: RElementBuilder<TypographyProps>.() -> Unit) =
    Typography {
        attrs.variant = "h2"
        block()
    }

inline fun RBuilder.typographyH3(crossinline block: RElementBuilder<TypographyProps>.() -> Unit) =
    Typography {
        attrs.variant = "h3"
        block()
    }

inline fun RBuilder.typographyH4(crossinline block: RElementBuilder<TypographyProps>.() -> Unit) =
    Typography {
        attrs.variant = "h4"
        block()
    }

inline fun RBuilder.typographyH5(crossinline block: RElementBuilder<TypographyProps>.() -> Unit) =
    Typography {
        attrs.variant = "h5"
        block()
    }

inline fun RBuilder.typographyH6(crossinline block: RElementBuilder<TypographyProps>.() -> Unit) =
    Typography {
        attrs.variant = "h6"
        block()
    }

inline fun RBuilder.typographySubtitle1(crossinline block: RElementBuilder<TypographyProps>.() -> Unit) =
    Typography { //    (*classMap, factory = { DIV(mapOf(), it) })
        attrs.variant = "subtitle1"
        block()
    }

inline fun RBuilder.typographySubtitle2(crossinline block: RElementBuilder<TypographyProps>.() -> Unit) =
    Typography { // (*classMap, factory = { DIV(mapOf(), it) }) {
        attrs.variant = "subtitle2"
        block()
    }

inline fun RBuilder.typographyBody1(crossinline block: RElementBuilder<TypographyProps>.() -> Unit) =
    Typography {
        attrs.variant = "body1"
        block()
    }

inline fun RBuilder.typographyBody2(crossinline block: RElementBuilder<TypographyProps>.() -> Unit) =
    Typography {
        attrs.variant = "body2"
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

fun csstype.Color.asColor(): Color = Color(this.unsafeCast<String>())