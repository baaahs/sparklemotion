package materialui

import baaahs.ui.Icon
import kotlinext.js.jsObject
import materialui.components.icon.icon
import materialui.components.svgicon.SvgIconProps
import react.*

typealias Icon = RComponent<SvgIconProps, RState>

fun RBuilder.icon(icon: RComponent<SvgIconProps, *>, handler: RHandler<SvgIconProps> = { }): ReactElement {
    return icon {
        child(icon, jsObject() { fontSize = "inherit" }, handler = handler)
    }
}

fun RBuilder.icon(icon: Icon, handler: RHandler<SvgIconProps> = { }): ReactElement =
    icon(icon.getReactIcon(), handler = handler)
