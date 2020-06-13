package materialui

import kotlinx.html.Tag
import kotlinx.html.TagConsumer
import materialui.components.MaterialElementBuilder
import materialui.components.button.enums.ButtonColor
import materialui.components.button.enums.ButtonSize
import materialui.components.button.enums.ButtonVariant
import materialui.components.getValue
import materialui.components.setValue
import materialui.lab.components.treeItem.enums.TreeItemStyle
import react.RBuilder
import react.RClass
import react.ReactElement
import react.buildElement

class ToggleButtonElementBuilder<T : Tag> internal constructor(
    type: RClass<ToggleButtonProps>,
    classMap: List<Pair<Enum<*>, String>>,
    factory: (TagConsumer<Unit>) -> T
) : MaterialElementBuilder<T, ToggleButtonProps>(type, classMap, factory) {
    fun Tag.classes(vararg classMap: Pair<TreeItemStyle, String>) {
        classes(classMap.toList())
    }

    var Tag.color: ButtonColor? by materialProps
    var Tag.endIcon: ReactElement? by materialProps
    var Tag.fullWidth: Boolean? by materialProps
    var Tag.href: String? by materialProps
    var Tag.size: ButtonSize? by materialProps
    var Tag.startIcon: ReactElement? by materialProps
    var Tag.variant: ButtonVariant? by materialProps
//    var Tag.selected: Boolean? by materialProps

    fun Tag.endIcon(builder: RBuilder.() -> Unit) { endIcon = buildElement(builder) }
    fun Tag.startIcon(builder: RBuilder.() -> Unit) { startIcon = buildElement(builder) }
}