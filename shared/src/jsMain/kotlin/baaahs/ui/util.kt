package baaahs.ui

import baaahs.dasherize
import baaahs.document
import baaahs.get2DContext
import js.objects.Object
import kotlinext.js.getOwnPropertyNames
import kotlinx.css.*
import kotlinx.css.properties.Time
import kotlinx.css.properties.Timing
import kotlinx.css.properties.Transition
import kotlinx.css.properties.s
import mui.icons.material.SvgIconComponent
import mui.material.SvgIconProps
import mui.material.Typography
import mui.material.TypographyProps
import mui.material.styles.Theme
import mui.material.styles.TypographyVariant
import mui.system.Breakpoint
import react.*
import react.dom.RDOMBuilder
import react.dom.events.*
import react.dom.setProp
import styled.GlobalStyles
import styled.StyleSheet
import web.animations.requestAnimationFrame
import web.cssom.ClassName
import web.cssom.Length
import web.dom.Element
import web.dom.getComputedStyle
import web.events.Event
import web.events.EventTarget
import web.html.HTMLCanvasElement
import web.html.HTMLElement
import web.html.HTMLInputElement
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
fun Function<*>.withoutEvent(): () -> Unit = this as () -> Unit

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

var alreadyScheduled = false
val RuleSet.name: String
    get() = CssBuilder().apply {
        +this@name
    }.classes.joinToString(" ").also {
        if (!alreadyScheduled) {
            alreadyScheduled = true
            requestAnimationFrame { _ ->
                alreadyScheduled = false
                GlobalStyles.injectScheduled()
            }
        }
    }

val RuleSet.selector: String
    get() = ".$name"

operator fun RuleSet.unaryPlus(): String = name
operator fun RuleSet.unaryMinus(): ClassName = ClassName(name)
operator fun SvgIconComponent.unaryPlus(): ReactElement<SvgIconProps> = create()
val String.className: ClassName get() = ClassName(this)
infix fun String.and(ruleSet: RuleSet?): String =
    if (ruleSet == null) this else this + " " + ruleSet.name
infix fun ClassName.and(ruleSet: RuleSet?): ClassName =
    if (ruleSet == null) this else ClassName(this.unsafeCast<String>() + " " + ruleSet.name)
infix fun ClassName.and(className: String?): ClassName =
    if (className == null) this else ClassName(this.unsafeCast<String>() + " " + className)
infix fun <T> RuleSet.on(clazz: T): Pair<T, String> = clazz to name
infix fun <T> String.on(clazz: T): Pair<T, String> = clazz to this
infix fun <T> List<RuleSet>.on(clazz: T): Pair<T, String> = clazz to joinToString(" ") { it.name }
infix fun String.and(that: String?): String =
    if (that == null) this else "$this $that"
fun <T> Boolean.then(value: T): T? =
    if (this) value else null

fun CssBuilder.child(styleSheet: StyleSheet, rule: KProperty0<RuleSet>, block: RuleSet) =
    child(".${styleSheet.name}-${rule.name}") { block() }

fun StyleSheet.selector(rule: KProperty0<RuleSet>) =
    ".$name-${rule.name}"

fun CssBuilder.on(vararg selector: String = arrayOf("*"), block: RuleSet): Rule {
    val selectorString = selector.joinToString { it }
    return selectorString(block)
}

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

fun RElementBuilder<*>.mixin(jsObj: dynamic) {
    keys(jsObj).forEach { key ->
        attrs.asDynamic()[key] = jsObj[key]
    }
}

fun StyleSheet.partial(block: CssBuilder.() -> Unit): CssBuilder {
    return CssBuilder().apply { block() }
}

fun <T> StyledElement.important(property: KProperty<T>, value: T) {
    put(property.name, "$value !important")
}

val <T : CssValue> T.important: T
    get() = (object : CssValue("${this.value} !important") {}) as T

val <T : Any> T.important: T
    get() = (object : CssValue("$this !important") {}) as T

inline fun RBuilder.typographyH1(crossinline block: RElementBuilder<TypographyProps>.() -> Unit) =
    Typography {
        attrs.variant = TypographyVariant.h1
        block()
    }

inline fun RBuilder.typographyH2(crossinline block: RElementBuilder<TypographyProps>.() -> Unit) =
    Typography {
        attrs.variant = TypographyVariant.h2
        block()
    }

inline fun RBuilder.typographyH3(crossinline block: RElementBuilder<TypographyProps>.() -> Unit) =
    Typography {
        attrs.variant = TypographyVariant.h3
        block()
    }

inline fun RBuilder.typographyH4(crossinline block: RElementBuilder<TypographyProps>.() -> Unit) =
    Typography {
        attrs.variant = TypographyVariant.h4
        block()
    }

inline fun RBuilder.typographyH5(crossinline block: RElementBuilder<TypographyProps>.() -> Unit) =
    Typography {
        attrs.variant = TypographyVariant.h5
        block()
    }

inline fun RBuilder.typographyH6(crossinline block: RElementBuilder<TypographyProps>.() -> Unit) =
    Typography {
        attrs.variant = TypographyVariant.h6
        block()
    }

