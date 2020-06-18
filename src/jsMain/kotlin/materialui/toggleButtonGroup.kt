package materialui

import kotlinx.html.DIV
import kotlinx.html.Tag
import kotlinx.html.TagConsumer
import materialui.components.MaterialElementBuilder
import materialui.components.button.enums.ButtonColor
import materialui.components.button.enums.ButtonSize
import materialui.components.button.enums.ButtonVariant
import materialui.components.buttonbase.ButtonBaseProps
import materialui.components.buttongroup.enums.ButtonGroupOrientation
import materialui.components.buttongroup.enums.ButtonGroupStyle
import materialui.components.getValue
import materialui.components.setValue
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

//fun <T : Tag> RBuilder.toggleButtonGroup(
//    vararg classMap: Pair<ToggleButtonGroupStyle, String>,
//    factory: (TagConsumer<Unit>) -> T,
//    block: ToggleButtonGroupElementBuilder<T>.() -> Unit
//) = child(ToggleButtonGroupElementBuilder(toggleButtonGroupComponent, classMap.toList(), factory).apply(block).create())

var DIV.exclusive : Boolean
    get() = attributeBooleanBoolean.get(this, "exclusive")
    set(newValue) = attributeBooleanBoolean.set(this, "exclusive", newValue)

class ToggleButtonGroupElementBuilder<T: Tag> internal constructor(
    type: RClass<ToggleButtonGroupProps>,
    classMap: List<Pair<Enum<*>, String>>,
    factory: (TagConsumer<Unit>) -> T
) : MaterialElementBuilder<T, ToggleButtonGroupProps>(type, classMap, factory) {
    fun Tag.classes(vararg classMap: Pair<ButtonGroupStyle, String>) {
        classes(classMap.map { it.first to it.second })
    }

    var Tag.color: ButtonColor? by materialProps
    var Tag.disabled: Boolean? by materialProps
    var Tag.disableRipple: Boolean? by materialProps
    var Tag.disableTouchRipple: Boolean? by materialProps
    var Tag.fullWidth: Boolean? by materialProps
    var Tag.orientation: ButtonGroupOrientation? by materialProps
    var Tag.size: ButtonSize? by materialProps
    var Tag.variant: ButtonVariant? by materialProps
}

@Suppress("EnumEntryName")
enum class ToggleButtonGroupStyle {
    root,
    expanded,
    group,
    content,
    iconContainer,
    label
}