package materialui

import kotlinx.html.DIV
import kotlinx.html.Tag
import kotlinx.html.TagConsumer
import materialui.components.buttonbase.ButtonBaseProps
import materialui.components.buttongroup.ToggleButtonGroupElementBuilder
import react.RBuilder
import react.RClass
import react.ReactElement

@JsModule("@material-ui/lab/ToggleButtonGroup")
private external val toggleButtonGroupModule: dynamic

external interface ToggleButtonGroupProps : ButtonBaseProps {
    var label: ReactElement?
    val selected: Boolean?
}

@Suppress("UnsafeCastFromDynamic")
private val toggleButtonGroupComponent: RClass<ToggleButtonGroupProps> = toggleButtonGroupModule.default

fun RBuilder.toggleButtonGroup(
    vararg classMap: Pair<ToggleButtonGroupStyle, String>,
    block: ToggleButtonGroupElementBuilder<DIV>.() -> Unit
) = child(ToggleButtonGroupElementBuilder(toggleButtonGroupComponent, classMap.toList()) { DIV(mapOf(), it) }.apply(block).create())

fun <T : Tag> RBuilder.toggleButtonGroup(
    vararg classMap: Pair<ToggleButtonGroupStyle, String>,
    factory: (TagConsumer<Unit>) -> T,
    block: ToggleButtonGroupElementBuilder<T>.() -> Unit
) = child(ToggleButtonGroupElementBuilder(toggleButtonGroupComponent, classMap.toList(), factory).apply(block).create())

var DIV.exclusive : Boolean
    get() = attributeBooleanTicker.get(this, "selected")
    set(newValue) = attributeBooleanTicker.set(this, "selected", newValue)

@Suppress("EnumEntryName")
enum class ToggleButtonGroupStyle {
    root,
    expanded,
    group,
    content,
    iconContainer,
    label
}