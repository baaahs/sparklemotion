package materialui

import baaahs.ui.Icon
import kotlinx.css.Color
import kotlinx.html.SVG
import kotlinx.html.Tag
import kotlinx.html.TagConsumer
import materialui.components.MaterialElementBuilder
import materialui.components.getValue
import materialui.components.setValue
import materialui.components.svgicon.SvgIconProps
import materialui.components.svgicon.enums.SvgIconColor
import materialui.components.svgicon.enums.SvgIconFontSize
import materialui.components.svgicon.enums.SvgIconStyle
import materialui.components.svgicon.htmlColor
import react.ComponentType
import react.RBuilder

typealias Icon = ComponentType<SvgIconProps>

fun RBuilder.icon(
    icon: Icon,
    vararg classMap: Pair<SvgIconStyle, String>,
    block: SvgIconElementBuilder<SVG>.() -> Unit = {}
) = child(
    SvgIconElementBuilder(icon.getReactIcon(), classMap.toList()) { SVG(mapOf(), it) }.apply(block)
        .create()
)

fun RBuilder.icon(
    icon: ComponentType<SvgIconProps>,
    vararg classMap: Pair<SvgIconStyle, String>,
    block: SvgIconElementBuilder<SVG>.() -> Unit = {}
) = child(
    SvgIconElementBuilder(icon, classMap.toList()) { SVG(mapOf(), it) }.apply(block)
        .create()
)

fun <T : Tag> RBuilder.icon(
    icon: ComponentType<SvgIconProps>,
    vararg classMap: Pair<SvgIconStyle, String>,
    factory: (TagConsumer<Unit>) -> T,
    block: SvgIconElementBuilder<T>.() -> Unit = {}
) = child(SvgIconElementBuilder(icon, classMap.toList(), factory).apply(block).create())




//fun RBuilder.icon(icon: Component<SvgIconProps, *>, handler: RHandler<SvgIconProps> = { }): ReactElement {
//    return icon {
//        child(icon::class, handler = handler)
//    }
//}
//
//fun RBuilder.icon(icon: Icon, handler: RHandler<SvgIconProps> = { }): ReactElement =
//    icon(icon.getReactIcon(), handler = handler)

class SvgIconElementBuilder<T: Tag> internal constructor(
    type: ComponentType<SvgIconProps>,
    classMap: List<Pair<Enum<*>, String>>,
    factory: (TagConsumer<Unit>) -> T
) : MaterialElementBuilder<T, SvgIconProps>(type, classMap, factory) {
    fun Tag.classes(vararg classMap: Pair<SvgIconStyle, String>) {
        classes(classMap.toList())
    }

    var Tag.color: SvgIconColor? by materialProps
    var Tag.fontSize: SvgIconFontSize? by materialProps
    var Tag.htmlColor: Color?
        get() = materialProps.htmlColor
        set(value) { materialProps.htmlColor = value }
    var Tag.shapeRendering: String? by materialProps
    var Tag.titleAccess: String? by materialProps
    var Tag.viewBox: String? by materialProps
}
