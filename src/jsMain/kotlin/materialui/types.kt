package materialui

import kotlinext.js.jsObject
import materialui.components.svgicon.SvgIconProps
import react.*

typealias Icon = RComponent<SvgIconProps, RState>

fun RBuilder.icon(icon: Icon, handler: SvgIconProps.() -> Unit = { }): ReactElement {
    return child(createElement(icon, jsObject(handler)))
}
