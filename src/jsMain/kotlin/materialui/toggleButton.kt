package materialui

import kotlinx.html.BUTTON
import kotlinx.html.Tag
import kotlinx.html.TagConsumer
import kotlinx.html.attributes.Attribute
import kotlinx.html.attributes.TickerAttribute
import materialui.components.buttonbase.ButtonBaseProps
import react.RBuilder
import react.RClass
import react.ReactElement

@JsModule("@material-ui/lab/ToggleButton")
private external val toggleButtonModule: dynamic

external interface ToggleButtonProps : ButtonBaseProps {
    var label: ReactElement?
    val selected: Boolean?
}

@Suppress("UnsafeCastFromDynamic")
private val toggleButtonComponent: RClass<ToggleButtonProps> = toggleButtonModule.default

fun RBuilder.toggleButton(
    vararg classMap: Pair<ToggleButtonStyle, String>,
    block: ToggleButtonElementBuilder<BUTTON>.() -> Unit
) = child(ToggleButtonElementBuilder(toggleButtonComponent, classMap.toList()) { BUTTON(mapOf(), it) }.apply(block).create())

fun <T : Tag> RBuilder.toggleButton(
    vararg classMap: Pair<ToggleButtonStyle, String>,
    factory: (TagConsumer<Unit>) -> T,
    block: ToggleButtonElementBuilder<T>.() -> Unit
) = child(ToggleButtonElementBuilder(toggleButtonComponent, classMap.toList(), factory).apply(block).create())

val attributeBooleanTicker : Attribute<Boolean> = TickerAttribute()

var BUTTON.selected : Boolean
    get() = attributeBooleanTicker.get(this, "selected")
    set(newValue) = attributeBooleanTicker.set(this, "selected", newValue)

@Suppress("EnumEntryName")
enum class ToggleButtonStyle {
    root,
    expanded,
    group,
    content,
    iconContainer,
    label
}