inline fun RBuilder.typographySubtitle1(crossinline block: RElementBuilder<TypographyProps>.() -> Unit) =
    Typography { //    (*classMap, factory = { DIV(mapOf(), it) })
        attrs.variant = TypographyVariant.subtitle1
        block()
    }

inline fun RBuilder.typographySubtitle2(crossinline block: RElementBuilder<TypographyProps>.() -> Unit) =
    Typography { // (*classMap, factory = { DIV(mapOf(), it) }) {
        attrs.variant = TypographyVariant.subtitle2
        block()
    }

inline fun RBuilder.typographyBody1(crossinline block: RElementBuilder<TypographyProps>.() -> Unit) =
    Typography {
        attrs.variant = TypographyVariant.body1
        block()
    }

inline fun RBuilder.typographyBody2(crossinline block: RElementBuilder<TypographyProps>.() -> Unit) =
    Typography {
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
    return object : JsView {
        override fun RBuilder.render() {
            block()
        }
    }
}

fun buildElements(handler: Render): ReactNode =
    react.buildElements(RBuilder(), handler)!!

object Events {
    object Button {
        const val primary = 0
    }

    object ButtonMask {
        const val primary = 1
    }
}

val Event.buttons: Int get() = asDynamic().buttons as Int
val Event.clientX: Int get() = asDynamic().clientX as Int
val Event.clientY: Int get() = asDynamic().clientY as Int

fun web.cssom.Color.asColor(): Color = Color(this.unsafeCast<String>())
fun kotlinx.css.Color.asColor(): web.cssom.Color = web.cssom.Color(this.value)


fun rgba(r: Int, g: Int, b: Int, a: Double): Color =
    Color("rgba($r, $g, $b, $a)")

fun Theme.paperContrast(amount: Double) =
    palette.text.primary.asColor()
        .withAlpha(amount)
        .blend(Color(palette.background.paper))

val Theme.paperLowContrast get() = paperContrast(.25)
val Theme.paperMediumContrast get() = paperContrast(.5)
val Theme.paperHighContrast get() = paperContrast(.75)

val Theme.isSmallScreen: String get() = breakpoints.down(Breakpoint.sm)

fun HTMLElement.fitText() {
    val parentEl = parentElement!!
    val parentStyle = getComputedStyle(parentEl)
    val marginX = with(parentStyle) { marginLeft.fromPx() + marginRight.fromPx() }
    val marginY = with(parentStyle) { marginTop.fromPx() + marginBottom.fromPx() }
    val buttonWidth = parentEl.clientWidth - marginX
    val buttonHeight = parentEl.clientHeight - marginY
    val canvas = document.createElement("canvas") as HTMLCanvasElement
    val ctx = canvas.get2DContext()
    val elementStyle = getComputedStyle(this)
    ctx.font = elementStyle.font
    val width = innerText.split(Regex("\\s+")).maxOf { word ->
        ctx.measureText(word).width
    }
    style.transform = if (width > buttonWidth) "scaleX(${buttonWidth / width})" else ""

    // This is extra dumb; we should check how many lines are likely to be rendered, not just use 2.
    style.lineHeight = if (buttonHeight < elementStyle.lineHeight.fromPx() * 2) "1em" else ""
}

private fun String.fromPx() = replace("px", "").toDouble()

fun Element.isParentOf(other: Element): Boolean {
    var current: Element? = other
    while (current != null) {
        val parent = current.parentElement
        if (parent === this) return true
        current = parent
    }
    return false
}

fun StyledElement.transition(
    property: String = "all",
    duration: Time = 0.s,
    timing: Timing = Timing.ease,
    delay: Time = 0.s
) {
    transition += Transition(property, duration, timing, delay)
}

fun StyledElement.transition(
    property: KProperty<*>,
    duration: Time = 0.s,
    timing: Timing = Timing.ease,
    delay: Time = 0.s
) {
    transition += Transition(property.name.dasherize(), duration, timing, delay)
}

fun Length.toLinearDimension(): LinearDimension =
    LinearDimension(toString())

external interface CopyableProps

fun RElementBuilder<*>.copyFrom(fromProps: Props?) {
    copyFrom(fromProps.unsafeCast<CopyableProps>())
}

fun RElementBuilder<*>.copyFrom(fromProps: CopyableProps?) {
    if (fromProps == null) return

    val from = fromProps.asDynamic()
    val keys = Object.keys(fromProps).unsafeCast<Array<String>>()
    keys.forEach { key -> attrs.asDynamic()[key] = from[key] }
}

fun RDOMBuilder<*>.copyFrom(fromProps: CopyableProps?) {
    if (fromProps == null) return

    val from = fromProps.asDynamic()
    val keys = Object.keys(fromProps).unsafeCast<Array<String>>()
    keys.forEach { key -> setProp(key, from[key]) }
}

fun <T> muiClasses(block: dynamic.() -> Unit): T {
    val obj = js("{}")
    block.invoke(obj)
    Object.keys(obj).forEach { key ->
        obj["Mui-$key"] = obj[key]
    }
    return obj.unsafeCast<T>()
}